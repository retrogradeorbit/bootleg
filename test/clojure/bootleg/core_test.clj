(ns bootleg.core-test
  (:require [clojure.test :refer :all]
            [bootleg.core :refer :all]
            [bootleg.hiccup :refer :all]
            [bootleg.yaml :refer :all]
            [bootleg.json :refer :all]))

(deftest slurp-test
  (testing "test slurp returns string"
    (is (= (slurp "test/files/simple.md")
           (process-hiccup-data []
            "./"
            "(slurp \"test/files/simple.md\")")))
    (is (= (slurp "test/files/simple.md")
           (process-hiccup-data []
            "./test/files/"
            "(slurp \"simple.md\")")))
    (is (= (slurp "test/files/simple.md")
           (process-hiccup-data []
            "./test/files/"
            "(slurp \"https://raw.githubusercontent.com/retrogradeorbit/bootleg/master/test/files/simple.md\")")))))

(deftest yaml-test
  (testing "test yaml function"
    (is (= (yaml "
foo: bar
baz:
 - one
 - two
" :data)
           {:foo "bar"
            :baz ["one" "two"]}))))

(deftest json-test
  (testing "test json function"
    (is (= (json "
{
  \"foo\": \"bar\",
  \"baz\": [\"one\", \"two\"]
}
" :data)
           {:foo "bar"
            :baz ["one" "two"]}))))
