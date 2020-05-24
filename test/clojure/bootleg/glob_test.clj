(ns bootleg.glob-test
  (:require [clojure.test :refer :all]
            [bootleg.hiccup :refer :all]))

(deftest glob-test
  (testing "glob tests"
    (is (= (process-hiccup-data
            []
            "test/files/glob"
            "(sort (glob \"*/*.yml\"))")
           '("1/test.yml" "1/test2.yml" "2/test.yml")))
    (is (= (process-hiccup
            []
            "test/files/glob"
            "glob.clj")
           '("1/test.yml" "1/test2.yml" "2/test.yml")))
    (is (= (process-hiccup
            []
            "test/files/glob/glob.clj")
           '("1/test.yml" "1/test2.yml" "2/test.yml")))))
