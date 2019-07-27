(ns textimg.parse-test
  (:require [clojure.test :refer :all]
            [textimg.parse :refer :all]
            [textimg.color :refer :all]))

(deftest parse-prefix-test
  (testing "(前景)ANSIエスケープシーケンスは1つだけ"
    (is (= {:kind "esc-seq-color" :prefix "\u001b[31m" :suffix "Red"}
           (parse-prefix "\u001b[31mRed")))
    (is (= {:kind "esc-seq-color" :prefix "\u001b[32m" :suffix "Green"}
           (parse-prefix "\u001b[32mGreen"))))
  (testing "(背景)ANSIエスケープシーケンスは1つだけ"
    (is (= {:kind "esc-seq-color" :prefix "\u001b[41m" :suffix "Red"}
           (parse-prefix "\u001b[41mRed")))
    (is (= {:kind "esc-seq-color" :prefix "\u001b[42m" :suffix "Green"}
           (parse-prefix "\u001b[42mGreen"))))
  (testing "ANSIリセット文字1つ"
    (is (= {:kind "esc-seq-color" :prefix "\u001b[0m" :suffix "Reset"}
           (parse-prefix "\u001b[0mReset")))
    (is (= {:kind "esc-seq-color" :prefix "\u001b[m" :suffix "Reset"}
           (parse-prefix "\u001b[mReset"))))
  (testing "セミコロン区切りで複数の条件を指定"
    (is (= {:kind "esc-seq-color" :prefix "\u001b[31;32;41;43m" :suffix "Red"}
           (parse-prefix "\u001b[31;32;41;43mRed")))
    (is (= {:kind "esc-seq-color" :prefix "\u001b[31;32;41;43;0m" :suffix "Reset"}
           (parse-prefix "\u001b[31;32;41;43;0mReset"))))
  (testing "末尾の文字が空"
    (is (= {:kind "esc-seq-color" :prefix "\u001b[31;32;41;43;0m" :suffix ""}
           (parse-prefix "\u001b[31;32;41;43;0m"))))
  (testing "空文字列のときはempty"
    (is (= {:kind "empty" :prefix "" :suffix ""}
           (parse-prefix ""))))
  (testing "色以外のエスケープシーケンス"
    (is (= {:kind "esc-seq-not-color" :prefix "\u001b[1A" :suffix "Test"}
           (parse-prefix "\u001b[1ATest")))
    (is (= {:kind "esc-seq-not-color" :prefix "\u001b[1H" :suffix "Test"}
           (parse-prefix "\u001b[1HTest")))
    (is (= {:kind "esc-seq-not-color" :prefix "\u001b[1f" :suffix "Test"}
           (parse-prefix "\u001b[1fTest")))
    (is (= {:kind "esc-seq-not-color" :prefix "\u001b[1K" :suffix "Test"}
           (parse-prefix "\u001b[1KTest"))))
  (testing "先頭がテキスト"
    (is (= {:kind "text" :prefix "ABC" :suffix ""}
           (parse-prefix "ABC")))
    (is (= {:kind "text" :prefix "ABC" :suffix "\u001b[31mRed"}
           (parse-prefix "ABC\u001b[31mRed")))
    (is (= {:kind "text" :prefix "x1b[31mRed" :suffix ""}
           (parse-prefix "x1b[31mRed")))))

(deftest parse-color-esc-test
  (testing "前景色のパース"
    (is (= [{:color-type :fg :color rgba-red}]
           (parse-color-esc "\u001b[31m")))
    (is (= [{:color-type :fg :color rgba-green}]
           (parse-color-esc "\u001b[32m")))
    (is (= [{:color-type :fg :color rgba-lightgray}]
           (parse-color-esc "\u001b[37m")))
    (is (= [{:color-type :fg :color rgba-darkgray}]
           (parse-color-esc "\u001b[90m")))
    (is (= [{:color-type :fg :color rgba-white}]
           (parse-color-esc "\u001b[97m"))))
  (testing "背景色のパース"
    (is (= [{:color-type :bg :color rgba-red}]
           (parse-color-esc "\u001b[41m")))
    (is (= [{:color-type :bg :color rgba-green}]
           (parse-color-esc "\u001b[42m")))
    (is (= [{:color-type :bg :color rgba-lightgray}]
           (parse-color-esc "\u001b[47m")))
    (is (= [{:color-type :bg :color rgba-darkgray}]
           (parse-color-esc "\u001b[100m")))
    (is (= [{:color-type :bg :color rgba-white}]
           (parse-color-esc "\u001b[107m"))))
  (testing "リセット"
    (is (= [{:color-type :reset :color nil}]
           (parse-color-esc "\u001b[0m")))
    (is (= [{:color-type :reset :color nil}]
           (parse-color-esc "\u001b[m"))))
  (testing "前景色、背景色、リセット混在"
    (is (= [{:color-type :fg :color rgba-red}
            {:color-type :bg :color rgba-green}
            {:color-type :reset :color nil}]
           (parse-color-esc "\u001b[31;42;0m"))))
  )