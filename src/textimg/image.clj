(ns textimg.image
  (:require [clojure.string :as str]
            [textimg.parse :refer :all])
  (:import [java.awt Font Graphics Color RenderingHints]
           [java.awt.image BufferedImage]
           [java.io File FileInputStream]
           [javax.imageio ImageIO]))

; ファイルからフォントを読み込む http://aidiary.hatenablog.com/entry/20070408/1251466316
; フォント指定 https://nompor.com/2017/12/02/post-1523/
; 画像への描画 https://www.javalife.jp/2018/02/28/post-462/

(def font-file "/usr/share/fonts/truetype/ricty-diminished/RictyDiminished-Regular.ttf")
(def font-size 24)

(defn read-font
  []
  (let [strm (FileInputStream. (File. font-file))]
    (.deriveFont (Font/createFont Font/TRUETYPE_FONT strm) 24.0)))

(defn draw
  []
  (do
    (let [img (BufferedImage. 200 200 BufferedImage/TYPE_INT_ARGB)]
      (doto (.getGraphics img)
        (.setRenderingHint RenderingHints/KEY_TEXT_ANTIALIASING RenderingHints/VALUE_TEXT_ANTIALIAS_ON)
        (.setFont (read-font))
        (.setColor Color/WHITE)
        (.fillRect 0 0 200 200)
        (.setColor Color/BLACK)
        (.drawString "Helloあいうえお" 0 24)
        .dispose)
      (ImageIO/write img "png" (File. "out.png")))))

(defn font-width
  [g c]
  (.. g getFontMetrics (stringWidth c)))

(defn font-height
  [g]
  (.. g getFontMetrics (getHeight)))

(defn draw-line
  [^Graphics g
   ^String text
   x
   y
   default-fg
   default-bg]
  (loop [g2 g
         c (str/split text #"")
         x2 x]
    (if (empty? c)
      g
      (let [fc (first c)]
        (let [w (font-width g2 fc)
              h (font-height g2)]
          (recur (doto g2
                   (.setColor Color/WHITE)
                   (.fillRect x2 (- y h) w h)
                   (.setColor Color/BLACK)
                   (.drawString fc x2 y))
                 (drop 1 c)
                 (+ x2 w)))))))

(defn draw3
  []
  (do
    (let [img (BufferedImage. 200 200 BufferedImage/TYPE_INT_ARGB)]
      (let [g (.getGraphics img)]
       (doto g
         (.setRenderingHint RenderingHints/KEY_TEXT_ANTIALIASING RenderingHints/VALUE_TEXT_ANTIALIAS_ON)
         (.setFont (read-font))
         (draw-line "Hello" 0 (font-height g) Color/BLACK Color/WHITE)
         (draw-line "あいうえお" 0 (* 2 (font-height g)) Color/BLACK Color/WHITE)
         .dispose))
      (ImageIO/write img "png" (File. "out3.png")))))

; (defn draw2
;   [^Graphics g
;    ^String text
;    ^Color default-fg
;    ^Color default-bg]
;   (loop [line (str/split text #"\n")]
;     (if (empty? line)
;       g
;       (loop [esc (parse-color-esc line)
;              fg default-fg
;              bg default-bg]
;         (if (empty? esc)
;           nil
;           (do
;             TODO)))
;       )))