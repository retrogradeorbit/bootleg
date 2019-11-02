(ns bootleg.selmer-test
  (:require [clojure.test :refer :all]
            [bootleg.hiccup :refer :all]))

(deftest selmer-tests
  (testing "selmer tests"
    (is (= (process-hiccup-data
            "test/files"
            "(selmer.parser/render \"Hello {{name}}!\" {:name \"world\"})")
           "Hello world!"))

    (is (= (process-hiccup-data
            "test/files"
            "(selmer.parser/render \"Hello {{name|capitalize}}!\" {:name \"world\"})")
           "Hello World!"))

    (is (= (process-hiccup-data
            "test/files"
            "
(selmer.parser/set-resource-path! \"/home/crispin/dev/epiccastle/bootleg/test/files/\")
(selmer.parser/render-file \"test.selmer\" {:nums [1 2 3]})")
           "<div>\n<p>1</p><p>2</p><p>3</p>\n</div>"))

    (is (= (process-hiccup-data
            "test/files"
            "(selmer \"test.selmer\" {:nums [1 2 3]})")
           '([:div {} "\n" [:p {} "1"] [:p {} "2"] [:p {} "3"] "\n"])))
    (is (= (process-hiccup-data
            "test/files"
            "(selmer \"test.selmer\" {:nums [1 2 3]} :html)")
           "<div>\n<p>1</p><p>2</p><p>3</p>\n</div>"))
    (is (= (process-hiccup-data
            "test/files"
            "(selmer \"test.selmer\" {:nums [1 2 3]} :hickory-seq)")
           '({:type :element
              :tag :div
              :attrs nil
              :content ["\n"
                        {:type :element
                         :tag :p
                         :attrs nil
                         :content ["1"]}
                        {:type :element
                         :tag :p
                         :attrs nil
                         :content ["2"]}
                        {:type :element
                         :tag :p
                         :attrs nil
                         :content ["3"]}
                        "\n"
                        ]})))

    )
  )
