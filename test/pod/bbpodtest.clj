(ns testbootlegpod
  (:require [babashka.pods :as pods]))

(if *command-line-args*
  (pods/load-pod *command-line-args*)
  (pods/load-pod ["lein" "run"]))

(require '[pod.retrogradeorbit.bootleg.markdown :as markdown]
         '[pod.retrogradeorbit.bootleg.mustache :as mustache]
         '[pod.retrogradeorbit.bootleg.yaml :as yaml]
         '[pod.retrogradeorbit.bootleg.utils :as utils]
         '[pod.retrogradeorbit.bootleg.selmer :as selmer]
         '[pod.retrogradeorbit.bootleg.glob :as glob]
         '[pod.retrogradeorbit.bootleg.json :as json]
         '[pod.retrogradeorbit.bootleg.html :as html]
         '[pod.retrogradeorbit.bootleg.enlive :as enlive]
         '[pod.retrogradeorbit.net.cgrand.enlive-html :as enlive-html]
         )

;; (assert
;;  (=
;;   (markdown/markdown "test/files/simple.md")
;;   '([:h1 "Markdown support"] [:p "This is some simple markdown"])))

;; (assert
;;  (=
;;   (yaml/yaml* "examples/quickstart/fields.yml")
;;   {:title "Bootleg", :author "Crispin", :body "I'm going to rewrite all my sites with this!"}))

;; (assert
;;  (=
;;   (mustache/mustache "examples/quickstart/quickstart.html"
;;                      (assoc (yaml/yaml* "examples/quickstart/fields.yml")
;;                             :body (markdown/markdown "examples/quickstart/simple.md" :html)))
;;   '([:h1 "Bootleg"] "\n"
;;     [:h2 "by Crispin"] "\n"
;;     [:div [:h1 "Markdown support"]
;;      [:p "This is some simple markdown"]]
;;     "\n")))

;; (assert
;;  (=
;;   (-> (mustache/mustache "examples/quickstart/quickstart.html"
;;                          (assoc (yaml/yaml* "examples/quickstart/fields.yml")
;;                                 :body (markdown/markdown "examples/quickstart/simple.md" :html)))
;;       (utils/convert-to :hickory-seq))
;;   '({:type :element
;;      :attrs nil
;;      :tag :h1
;;      :content ["Bootleg"]} "\n"
;;     {:type :element
;;      :attrs nil
;;      :tag :h2
;;      :content ["by Crispin"]} "\n"
;;     {:type :element
;;      :attrs nil
;;      :tag :div
;;      :content [{:type :element
;;                 :attrs nil
;;                 :tag :h1
;;                 :content ["Markdown support"]}
;;                {:type :element
;;                 :attrs nil
;;                 :tag :p
;;                 :content ["This is some simple markdown"]}]} "\n")))

;; (assert
;;  (=
;;   (utils/convert-to [:Link "foo"] :xml)
;;   "<?xml version=\"1.0\" encoding=\"UTF-8\"?><Link>foo</Link>"))

;; ;;
;; ;; selmer
;; ;;
;; (assert
;;  (=
;;   (selmer/selmer "<p>Hello {{name|capitalize}}!</p>" {:name "world"} :data)
;;   '([:p "Hello World!"])
;;   ))

;; (assert
;;  (=
;;   (pod.retrogradeorbit.selmer.parser/render "Hello {{name}}!" {:name "world"})
;;   "Hello world!"))

;; (assert
;;  (=
;;   (pod.retrogradeorbit.selmer.parser/render "Hello {{name|capitalize}}!" {:name "world"})
;;   "Hello World!"))

;; #_
;; (assert
;;  (=
;;   (do
;;     (pod.retrogradeorbit.selmer.parser/set-resource-path! "./test/files/")
;;     (pod.retrogradeorbit.selmer.parser/render-file "test.selmer" {:nums [1 2 3]}))
;;   "<div>\n<p>1</p><p>2</p><p>3</p>\n</div>"))

