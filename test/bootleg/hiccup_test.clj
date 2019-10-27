(ns bootleg.hiccup-test
  (:require [clojure.test :refer :all]
            [bootleg.hiccup :refer :all]
            [flatland.ordered.map :refer [ordered-map]]))

(deftest evaluation
  (testing "hiccup"
    (is (= (process-hiccup-data "." "[:a {:href \"url\"} \"mylink\"]")
           [:a {:href "url"} "mylink"])))
  (testing "markdown"
    (is (= (process-hiccup-data "test/files" "(markdown \"simple.md\")")
           '([:h1 {} "Markdown support"]
             [:p {} "This is some simple markdown"])))
    (is (= (process-hiccup-data "test/files" "(markdown \"simple.md\" :hickory-seq)")
           '({:type :element
              :attrs nil
              :tag :h1
              :content ["Markdown support"]}
             {:type :element
              :attrs nil
              :tag :p
              :content ["This is some simple markdown"]})))
    (is (= (process-hiccup-data "test/files" "(markdown \"simple.md\" :html)")
           "<h1>Markdown support</h1><p>This is some simple markdown</p>"))
    (is (= (process-hiccup-data "." "(markdown \"# Markdown support\nThis is some simple markdown\n\" :data)")
           '([:h1 {} "Markdown support"]
             [:p {} "This is some simple markdown"]))))
  (testing "yaml"
    (is (= (process-hiccup-data "test/files" "(yaml \"simple.yml\")")
           (ordered-map :foo "bar"
                        :bar ["one"
                              "two"
                              (ordered-map :baz "bing"
                                           :boof "bong")
                              {:pow "This is some preserved\nnewline block text\n"}
                              {:wap "This is a multiline string\n"}]))))
  (testing "edn"
    (is (= (process-hiccup-data "test/files" "(edn \"simple.edn\")")
           {:foo "bar"
            :baz [1 2 3.4 "bing" :bosh]})))
  (testing "json"
    (is (= (process-hiccup-data "test/files" "(json \"simple.json\")")
           {:foo "bar"
            :baz [1 2 3.4 "bing"]})))
  (testing "mustache"
    (is (= (process-hiccup-data "test/files" "(mustache \"mustache.html\" {:title \"title\" :heading \"heading\" :body (markdown \"simple.md\" :html)} :html)")
           "<html>
  <head>
    <title>title</title>
  </head>
  <body>
    <h1>heading</h1>
    <h1>Markdown support</h1><p>This is some simple markdown</p>
  </body>
</html>
"))))
