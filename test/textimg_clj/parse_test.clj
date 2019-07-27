(ns textimg-clj.parse-test
  (:require [clojure.test :refer :all]
            [textimg-clj.parse :refer :all]))

(deftest parse-prefix-test
  (testing "FIXME, I fail."
    (is (= {:kind "esc-seq-color" :prefix "\u001b[31m" :suffix "Red"}
           (parse-prefix "\u001b[31mRed")))))