;; (assert
;;  (=
;;   (selmer/selmer "test/files/test.selmer" {:nums [1 2 3]})
;;   '([:div "\n" [:p "1"] [:p "2"] [:p "3"] "\n"])))

;; (assert
;;  (=
;;   (selmer/selmer "test/files/test.selmer" {:nums [1 2 3]} :html)
;;   "<div>\n<p>1</p><p>2</p><p>3</p>\n</div>"))

;; (assert
;;  (=
;;   (selmer/selmer "test/files/test.selmer" {:nums [1 2 3]} :hickory-seq)
;;   '({:type :element
;;      :tag :div
;;      :attrs nil
;;      :content ["\n"
;;                {:type :element
;;                 :tag :p
;;                 :attrs nil
;;                 :content ["1"]}
;;                {:type :element
;;                 :tag :p
;;                 :attrs nil
;;                 :content ["2"]}
;;                {:type :element
;;                 :tag :p
;;                 :attrs nil
;;                 :content ["3"]}
;;                "\n"
;;                ]})))

;; (assert
;;  (=
;;   (selmer/selmer "test/files/selmer/child-a.html" {})
;;   '([:html
;;      "\n"
;;      [:body
;;       "\n\n"
;;       [:h1 "child-a header"]
;;       "\n\n\n\n\n\n\n"
;;       [:p "footer"]
;;       "\n\n"]
;;      "\n"]
;;     "\n")))

;; (assert
;;  (=
;;   (selmer/selmer "test/files/selmer/child-b.html" {})
;;   '([:html
;;      "\n"
;;      [:body
;;       "\n\n\n"
;;       [:h1 "child-a header"]
;;       "\n\n"
;;       [:h1 "child-b header"]
;;       "\n\n\n\nSome content\n\n\n\n"
;;       [:p "footer"]
;;       "\n\n"]
;;      "\n"]
;;     "\n")))

;; (assert
;;  (=
;;   (selmer/selmer "test/files/selmer/child-b.html" {})
;;   '([:html
;;      "\n"
;;      [:body
;;       "\n\n\n"
;;       [:h1 "child-a header"]
;;       "\n\n"
;;       [:h1 "child-b header"]
;;       "\n\n\n\nSome content\n\n\n\n"
;;       [:p "footer"]
;;       "\n\n"]
;;      "\n"]
;;     "\n")))

;; ;;
;; ;; glob
;; ;;
;; (assert
;;  (=
;;   (into #{} (glob/glob "**/*.y?l"))
;;   #{".github/workflows/clojure.yml"
;;     "test/files/glob/1/test.yml"
;;     ".circleci/config.yml"
;;     "test/files/simple.yml"
;;     "test/files/glob/2/test.yml"
;;     "test/files/glob/1/test2.yml"
;;     "examples/quickstart/fields.yml"}))

;; (assert
;;  (=
;;   (json/json "{\"a\": 1, \"b\": [\"a\",\"b\"]}" :data)
;;   {:a 1, :b ["a" "b"]}))

;; (assert
;;  (=
;;   (html/html "
;; <div>
;;  <h1 id=\"head\" class=\"heading foo\">Heading</h1>
;;  <p>paragraph</p>
;; </div>
;; <div>
;;  <a href=\"url\">
;;   <img src=\"url\"/>
;;  </a>
;; </div>" :data)
;;   '("\n"
;;     [:div "\n "
;;      [:h1 {:id "head", :class "heading foo"} "Heading"] "\n "
;;      [:p "paragraph"] "\n"] "\n"
;;     [:div "\n "
;;      [:a {:href "url"} "\n  "
;;       [:img {:src "url"}] "\n "]
;;      "\n"])))

