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

(deftest classify-color-genre-test
  (testing "リセット"
    (is (= [{:genre :normal :code [0]}]
           (classify-color-genre [0])))
    (is (= [{:genre :normal :code [31]}
            {:genre :normal :code [47]}
            {:genre :normal :code [30]}]
           (classify-color-genre [31 47 30]))))
  (testing "前景色と背景色"
    (is (= [{:genre :normal :code [31]}]
           (classify-color-genre [31])))
    (is (= [{:genre :normal :code [31]}
            {:genre :normal :code [47]}
            {:genre :normal :code [30]}]
           (classify-color-genre [31 47 30]))))
  (testing "256色"
    (is (= [{:genre :ext-256 :code [38 5 0]}]
           (classify-color-genre [38 5 0])))
    (is (= [{:genre :ext-256 :code [38 5 0]}
            {:genre :ext-256 :code [48 5 255]}]
           (classify-color-genre [38 5 0 48 5 255])))
    (testing "RGB"
      (is (= [{:genre :ext-rgb :code [38 2 0 1 2]}]
             (classify-color-genre [38 2 0 1 2])))
      (is (= [{:genre :ext-rgb :code [38 2 0 1 2]}
              {:genre :ext-rgb :code [48 2 255 10 5]}]
             (classify-color-genre [38 2 0 1 2 48 2 255 10 5])))))
  (testing "混在"
    (is (= [{:genre :normal :code [31]}
            {:genre :ext-rgb :code [38 2 0 1 2]}
            {:genre :normal :code [45]}
            {:genre :ext-256 :code [38 5 222]}
            {:genre :ext-rgb :code [48 2 255 10 5]}
            {:genre :ext-256 :code [48 5 100]}]
           (classify-color-genre [31 38 2 0 1 2 45 38 5 222 48 2 255 10 5 48 5 100])))
    )
  (testing "空"
    (is (= []
           (classify-color-genre []))))
  (testing "存在しないコード"
    (is (= [{:genre nil :code [21]}
            {:genre nil :code [99]}]
           (classify-color-genre [21 99]))))
  )