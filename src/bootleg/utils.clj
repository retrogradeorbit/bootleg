(ns bootleg.utils
  (:require [bootleg.context :as context]
            [clojure.string :as string]
            [hickory.core :as hickory]
            [hickory.render :as render]
            [hickory.convert :as convert]
            [clojure.walk :as walk]
            [fipp.edn :as fipp]
            [puget.printer :as puget]))

(defn- i-starts-with?
  "efficient case insensitive string start-with?"
  [haystack needle]
  (let [uneedle (string/upper-case needle)
        uhaystack-start (string/upper-case
                         (subs haystack 0 (min (count needle) (count haystack))))]
    (= uneedle uhaystack-start)))

(defn- html? [markup]
  ;; todo: trim start
  (or (i-starts-with? (string/triml markup) "<!DOCTYPE HTML")
      (i-starts-with? (string/triml markup) "<html")))

(defn- doctype? [markup]
  (i-starts-with? markup "<!DOCTYPE HTML"))

(defn- split-doctype [markup]
  (let [[_ doctype-suffix remain] (string/split markup #"<" 3)]
    [(str "<" doctype-suffix) (if remain (str "<" remain) "")]))

(defn- munge-html-tags [markup]
  (-> markup
      (string/replace #"<html" "<html-bootleg-munged")
      (string/replace #"</html" "</html-bootleg-munged")
      (string/replace #"<head" "<head-bootleg-munged")
      (string/replace #"</head" "</head-bootleg-munged")
      (string/replace #"<body" "<body-bootleg-munged")
      (string/replace #"</body" "</body-bootleg-munged")))

(def munge-map {:html :html-bootleg-munged
                :body :body-bootleg-munged
                :head :head-bootleg-munged})

(def demunge-map
  (into {} (for [[k v] munge-map] [v k])))

(defn- demunge-hiccup-tags [hiccup]
  (walk/postwalk #(get demunge-map % %) hiccup))

(defn- munge-hiccup-tags [hiccup]
  (walk/postwalk #(get munge-map % %) hiccup))

(defn- demunge-hickory-tags [hickory]
  (walk/postwalk #(if (:tag %)
                    (update % :tag demunge-map (:tag %))
                    %) hickory))

(defn- munge-hickory-tags [hickory]
  (walk/postwalk #(if (:tag %)
                    (update % :tag munge-map (:tag %))
                    %) hickory))

(defn hickory-seq-add-missing-types [hickory]
  (walk/postwalk
   (fn [el]
     (if (and (:tag el) (not (:type el)))
       (assoc el :type :element)
       el))
   hickory))

(defn hickory-seq-convert-dtd [hickory]
  (walk/postwalk
   (fn [el]
     (if (and (= :dtd (:type el)) (:data el))
       (let [[name publicid systemid] (:data el)]
         (str "<!DOCTYPE " name ">"))
       el))
   hickory))

;;
;; hiccup / html
;;
(defn html->hiccup-seq [markup]
  (if (html? markup)
    (if (doctype? markup)
      (let [[doctype markup] (split-doctype markup)]
        (-> (map hickory/as-hiccup (hickory/parse-fragment (munge-html-tags markup)))
            demunge-hiccup-tags
            (conj doctype)))
      (-> (map hickory/as-hiccup (hickory/parse-fragment (munge-html-tags markup)))
          demunge-hiccup-tags))
    (map hickory/as-hiccup (hickory/parse-fragment markup))))

(defn html->hiccup [markup]
  (last (html->hiccup-seq markup)))

(defn hiccup-seq->html [hiccup-seq]
  (->> hiccup-seq
       ;; hiccup-to-html cant handle numbers
       (walk/postwalk #(if (number? %) (str %) %))
       render/hiccup-to-html))

(defn hiccup->html [hiccup]
  (hiccup-seq->html [hiccup]))

;;
;; hiccup / hickory
;;
(defn hiccup->hickory [hiccup]
  (if (string? hiccup)
    hiccup
    (-> hiccup
        munge-hiccup-tags
        vector
        convert/hiccup-fragment-to-hickory
        first
        demunge-hickory-tags)))

(defn hickory-to-hiccup-preserve-doctype [hickory]
  (if (and (string? hickory)
           (i-starts-with? (string/triml hickory) "<!DOCTYPE"))
    hickory
    (convert/hickory-to-hiccup hickory)))

(defn hickory->hiccup [hickory]
  (if (string? hickory)
    hickory
    (-> hickory
        hickory-seq-convert-dtd
        munge-hickory-tags
        hickory-seq-add-missing-types
        hickory-to-hiccup-preserve-doctype
        demunge-hiccup-tags)))

(defn hiccup-seq->hickory-seq [hiccup-seq]
  (map hiccup->hickory hiccup-seq))

(defn hickory-seq->hiccup-seq [hickory-seq]
  (map hickory->hiccup hickory-seq))

;;
;; hickory / html
;;
(defn html->hickory-seq [markup]
  (if (html? markup)
    (if (doctype? markup)
      (let [[doctype markup] (split-doctype markup)]
        (-> (map hickory/as-hickory (hickory/parse-fragment (munge-html-tags markup)))
            demunge-hickory-tags
            (conj doctype)
            ))
      (-> (map hickory/as-hickory (hickory/parse-fragment (munge-html-tags markup)))
          demunge-hickory-tags))
    (map hickory/as-hickory (hickory/parse-fragment markup))))

(defn html->hickory [markup]
  (-> markup
      html->hickory-seq
      last))

(defn- hickory-to-html-preserve-doctype [hickory]
  (if (and (string? hickory)
           (i-starts-with? (string/triml hickory) "<!DOCTYPE"))
    hickory
    (render/hickory-to-html hickory)))

(defn hickory-seq->html [hickory]
  (->> hickory
       (map #(if (string? %)
               %
               (-> %
                   hickory-seq-convert-dtd
                   hickory-seq-add-missing-types
                   hickory-to-html-preserve-doctype)))
       (apply str)))

(defn hickory->html [hickory]
  (-> hickory
      hickory-seq-convert-dtd
      hickory-seq-add-missing-types
      hickory-to-html-preserve-doctype))

;;
;; testing
;;
(defn is-hiccup? [data]
  (keyword? (first data)))

(defn is-hickory? [data]
  (and (map? data) (or (:tag data) (:type data))))

(defn is-hickory-seq? [data]
  (and (or (seq? data) (vector? data))
       (some is-hickory? data)))

(defn is-hiccup-seq? [data]
  (and (or (seq? data) (vector? data))
       (some is-hiccup? data)))

(defn markup-type [data]
  (cond
    (is-hiccup? data) :hiccup
    (is-hiccup-seq? data) :hiccup-seq
    (is-hickory? data) :hickory
    (is-hickory-seq? data) :hickory-seq
    (string? data) :html
    (every? string? data) :hiccup-seq
    :else nil))

(defn- escape-code [n]
  (str "\033[" (or n 0) "m"))

(def colour-map
  {:red 31
   :yellow 33})

(defn colour [& [colour-name]]
  (if context/*colour*
    (escape-code (colour-map colour-name))
    ""))

(defn warn-last [from to data]
  (when (< 1 (count data))
    (let [n (dec (count data))]
      (binding [*out* *err*]
        (println
         (str
          (colour :yellow)
          "Warning: converting markup from " from " to " to " lost " n " form" (when (< 1 n) "s")
          (colour))))))
  (last data))

(def conversion-fns
  ;; keys: [from-type to-type]
  ;; values: converter function
  ;;
  ;; WARNING: some of these are possibly lossy (ones using warn-last)
  {
   [:hiccup :hiccup] identity
   [:hiccup :hickory] hiccup->hickory
   [:hiccup :hickory-seq] (comp list hiccup->hickory)
   [:hiccup :hiccup-seq] list
   [:hiccup :html] hiccup->html

   [:hiccup-seq :hiccup] (partial warn-last :hiccup-seq :hiccup)
   [:hiccup-seq :hickory] (comp (partial warn-last :hiccup-seq :hickory)
                                hiccup-seq->hickory-seq)
   [:hiccup-seq :hickory-seq] hiccup-seq->hickory-seq
   [:hiccup-seq :hiccup-seq] identity
   [:hiccup-seq :html] hiccup-seq->html

   [:hickory :hiccup] hickory->hiccup
   [:hickory :hickory] identity
   [:hickory :hickory-seq] list
   [:hickory :hiccup-seq] (comp list hickory->hiccup)
   [:hickory :html] hickory->html

   [:hickory-seq :hiccup] (comp (partial warn-last :hickory-seq :hiccup)
                                hickory-seq->hiccup-seq)
   [:hickory-seq :hickory] (partial warn-last :hickory-seq :hickory)
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

(defn pprint [& forms]
  (if context/*colour*
    (apply puget/cprint forms)
    (apply fipp/pprint forms)))

(defn split-camel-case [s]
  (let [parts (string/split s #"[a-z][A-Z]")
        split-points (-> (map (comp inc inc count) parts)
                         (->> (into []))
                         (update 0 dec)
                         (->> (reductions + 0)))
        from-to (map vector split-points (rest split-points))]
    (map
     (fn [[from to]] (subs s from (min to (count s))))
     from-to)))

(defn exception-nice-name
  "Turn a class java.nio.file.FileAlreadyExistsException
  into the string \"File already exists\""
  [e]
  (-> e class str
      (string/split #"[ .]")
      last
      (string/replace #"Exception" "")
      split-camel-case
      (->> (string/join " ")
           (string/capitalize))))
