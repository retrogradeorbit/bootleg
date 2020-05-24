(ns bootleg.hickory-test
  (:require [clojure.test :refer :all]
            [bootleg.hiccup :refer :all]))

(deftest hickory
  (testing "hickory select tag"
    (is (= (process-hiccup-data []
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

    (is (= (process-hiccup-data []
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
    (is (= (process-hiccup-data []
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
    (is (= (process-hiccup-data []
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

  (testing "hickory select id"
    (is (= (process-hiccup-data []
            "test/files"
            "
(require '[hickory.select :as s])

(def tree
  (convert-to '([:div [:span#foo \"hello\"]]) :hickory))

(s/select (s/id :foo) tree)
")
           [{:type :element
             :attrs {:id "foo"}
             :tag :span
             :content ["hello"]}])))

  (testing "hickory select class"
    (is (= (process-hiccup-data []
            "test/files"
            "
(require '[hickory.select :as s])

(def tree
  (convert-to '([:div [:span.foo \"hello\"]]) :hickory))

(s/select (s/class :foo) tree)
")
           [{:type :element
             :attrs {:class "foo"}
             :tag :span
             :content ["hello"]}])))

  (testing "hickory select any"
    (is (= (process-hiccup-data []
            "test/files"
            "
(require '[hickory.select :as s])

(def tree
  (convert-to '([:div [:span.foo \"hello\"]]) :hickory))

(s/select s/any tree)
")
           [{:type :element
             :attrs nil
             :tag :div
             :content [{:type :element
                        :attrs {:class "foo"}
                        :tag :span
                        :content ["hello"]}]}
            {:type :element
             :attrs {:class "foo"}
             :tag :span
             :content ["hello"]}])))

  (testing "hickory select element"
    (is (= (process-hiccup-data []
            "test/files"
            "
(require '[hickory.select :as s])

(def tree
  (convert-to '([:div [:span.foo \"hello\"]]) :hickory))

(s/select s/element tree)
")
           [{:type :element
             :attrs nil
             :tag :div
             :content [{:type :element
                        :attrs {:class "foo"}
                        :tag :span
                        :content ["hello"]}]}
            {:type :element
             :attrs {:class "foo"}
             :tag :span
             :content ["hello"]}])))

  (testing "hickory select root"
    (is (= (process-hiccup-data []
            "test/files"
            "
(require '[hickory.select :as s])

(def tree
  (convert-to [:html [:body [:div [:span.foo \"hello\"]]]] :hickory))

(s/select s/root tree)
")
           [{:type :element
             :attrs nil
             :tag :html
             :content [{:type :element
                        :attrs nil
                        :tag :body
                        :content [{:type :element
                                   :attrs nil
                                   :tag :div
                                   :content [{:type :element
                                              :attrs {:class "foo"}
                                              :tag :span
                                              :content ["hello"]}]}]}]}])))

  (testing "hickory select n-moves-until"
    (is (= (process-hiccup-data []
            "test/files"
            "
(require '[hickory.select :as s]
         '[clojure.zip :as zip])

(def hicc
 '(\"<!DOCTYPE html>\n\"
   \"<!-- Comment 1 -->\"
   \"\n\"
   [:html
    \"\n\"
    [:head]
    \"\n\"
    [:body
     \"\n\"
     [:h1 \"Heading\"]
     \"\n\"
     [:p \"Paragraph\"]
     \"\n\"
     [:a {:href \"http://example.com\"} \"Link\"]
     \"\n\"
     [:div
      {:class \"aclass bclass cool\"}
      \"\n\"
      [:span
       {:disabled \"\",
        :anotherattr \"\",
        :thirdthing \"44\",
        :id \"attrspan\",
        :capitalized \"UPPERCASED\"}
       \"\n\"
       [:div {:class \"subdiv cool\", :id \"deepestdiv\"} \"Div\"]
       \"\n\"]
      \"\n\"
      \"<!-- Comment 2 -->\"
      \"\n\"
      [:span {:id \"anid\", :class \"cool\"} \"Span\"]
      \"\n\"]
     \"\n\"]
    \"\n\"]
   \"\n\")
)

(def tree
  (first (drop 3 (convert-to hicc :hickory-seq))))

(s/select (s/n-moves-until 0 6 zip/up nil?) tree)
")
           ["Div"])))

  (testing "hickory select nth-of-type"
    (is (= (process-hiccup-data []
            "test/files"
            "
(require '[hickory.select :as s])

(def tree
  (convert-to [:html [:body [:div [:span.foo \"hello\" [:div \"foo\"]]]]] :hickory))

(s/select (s/nth-of-type 1 :body) tree)
")
           [{:type :element
             :attrs nil
             :tag :body
             :content [{:type :element
                        :attrs nil
                        :tag :div
                        :content [{:type :element
                                   :attrs {:class "foo"}
                                   :tag :span
                                   :content ["hello" {:type :element
                                                      :attrs nil
                                                      :tag :div
                                                      :content ["foo"]}]}]}]}])))

  (testing "hickory select nth-last-of-type"
    (is (= (process-hiccup-data []
            "test/files"
            "
(require '[hickory.select :as s])

(def tree
  (convert-to [:html [:body [:div [:span.foo \"hello\" [:div \"one\"]]]
[:div \"two\"]
               ]] :hickory))

(s/select (s/nth-last-of-type 1 :div) tree)
")
           [{:type :element
             :attrs nil
             :tag :div
             :content ["one"]}
            {:type :element
             :attrs nil
             :tag :div
             :content ["two"]}]
           )))

  (testing "hickory select nth-child"
    (is (= (process-hiccup-data []
            "test/files"
            "
(require '[hickory.select :as s])

(def tree
  (convert-to [:html [:body [:div [:span.foo \"hello\" [:div \"one\"]]]
[:div \"two\"]
               ]] :hickory))

(s/select (s/nth-child 0 1) tree)
")
           [{:type :element
             :attrs nil
             :tag :body
             :content [{:type :element
                        :attrs nil
                        :tag :div
                        :content [{:type :element
                                   :attrs {:class "foo"}
                                   :tag :span
                                   :content ["hello" {:type :element
                                                      :attrs nil
                                                      :tag :div
                                                      :content ["one"]}]}]}
                       {:type :element
                        :attrs nil
                        :tag :div
                        :content ["two"]}]} {:type :element
             :attrs nil
             :tag :div
             :content [{:type :element
                        :attrs {:class "foo"}
                        :tag :span
                        :content ["hello" {:type :element
                                           :attrs nil
                                           :tag :div
                                           :content ["one"]}]}]}
            {:type :element
             :attrs {:class "foo"}
             :tag :span
             :content ["hello" {:type :element
                                :attrs nil
                                :tag :div
                                :content ["one"]}]}
            {:type :element
             :attrs nil
             :tag :div
             :content ["one"]}]
           )))

  (testing "hickory select nth-last-child"
    (is (= (process-hiccup-data []
            "test/files"
            "
(require '[hickory.select :as s])

(def tree
  (convert-to [:html [:body [:div [:span.foo \"hello\" [:div \"one\"]]]
[:div \"two\"]
               ]] :hickory))

(s/select (s/nth-last-child 0 1) tree)
")
           [{:type :element
             :attrs nil
             :tag :body
             :content [{:type :element
                        :attrs nil
                        :tag :div
                        :content [{:type :element
                                   :attrs {:class "foo"}
                                   :tag :span
                                   :content ["hello" {:type :element
                                                      :attrs nil
                                                      :tag :div
                                                      :content ["one"]}]}]}
                       {:type :element
                        :attrs nil
                        :tag :div
                        :content ["two"]}]}
            {:type :element
             :attrs {:class "foo"}
             :tag :span
             :content ["hello" {:type :element
                                :attrs nil
                                :tag :div
                                :content ["one"]}]}
            {:type :element
             :attrs nil
             :tag :div
             :content ["one"]}
            {:type :element
             :attrs nil
             :tag :div
             :content ["two"]}]
           )))

  (testing "hickory select first-child"
    (is (= (process-hiccup-data []
            "test/files"
            "
(require '[hickory.select :as s])

(def tree
  (convert-to [:html [:body [:div [:span.foo \"hello\" [:div \"one\"]]]
[:div \"two\"]
               ]] :hickory))

(s/select s/first-child tree)
")
           [{:type :element
             :attrs nil
             :tag :body
             :content [{:type :element
                        :attrs nil
                        :tag :div
                        :content [{:type :element
                                   :attrs {:class "foo"}
                                   :tag :span
                                   :content ["hello"
                                             {:type :element
                                              :attrs nil
                                              :tag :div
                                              :content ["one"]}]}]}
                       {:type :element
                        :attrs nil
                        :tag :div
                        :content ["two"]}]}
            {:type :element
             :attrs nil
             :tag :div
             :content [{:type :element
                        :attrs {:class "foo"}
                        :tag :span
                        :content ["hello"
                                  {:type :element
                                   :attrs nil
                                   :tag :div
                                   :content ["one"]}]}]}
            {:type :element
             :attrs {:class "foo"}
             :tag :span
             :content ["hello"
                       {:type :element
                        :attrs nil
                        :tag :div
                        :content ["one"]}]}
            {:type :element
             :attrs nil
             :tag :div
             :content ["one"]}]
           )))


  (testing "hickory select last-child"
    (is (= (process-hiccup-data []
            "test/files"
            "
(require '[hickory.select :as s])

(def tree
  (convert-to [:html [:body [:div [:span.foo \"hello\" [:div \"one\"]]]
[:div \"two\"]
               ]] :hickory))

(s/select s/last-child tree)
")
           [{:type :element
             :attrs nil
             :tag :body
             :content [{:type :element
                        :attrs nil
                        :tag :div
                        :content [{:type :element
                                   :attrs {:class "foo"}
                                   :tag :span
                                   :content ["hello"
                                             {:type :element
                                              :attrs nil
                                              :tag :div
                                              :content ["one"]}]}]}
                       {:type :element
                        :attrs nil
                        :tag :div
                        :content ["two"]}]}
            {:type :element
             :attrs {:class "foo"}
             :tag :span
             :content ["hello"
                       {:type :element
                        :attrs nil
                        :tag :div
                        :content ["one"]}]}
            {:type :element
             :attrs nil
             :tag :div
             :content ["one"]}
            {:type :element
             :attrs nil
             :tag :div
             :content ["two"]}]
           )))

  (testing "hickory select and"
    (is (= (process-hiccup-data []
            "test/files"
            "
(require '[hickory.select :as s])

(def tree
  (convert-to [:html [:body [:div [:span.foo \"hello\" [:div.foo \"one\"]]]
[:div \"two\"]
               ]] :hickory))

(s/select (s/and (s/tag :div) (s/class :foo)) tree)
")
           [{:type :element
             :attrs {:class "foo"}
             :tag :div
             :content ["one"]}])))

  (testing "hickory select or"
    (is (= (process-hiccup-data []
            "test/files"
            "
(require '[hickory.select :as s])

(def tree
  (convert-to [:html [:body [:div [:span.foo \"hello\" [:div.foo \"one\"]]]
[:div \"two\"]
               ]] :hickory))

(s/select (s/or (s/tag :div) (s/class :foo)) tree)
")
           [{:type :element
             :attrs nil
             :tag :div
             :content [{:type :element
                        :attrs {:class "foo"}
                        :tag :span
                        :content ["hello"
                                  {:type :element
                                   :attrs {:class "foo"}
                                   :tag :div
                                   :content ["one"]}]}]}
            {:type :element
             :attrs {:class "foo"}
             :tag :span
             :content ["hello"
                       {:type :element
                        :attrs {:class "foo"}
                        :tag :div
                        :content ["one"]}]}
            {:type :element
             :attrs {:class "foo"}
             :tag :div
             :content ["one"]}
            {:type :element
             :attrs nil
             :tag :div
             :content ["two"]}])))

  (testing "hickory select not"
    (is (= (process-hiccup-data []
            "test/files"
            "
(require '[hickory.select :as s])

(def tree
  (convert-to [:html [:body [:div [:span.foo \"hello\" [:div.foo \"one\"]]]
[:div \"two\"]
               ]] :hickory))

(s/select (s/and (s/not (s/tag :div)) (s/class :foo)) tree)
")
           [{:type :element
             :attrs {:class "foo"}
             :tag :span
             :content ["hello"
                       {:type :element
                        :attrs {:class "foo"}
                        :tag :div
                        :content ["one"]}]}])))

  (testing "hickory select el-not"
    (is (= (process-hiccup-data []
            "test/files"
            "
(require '[hickory.select :as s])

(def tree
  (convert-to [:html [:body [:div [:span.foo \"hello\" [:div.foo \"one\"]]]
[:div \"two\"]
               ]] :hickory))

(s/select (s/and (s/el-not (s/tag :div)) (s/class :foo)) tree)
")
           [{:type :element
             :attrs {:class "foo"}
             :tag :span
             :content ["hello"
                       {:type :element
                        :attrs {:class "foo"}
                        :tag :div
                        :content ["one"]}]}])))

  (testing "hickory select child"
    (is (= (process-hiccup-data []
            "test/files"
            "
(require '[hickory.select :as s])

(def tree
  (convert-to [:html [:body [:div [:span.foo \"hello\" [:div.foo \"one\"]]]
[:div \"two\"]
               ]] :hickory))

(s/select (s/child (s/el-not (s/tag :div)) (s/class :foo)) tree)
")
           [{:type :element
             :attrs {:class "foo"}
             :tag :div
             :content ["one"]}])))

  (testing "hickory select follow-adjacent"
    (is (= (process-hiccup-data []
            "test/files"
            "
(require '[hickory.select :as s])

(def tree
  (convert-to [:html [:body [:div [:span.foo \"hello\" [:div.bar \"one\"] [:div.foo \"two\"]]]
[:div \"three\"]
               ]] :hickory))

(s/select (s/follow-adjacent (s/tag :div) (s/class :foo)) tree)
")
           [{:type :element
             :attrs {:class "foo"}
             :tag :div
             :content ["two"]}])))

  (testing "hickory select precede-adjacent"
    (is (= (process-hiccup-data []
            "test/files"
            "
(require '[hickory.select :as s])

(def tree
  (convert-to [:html [:body [:div [:span.foo \"hello\" [:div.bar \"one\"] [:div.foo \"two\"]]]
[:div \"three\"]
               ]] :hickory))

(s/select (s/precede-adjacent (s/tag :div) (s/class :foo)) tree)
")
           [{:type :element
             :attrs {:class "bar"}
             :tag :div
             :content ["one"]}])))

  (testing "hickory select descendant"
    (is (= (process-hiccup-data []
            "test/files"
            "
(require '[hickory.select :as s])

(def tree
  (convert-to [:html [:body [:div [:span.foo \"hello\" [:div.bar \"one\"] [:div.foo \"two\"]]]
[:div \"three\"]
               ]] :hickory))

(s/select (s/descendant (s/tag :span)) tree)
")
           [{:type :element
             :attrs {:class "foo"}
             :tag :span
             :content ["hello"
                       {:type :element
                        :attrs {:class "bar"}
                        :tag :div
                        :content ["one"]}
                       {:type :element
                        :attrs {:class "foo"}
                        :tag :div
                        :content ["two"]}]}])))

  (testing "hickory select follow"
    (is (= (process-hiccup-data []
            "test/files"
            "
(require '[hickory.select :as s])

(def tree
  (convert-to [:html [:body [:div [:span.foo \"hello\" [:div.bar \"one\"] [:div.foo \"two\"]]]
[:div \"three\"]
               ]] :hickory))

(s/select (s/follow (s/class :bar) (s/class :foo)) tree)
")
           [{:type :element
             :attrs {:class "foo"}
             :tag :div
             :content ["two"]}])))

  (testing "hickory select precede"
    (is (= (process-hiccup-data []
            "test/files"
            "
(require '[hickory.select :as s])

(def tree
  (convert-to [:html [:body [:div [:span.foo \"hello\" [:div.bar \"one\"] [:div.foo \"two\"]]]
[:div \"three\"]
               ]] :hickory))

(s/select (s/precede (s/class :bar) (s/class :foo)) tree)
")
           [{:type :element
             :attrs {:class "bar"}
             :tag :div
             :content ["one"]}])))

  (testing "hickory select has-descendant"
    (is (= (process-hiccup-data []
            "test/files"
            "
(require '[hickory.select :as s])

(def tree
  (convert-to [:html [:body [:div [:span.foo \"hello\" [:div.bar \"one\"] [:div#inner \"two\"]]]
[:div \"three\"]
               ]] :hickory))

(s/select
  (s/and (s/tag :div)
         (s/has-descendant (s/id \"inner\")))
  tree)
")
           [{:type :element
             :attrs nil
             :tag :div
             :content [{:type :element
                        :attrs {:class "foo"}
                        :tag :span
                        :content ["hello"
                                  {:type :element
                                   :attrs {:class "bar"}
                                   :tag :div
                                   :content ["one"]}
                                  {:type :element
                                   :attrs {:id "inner"}
                                   :tag :div
                                   :content ["two"]}]}]}])))

  (testing "hickory select has-child"
    (is (= (process-hiccup-data []
            "test/files"
            "
(require '[hickory.select :as s])

(def tree
  (convert-to [:html [:body [:div [:span.foo \"hello\" [:div.bar \"one\"] [:div#inner \"two\"]]]
[:div \"three\"]
               ]] :hickory))

(s/select (s/has-child (s/id \"inner\"))
  tree)
")
           [{:type :element
             :attrs {:class "foo"}
             :tag :span
             :content ["hello"
                       {:type :element
                        :attrs {:class "bar"}
                        :tag :div
                        :content ["one"]}
                       {:type :element
                        :attrs {:id "inner"}
                        :tag :div
                        :content ["two"]}]}]))))
