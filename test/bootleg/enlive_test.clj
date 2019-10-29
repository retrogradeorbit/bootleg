(ns bootleg.enlive-test
  (:require [clojure.test :refer :all]
            [bootleg.enlive :refer :all]
            [bootleg.hiccup :refer :all]))

(deftest tranform-tests
  (testing "transform content*"
    (is (= (process-hiccup-data
            "test/files"
            "
(require '[net.cgrand.enlive-html :as html])

(-> [:div [:p] [:p#flag]]
    (html/at [:p#flag] (html/content* \"test\")))
")
           [:div {} [:p {}] [:p {:id "flag"} "test"]]))

    (is (= (process-hiccup-data
            "test/files"
            "
(require '[net.cgrand.enlive-html :as html])

(-> [:div [:p] [:p#flag]]
    (html/at [:p#flag] (html/content [:span \"test\"])))
")
           [:div {} [:p {}] [:p {:id "flag"} [:span {} "test"]]]))

    (is (= (process-hiccup-data
            "test/files"
            "
(require '[net.cgrand.enlive-html :as html])

(-> [:div [:p] [:p#flag]]
    (html/at [:p#flag] (html/content [:span \"test\"] [:p \"p\"])))
")
           [:div {} [:p {}] [:p {:id "flag"} [:span {} "test"] [:p {} "p"]]]))

    (is (= (process-hiccup-data
            "test/files"
            "
(require '[net.cgrand.enlive-html :as html])

(-> [:div [:p] [:p#flag]]
    (html/at [:p#flag] (html/content* \"<p>foo</p>\")))
")
           [:div {} [:p {}] [:p {:id "flag"} "&lt;p&gt;foo&lt;/p&gt;"]]))

    (is (= (process-hiccup-data
            "test/files"
            "
(require '[net.cgrand.enlive-html :as html])

(-> [:div [:p] [:p#flag]]
    (html/at [:p#flag] (html/content \"<p>foo</p>\")))
")
           [:div {} [:p {}] [:p {:id "flag"} [:p {} "foo"]]]))

    (is (= (process-hiccup-data
            "test/files"
            "
(require '[net.cgrand.enlive-html :as html])

(-> [:div [:p] [:p#flag]]
    (html/at [:p#flag] (html/content \"foo>bar\")))
")
           [:div {} [:p {}] [:p {:id "flag"} "foo&gt;bar"]]))

    (is (= (process-hiccup-data
            "test/files"
            "
(require '[net.cgrand.enlive-html :as html])

(-> [:div [:p] [:p#flag]]
    (html/at [:p#flag] (html/content {:type :element :tag :p :attrs nil :content [\"foo\"]})))
")
           [:div {} [:p {}] [:p {:id "flag"} [:p {} "foo"]]]))





    ))


#_
(defn at [node-or-nodes & rules]
  `(let [input-type# (or (bootleg.utils/markup-type ~node-or-nodes) :hickory)
         converted-nodes# (bootleg.utils/convert-to ~node-or-nodes :hickory-seq)]
     (-> converted-nodes# net.cgrand.enlive-html/as-nodes
         ~@(for [[s t] (partition 2 rules)]
             (if (= :lockstep s)
               `(net.cgrand.enlive-html/lockstep-transform
                 ~(into {} (for [[s t] t]
                             [(if (#'net.cgrand.enlive-html/static-selector? s) (net.cgrand.enlive-html/cacheable s) s) t])))
               `(net.cgrand.enlive-html/transform ~(if (#'net.cgrand.enlive-html/static-selector? s) (net.cgrand.enlive-html/cacheable s) s) ~t)))
         bootleg.utils/hickory-seq-add-missing-types
         (bootleg.utils/convert-to input-type#))))
