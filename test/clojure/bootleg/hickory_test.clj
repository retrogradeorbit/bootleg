(ns bootleg.hickory-test
  (:require [clojure.test :refer :all]
            [bootleg.hiccup :refer :all]))

(deftest hickory
  (testing "hickory select tag"
    (is (= (process-hiccup-data
            "test/files"
            "
(require '[hickory.select :as s])

(def tree
  (convert-to '([:div [:span \"hello\"]]) :hickory))

(s/select (s/tag :span) tree)
")
           [{:type :element
             :attrs nil
             :tag :span
             :content ["hello"]}]))

    (is (= (process-hiccup-data
            "test/files"
            "
(require '[hickory.select :as s])

(def tree
  (convert-to '([:div [:span \"1\"] [:span \"2\" [:span \"sub\"]]]) :hickory))

(s/select (s/tag :span) tree)
")
           [{:type :element
             :attrs nil
             :tag :span
             :content ["1"]}
            {:type :element
             :attrs nil
             :tag :span
             :content ["2"
                       {:type :element
                        :attrs nil
                        :tag :span
                        :content ["sub"]}]}
            {:type :element
             :attrs nil
             :tag :span
             :content ["sub"]}])))

  (testing "hickory select node-type"
    (is (= (process-hiccup-data
            "test/files"
            "
(require '[hickory.select :as s])

(def tree
  (convert-to '([:div [:span \"hello\" \"<!-- wat -->\"]]) :hickory))

(s/select (s/node-type :comment) tree)
")
           [{:type :comment
             :content [" wat "]}])))

  (testing "hickory select attr"
    (is (= (process-hiccup-data
            "test/files"
            "
(require '[hickory.select :as s])

(def tree
  (convert-to '([:div {:foo \"baz\"} [:span {:foo \"barry\"} \"hello\"]]) :hickory))

(s/select (s/attr :foo #(.startsWith % \"bar\")) tree)
")
           [{:type :element
             :attrs {:foo "barry"}
             :tag :span
             :content ["hello"]}]))

    )
  )
