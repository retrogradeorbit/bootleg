(ns bootleg.utils
  (:require [clojure.string :as string]
            [hickory.core :as hickory]
            [hickory.render :as render]
            [hickory.convert :as convert]
            [clojure.walk :as walk]))

(defn- i-starts-with?
  "efficient case insensitive string start-with?"
  [haystack needle]
  (let [uneedle (string/upper-case needle)
        uhaystack-start (string/upper-case (subs haystack 0 (min (count needle) (count haystack))))]
    (= uneedle uhaystack-start)))

(defn html? [markup]
  (or (i-starts-with? markup "<!DOCTYPE HTML")
      (i-starts-with? markup "<html")))

(defn hickory-seq-add-missing-types [hickory]
  (walk/postwalk
   (fn [el]
     (if (and (:tag el) (not (:type el)))
       (assoc el :type :element)
       el))
   hickory))

;;
;; hiccup / html
;;
(defn html->hiccup-seq [markup]
  (if (html? markup)
    (hickory/as-hiccup (hickory/parse markup))
    (map hickory/as-hiccup (hickory/parse-fragment markup))))

(defn html->hiccup [markup]
  (last (html->hiccup-seq markup)))

(def hiccup-seq->html render/hiccup-to-html)

(defn hiccup->html [hiccup]
  (hiccup-seq->html [hiccup]))

;;
;; hiccup / hickory
;;
(defn hiccup-seq->hickory-seq [hiccup-seq]
  (if (some-> hiccup-seq first first name string/lower-case (= "html"))
    (:content (convert/hiccup-to-hickory hiccup-seq))
    (convert/hiccup-fragment-to-hickory hiccup-seq)))

(defn hickory-seq->hiccup-seq [hickory-seq]
  (map #(-> % hickory-seq-add-missing-types convert/hickory-to-hiccup) hickory-seq))

(defn hiccup->hickory [hiccup]
  (first (hiccup-seq->hickory-seq (list hiccup))))

(defn hickory->hiccup [hickory]
  (first (hickory-seq->hiccup-seq (list hickory))))

;;
;; hickory / html
;;
(defn html->hickory-seq [markup]
  (if (html? markup)
    (-> (hickory/parse markup) hickory/as-hickory :content)
    (map hickory/as-hickory (hickory/parse-fragment markup))))

(defn html->hickory [markup]
  (last (html->hickory-seq markup)))

(defn hickory-seq->html [hickory]
  (apply str (map #(-> % hickory-seq-add-missing-types render/hickory-to-html) hickory)))

(defn hickory->html [hickory]
  (-> hickory hickory-seq-add-missing-types render/hickory-to-html))

;;
;; testing
;;
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

   [:hiccup-seq :hiccup] last
   [:hiccup-seq :hickory] (comp last hiccup-seq->hickory-seq)
   [:hiccup-seq :hickory-seq] hiccup-seq->hickory-seq
   [:hiccup-seq :hiccup-seq] identity
   [:hiccup-seq :html] hiccup-seq->html

   [:hickory :hiccup] hickory->hiccup
   [:hickory :hickory] identity
   [:hickory :hickory-seq] list
   [:hickory :hiccup-seq] (comp list hickory->hiccup)
   [:hickory :html] hickory->html

   [:hickory-seq :hiccup] (comp last hickory-seq->hiccup-seq)
   [:hickory-seq :hickory] last
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
