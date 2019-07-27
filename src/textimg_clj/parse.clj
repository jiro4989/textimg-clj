(ns textimg-clj.parse)

(def esc-kind [:empty :text :esc-seq-color :esc-seq-not-color])

(defn parse-prefix
  "先頭の文字要素を取得して分類する。"
  [text]
  {:kind "esc-seq-color" :prefix "\u001b[31m" :suffix "Red"})