;; (assert
;;  (=
;;   (-> (markdown/markdown "examples/quickstart/simple.md")
;;       (enlive/at [:p]
;;                  (enlive/content "foo")))
;;   '([:h1 "Markdown support"] [:p "foo"])))

;; (assert
;;  (=
;;   (-> (markdown/markdown "examples/quickstart/simple.md")
;;       (enlive/at [:p] (enlive/set-attr :style "color:green;")))
;;   '([:h1 "Markdown support"] [:p {:style "color:green;"} "This is some simple markdown"])
;;   ))

;; (assert
;;  (=
;;   (-> [:div [:p] [:p#flag]]
;;       (enlive-html/at [:p#flag] (enlive-html/content* "test")))
;;   [:div [:p] [:p {:id "flag"} "test"]]))

;; (assert
;;  (=
;;   (-> [:div [:p] [:p#flag]]
;;       (enlive-html/at [:p#flag] (enlive-html/content [:span "test"])))
;;   [:div [:p] [:p {:id "flag"} [:span "test"]]]
;;   ))

;; (assert
;;  (=
;;   (-> [:div [:p] [:p#flag]]
;;       (enlive-html/at [:p#flag] (enlive-html/content [:span  "test"] [:p "p"])))
;;   [:div [:p] [:p {:id "flag"} [:span "test"] [:p "p"]]]
;;   ))

;; (assert
;;  (=
;;   (-> [:div [:p] [:p#flag]]
;;       (enlive-html/at [:p#flag] (enlive-html/content* "<p>foo</p>")))
;;   [:div [:p] [:p {:id "flag"} "&lt;p&gt;foo&lt;/p&gt;"]]
;;   ))

;; (assert
;;  (=
;;   (-> [:div [:p] [:p#flag]]
;;       (enlive-html/at [:p#flag] (enlive-html/content "<p>foo</p>")))
;;   [:div [:p] [:p {:id "flag"} [:p "foo"]]]
;;   ))

;; (assert
;;  (=
;;   (-> [:div [:p] [:p#flag]]
;;       (enlive-html/at [:p#flag] (enlive-html/content "foo>bar")))
;;   [:div [:p] [:p {:id "flag"} "foo&gt;bar"]]
;;   ))

;; (assert
;;  (=
;;   (-> [:div [:p] [:p#flag]]
;;       (enlive-html/at [:p#flag] (enlive-html/content {:type :element :tag :p :attrs nil :content ["foo"]})))
;;   [:div [:p] [:p {:id "flag"} [:p "foo"]]]
;;   ))

;; (assert
;;  (=
;;   (-> [:div [:p] [:p#flag "foo"]]
;;       (enlive-html/at [:p#flag] (enlive-html/append [:div "one"] "<div>two</div>" {:type :element :tag :div :attrs nil :content ["three"]})))
;;   [:div [:p] [:p {:id "flag"} "foo" [:div "one"] [:div "two"] [:div "three"]]]
;;   ))

;; (assert
;;  (=
;;   (-> [:div [:p] [:p#flag "foo"]]
;;       (enlive-html/at [:p#flag] (enlive-html/prepend [:div "one"] "<div>two</div>" {:type :element :tag :div :attrs nil :content ["three"]})))
;;   [:div [:p] [:p {:id "flag"} [:div "one"] [:div "two"] [:div "three"] "foo"]]
;;   ))

;; (assert
;;  (=
;;   (-> [:div [:p] [:p#flag "foo"]]
;;       (enlive-html/at [:p#flag] (enlive-html/after [:div "one"] "<div>two</div>" {:type :element :tag :div :attrs nil :content ["three"]})))
;;   [:div [:p] [:p {:id "flag"} "foo"] [:div "one"] [:div "two"] [:div "three"]]))

;; (assert
;;  (=
;;   (-> [:div [:p] [:p#flag "foo"]]
;;       (enlive-html/at [:p#flag] (enlive-html/before [:div "one"] "<div>two</div>" {:type :element :tag :div :attrs nil :content ["three"]})))
;;   [:div [:p] [:div "one"] [:div "two"] [:div "three"] [:p {:id "flag"} "foo"]]))

;; (assert
;;  (=
;;   (-> [:div [:p] [:p#flag "foo"]]
;;       (enlive-html/at [:p#flag] (enlive-html/substitute [:div "one"] "<div>two</div>" {:type :element :tag :div :attrs nil :content ["three"]})))
;;   [:div [:p] [:div "one"] [:div "two"] [:div "three"]]))

;; (assert
;;  (=
;;   (-> [:div [:p {:style {:margin-top "10px"}}]]
;;       (enlive-html/at [:div] (enlive-html/set-attr :style "margin-top:10px;")))
;;   [:div {:style "margin-top:10px;"}
;;    [:p {:style "margin-top:10px;"}]]))

;; (assert
;;  (=
;;   (-> [:div [:p {:style {:margin-top "10px"}}]]
;;       (enlive-html/at [:div] (enlive-html/set-attr :style {:margin-top "10px"})))
;;   [:div {:style "margin-top:10px;"}
;;    [:p {:style "margin-top:10px;"}]]))

;; (assert
;;  (=
;;   (-> [:div {:style "foo: bar;"} [:p {:style {:margin-top "10px"}}]]
;;       (enlive-html/at [:div] (enlive-html/set-attr "style" {:margin-top "10px"})))
;;   [:div {:style "margin-top:10px;"}
;;    [:p {:style "margin-top:10px;"}]]))

;; (assert
;;  (=
;;   (-> [:div {:style {:foo "bar"}} [:p {:style {:margin-top "10px"}}]]
;;       (enlive-html/at [:div] (enlive-html/set-attr "style" {:margin-top "10px"})))
;;   [:div {:style "margin-top:10px;"}
;;    [:p {:style "margin-top:10px;"}]]))

;; (assert
;;  (=
;;   (-> [:div [:p] [:p#flag]]
;;       (enlive/at [:p#flag] (enlive/content* "test")))
;;   [:div [:p] [:p {:id "flag"} "test"]]))

;; (assert
;;  (=
;;   (-> [:div [:p] [:p#flag]]
;;       (enlive/at [:p#flag] (enlive/content [:span "test"])))
;;   [:div [:p] [:p {:id "flag"} [:span "test"]]]
;;   ))

;; (assert
;;  (=
;;   (-> [:div [:p] [:p#flag]]
;;       (enlive/at [:p#flag] (enlive/content [:span  "test"] [:p "p"])))
;;   [:div [:p] [:p {:id "flag"} [:span "test"] [:p "p"]]]
;;   ))

;; (assert
;;  (=
;;   (-> [:div [:p] [:p#flag]]
;;       (enlive/at [:p#flag] (enlive/content* "<p>foo</p>")))
;;   [:div [:p] [:p {:id "flag"} "&lt;p&gt;foo&lt;/p&gt;"]]
;;   ))

;; (assert
;;  (=
;;   (-> [:div [:p] [:p#flag]]
;;       (enlive/at [:p#flag] (enlive/content "<p>foo</p>")))
;;   [:div [:p] [:p {:id "flag"} [:p "foo"]]]
;;   ))

;; (assert
;;  (=
;;   (-> [:div [:p] [:p#flag]]
;;       (enlive/at [:p#flag] (enlive/content "foo>bar")))
;;   [:div [:p] [:p {:id "flag"} "foo&gt;bar"]]
;;   ))

;; (assert
;;  (=
;;   (-> [:div [:p] [:p#flag]]
;;       (enlive/at [:p#flag] (enlive/content {:type :element :tag :p :attrs nil :content ["foo"]})))
;;   [:div [:p] [:p {:id "flag"} [:p "foo"]]]
;;   ))

;; (assert
;;  (=
;;   (-> [:div [:p] [:p#flag "foo"]]
;;       (enlive/at [:p#flag] (enlive/append [:div "one"] "<div>two</div>" {:type :element :tag :div :attrs nil :content ["three"]})))
;;   [:div [:p] [:p {:id "flag"} "foo" [:div "one"] [:div "two"] [:div "three"]]]
;;   ))

;; (assert
;;  (=
;;   (-> [:div [:p] [:p#flag "foo"]]
;;       (enlive/at [:p#flag] (enlive/prepend [:div "one"] "<div>two</div>" {:type :element :tag :div :attrs nil :content ["three"]})))
;;   [:div [:p] [:p {:id "flag"} [:div "one"] [:div "two"] [:div "three"] "foo"]]
;;   ))

;; (assert
;;  (=
;;   (-> [:div [:p] [:p#flag "foo"]]
;;       (enlive/at [:p#flag] (enlive/after [:div "one"] "<div>two</div>" {:type :element :tag :div :attrs nil :content ["three"]})))
;;   [:div [:p] [:p {:id "flag"} "foo"] [:div "one"] [:div "two"] [:div "three"]]))

;; (assert
;;  (=
;;   (-> [:div [:p] [:p#flag "foo"]]
;;       (enlive/at [:p#flag] (enlive/before [:div "one"] "<div>two</div>" {:type :element :tag :div :attrs nil :content ["three"]})))
;;   [:div [:p] [:div "one"] [:div "two"] [:div "three"] [:p {:id "flag"} "foo"]]))

;; (assert
;;  (=
;;   (-> [:div [:p] [:p#flag "foo"]]
;;       (enlive/at [:p#flag] (enlive/substitute [:div "one"] "<div>two</div>" {:type :element :tag :div :attrs nil :content ["three"]})))
;;   [:div [:p] [:div "one"] [:div "two"] [:div "three"]]))

;; (assert
;;  (=
;;   (-> [:div [:p {:style {:margin-top "10px"}}]]
;;       (enlive/at [:div] (enlive/set-attr :style "margin-top:10px;")))
;;   [:div {:style "margin-top:10px;"}
;;    [:p {:style "margin-top:10px;"}]]))

;; (assert
;;  (=
;;   (-> [:div [:p {:style {:margin-top "10px"}}]]
;;       (enlive/at [:div] (enlive/set-attr :style {:margin-top "10px"})))
;;   [:div {:style "margin-top:10px;"}
;;    [:p {:style "margin-top:10px;"}]]))

;; (assert
;;  (=
;;   (-> [:div {:style "foo: bar;"} [:p {:style {:margin-top "10px"}}]]
;;       (enlive/at [:div] (enlive/set-attr "style" {:margin-top "10px"})))
;;   [:div {:style "margin-top:10px;"}
;;    [:p {:style "margin-top:10px;"}]]))

;; (assert
;;  (=
;;   (-> [:div {:style {:foo "bar"}} [:p {:style {:margin-top "10px"}}]]
;;       (enlive/at [:div] (enlive/set-attr "style" {:margin-top "10px"})))
;;   [:div {:style "margin-top:10px;"}
;;    [:p {:style "margin-top:10px;"}]]))



;; (assert
;;  (=
;;   (-> [:div {:style  {:foo "bar"}} [:p {:style {:margin-top "10px"}}]]
;;       (enlive/at [:div] (enlive/remove-attr :style)))
;;   [:div [:p {:style "margin-top:10px;"}]]))

;; ;; doesnt work
;; #_ (-> [:div {:style  "foo: bar"} "Hello, ${name}"]
;;     (enlive/at [:div] (enlive/replace-vars {:name "world"})))

;; (assert
;;  (=
;;   (-> [:div {:style {:foo "bar"}} [:p {:style {:margin-top "10px"}}]]
;;       (enlive/at [:p] (enlive/wrap :span)))
;;   [:div {:style "foo:bar;"} [:span [:p {:style "margin-top:10px;"}]]]))

;; (assert
;;  (=
;;   (-> [:div {:style {:foo "bar"}} [:p {:style {:margin-top "10px"}}]]
;;       (enlive/at [:div] enlive/unwrap))
;;   [:p {:style "margin-top:10px;"}]))

;; (assert
;;  (=
;;   (-> [:div {:style {:foo "bar"}} [:p {:style {:margin-top "10px"}}]]
;;       (enlive/at [:div] (enlive/add-class "bar" "baz")))
;;   [:div {:style "foo:bar;", :class "bar baz"} [:p {:style "margin-top:10px;"}]]))

;; (assert
;;  (=
;;   (-> [:div.foo.bar.baz {:style {:foo "bar"}} [:p {:style {:margin-top "10px"}}]]
;;       (enlive/at [:div] (enlive/remove-class "foo" "baz")))
;;   [:div {:class "bar", :style "foo:bar;"} [:p {:style "margin-top:10px;"}]]))

;; (assert
;;  (=
;;   (-> [:div.foo.bar.baz {:style {:foo "bar"}} [:p {:style {:margin-top "10px"}}]]
;;       (enlive/at [:div] (enlive/do->
;;                          (enlive/remove-class "foo" "baz")
;;                          (enlive/add-class "bing")
;;                          (enlive/wrap :div)
;;                          (enlive/set-attr :style {:foo "bar"}))))
;;   [:div {:style "foo:bar;"} [:div {:class "bar bing", :style "foo:bar;"} [:p {:style "margin-top:10px;"}]]]))

;; (assert
;;  (=
;;   (-> [:div.foo.bar.baz {:style {:foo "bar"}} [:p "1"] [:p "2"] [:span "3"]]
;;       (enlive/at [:span] (enlive/clone-for [n (range 5)]
;;                                            (enlive/content (str n)))))
;;   [:div {:class "foo bar baz", :style "foo:bar;"} [:p "1"] [:p "2"] [:span "0"] [:span "1"] [:span "2"] [:span "3"] [:span "4"]]))

;; ;; doesn't work. docs are non existent
;; #_ (-> [:div.foo.bar.baz {:style {:foo "bar"}} [:p "1"] [:p "2"] [:span "3"]]
;;     (enlive/at [:div] (enlive/move [:p] [:span])))




;; (assert
;;  (=
;;   (-> [:div {:style  {:foo "bar"}} [:p {:style {:margin-top "10px"}}]]
;;       (enlive-html/at [:div] (enlive-html/remove-attr :style)))
;;   [:div [:p {:style "margin-top:10px;"}]]))

;; ;; doesnt work
;; #_ (-> [:div {:style  "foo: bar"} "Hello, ${name}"]
;;     (enlive-html/at [:div] (enlive-html/replace-vars {:name "world"})))

;; (assert
;;  (=
;;   (-> [:div {:style {:foo "bar"}} [:p {:style {:margin-top "10px"}}]]
;;       (enlive-html/at [:p] (enlive-html/wrap :span)))
;;   [:div {:style "foo:bar;"} [:span [:p {:style "margin-top:10px;"}]]]))

;; (assert
;;  (=
;;   (-> [:div {:style {:foo "bar"}} [:p {:style {:margin-top "10px"}}]]
;;       (enlive-html/at [:div] enlive-html/unwrap))
;;   [:p {:style "margin-top:10px;"}]))

;; (assert
;;  (=
;;   (-> [:div {:style {:foo "bar"}} [:p {:style {:margin-top "10px"}}]]
;;       (enlive-html/at [:div] (enlive-html/add-class "bar" "baz")))
;;   [:div {:style "foo:bar;", :class "bar baz"} [:p {:style "margin-top:10px;"}]]))

;; (assert
;;  (=
;;   (-> [:div.foo.bar.baz {:style {:foo "bar"}} [:p {:style {:margin-top "10px"}}]]
;;       (enlive-html/at [:div] (enlive-html/remove-class "foo" "baz")))
;;   [:div {:class "bar", :style "foo:bar;"} [:p {:style "margin-top:10px;"}]]))

;; (assert
;;  (=
;;   (-> [:div.foo.bar.baz {:style {:foo "bar"}} [:p {:style {:margin-top "10px"}}]]
;;       (enlive-html/at [:div] (enlive-html/do->
;;                          (enlive-html/remove-class "foo" "baz")
;;                          (enlive-html/add-class "bing")
;;                          (enlive-html/wrap :div)
;;                          (enlive-html/set-attr :style {:foo "bar"}))))
;;   [:div {:style "foo:bar;"} [:div {:class "bar bing", :style "foo:bar;"} [:p {:style "margin-top:10px;"}]]]))

;; (assert
;;  (=
;;   (-> [:div.foo.bar.baz {:style {:foo "bar"}} [:p "1"] [:p "2"] [:span "3"]]
;;       (enlive-html/at [:span] (enlive-html/clone-for [n (range 5)]
;;                                            (enlive-html/content (str n)))))
;;   [:div {:class "foo bar baz", :style "foo:bar;"} [:p "1"] [:p "2"] [:span "0"] [:span "1"] [:span "2"] [:span "3"] [:span "4"]]))

;; ;; doesn't work. docs are non existent
;; #_ (-> [:div.foo.bar.baz {:style {:foo "bar"}} [:p "1"] [:p "2"] [:span "3"]]
;;     (enlive-html/at [:div] (enlive-html/move [:p] [:span])))


;;
;; hickory tests
;;
(require '[pod.retrogradeorbit.hickory.select :as s]
         '[pod.retrogradeorbit.hickory.zip :as z]
         '[pod.retrogradeorbit.bootleg.utils :refer [convert-to]]
         '[clojure.zip :as zip]
         )

(assert
 (->>
  (convert-to '([:div [:span "hello"]]) :hickory)
  (s/select (s/tag :span))
  (= [{:type :element
       :attrs nil
       :tag :span
       :content ["hello"]}])))


(assert
 (->>
  (convert-to '([:div [:span "1"] [:span "2" [:span "sub"]]]) :hickory)
  (s/select (s/tag :span))
  (= [{:type :element
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

(assert
 (->>
  (convert-to '([:div [:span "hello" "<!-- wat -->"]]) :hickory)
  (s/select (s/node-type :comment))
  (= [{:type :comment
       :content [" wat "]}])))

(assert
 (->>
  (convert-to '([:div {:foo "baz"} [:span {:foo "barry"} "hello"]]) :hickory)
  (s/select (s/attr :foo #(.startsWith % "bar")))
  (= [{:type :element
       :attrs {:foo "barry"}
       :tag :span
       :content ["hello"]}])))

(assert
 (->>
  (convert-to '([:div [:span#foo "hello"]]) :hickory)
  (s/select (s/id :foo))
  (=
   [{:type :element
     :attrs {:id "foo"}
     :tag :span
     :content ["hello"]}]

   )))


(assert
 (->>
  (convert-to '([:div [:span.foo "hello"]]) :hickory)
  (s/select (s/class :foo))
  (=
   [{:type :element
     :attrs {:class "foo"}
     :tag :span
     :content ["hello"]}]

   )))



(assert
 (->>
  (convert-to '([:div [:span.foo "hello"]]) :hickory)
  (s/select s/any)
  (=
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
     :content ["hello"]}]

   )))



(assert
 (->>
  (convert-to '([:div [:span.foo "hello"]]) :hickory)
  (s/select s/element)
  (=
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
     :content ["hello"]}]

   )))




(assert
 (->>
  (convert-to [:html [:body [:div [:span.foo "hello"]]]] :hickory)
  (s/select s/root)
  (=
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
                                      :content ["hello"]}]}]}]}]

   )))


(def hicc
 '("<!DOCTYPE html>\n"
   "<!-- Comment 1 -->"
   "\n"
   [:html
    "\n"
    [:head]
    "\n"
    [:body
     "\n"
     [:h1 "Heading"]
     "\n"
     [:p "Paragraph"]
     "\n"
     [:a {:href "http://example.com"} "Link"]
     "\n"
     [:div
      {:class "aclass bclass cool"}
      "\n"
      [:span
       {:disabled "",
        :anotherattr "",
        :thirdthing "44",
        :id "attrspan",
        :capitalized "UPPERCASED"}
       "\n"
       [:div {:class "subdiv cool", :id "deepestdiv"} "Div"]
       "\n"]
      "\n"
      "<!-- Comment 2 -->"
      "\n"
      [:span {:id "anid", :class "cool"} "Span"]
      "\n"]
     "\n"]
    "\n"]
   "\n"))



(assert
 (->>
  (first (drop 3 (convert-to hicc :hickory-seq)))
  (s/select (s/n-moves-until 0 6 zip/up nil?))
  (= ["Div"])))

(assert
 (->>
  (convert-to [:html [:body [:div [:span.foo "hello" [:div "foo"]]]]] :hickory)
  (s/select (s/nth-of-type 1 :body))
  (= [{:type :element
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

(assert
 (->>
  (convert-to [:html [:body [:div [:span.foo "hello" [:div "one"]]]
                      [:div "two"]
                      ]] :hickory)
  (s/select (s/nth-last-of-type 1 :div))
  (=
   [{:type :element
     :attrs nil
     :tag :div
     :content ["one"]}
    {:type :element
     :attrs nil
     :tag :div
     :content ["two"]}]
   )))

(assert
 (->>
  (convert-to [:html [:body [:div [:span.foo "hello" [:div "one"]]]
                      [:div "two"]
                      ]] :hickory)
  (s/select (s/nth-child 0 1))
  (=
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
     :content ["one"]}])))

(assert
 (->>
  (convert-to [:html [:body [:div [:span.foo "hello" [:div "one"]]]
                      [:div "two"]
                      ]] :hickory)
  (s/select (s/nth-last-child 0 1))
  (=
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
     :content ["two"]}])))

(assert
 (->>
  (convert-to [:html [:body [:div [:span.foo "hello" [:div "one"]]]
                      [:div "two"]
                      ]] :hickory)
  (s/select s/first-child)
  (=
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
     :content ["one"]}])))

(assert
 (->>
  (convert-to [:html [:body [:div [:span.foo "hello" [:div "one"]]]
                      [:div "two"]
                      ]] :hickory)
  (s/select s/last-child)
  (=
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
     :content ["two"]}])))

(assert
 (->>
  (convert-to [:html [:body [:div [:span.foo "hello" [:div.foo "one"]]]
                      [:div "two"]
                      ]] :hickory)
  (s/select (s/and (s/tag :div) (s/class :foo)))
  (=
   [{:type :element
     :attrs {:class "foo"}
     :tag :div
     :content ["one"]}])))

(assert
 (->>
  (convert-to [:html [:body [:div [:span.foo "hello" [:div.foo "one"]]]
                      [:div "two"]
                      ]] :hickory)
  (s/select (s/or (s/tag :div) (s/class :foo)))
  (=
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

(assert
 (->>
  (convert-to [:html [:body [:div [:span.foo "hello" [:div.foo "one"]]]
                      [:div "two"]
                      ]] :hickory)
  (s/select (s/and (s/not (s/tag :div)) (s/class :foo)))
  (=
   [{:type :element
     :attrs {:class "foo"}
     :tag :span
     :content ["hello"
               {:type :element
                :attrs {:class "foo"}
                :tag :div
                :content ["one"]}]}])))

(assert
 (->>
  (convert-to [:html [:body [:div [:span.foo "hello" [:div.foo "one"]]]
                      [:div "two"]
                      ]] :hickory)
  (s/select (s/and (s/el-not (s/tag :div)) (s/class :foo)))
  (=
   [{:type :element
     :attrs {:class "foo"}
     :tag :span
     :content ["hello"
               {:type :element
                :attrs {:class "foo"}
                :tag :div
                :content ["one"]}]}])))

(assert
 (->>
  (convert-to [:html [:body [:div [:span.foo "hello" [:div.foo "one"]]]
                      [:div "two"]
                      ]] :hickory)
  (s/select (s/child (s/el-not (s/tag :div)) (s/class :foo)))
  (=
   [{:type :element
     :attrs {:class "foo"}
     :tag :div
     :content ["one"]}])))

(assert
 (->>
  (convert-to [:html [:body [:div [:span.foo "hello" [:div.bar "one"] [:div.foo "two"]]]
                      [:div "three"]
                      ]] :hickory)
  (s/select (s/follow-adjacent (s/tag :div) (s/class :foo)))
  (=
   [{:type :element
     :attrs {:class "foo"}
     :tag :div
     :content ["two"]}])))

(assert
 (->>
  (convert-to [:html [:body [:div [:span.foo "hello" [:div.bar "one"] [:div.foo "two"]]]
                      [:div "three"]
                      ]] :hickory)
  (s/select (s/precede-adjacent (s/tag :div) (s/class :foo)))
  (=
   [{:type :element
     :attrs {:class "bar"}
     :tag :div
     :content ["one"]}])))

(assert
 (->>
  (convert-to [:html [:body [:div [:span.foo "hello" [:div.bar "one"] [:div.foo "two"]]]
                      [:div "three"]
                      ]] :hickory)
  (s/select (s/descendant (s/tag :span)))
  (=
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

(assert
 (->>
  (convert-to [:html [:body [:div [:span.foo "hello" [:div.bar "one"] [:div.foo "two"]]]
                      [:div "three"]
                      ]] :hickory)
  (s/select (s/follow (s/class :bar) (s/class :foo)))
  (=
   [{:type :element
     :attrs {:class "foo"}
     :tag :div
     :content ["two"]}])))

(assert
 (->>
  (convert-to [:html [:body [:div [:span.foo "hello" [:div.bar "one"] [:div.foo "two"]]]
                      [:div "three"]
                      ]] :hickory)
  (s/select (s/precede (s/class :bar) (s/class :foo)))
  (=
   [{:type :element
     :attrs {:class "bar"}
     :tag :div
     :content ["one"]}])))

(assert
 (->>
  (convert-to [:html [:body [:div [:span.foo "hello" [:div.bar "one"] [:div#inner "two"]]]
                      [:div "three"]
                      ]] :hickory)
  (s/select
   (s/and (s/tag :div)
          (s/has-descendant (s/id "inner")))
   )
  (=
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

(assert
 (->>
  (convert-to [:html [:body [:div [:span.foo "hello" [:div.bar "one"] [:div#inner "two"]]]
                      [:div "three"]
                      ]] :hickory)
  (s/select (s/has-child (s/id "inner")))
  (=
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
                :content ["two"]}]}])))
