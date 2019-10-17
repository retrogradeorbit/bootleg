(ns bootleg.utils
  (:require [clojure.string :as string]
            [hickory.core :as hickory]
            [hickory.render :as render]
            [hickory.convert :as convert]))

(defn- i-starts-with?
  "efficient case insensitive string start-with?"
  [haystack needle]
  (let [uneedle (string/upper-case needle)
        uhaystack-start (string/upper-case (subs haystack 0 (min (count needle) (count haystack))))]
    (= uneedle uhaystack-start)))

(defn html? [markup]
  (or (i-starts-with? markup "<!DOCTYPE HTML")
      (i-starts-with? markup "<html")))

;;
;; hiccup / html
;;
(defn html->hiccup-seq [markup]
  (if (html? markup)
    (hickory/as-hiccup (hickory/parse markup))
    (map hickory/as-hiccup (hickory/parse-fragment markup))))

#_ (html->hiccup-seq "<div>div</div><p>p</p>")

(defn html->hiccup [markup]
  (last (html->hiccup-seq markup)))

#_ (html->hiccup "<div>div</div><p>p</p>")

(def hiccup-seq->html render/hiccup-to-html)

#_ (hiccup-seq->html '([:div "div"] [:p "p"]))

(defn hiccup->html [hiccup]
  (hiccup-seq->html [hiccup]))

#_ (hiccup->html [:div "div"])

;;
;; hiccup / hickory
;;
(defn hiccup->hickory [hiccup]
  (first (convert/hiccup-fragment-to-hickory [hiccup])))

#_ (hiccup->hickory [:div "div"])

(defn hickory->hiccup [hickory]
  (convert/hickory-to-hiccup hickory))

#_ (hickory->hiccup {:type :element, :attrs nil, :tag :div, :content ["div"]})

(defn hiccup-seq->hickory-seq [hiccup-seq]
  (map hiccup->hickory hiccup-seq))

#_ (hiccup-seq->hickory-seq '([:div "div"] [:p "p"]))

(defn hickory-seq->hiccup-seq [hickory-seq]
  (map hickory->hiccup hickory-seq))

#_ (hickory-seq->hiccup-seq '({:type :element, :attrs nil, :tag :div, :content ["div"]} {:type :element, :attrs nil, :tag :p, :content ["p"]}))



;;
;; hickory / html
;;
(defn html->hickory-seq [markup]
  (map hickory/as-hickory (hickory/parse-fragment markup)))

#_ (html->hickory-seq "<div>div</div><p>p</p>")

(defn html->hickory [markup]
  (last (html->hickory-seq markup)))

#_ (html->hickory "<div>div</div><p>p</p>")

(defn hickory-seq->html [hickory]
  (apply str (map render/hickory-to-html hickory)))

#_ (hickory-seq->html
    '({:type :element, :attrs nil, :tag :div, :content ["div"]} "-" {:type :element, :attrs nil, :tag :p, :content ["p"]}))

(defn hickory->html [hickory]
  (render/hickory-to-html hickory))

#_ (hickory->html {:type :element, :attrs nil, :tag :div, :content ["div"]})

(defn is-hiccup? [data]
  (keyword? (first data)))

(defn is-hickory? [data]
  (and (map? data) (:type data)))

(defn is-hickory-seq? [data]
  (and (or (seq? data) (vector? data))
       (is-hickory? (first data))))

(defn is-hiccup-seq? [data]
  (and (or (seq? data) (vector? data))
       (or (string? (first data)) (is-hiccup? (first data)))))

(defn markup-type [data]
  (cond
    (is-hiccup? data) :hiccup
    (is-hiccup-seq? data) :hiccup-seq
    (is-hickory? data) :hickory
    (is-hickory-seq? data) :hickory-seq
    (string? data) :html
    :else nil))

(def conversion-fns
  ;; keys: [from-type to-type]
  ;; values: converter function
  ;;
  ;; WARNING: some of these are possibly lossy (once using `first`)
  {
   [:hiccup :hiccup] identity
   [:hiccup :hickory] hiccup->hickory
   [:hiccup :hickory-seq] (comp list hiccup->hickory)
   [:hiccup :hiccup-seq] list
   [:hiccup :html] hiccup->html

   [:hiccup-seq :hiccup] first
   [:hiccup-seq :hickory] (comp first hiccup-seq->hickory-seq)
   [:hiccup-seq :hickory-seq] hiccup-seq->hickory-seq
   [:hiccup-seq :hiccup-seq] identity
   [:hiccup-seq :html] hiccup-seq->html

   [:hickory :hiccup] hickory->hiccup
   [:hickory :hickory] identity
   [:hickory :hickory-seq] list
   [:hickory :hiccup-seq] (comp list hickory->hiccup)
   [:hickory :html] hickory->html

   [:hickory-seq :hiccup] (comp first hickory-seq->hiccup-seq)
   [:hickory-seq :hickory] first
   [:hickory-seq :hickory-seq] identity
   [:hickory-seq :hiccup-seq] hickory-seq->hiccup-seq
   [:hickory-seq :html] hickory-seq->html

   [:html :hiccup] html->hiccup
   [:html :hickory] html->hickory
   [:html :hickory-seq] html->hickory-seq
   [:html :hiccup-seq] html->hiccup-seq
   [:html :html] identity})

(defn convert-to [data to-type]
  (let [from-type (markup-type data)
        converter (conversion-fns [from-type to-type])]
    (converter data)))

(defn html-output-to [flags html]
  (cond
    (:hiccup flags) (html->hiccup html)
    (:hiccup-seq flags) (html->hiccup-seq html)
    (:hickory flags) (html->hickory html)
    (:hickory-seq flags) (html->hickory-seq html)
    (:html flags) html
    :else (html->hiccup-seq html)))

(defn as-html
  "Intelligently coerce input to html
  hiccup is a single form. However, html snippets can be multiple sequential forms
  resulting in a sequence of hiccup. The following function converts either of these
  to html without throwing an error for one of them"
  [data]
  (convert-to data :html))

;; full html
#_ (hickory/parse "<html><body><h1>foo</h1></body></html>")
#_ (hickory/parse "<h1>foo</h1>")

;; fragment
#_ (hickory/parse-fragment "<html><body><h1>foo</h1></body></html>")
#_ (hickory/parse-fragment "<h1>foo</h1>")

;; smart-parse
#_ (smart-parse "<html><body><h1>foo</h1></body></html>")
#_ (smart-parse "<h1>foo</h1>")

;; html->hiccup-seq
#_ (html->hiccup-seq "<html><body><h1>foo</h1></body></html>")
#_ (html->hiccup-seq "<h1>foo</h1><p>b</p>")
#_ (html->hiccup-seq "<h1>foo</h1>")

;; html->hickory
#_ (html->hickory "<html><body><h1>foo</h1></body></html>")
#_ (html->hickory "<h1>foo</h1><p>b</p>")
#_ (html->hickory "<h1>foo</h1>")

;; hiccup->html
#_ (hiccup->html [:div])

;; hiccup->hickory
#_ (hiccup->hickory [:div])

;; hiccup-seq->hickory
#_ (hiccup-seq->hickory '([:div] [:p]))

;; hiccup-seq->hickory-seq
#_ (hiccup-seq->hickory-seq '([:div] [:p]))

;; hickup*->html
#_ (hiccup*->html [:div])
#_ (hiccup*->html '([:div]))
#_ (hiccup*->html '("<!DOCTYPE HTML>" [:div]))
