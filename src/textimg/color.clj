(ns textimg.color
  (:import [java.awt Color]))

; (def color-map )

(def rgba-none (Color. 0 0 0 0))
(def rgba-black (Color. 0 0 0 255))
(def rgba-red (Color. 255 0 0 255))
(def rgba-green (Color. 0 255 0 255))
(def rgba-yellow (Color. 255 255 0 255))
(def rgba-blue (Color. 0 0 255 255))
(def rgba-magenta (Color. 255 0 255 255))
(def rgba-cyan (Color. 0 255 255 255))
(def rgba-lightgray (Color. 211 211 211 255))
(def rgba-darkgray (Color. 169 169 169 255))
(def rgba-lightred (Color. 255 144 144 255))
(def rgba-lightgreen (Color. 144 238 144 255))
(def rgba-lightyellow (Color. 255 255 224 255))
(def rgba-lightblue (Color. 173 216 230 255))
(def rgba-lightmagenta (Color. 255 224 255 255))
(def rgba-lightcyan (Color. 224 255 255 255))
(def rgba-white (Color. 255 255 255 255))

(def color-name-map {:black rgba-black
                     :red rgba-red
                     :green rgba-green
                     :yellow rgba-yellow
                     :blue rgba-blue
                     :magenta rgba-magenta
                     :cyan rgba-cyan
                     :white rgba-white})

(def color-code-map {; 前景色
                     30 rgba-black
                     31 rgba-red
                     32 rgba-green
                     33 rgba-yellow
                     34 rgba-blue
                     35 rgba-magenta
                     36 rgba-cyan
                     37 rgba-lightgray
                     90 rgba-darkgray
                     91 rgba-lightred
                     92 rgba-lightgreen
                     93 rgba-lightyellow
                     94 rgba-lightblue
                     95 rgba-lightmagenta
                     96 rgba-lightcyan
                     97 rgba-white
                     ; 背景色
                     40 rgba-black
                     41 rgba-red
                     42 rgba-green
                     43 rgba-yellow
                     44 rgba-blue
                     45 rgba-magenta
                     46 rgba-cyan
                     47 rgba-lightgray
                     100 rgba-darkgray
                     101 rgba-lightred
                     102 rgba-lightgreen
                     103 rgba-lightyellow
                     104 rgba-lightblue
                     105 rgba-lightmagenta
                     106 rgba-lightcyan
                     107 rgba-white})