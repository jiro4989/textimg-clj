(ns textimg-clj.parse)

(def esc-kind [:empty :text :esc-seq-color :esc-seq-not-color])
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
