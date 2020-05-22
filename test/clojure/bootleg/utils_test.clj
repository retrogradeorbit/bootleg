(ns bootleg.utils-test
  (:require [clojure.test :refer :all]
            [bootleg.utils :refer :all]))

(def html-snippet "<p>1</p>")
(def html-snippet-multi "<p>1</p><p>2</p>")
(def html-snippet-multi-2 "<p>2</p>")
(def html-doc "<html><head></head><body><p>1</p><p>2</p></body></html>")

(def hiccup-seq-snippet '([:p "1"]))
(def hiccup-seq-snippet-multi '([:p "1"] [:p "2"]))
(def hiccup-seq-doc '([:html [:head] [:body [:p "1"] [:p "2"]]]))

(def hiccup-snippet (last hiccup-seq-snippet))
(def hiccup-snippet-multi (last hiccup-seq-snippet-multi))
(def hiccup-doc (last hiccup-seq-doc))

(def hickory-seq-snippet '({:type :element, :attrs nil, :tag :p, :content ["1"]}))
(def hickory-seq-snippet-multi '({:type :element, :attrs nil, :tag :p, :content ["1"]}
                                 {:type :element, :attrs nil, :tag :p, :content ["2"]}))
(def hickory-seq-doc
  '({:type :element, :attrs nil, :tag :html,
     :content [{:type :element, :attrs nil, :tag :head, :content nil}
               {:type :element, :attrs nil, :tag :body,
                :content [{:type :element, :attrs nil, :tag :p, :content ["1"]}
                          {:type :element, :attrs nil, :tag :p, :content ["2"]}]}]}))

(def hickory-snippet (last hickory-seq-snippet))
(def hickory-snippet-multi (last hickory-seq-snippet-multi))
(def hickory-doc (last hickory-seq-doc))

(deftest convert-html-test
  (testing "html->hiccup-seq"
    (is (= (html->hiccup-seq html-snippet) hiccup-seq-snippet))
    (is (= (html->hiccup-seq html-snippet-multi) hiccup-seq-snippet-multi))
    (is (= (html->hiccup-seq html-doc) hiccup-seq-doc)))
  (testing "html->hiccup"
    (is (= (html->hiccup html-snippet) hiccup-snippet))
    (is (= (html->hiccup html-snippet-multi) hiccup-snippet-multi))
    (is (= (html->hiccup html-doc) hiccup-doc)))
  (testing "html->hickory-seq"
    (is (= (html->hickory-seq html-snippet) hickory-seq-snippet))
    (is (= (html->hickory-seq html-snippet-multi) hickory-seq-snippet-multi))
    (is (= (html->hickory-seq html-doc) hickory-seq-doc)))
  (testing "html->hickory"
    (is (= (html->hickory html-snippet) hickory-snippet))
    (is (= (html->hickory html-snippet-multi) hickory-snippet-multi))
    (is (= (html->hickory html-doc) hickory-doc))))

(deftest convert-hiccup-seq-test
  (testing "hiccup-seq->html"
    (is (= (hiccup-seq->html hiccup-seq-snippet) html-snippet))
    (is (= (hiccup-seq->html hiccup-seq-snippet-multi) html-snippet-multi))
    (is (= (hiccup-seq->html hiccup-seq-doc) html-doc)))
  (testing "hiccup-seq->hickory-seq"
    (is (= (hiccup-seq->hickory-seq hiccup-seq-snippet) hickory-seq-snippet))
    (is (= (hiccup-seq->hickory-seq hiccup-seq-snippet-multi) hickory-seq-snippet-multi))
    (is (= (hiccup-seq->hickory-seq hiccup-seq-doc) hickory-seq-doc))))

(deftest convert-hickory-seq-test
  (testing "hickory-seq->html"
    (is (= (hickory-seq->html hickory-seq-snippet) html-snippet))
    (is (= (hickory-seq->html hickory-seq-snippet-multi) html-snippet-multi))
    (is (= (hickory-seq->html hickory-seq-doc) html-doc)))
  (testing "hickory-seq->hiccup-seq"
    (is (= (hickory-seq->hiccup-seq hickory-seq-snippet) hiccup-seq-snippet))
    (is (= (hickory-seq->hiccup-seq hickory-seq-snippet-multi) hiccup-seq-snippet-multi))
    (is (= (hickory-seq->hiccup-seq hickory-seq-doc) hiccup-seq-doc))))

(deftest convert-hiccup-test
  (testing "hiccup->html"
    (is (= (hiccup->html hiccup-snippet) html-snippet))
    (is (= (hiccup->html hiccup-snippet-multi) html-snippet-multi-2))
    (is (= (hiccup->html hiccup-doc) html-doc)))
  (testing "hiccup->hickory"
    (is (= (hiccup->hickory hiccup-snippet) hickory-snippet))
    (is (= (hiccup->hickory hiccup-snippet-multi) hickory-snippet-multi))
    (is (= (hiccup->hickory hiccup-doc) hickory-doc))))

(deftest convert-hickory-test
  (testing "hickory->html"
    (is (= (hickory->html hickory-snippet) html-snippet))
    (is (= (hickory->html hickory-snippet-multi) html-snippet-multi-2))
    (is (= (hickory->html hickory-doc) html-doc)))
  (testing "hickory->hiccup"
    (is (= (hickory->hiccup hickory-snippet) hiccup-snippet))
    (is (= (hickory->hiccup hickory-snippet-multi) hiccup-snippet-multi))
    (is (= (hickory->hiccup hickory-doc) hiccup-doc))))

(deftest convert-to-test
  (testing "convert-to itself"
    (is (= (convert-to html-snippet :html) html-snippet))
    (is (= (convert-to html-snippet-multi :html) html-snippet-multi))
    (is (= (convert-to html-doc :html) html-doc))
    (is (= (convert-to hickory-snippet :hickory) hickory-snippet))
    (is (= (convert-to hickory-snippet-multi :hickory) hickory-snippet-multi))
    (is (= (convert-to hickory-doc :hickory) hickory-doc))
    (is (= (convert-to hiccup-snippet :hiccup) hiccup-snippet))
    (is (= (convert-to hiccup-snippet-multi :hiccup) hiccup-snippet-multi))
    (is (= (convert-to hiccup-doc :hiccup) hiccup-doc))
    (is (= (convert-to hickory-seq-snippet :hickory-seq) hickory-seq-snippet))
    (is (= (convert-to hickory-seq-snippet-multi :hickory-seq) hickory-seq-snippet-multi))
    (is (= (convert-to hickory-seq-doc :hickory-seq) hickory-seq-doc))
    (is (= (convert-to hiccup-seq-snippet :hiccup-seq) hiccup-seq-snippet))
    (is (= (convert-to hiccup-seq-snippet-multi :hiccup-seq) hiccup-seq-snippet-multi))
    (is (= (convert-to hiccup-seq-doc :hiccup-seq) hiccup-seq-doc)))
  (testing "convert-to seq to seq"
    (is (= (convert-to html-snippet :hickory-seq) hickory-seq-snippet))
    (is (= (convert-to html-snippet-multi :hickory-seq) hickory-seq-snippet-multi))
    (is (= (convert-to html-doc :hickory-seq) hickory-seq-doc))
    (is (= (convert-to html-snippet :hiccup-seq) hiccup-seq-snippet))
    (is (= (convert-to html-snippet-multi :hiccup-seq) hiccup-seq-snippet-multi))
    (is (= (convert-to html-doc :hiccup-seq) hiccup-seq-doc))
    (is (= (convert-to hiccup-seq-snippet :hickory-seq) hickory-seq-snippet))
    (is (= (convert-to hiccup-seq-snippet-multi :hickory-seq) hickory-seq-snippet-multi))
    (is (= (convert-to hiccup-seq-doc :hickory-seq) hickory-seq-doc))
    (is (= (convert-to hickory-seq-snippet :hiccup-seq) hiccup-seq-snippet))
    (is (= (convert-to hickory-seq-snippet-multi :hiccup-seq) hiccup-seq-snippet-multi))
    (is (= (convert-to hickory-seq-doc :hiccup-seq) hiccup-seq-doc)))
  (testing "convert-to nonseq to nonseq"
    (is (= (convert-to html-snippet :hickory) hickory-snippet))
    (is (= (convert-to html-snippet-multi :hickory) hickory-snippet-multi))
    (is (= (convert-to html-doc :hickory) hickory-doc))
    (is (= (convert-to html-snippet :hiccup) hiccup-snippet))
    (is (= (convert-to html-snippet-multi :hiccup) hiccup-snippet-multi))
    (is (= (convert-to html-doc :hiccup) hiccup-doc))
    (is (= (convert-to hiccup-snippet :hickory) hickory-snippet))
    (is (= (convert-to hiccup-snippet-multi :hickory) hickory-snippet-multi))
    (is (= (convert-to hiccup-doc :hickory) hickory-doc))
    (is (= (convert-to hickory-snippet :hiccup) hiccup-snippet))
    (is (= (convert-to hickory-snippet-multi :hiccup) hiccup-snippet-multi))
    (is (= (convert-to hickory-doc :hiccup) hiccup-doc)))
  (testing "convert-to nonseq to seq"
    (is (= (convert-to hiccup-snippet :hickory-seq) (list hickory-snippet)))
    (is (= (convert-to hiccup-snippet-multi :hickory-seq) (list hickory-snippet-multi)))
    (is (= (convert-to hiccup-doc :hickory-seq) (list hickory-doc)))
    (is (= (convert-to hickory-snippet :hiccup-seq) (list hiccup-snippet)))
    (is (= (convert-to hickory-snippet-multi :hiccup-seq) (list hiccup-snippet-multi)))
    (is (= (convert-to hickory-doc :hiccup-seq) (list hiccup-doc))))
  (testing "convert-to seq to nonseq lossy"
    (is (= (convert-to html-snippet :hickory) hickory-snippet))
    (is (= (convert-to html-snippet-multi :hickory) hickory-snippet-multi))
    (is (= (convert-to html-doc :hickory) hickory-doc))
    (is (= (convert-to html-snippet :hiccup) hiccup-snippet))
    (is (= (convert-to html-snippet-multi :hiccup) hiccup-snippet-multi))
    (is (= (convert-to html-doc :hiccup) hiccup-doc))
    (is (= (convert-to hiccup-seq-snippet :hickory) hickory-snippet))
    (is (= (convert-to hiccup-seq-snippet-multi :hickory) hickory-snippet-multi))
    (is (= (convert-to hiccup-seq-doc :hickory) hickory-doc))
    (is (= (convert-to hickory-seq-snippet :hiccup) hiccup-snippet))
    (is (= (convert-to hickory-seq-snippet-multi :hiccup) hiccup-snippet-multi))
    (is (= (convert-to hickory-seq-doc :hiccup) hiccup-doc))))


(deftest test-issues
  (testing "issue #7"
    (let [html "<h1>heading</h1>\n"
          hiccup-seq '([:h1 "heading"] "\n")
          hickory-seq '({:type :element, :attrs nil, :tag :h1, :content ["heading"]}
                        "\n")
          hiccup (last hiccup-seq)
          hickory (last hickory-seq)
          html-lost "\n"
          ]
      ;; convert to same
      (is (= (convert-to html :html) html))
      (is (= (convert-to hiccup-seq :hiccup-seq) hiccup-seq))
      (is (= (convert-to hickory-seq :hickory-seq) hickory-seq))
      (is (= (convert-to hiccup :hiccup) hiccup))
      (is (= (convert-to hickory :hickory) hickory))

      ;; from html
      (is (= (convert-to html :hiccup) hiccup))
      (is (= (convert-to html :hickory) hickory))
      (is (= (convert-to html :hiccup-seq) hiccup-seq))
      (is (= (convert-to html :hickory-seq) hickory-seq))

      ;; seq to seq
      (is (= (convert-to hiccup-seq :html) html))
      (is (= (convert-to hiccup-seq :hickory-seq) hickory-seq))
      (is (= (convert-to hickory-seq :html) html))
      (is (= (convert-to hickory-seq :hiccup-seq) hiccup-seq))

      ;; seq to non-seq
      (is (= (convert-to hiccup-seq :hiccup) hiccup))
      (is (= (convert-to hiccup-seq :hickory) hickory))
      (is (= (convert-to hickory-seq :hiccup) hiccup))
      (is (= (convert-to hickory-seq :hickory) hickory))

      ;; non-seq to seq
      (is (= (convert-to hiccup :html) html-lost))
      (is (= (convert-to hiccup :hiccup-seq) (list hiccup)))
      (is (= (convert-to hiccup :hickory-seq) (list hickory)))
      (is (= (convert-to hickory :html) html-lost))
      (is (= (convert-to hickory :hiccup-seq) (list hiccup)))
      (is (= (convert-to hickory :hickory-seq) (list hickory)))))

  (testing "issue #2"
    (let [html
          "<!DOCTYPE html>
<html>
  <head>
    <meta charset=\"utf-8\">
    <meta content=\"width=device-width, initial-scale=1\">
    <title>ABC</title>
  </head>
  <body>
    <main>
      <h1>ABC</h1>
      <section id=\"content\"></section>
    </main>
  </body>
</html>"
          hiccup-seq '("<!DOCTYPE html>\n"
                       [:html "\n  "
                        [:head "\n    "
                         [:meta {:charset "utf-8"}] "\n    "
                         [:meta {:content "width=device-width, initial-scale=1"}] "\n    "
                         [:title "ABC"] "\n  "
                         ] "\n  "
                        [:body "\n    "
                         [:main "\n      "
                          [:h1 "ABC"] "\n      "
                          [:section {:id "content"}] "\n    "
                          ] "\n  "
                         ] "\n"])

          hickory-seq '("<!DOCTYPE html>\n" {:type :element, :attrs nil, :tag :html, :content ["\n  " {:type :element, :attrs nil, :tag :head, :content ["\n    " {:type :element, :attrs {:charset "utf-8"}, :tag :meta, :content nil} "\n    " {:type :element, :attrs {:content "width=device-width, initial-scale=1"}, :tag :meta, :content nil} "\n    " {:type :element, :attrs nil, :tag :title, :content ["ABC"]} "\n  "]} "\n  " {:type :element, :attrs nil, :tag :body, :content ["\n    " {:type :element, :attrs nil, :tag :main, :content ["\n      " {:type :element, :attrs nil, :tag :h1, :content ["ABC"]} "\n      " {:type :element, :attrs {:id "content"}, :tag :section, :content nil} "\n    "]} "\n  "]} "\n"]})

          hiccup (last hiccup-seq)
          hickory (last hickory-seq)
          html-lost "<html>
  <head>
    <meta charset=\"utf-8\">
    <meta content=\"width=device-width, initial-scale=1\">
    <title>ABC</title>
  </head>
  <body>
    <main>
      <h1>ABC</h1>
      <section id=\"content\"></section>
    </main>
  </body>
</html>"
          ]
      ;; convert to same
      (is (= (convert-to html :html) html))
      (is (= (convert-to hiccup-seq :hiccup-seq) hiccup-seq))
      (is (= (convert-to hickory-seq :hickory-seq) hickory-seq))
      (is (= (convert-to hiccup :hiccup) hiccup))
      (is (= (convert-to hickory :hickory) hickory))

      ;; from html
      (is (= (convert-to html :hiccup) hiccup))
      (is (= (convert-to html :hickory) hickory))
      (is (= (convert-to html :hiccup-seq) hiccup-seq))
      (is (= (convert-to html :hickory-seq) hickory-seq))

      ;; seq to seq
      ;; (println (convert-to hiccup-seq :html))
      ;; (println html)
      (is (= (convert-to hiccup-seq :html) html))
      (is (= (convert-to hiccup-seq :hickory-seq) hickory-seq))
      (is (= (convert-to hickory-seq :html) html))
      (is (= (convert-to hickory-seq :hiccup-seq) hiccup-seq))

      ;; seq to non-seq
      (is (= (convert-to hiccup-seq :hiccup) hiccup))
      (is (= (convert-to hiccup-seq :hickory) hickory))
      (is (= (convert-to hickory-seq :hiccup) hiccup))
      (is (= (convert-to hickory-seq :hickory) hickory))

      ;; non-seq to seq
      (is (= (convert-to hiccup :html) html-lost))
      (is (= (convert-to hiccup :hiccup-seq) (list hiccup)))
      (is (= (convert-to hiccup :hickory-seq) (list hickory)))
      (is (= (convert-to hickory :html) html-lost))
      (is (= (convert-to hickory :hiccup-seq) (list hiccup)))
      (is (= (convert-to hickory :hickory-seq) (list hickory)))))


  (testing "issue #2 without DOCTYPE but leading whitespace"
    (let [html
          "
<html>
  <head>
    <meta charset=\"utf-8\">
    <meta content=\"width=device-width, initial-scale=1\">
    <title>ABC</title>
  </head>
  <body>
    <main>
      <h1>ABC</h1>
      <section id=\"content\"></section>
    </main>
  </body>
</html>"
          hiccup-seq '("\n"
                       [:html "\n  "
                        [:head "\n    "
                         [:meta {:charset "utf-8"}] "\n    "
                         [:meta {:content "width=device-width, initial-scale=1"}] "\n    "
                         [:title "ABC"] "\n  "
                         ] "\n  "
                        [:body "\n    "
                         [:main "\n      "
                          [:h1 "ABC"] "\n      "
                          [:section {:id "content"}] "\n    "
                          ] "\n  "
                         ] "\n"])

          hickory-seq '("\n" {:type :element, :attrs nil, :tag :html, :content ["\n  " {:type :element, :attrs nil, :tag :head, :content ["\n    " {:type :element, :attrs {:charset "utf-8"}, :tag :meta, :content nil} "\n    " {:type :element, :attrs {:content "width=device-width, initial-scale=1"}, :tag :meta, :content nil} "\n    " {:type :element, :attrs nil, :tag :title, :content ["ABC"]} "\n  "]} "\n  " {:type :element, :attrs nil, :tag :body, :content ["\n    " {:type :element, :attrs nil, :tag :main, :content ["\n      " {:type :element, :attrs nil, :tag :h1, :content ["ABC"]} "\n      " {:type :element, :attrs {:id "content"}, :tag :section, :content nil} "\n    "]} "\n  "]} "\n"]})

          hiccup (last hiccup-seq)
          hickory (last hickory-seq)
          html-lost "<html>
  <head>
    <meta charset=\"utf-8\">
    <meta content=\"width=device-width, initial-scale=1\">
    <title>ABC</title>
  </head>
  <body>
    <main>
      <h1>ABC</h1>
      <section id=\"content\"></section>
    </main>
  </body>
</html>"
          ]
      ;; convert to same
      (is (= (convert-to html :html) html))
      (is (= (convert-to hiccup-seq :hiccup-seq) hiccup-seq))
      (is (= (convert-to hickory-seq :hickory-seq) hickory-seq))
      (is (= (convert-to hiccup :hiccup) hiccup))
      (is (= (convert-to hickory :hickory) hickory))

      ;; from html
      (is (= (convert-to html :hiccup) hiccup))
      (is (= (convert-to html :hickory) hickory))
      (is (= (convert-to html :hiccup-seq) hiccup-seq))
      (is (= (convert-to html :hickory-seq) hickory-seq))

      ;; seq to seq
      ;; (println (convert-to hiccup-seq :html))
      ;; (println html)
      (is (= (convert-to hiccup-seq :html) html))
      (is (= (convert-to hiccup-seq :hickory-seq) hickory-seq))
      (is (= (convert-to hickory-seq :html) html))
      (is (= (convert-to hickory-seq :hiccup-seq) hiccup-seq))

      ;; seq to non-seq
      (is (= (convert-to hiccup-seq :hiccup) hiccup))
      (is (= (convert-to hiccup-seq :hickory) hickory))
      (is (= (convert-to hickory-seq :hiccup) hiccup))
      (is (= (convert-to hickory-seq :hickory) hickory))

      ;; non-seq to seq
      (is (= (convert-to hiccup :html) html-lost))
      (is (= (convert-to hiccup :hiccup-seq) (list hiccup)))
      (is (= (convert-to hiccup :hickory-seq) (list hickory)))
      (is (= (convert-to hickory :html) html-lost))
      (is (= (convert-to hickory :hiccup-seq) (list hiccup)))
      (is (= (convert-to hickory :hickory-seq) (list hickory)))))

  (testing "issue #2 without DOCTYPE"
    (let [html
          "<html>
  <head>
    <meta charset=\"utf-8\">
    <meta content=\"width=device-width, initial-scale=1\">
    <title>ABC</title>
  </head>
  <body>
    <main>
      <h1>ABC</h1>
      <section id=\"content\"></section>
    </main>
  </body>
</html>"
          hiccup-seq '([:html "\n  "
                        [:head "\n    "
                         [:meta {:charset "utf-8"}] "\n    "
                         [:meta {:content "width=device-width, initial-scale=1"}] "\n    "
                         [:title "ABC"] "\n  "
                         ] "\n  "
                        [:body "\n    "
                         [:main "\n      "
                          [:h1 "ABC"] "\n      "
                          [:section {:id "content"}] "\n    "
                          ] "\n  "
                         ] "\n"])

          hickory-seq '({:type :element, :attrs nil, :tag :html, :content ["\n  " {:type :element, :attrs nil, :tag :head, :content ["\n    " {:type :element, :attrs {:charset "utf-8"}, :tag :meta, :content nil} "\n    " {:type :element, :attrs {:content "width=device-width, initial-scale=1"}, :tag :meta, :content nil} "\n    " {:type :element, :attrs nil, :tag :title, :content ["ABC"]} "\n  "]} "\n  " {:type :element, :attrs nil, :tag :body, :content ["\n    " {:type :element, :attrs nil, :tag :main, :content ["\n      " {:type :element, :attrs nil, :tag :h1, :content ["ABC"]} "\n      " {:type :element, :attrs {:id "content"}, :tag :section, :content nil} "\n    "]} "\n  "]} "\n"]})

          hiccup (last hiccup-seq)
          hickory (last hickory-seq)
          html-lost "<html>
  <head>
    <meta charset=\"utf-8\">
    <meta content=\"width=device-width, initial-scale=1\">
    <title>ABC</title>
  </head>
  <body>
    <main>
      <h1>ABC</h1>
      <section id=\"content\"></section>
    </main>
  </body>
</html>"
          ]
      ;; convert to same
      (is (= (convert-to html :html) html))
      (is (= (convert-to hiccup-seq :hiccup-seq) hiccup-seq))
      (is (= (convert-to hickory-seq :hickory-seq) hickory-seq))
      (is (= (convert-to hiccup :hiccup) hiccup))
      (is (= (convert-to hickory :hickory) hickory))

      ;; from html
      (is (= (convert-to html :hiccup) hiccup))
      (is (= (convert-to html :hickory) hickory))
      (is (= (convert-to html :hiccup-seq) hiccup-seq))
      (is (= (convert-to html :hickory-seq) hickory-seq))

      ;; seq to seq
      ;; (println (convert-to hiccup-seq :html))
      ;; (println html)
      (is (= (convert-to hiccup-seq :html) html))
      (is (= (convert-to hiccup-seq :hickory-seq) hickory-seq))
      (is (= (convert-to hickory-seq :html) html))
      (is (= (convert-to hickory-seq :hiccup-seq) hiccup-seq))

      ;; seq to non-seq
      (is (= (convert-to hiccup-seq :hiccup) hiccup))
      (is (= (convert-to hiccup-seq :hickory) hickory))
      (is (= (convert-to hickory-seq :hiccup) hiccup))
      (is (= (convert-to hickory-seq :hickory) hickory))

      ;; non-seq to seq
      (is (= (convert-to hiccup :html) html-lost))
      (is (= (convert-to hiccup :hiccup-seq) (list hiccup)))
      (is (= (convert-to hiccup :hickory-seq) (list hickory)))
      (is (= (convert-to hickory :html) html-lost))
      (is (= (convert-to hickory :hiccup-seq) (list hiccup)))
      (is (= (convert-to hickory :hickory-seq) (list hickory)))))

  (testing "dtd conversions"
    (is (= (convert-to {:type :dtd :data ["html" nil nil]} :html)
           "<!DOCTYPE html>"))
    (is (= (convert-to {:type :dtd :data ["html" nil nil]} :hiccup)
           "<!DOCTYPE html>"))
    (is (= (convert-to '({:type :dtd :data ["html" nil nil]}) :hiccup-seq)
           ["<!DOCTYPE html>"]))
    (is (= (markup-type "<!DOCTYPE html>") :html))
    (is (= (convert-to "<!DOCTYPE html>" :hiccup) "<!DOCTYPE html>"))
    (is (= (convert-to "<!DOCTYPE html>" :hickory) "<!DOCTYPE html>"))
    )

  (testing "text seq conversions"
    (is (= (convert-to ["text"] :html)
           "text"))
    (is (= (convert-to ["text"] :hiccup-seq)
           ["text"]))
    (is (= (convert-to ["text"] :hickory-seq)
           ["text"]))
    (is (= (convert-to ["text" "string"] :html)
           "textstring"))
    (is (= (convert-to ["text" "string"] :hiccup-seq)
           ["text" "string"]))
    (is (= (convert-to ["text" "string"] :hickory-seq)
           ["text" "string"])))

  (testing "issue #31 - header tag munged"
    (is (= (convert-to [:header [:div]] :html)
           "<header><div></div></header>"))
    (is (= (convert-to "<header><div></div></header>" :hiccup)
           [:header [:div]]))
    (is (= (convert-to "<html><header><div></div></header></html>" :hiccup)
           [:html [:header [:div]]]))))

(deftest new-test-issues
  (testing "issue #62 - Generating XML preserving the tag name case"
    (is (= (html-output-to #{:hiccup} "<?xml version=\"1.0\" encoding=\"UTF-8\"?><Foo>bar</Foo>")
           [:Foo "bar"]))
    )
  )
