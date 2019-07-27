(ns textimg.image
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