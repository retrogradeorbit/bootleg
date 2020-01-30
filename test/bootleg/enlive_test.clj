(ns bootleg.enlive-test
  (:require [clojure.test :refer :all]
            [bootleg.hiccup :refer :all]))

(deftest transform-tests
  (testing "transform content"
    (is (= (process-hiccup-data
            "test/files"
            "
(require '[net.cgrand.enlive-html :as html])

(-> [:div [:p] [:p#flag]]
    (html/at [:p#flag] (html/content* \"test\")))
")
           [:div [:p] [:p {:id "flag"} "test"]]))

    (is (= (process-hiccup-data
            "test/files"
            "
(require '[net.cgrand.enlive-html :as html])

(-> [:div [:p] [:p#flag]]
    (html/at [:p#flag] (html/content [:span \"test\"])))
")
           [:div [:p] [:p {:id "flag"} [:span "test"]]]))

    (is (= (process-hiccup-data
            "test/files"
            "
(require '[net.cgrand.enlive-html :as html])

(-> [:div [:p] [:p#flag]]
    (html/at [:p#flag] (html/content [:span \"test\"] [:p \"p\"])))
")
           [:div [:p] [:p {:id "flag"} [:span "test"] [:p "p"]]]))

    (is (= (process-hiccup-data
            "test/files"
            "
(require '[net.cgrand.enlive-html :as html])

(-> [:div [:p] [:p#flag]]
    (html/at [:p#flag] (html/content* \"<p>foo</p>\")))
")
           [:div [:p] [:p {:id "flag"} "&lt;p&gt;foo&lt;/p&gt;"]]))

    (is (= (process-hiccup-data
            "test/files"
            "
(require '[net.cgrand.enlive-html :as html])

(-> [:div [:p] [:p#flag]]
    (html/at [:p#flag] (html/content \"<p>foo</p>\")))
")
           [:div [:p] [:p {:id "flag"} [:p "foo"]]]))

    (is (= (process-hiccup-data
            "test/files"
            "
(require '[net.cgrand.enlive-html :as html])

(-> [:div [:p] [:p#flag]]
    (html/at [:p#flag] (html/content \"foo>bar\")))
")
           [:div [:p] [:p {:id "flag"} "foo&gt;bar"]]))

    (is (= (process-hiccup-data
            "test/files"
            "
(require '[net.cgrand.enlive-html :as html])

(-> [:div [:p] [:p#flag]]
    (html/at [:p#flag] (html/content {:type :element :tag :p :attrs nil :content [\"foo\"]})))
")
           [:div [:p] [:p {:id "flag"} [:p "foo"]]])))


  (testing "transform append"
    (is (= (process-hiccup-data
            "test/files"
            "
(require '[net.cgrand.enlive-html :as html])

(-> [:div [:p] [:p#flag \"foo\"]]
    (html/at [:p#flag] (html/append [:div \"one\"] \"<div>two</div>\" {:type :element :tag :div :attrs nil :content [\"three\"]})))
")
           [:div [:p] [:p {:id "flag"} "foo" [:div "one"] [:div "two"] [:div "three"]]])))

  (testing "transform prepend"
    (is (= (process-hiccup-data
            "test/files"
            "
(require '[net.cgrand.enlive-html :as html])

(-> [:div [:p] [:p#flag \"foo\"]]
    (html/at [:p#flag] (html/prepend [:div \"one\"] \"<div>two</div>\" {:type :element :tag :div :attrs nil :content [\"three\"]})))
")
           [:div [:p] [:p {:id "flag"} [:div "one"] [:div "two"] [:div "three"] "foo"]])))

  (testing "transform after"
    (is (= (process-hiccup-data
            "test/files"
            "
(require '[net.cgrand.enlive-html :as html])

(-> [:div [:p] [:p#flag \"foo\"]]
    (html/at [:p#flag] (html/after [:div \"one\"] \"<div>two</div>\" {:type :element :tag :div :attrs nil :content [\"three\"]})))
")
           [:div [:p] [:p {:id "flag"} "foo"] [:div "one"] [:div "two"] [:div "three"]])))

  (testing "transform before"
    (is (= (process-hiccup-data
            "test/files"
            "
(require '[net.cgrand.enlive-html :as html])

(-> [:div [:p] [:p#flag \"foo\"]]
    (html/at [:p#flag] (html/before [:div \"one\"] \"<div>two</div>\" {:type :element :tag :div :attrs nil :content [\"three\"]})))
")
           [:div [:p] [:div "one"] [:div "two"] [:div "three"] [:p {:id "flag"} "foo"]])))

  (testing "transform substitute"
    (is (= (process-hiccup-data
            "test/files"
            "
(require '[net.cgrand.enlive-html :as html])

(-> [:div [:p] [:p#flag \"foo\"]]
    (html/at [:p#flag] (html/substitute [:div \"one\"] \"<div>two</div>\" {:type :element :tag :div :attrs nil :content [\"three\"]})))
")
           [:div [:p] [:div "one"] [:div "two"] [:div "three"]])))

  (testing "transform reagent styles"
    (is (= (process-hiccup-data
            "test/files"
            "
(require '[net.cgrand.enlive-html :as html])

(-> [:div [:p {:style {:margin-top \"10px\"}}]]
    (html/at [:div] (html/set-attr :style \"margin-top:10px;\")))
")
           [:div {:style "margin-top:10px;"}
            [:p {:style "margin-top:10px;"}]])))

  (testing "transform set-attr reagent styles"
    (is (= (process-hiccup-data
            "test/files"
            "
(require '[net.cgrand.enlive-html :as html])

(-> [:div [:p {:style {:margin-top \"10px\"}}]]
    (html/at [:div] (html/set-attr :style {:margin-top \"10px\"})))
")
           [:div {:style "margin-top:10px;"}
            [:p {:style "margin-top:10px;"}]]))

    (is (= (process-hiccup-data
            "test/files"
            "
(require '[net.cgrand.enlive-html :as html])

(-> [:div {:style \"foo: bar;\"} [:p {:style {:margin-top \"10px\"}}]]
    (html/at [:div] (html/set-attr \"style\" {:margin-top \"10px\"})))
")
           [:div {:style "margin-top:10px;"}
            [:p {:style "margin-top:10px;"}]])))


  )
