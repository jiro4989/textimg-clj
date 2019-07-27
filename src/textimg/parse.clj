(ns textimg.parse
  (:require [textimg.color :refer :all]
            [clojure.string :as str]))

(def esc-kind [:empty :text :esc-seq-color :esc-seq-not-color])
(def color-type [:fg :bg :reset])

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

(defn to-color-esc
  [color-code]
  (let [color-type (cond
                     (= 1 (int (/ color-code 100))) :bg
                     (= 1 (int (/ color-code 90))) :fg
                     (= 1 (int (/ color-code 40))) :bg
                     (= 1 (int (/ color-code 30))) :fg
                     (= color-code 0) :reset
                     :else nil)]
    {:color-type color-type :color (color-code-map color-code)}))

(defn parse-color-esc
  [^String esc]
  (-> esc
      (str/replace #"\u001b\[" "")
      (str/replace #"m" "")
      (str/split #";")
      (->> (map #(if (= % "") "0" %))
           (map #(to-color-esc (Integer/parseInt %))))))