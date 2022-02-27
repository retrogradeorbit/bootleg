(ns bootleg.hiccup-test
  (:require [clojure.test :refer :all]
            [bootleg.hiccup :refer :all]
            [flatland.ordered.map :refer [ordered-map]]))

(deftest evaluation
  (testing "hiccup"
    (is (= (process-hiccup-data [] "." "[:a {:href \"url\"} \"mylink\"]")
           [:a {:href "url"} "mylink"])))
  (testing "markdown"
    (is (= (process-hiccup-data [] "test/files" "(markdown \"simple.md\")")
           '([:h1 "Markdown support"]
             [:p "This is some simple markdown"])))
    (is (= (process-hiccup-data [] "test/files" "(markdown \"complex.md\" :footnotes? :parse-meta?)")
           '([:h1 "Markdown support"]
             [:p "This is some more complex" [:a {:href "#fn-1", :id "fnref1"} [:sup "1"]] " markdown."]
             [:p "The footnote will appear at the end. "]
             [:ol {:class "footnotes"} [:li {:id "fn-1"} "Testing optional footnote feature." [:a {:href "#fnref1"} "↩"]]] [:p])
           ))
    (is (= (process-hiccup-data [] "test/files" "(markdown \"simple.md\" :hickory-seq)")
           '({:type :element
              :attrs nil
              :tag :h1
              :content ["Markdown support"]}
             {:type :element
              :attrs nil
              :tag :p
              :content ["This is some simple markdown"]})))
    (is (= (process-hiccup-data [] "test/files" "(markdown \"simple.md\" :html)")
           "<h1>Markdown support</h1><p>This is some simple markdown</p>"))
    (is (= (process-hiccup-data [] "." "(markdown \"# Markdown support\nThis is some simple markdown\n\" :data)")
           '([:h1 "Markdown support"]
             [:p "This is some simple markdown"]))))
  (testing "yaml"
    (is (= (process-hiccup-data [] "test/files" "(yaml \"simple.yml\")")
           (ordered-map :foo "bar"
                        :bar ["one"
                              "two"
                              (ordered-map :baz "bing"
                                           :boof "bong")
                              {:pow "This is some preserved\nnewline block text\n"}
                              {:wap "This is a multiline string\n"}]))))
  (testing "edn"
    (is (= (process-hiccup-data [] "test/files" "(edn \"simple.edn\")")
           {:foo "bar"
            :baz [1 2 3.4 "bing" :bosh]})))
  (testing "json"
    (is (= (process-hiccup-data [] "test/files" "(json \"simple.json\")")
           {:foo "bar"
            :baz [1 2 3.4 "bing"]})))
  (testing "mustache"
    (is (= (process-hiccup-data [] "test/files" "(mustache \"mustache.html\" {:title \"title\" :heading \"heading\" :body (markdown \"simple.md\" :html)} :html)")
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
    (is (= (process-hiccup-data [] "test/files"
                                "(-> [:div [:p] [:p#id]]
                                     (convert-to :hickory-seq)
                                     (net.cgrand.enlive-html/at
                                       [:p#id] (net.cgrand.enlive-html/content \"new content\"))
                                     (convert-to :hiccup))")
           [:div [:p] [:p {:id "id"} "new content"]]))
    (is (= (process-hiccup-data [] "test/files"
                                "(-> [:div [:p] [:p#id]]
                                     (net.cgrand.enlive-html/at
                                       [:p#id] (net.cgrand.enlive-html/content \"new content\")))")
           [:div [:p] [:p {:id "id"} "new content"]]))
    (is (= (process-hiccup-data [] "test/files"
                                "(-> [:div [:p] [:p#id]]
                                     (convert-to :html)
                                     (net.cgrand.enlive-html/at
                                       [:p#id] (net.cgrand.enlive-html/content \"new content\")))")
           "<div><p></p><p id=\"id\">new content</p></div>"))
    (is (= (process-hiccup-data [] "test/files"
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
    (is (= (process-hiccup-data [] "test/files"
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
    (is (= (process-hiccup-data []
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

    (is (= (process-hiccup-data []
            "test/files"
            "
(require '[net.cgrand.enlive-html :as html])

(html/deftemplate main-template \"application.html\" [title body]
  [:head :title] (html/content title)
  [:body] (html/content body)
)

(html/defsnippet main-snippet \"header.html\" [:header] [heading navigation-elements]
  [:h1] (html/content heading)
  [:ul [:li html/first-of-type]] (html/clone-for [[caption url] navigation-elements]
                                                 [:li :a] (html/content caption)
                                                 [:li :a] (html/set-attr :href url)))

(-> (main-template \"new title\" (main-snippet \"heading\" [[\"caption 1\" \"url 1\"] [\"caption 2\" \"url 2\"]]))
    (convert-to :hickory-seq))
")

           '("<!DOCTYPE html>"
             {:type :element
              :tag :html
              :attrs {:lang "en"}
              :content ["\n  "
                        {:type :element
                         :tag :head
                         :attrs nil
                         :content ["\n    "
                                   {:type :element
                                    :tag :title
                                    :attrs nil
                                    :content ["new title"]}
                                   "\n  "]}
                        "\n  "
                        {:type :element
                         :tag :body
                         :attrs nil
                         :content ({:type :element
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
                                              "\n    "]})}
                        "\n\n"]})))

    (is (= (process-hiccup-data []
            "test/files"
            "
(require '[net.cgrand.enlive-html :as html])

(html/deftemplate main-template \"application.html\" [title body]
  [:head :title] (html/content title)
  [:body] (html/content body)
)

(html/defsnippet main-snippet \"header.html\" [:header] [heading navigation-elements]
  [:h1] (html/content heading)
  [:ul [:li html/first-of-type]] (html/clone-for [[caption url] navigation-elements]
                                                 [:li :a] (html/content caption)
                                                 [:li :a] (html/set-attr :href url)))

(main-template \"new title\" (main-snippet \"heading\" [[\"caption 1\" \"url 1\"] [\"caption 2\" \"url 2\"]]))
")

           '("<!DOCTYPE html>"
             [:html {:lang "en"} "\n  "
              [:head "\n    "
               [:title "new title"] "\n  "] "\n  "
              [:body
               [:header "\n      "
                [:h1 "heading"] "\n      "
                [:ul {:id "navigation"} "\n        "
                 [:li
                  [:a {:href "url 1"} "caption 1"]]
                 [:li
                  [:a {:href "url 2"} "caption 2"]] "\n      "] "\n    "]] "\n\n"])))

    (is (= (process-hiccup-data []
            "test/files"
            "
(require '[net.cgrand.enlive-html :as html])

(html/deftemplate main-template \"application.html\" [title body]
  [:head :title] (html/content title)
  [:body] (html/content body)
)

(html/defsnippet main-snippet \"header.html\" [:header] [heading navigation-elements]
  [:h1] (html/content heading)
  [:ul [:li html/first-of-type]] (html/clone-for [[caption url] navigation-elements]
                                                 [:li :a] (html/content caption)
                                                 [:li :a] (html/set-attr :href url)))

(-> (main-template \"new title\" (main-snippet \"heading\" [[\"caption 1\" \"url 1\"] [\"caption 2\" \"url 2\"]]))
    (convert-to :html))
")

           "<!DOCTYPE html><html lang=\"en\">
  <head>
    <title>new title</title>
  </head>
  <body><header>
      <h1>heading</h1>
      <ul id=\"navigation\">
        <li><a href=\"url 1\">caption 1</a></li><li><a href=\"url 2\">caption 2</a></li>
      </ul>
    </header></body>

</html>"))

    (testing "issue #31 - header tag munged"
      (is (= (process-hiccup-data []
              "test/files"
              "(mustache \"header.html\" {})")
             '("<!DOCTYPE html>\n"
               [:html {:lang "en"} "\n  "
                [:body "\n    "
                 [:header "\n      "
                  [:h1 "Header placeholder"] "\n      "
                  [:ul {:id "navigation"} "\n        "
                   [:li
                    [:a {:href "#"} "Placeholder for navigation"]]
                   "\n      "]
                  "\n    "]
                 "\n  "]
                "\n"]
               "\n"))))))

(deftest hiccup-test
  (testing "hiccup style map"
    (is (= (process-hiccup-data [] "." "(convert-to [:div [:p {:style {:color \"red\" :margin-top \"20px\"}} \"one\"]] :html)")
           "<div><p style=\"color:red;margin-top:20px;\">one</p></div>")))
  (testing "hiccup nil forms"
    (is (= (process-hiccup-data [] "." "(convert-to [:div nil [:p \"one\"] nil [:p nil \"two\" nil] nil] :html)")
           "<div><p>one</p><p>two</p></div>")))
  (testing "empty forms"
    (is (= (process-hiccup-data [] "." "(convert-to [:div []] :html)")
           "<div></div>")))
  (testing "empty forms 2"
    (is (= (process-hiccup-data [] "." "(convert-to [[]] :html)")
           "")))
  (testing "empty forms 3"
    (is (= (process-hiccup-data [] "." "(convert-to [[nil nil] [[[[[[nil [nil [nil []]]]]]]] nil] []] :html)")
           "")))
  (testing "empty forms 4"
    (is (= (process-hiccup-data [] "." "(convert-to [:img {:src \"/images/logo.png\", :alt \"\"}] :html)")
           "<img alt=\"\" src=\"/images/logo.png\">"))))

(deftest hiccup-to-xml-test
  (testing "hiccup to xml"
    (is (= (process-hiccup-data [] "." "(convert-to [:link (for [n (range 5)] [:div n])] :xml)")
           "<?xml version=\"1.0\" encoding=\"UTF-8\"?><link><div>0</div><div>1</div><div>2</div><div>3</div><div>4</div></link>"))))

(deftest selmer-to-hiccup-test
  (testing "issue #62 - Generating XML preserving the tag name case"
    (is (= (process-hiccup-data [] "." "(selmer \"<?xml version=\\\"1.0\\\" encoding=\\\"UTF-8\\\"?><Foo>{{foo}}</Foo>\" {:foo \"bar\"} :data :hiccup)")
           [:Foo "bar"]))
    (is (= (process-hiccup-data [] "." "(selmer \"<?xml version=\\\"1.0\\\" encoding=\\\"UTF-8\\\"?><Foo>{{foo}}</Foo>\" {:foo \"bar\"} :data :hiccup-seq)")
           [[:Foo "bar"]]))

    (is (= (process-hiccup-data [] "." "(-> (selmer \"<?xml version=\\\"1.0\\\" encoding=\\\"UTF-8\\\"?><Foo>{{foo}}</Foo>\" {:foo \"bar\"} :data :hiccup) (convert-to :xml))")
           "<?xml version=\"1.0\" encoding=\"UTF-8\"?><Foo>bar</Foo>"))
    (is (= (process-hiccup-data [] "." "(-> (selmer \"<?xml version=\\\"1.0\\\" encoding=\\\"UTF-8\\\"?><Foo>{{foo}}</Foo>\" {:foo \"bar\"} :data :hiccup-seq) (convert-to :xml))")
           "<?xml version=\"1.0\" encoding=\"UTF-8\"?><Foo>bar</Foo>"))
    ))
