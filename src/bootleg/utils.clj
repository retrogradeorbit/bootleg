(ns bootleg.utils
  (:require [bootleg.context :as context]
            [bootleg.file :as file]
            [clojure.string :as string]
            [hickory.core :as hickory]
            [hickory.render :as render]
            [hickory.convert :as convert]
            [clojure.walk :as walk]
            [clojure.java.io :as io]
            [fipp.edn :as fipp]
            [puget.printer :as puget]
            [clojure.data.xml :as xml]
            [sci.impl.vars :as sci-vars]
            ))

(defn- i-starts-with?
  "efficient case insensitive string start-with?"
  [haystack needle]
  (let [uneedle (string/upper-case needle)
        uhaystack-start (string/upper-case
                         (subs haystack 0 (min (count needle) (count haystack))))]
    (= uneedle uhaystack-start)))

(defn- html? [markup]
  (let [trimmed (string/triml markup)]
    (or (i-starts-with? trimmed "<!DOCTYPE HTML")
        (i-starts-with? trimmed "<html"))))

(defn- xml? [markup]
  (let [trimmed (string/triml markup)]
    (i-starts-with? trimmed "<?xml")))

(defn- doctype? [markup]
  (i-starts-with? markup "<!DOCTYPE HTML"))

(defn- split-doctype [markup]
  (let [[_ doctype-suffix remain] (string/split markup #"<" 3)]
    [(str "<" doctype-suffix) (if remain (str "<" remain) "")]))

(defn- munge-html-tags [markup]
  (-> markup
      (string/replace #"<html([> ])" "<html-bootleg-munged$1")
      (string/replace #"</html([> ])" "</html-bootleg-munged$1")
      (string/replace #"<head([> ])" "<head-bootleg-munged$1")
      (string/replace #"</head([> ])" "</head-bootleg-munged$1")
      (string/replace #"<body([> ])" "<body-bootleg-munged$1")
      (string/replace #"</body([> ])" "</body-bootleg-munged$1")))

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

(defn- strip-empty-hiccup-attr-hashmaps [hiccup]
  (->> hiccup
       (walk/postwalk
        (fn [form]
          (if (and
               (vector? form)
               (keyword? (first form))
               (= {} (second form)))
            (into [(first form)] (subvec form 2))
            form)))))

(defn- stringify-style-map [style-map]
  (->> style-map
       (map (fn [[k v]] (str (name k) ":" (name v) ";")))
       (apply str)))

(defn- stringify-all-style-maps [hiccup]
  (->> hiccup
       (walk/postwalk
        (fn [form]
          (if (and (vector? form)
                   (keyword (first form))
                   (map? (second form))
                   (map? (:style (second form))))
            (update-in form [1 :style] stringify-style-map)
            form)))))

(defn- strip-nils [hiccup]
  (->> hiccup
       (walk/postwalk
        (fn [form]
          (cond
            (vector? form) (filterv identity form)
            (seq? form) (filter identity form)
            :else form)))))

(defn- strip-empty-forms [hiccup]
  (->> hiccup
       (walk/postwalk
        (fn [form]
          (if (and (or (seq? form) (vector? form))
                   (empty? form))
            nil
            form)))))

(defn- collapse-nils-and-empty-forms [hiccup]
  (loop [forms hiccup]
    (let [new-forms (->> forms
                         strip-empty-forms
                         strip-nils)]
      (if (= new-forms forms)
        forms
        (recur new-forms)))))

(defn- keywordize-all-attrs [hiccup]
  (->> hiccup
       (walk/postwalk
        (fn [form]
          (if (and (vector? form)
                   (keyword (first form))
                   (map? (second form)))
            (update form 1 walk/keywordize-keys)
            form)))))

(defn- remove-attrs-with-nil-values [hiccup]
  (->> hiccup
       (walk/postwalk
        (fn [form]
          (if (and (vector? form)
                   (keyword (first form))
                   (map? (second form)))
            (update form 1
                    (fn [attrs]
                      (->> attrs
                           (filter (comp not nil? second))
                           (into {}))))
            form)))))

(defn- preprocess-hiccup [hiccup]
  (->> hiccup
       keywordize-all-attrs
       remove-attrs-with-nil-values
       stringify-all-style-maps
       collapse-nils-and-empty-forms))

#_ (preprocess-hiccup [:div [:p {:style {:color "red" :margin-top "20px"}} "one"]])
#_ (preprocess-hiccup [[nil nil] [[] nil] []])
#_ (preprocess-hiccup [nil])
#_ (convert-to (preprocess-hiccup [[]]) :html)
#_ (markup-type [[]])

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
;; conversion warning
;;
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

;;
;; hiccup / html
;;
(defn html->hiccup-seq [markup]
  (if (html? markup)
    (if (doctype? markup)
      (let [[doctype markup] (split-doctype markup)]
        (-> (map hickory/as-hiccup (hickory/parse-fragment (munge-html-tags markup)))
            demunge-hiccup-tags
            strip-empty-hiccup-attr-hashmaps
            (conj doctype)))
      (-> (map hickory/as-hiccup (hickory/parse-fragment (munge-html-tags markup)))
          demunge-hiccup-tags
          strip-empty-hiccup-attr-hashmaps))
    (strip-empty-hiccup-attr-hashmaps
     (map hickory/as-hiccup (hickory/parse-fragment markup)))))

(defn html->hiccup [markup]
  (->> markup
       html->hiccup-seq
       (warn-last :html :hiccup)))

(defn hiccup-seq->html [hiccup-seq]
  (str
   (some->> hiccup-seq
            preprocess-hiccup
            ;; hiccup-to-html cant handle numbers
            (walk/postwalk #(if (number? %) (str %) %))
            render/hiccup-to-html)))

(defn hiccup->html [hiccup]
  (hiccup-seq->html [hiccup]))

;;
;; hiccup / hickory
;;
(defn hiccup->hickory [hiccup]
  (if (string? hiccup)
    hiccup
    (-> hiccup
        preprocess-hiccup
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
        demunge-hiccup-tags
        strip-empty-hiccup-attr-hashmaps
        preprocess-hiccup)))

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
  (->> markup
       html->hickory-seq
       (warn-last :html :hickory)))

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
;; xml / hiccup / hickory
;;
(defn xmlparsed->xmlhiccup [tree]
  (if (string? tree)
    tree
    (let [tag (:tag tree)
          attrs (:attrs tree)
          content (:content tree)
          metadata (meta tree)]
      (-> [tag attrs]
          (concat (map xmlparsed->xmlhiccup content))
          (->> (into []))
          (with-meta metadata)))))

(defn- collapse-nested-lists [form]
  (cond
    (string? form)
    form

    (and (vector? form) (keyword? (first form)))
    (->> form
         (mapv #(if (seq? %) % [%]))
         (apply concat)
         (into []))

    :else
    form))

(defn xmlhiccup->xmlparsed [tree]
  (cond
    (string? tree) tree
    (number? tree) (str tree)
    (keyword? tree) (str tree)
    :else (let [metadata (meta tree)
                tree (collapse-nested-lists tree)
                [tag maybe-attrs & remain] tree
                attrs? (map? maybe-attrs)
                attrs (if attrs? maybe-attrs {})
                content (if attrs? remain (concat [maybe-attrs] remain))]
            (-> {:tag tag
                 :attrs attrs
                 :content (map xmlhiccup->xmlparsed content)}
                (with-meta metadata)))))

(defn xml->hickory [markup]
  (-> markup
      java.io.StringReader.
      xml/parse))

(defn xml->hiccup [markup]
  (-> markup
      java.io.StringReader.
      xml/parse
      xmlparsed->xmlhiccup
      strip-empty-hiccup-attr-hashmaps))

(defn hickory->xml [hickory]
  (-> hickory
      xml/emit-str))

(defn hiccup->xml [hiccup]
  (-> hiccup
      xmlhiccup->xmlparsed
      xml/emit-str))

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
    (and (string? data) (xml? data)) :xml
    (string? data) :html
    (every? string? data) :hiccup-seq
    :else :hiccup-seq))

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
   [:hiccup :xml] hiccup->xml

   [:hiccup-seq :hiccup] (partial warn-last :hiccup-seq :hiccup)
   [:hiccup-seq :hickory] (comp (partial warn-last :hiccup-seq :hickory)
                                hiccup-seq->hickory-seq)
   [:hiccup-seq :hickory-seq] hiccup-seq->hickory-seq
   [:hiccup-seq :hiccup-seq] identity
   [:hiccup-seq :html] hiccup-seq->html
   [:hiccup-seq :xml] #(->> %
                            (warn-last :hiccup-seq :xml)
                            hiccup->xml)

   [:hickory :hiccup] hickory->hiccup
   [:hickory :hickory] identity
   [:hickory :hickory-seq] list
   [:hickory :hiccup-seq] (comp list hickory->hiccup)
   [:hickory :html] hickory->html
   [:hickory :xml] hickory->xml

   [:hickory-seq :hiccup] (comp (partial warn-last :hickory-seq :hiccup)
                                hickory-seq->hiccup-seq)
   [:hickory-seq :hickory] (partial warn-last :hickory-seq :hickory)
   [:hickory-seq :hickory-seq] identity
   [:hickory-seq :hiccup-seq] hickory-seq->hiccup-seq
   [:hickory-seq :html] hickory-seq->html
   [:hickory-seq :xml] #(->> %
                             (warn-last :hickory-seq :xml)
                             hickory->xml)

   [:html :hiccup] html->hiccup
   [:html :hickory] html->hickory
   [:html :hickory-seq] html->hickory-seq
   [:html :hiccup-seq] html->hiccup-seq
   [:html :html] identity

   [:xml :xml] identity
   [:xml :hickory] xml->hickory
   [:xml :hickory-seq] (comp list xml->hickory)
   [:xml :hiccup] xml->hiccup
   [:xml :hiccup-seq] (comp list xml->hiccup)
   [:xml :html] identity ;; to support outputing xml to terminal without `-d` flag
   })

(defn convert-to [data to-type]
  (let [from-type (markup-type data)
        converter (conversion-fns [from-type to-type])]
    (converter data)))

(defn html-output-to [flags html]
  (cond
    (:hiccup flags) (convert-to html :hiccup)
    (:hiccup-seq flags) (convert-to html :hiccup-seq)
    (:hickory flags) (convert-to html :hickory)
    (:hickory-seq flags) (convert-to html :hickory-seq)
    (:html flags) html
    (:xml flags) html
    :else (convert-to html :hiccup-seq)))

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

(defmulti slurp-relative type)

(defmethod slurp-relative String [src]
  (-> src
      file/path-relative
      io/input-stream
      slurp))

(defmethod slurp-relative :default [src]
  (slurp src))

#_ (slurp-relative "test/files/simple.md")
#_ (slurp-relative *in*)

(defmulti spit-relative (fn [f data & opts] (type f)))

(defmethod spit-relative String [f data & opts]
  (apply spit (file/path-relative f) data opts))

(defmethod spit-relative :default [f data & opts]
  (apply spit f data opts))

(defmacro embed [filename]
  (slurp filename))

(defn pprint-str [form & [opts]]
  (with-out-str (fipp/pprint form opts)))

(defn current-file []
  (or @sci-vars/current-file ""))

(defn current-file-parent []
  (or (some-> (current-file) io/file .getParent) "."))
