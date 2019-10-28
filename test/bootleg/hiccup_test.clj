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
")))
  (testing "enlive"
    (is (= (process-hiccup-data "test/files"
                                "(-> [:div [:p] [:p#id]]
                                     (convert-to :hickory-seq)
                                     (net.cgrand.enlive-html/at
                                       [:p#id] (net.cgrand.enlive-html/content \"new content\"))
                                     (convert-to :hiccup))")
           [:div {} [:p {}] [:p {:id "id"} "new content"]]))
    (is (= (process-hiccup-data "test/files"
                                "(-> [:div [:p] [:p#id]]
                                     (net.cgrand.enlive-html/at
                                       [:p#id] (net.cgrand.enlive-html/content \"new content\")))")
           [:div {} [:p {}] [:p {:id "id"} "new content"]]))
    (is (= (process-hiccup-data "test/files"
                                "(-> [:div [:p] [:p#id]]
                                     (convert-to :html)
                                     (net.cgrand.enlive-html/at
                                       [:p#id] (net.cgrand.enlive-html/content \"new content\")))")
           "<div><p></p><p id=\"id\">new content</p></div>"))
    (is (= (process-hiccup-data "test/files"
                                "(-> [:div [:p] [:p#id]]
                                     (convert-to :hickory)
                                     (net.cgrand.enlive-html/at
                                       [:p#id] (net.cgrand.enlive-html/content \"new content\")))")
           {:type :element
            :attrs nil
            :tag :div
            :content [{:type :element
                       :attrs nil
                       :tag :p
                       :content []}
                      {:type :element
                       :attrs {:id "id"}
                       :tag :p
                       :content ["new content"]}]}))
    (is (= (process-hiccup-data "test/files"
                                "(-> [:div [:p] [:p#id]]
                                     (convert-to :hickory-seq)
                                     (net.cgrand.enlive-html/at
                                       [:p#id] (net.cgrand.enlive-html/content \"new content\")))")
           '({:type :element
               :attrs nil
               :tag :div
               :content [{:type :element
                          :attrs nil
                          :tag :p
                          :content []}
                         {:type :element
                          :attrs {:id "id"}
                          :tag :p
                          :content ["new content"]}]})))
    (is (= (process-hiccup-data
            "test/files"
            "
(require '[net.cgrand.enlive-html :as html])

(html/defsnippet main-snippet \"header.html\" [:header] [heading navigation-elements]
  [:h1] (html/content heading)
  [:ul [:li html/first-of-type]] (html/clone-for [[caption url] navigation-elements]
                                                 [:li :a] (html/content caption)
                                                 [:li :a] (html/set-attr :href url)))

(main-snippet \"heading\" [[\"caption 1\" \"url 1\"] [\"caption 2\" \"url 2\"]])

")
           '({:type :element
              :tag :header
              :attrs nil
              :content ["\n      "
                        {:type :element
                         :tag :h1
                         :attrs nil
                         :content ["heading"]}
                        "\n      "
                        {:type :element
                         :tag :ul
                         :attrs {:id "navigation"}
                         :content ["\n        "
                                   {:type :element
                                    :tag :li
                                    :attrs nil
                                    :content [{:type :element
                                               :tag :a
                                               :attrs {:href "url 1"}
                                               :content ["caption 1"]}]}
                                   {:type :element
                                    :tag :li
                                    :attrs nil
                                    :content [{:type :element
                                               :tag :a
                                               :attrs {:href "url 2"}
                                               :content ["caption 2"]}]}
                                   "\n      "]}
                        "\n    "]})))



    ))
