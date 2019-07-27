(ns textimg.image-test
  (:require [clojure.test :refer :all]
            [textimg.image :refer :all]))

(deftest a-test
  (testing "FIXME, I fail."
    (do
      (println "read-font")
      (println (read-font))
      (draw)
      true)))
