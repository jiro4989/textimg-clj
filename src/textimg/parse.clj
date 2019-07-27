(ns textimg.parse
  (:import [java.awt Color])
  (:require [textimg.color :refer :all]
            [clojure.string :as str]))

(def esc-kind [:empty :text :esc-seq-color :esc-seq-not-color])
(def color-type [:fg :bg :reset])
(def color-genre [:normal :ext-256 :ext-rgb])

(def color-esc-re #"^\u001b\[[\d;]*m")
(def not-color-esc-re #"^\u001b\[\d*[A-HfSTJK]")
(def text-re #"^[^\u001b]+")

(defn parse-prefix
  "先頭の文字要素を取得して分類する。"
  [^String text]
  (if (empty? text)
    ; 空のときはさっさと終了
    {:kind "empty" :prefix "" :suffix ""}
    (let [matched (re-find color-esc-re text)]
      (if (not (nil? matched))
        ; 色のANSIエスケープシーケンスなので返却
        {:kind "esc-seq-color" :prefix matched :suffix (subs text (count matched))}
        (let [matched (re-find not-color-esc-re text)]
          (if (not (nil? matched))
            ; 色のANSIエスケープシーケンスではないがマッチしたので返却
            {:kind "esc-seq-not-color" :prefix matched :suffix (subs text (count matched))}
            (let [matched (re-find text-re text)]
              (if (not (nil? matched))
                ; 次のエスケープシーケンスの直前までテキストとして返却
                {:kind "text" :prefix matched :suffix (subs text (count matched))}
                {:kind "text" :prefix text :suffix ""}))))))))

(defn base-color?
  [^long code ^long num]
  (= 1 (int (/ code num))))

(defn fg-color?
  [^long code]
  (base-color? code 30))

(defn bg-color?
  [^long code]
  (base-color? code 40))

(defn get-color-type
 [^long code]
 (cond
   (base-color? code 100) :bg
   (base-color? code 90) :fg
   (bg-color? code) :bg
   (fg-color? code) :fg
   (= code 0) :reset
   :else nil))

(defn to-color-esc
  [code]
  (let [type (get-color-type code)]
    {:type type :color (color-code-map code)}))

(defn to-color-256
  [data]
  (let [code (first data)]
    (let [type (get-color-type code)]
      {:type type :color (color-rgba-map (nth data 2))})))

(defn to-color-rgb
  [data]
  (let [code (first data)]
    (let [type (get-color-type code)]
      {:type type :color (Color. (nth data 2)
                                 (nth data 3)
                                 (nth data 4))})))

(defn classify-color-genre
  "色コードの分類を返す"
  [codes]
  ; 31;42;0
  ; 48;2;0;0;0
  ; 38;5;255
  ; 31;42;0;38;5;255;48;2;0;0;0
  (loop [cs codes ret []]
    (if (empty? cs)
      ret
      (let [pref (first cs)]
        (cond
          (= pref 0) (recur (drop 1 cs)
                            (conj ret {:genre :normal
                                       :code (take 1 cs)}))
          (or (and (<= 30 pref) (<= pref 37))
              (and (<= 40 pref) (<= pref 47))) (recur (drop 1 cs)
                                                      (conj ret {:genre :normal
                                                                 :code (take 1 cs)}))
          (or (= 38 pref)
              (= 48 pref)) (let [suff-type (second cs)]
                             (cond
                               (= suff-type 2) (recur (drop 5 cs)
                                                      (conj ret {:genre :ext-rgb
                                                                 :code (take 5 cs)}))
                               (= suff-type 5) (recur (drop 3 cs)
                                                      (conj ret {:genre :ext-256
                                                                 :code (take 3 cs)}))
                               :else (recur (drop 1 cs)
                                            (conj ret {:genre nil
                                                       :code (take 1 cs)}))))
          :else (recur (drop 1 cs)
                       (conj ret {:genre nil
                                  :code (take 1 cs)})))))))

(defn parse-color-esc
  [^String esc]
  (-> esc
      (str/replace #"\u001b\[" "")
      (str/replace #"m" "")
      (str/split #";")
      (->> (map #(if (= % "") "0" %))
           (map #(. Integer parseInt %)))
      classify-color-genre
      (->> (map #(cond
                   (= :normal (:genre %)) (to-color-esc (:code %))
                   (= :ext-256 (:genre %)) (to-color-256 (:code %))
                   (= :ext-rgb (:genre %)) (to-color-rgb (:code %))
                   :else nil)))))