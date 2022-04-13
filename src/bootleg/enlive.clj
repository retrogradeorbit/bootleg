(ns bootleg.enlive
  (:require [bootleg.file :as file]
            [bootleg.context :as context]
            [net.cgrand.enlive-html]
            [clojure.java.io :as io]
            ))

;; implement our own resource getter that abides by path
(defmethod net.cgrand.enlive-html/get-resource String
  [path loader]
  (-> path file/path-relative io/input-stream loader))

(def at* net.cgrand.enlive-html/at*)

(defmacro at [node-or-nodes & rules]
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

(defmacro template [source args & forms]
  (let [[options source & body]
        (#'net.cgrand.enlive-html/pad-unless map? {} (list* source args forms))]
    `(let [opts# (merge (net.cgrand.enlive-html/ns-options (clojure.core/find-ns '~(ns-name *ns*)))
                        ~options)
           source# ~source]
       (net.cgrand.enlive-html/register-resource! source#)
       (comp #(bootleg.utils/convert-to % :hiccup-seq)
             bootleg.utils/hickory-seq-convert-dtd
             (net.cgrand.enlive-html/snippet*
              (net.cgrand.enlive-html/html-resource source# opts#)
              ~@body)))))

(defmacro deftemplate [name source args & forms]
  `(def ~name (net.cgrand.enlive-html/template ~source ~args ~@forms)))

(defmacro snippet* [nodes & body]
  (let [nodesym (gensym "nodes")]
    `(let [~nodesym (map net.cgrand.enlive-html/annotate ~nodes)]
       (fn ~@(for [[args & forms] (#'net.cgrand.enlive-html/bodies body)]
               `(~args
                 (clojure.core/doall (net.cgrand.enlive-html/flatmap (net.cgrand.enlive-html/transformation ~@forms) ~nodesym))))))))

(defmacro snippet
 "A snippet is a function that returns a seq of nodes."
 [source selector args & forms]
  (let [[options source selector args & forms]
         (#'net.cgrand.enlive-html/pad-unless map? {} (list* source selector args forms))]
    `(let [opts# (merge (net.cgrand.enlive-html/ns-options (clojure.core/find-ns '~(ns-name *ns*)))
                   ~options)
           source# ~source]
       (net.cgrand.enlive-html/register-resource! source#)
       (net.cgrand.enlive-html/snippet*
        (net.cgrand.enlive-html/select
         (net.cgrand.enlive-html/html-resource source# opts#)
         ~selector)
        ~args ~@forms))))

(defmacro defsnippet
 "Define a named snippet -- equivalent to (def name (snippet source selector args ...))."
 [name source selector args & forms]
 `(def ~name (net.cgrand.enlive-html/snippet ~source ~selector ~args ~@forms)))

(defmacro transformation
  ([] `identity)
  ([form] form)
  ([form & forms] `(fn [node#] (net.cgrand.enlive-html/at node# ~form ~@forms))))

(defmacro lockstep-transformation
 [& forms] `(fn [node#] (net.cgrand.enlive-html/at node# :lockstep ~(apply array-map forms))))

(defmacro clone-for
 [comprehension & forms]
  `(fn [node#]
     (net.cgrand.enlive-html/flatten-nodes-coll (for ~comprehension ((net.cgrand.enlive-html/transformation ~@forms) node#)))))

(defmacro defsnippets
 [source & specs]
 (let [xml-sym (gensym "xml")]
   `(let [~xml-sym (net.cgrand.enlive-html/html-resource ~source)]
      ~@(for [[name selector args & forms] specs]
               `(def ~name (net.cgrand.enlive-html/snippet ~xml-sym ~selector ~args ~@forms))))))

(defmacro transform-content [& body]
 `(let [f# (net.cgrand.enlive-html/transformation ~@body)]
    (fn [elt#]
      (assoc elt# :content (net.cgrand.enlive-html/flatmap f# (:content elt#))))))

(defmacro strict-mode
 "Adds xhtml-transitional DTD to switch browser in 'strict' mode."
 [& forms]
  `(net.cgrand.enlive-html/do-> (net.cgrand.enlive-html/transformation ~@forms) strict-mode*))

(defmacro let-select
 "For each node or fragment, performs a subselect and bind it to a local,
  then evaluates body.
  bindings is a vector of binding forms and selectors."
 [nodes-or-fragments bindings & body]
  (let [node-or-fragment (gensym "node-or-fragment__")
        bindings
          (map (fn [f x] (f x))
            (cycle [identity (fn [spec] `(net.cgrand.enlive-html/select ~node-or-fragment ~spec))])
            bindings)]
    `(map (fn [~node-or-fragment]
            (let [~@bindings]
              ~@body)) ~nodes-or-fragments)))

(defmacro sniptest
 "A handy macro for experimenting at the repl"
 [source-string & forms]
  `(net.cgrand.enlive-html/sniptest*
    (net.cgrand.enlive-html/html-snippet ~source-string)
    (net.cgrand.enlive-html/transformation ~@forms)))


;; coercing transformations
(defn content
  "Replaces the content of the element. Values can be nodes or collection of nodes."
  [& values]
  (fn [el]
    (assoc el :content (apply concat (map #(bootleg.utils/convert-to % :hickory-seq) values)))))

(defn append
  "Appends the values to the content of the selected element."
  [& values]
  (fn [el]
    (assoc el :content (concat (:content el) (map #(bootleg.utils/convert-to % :hickory) values)))))

(defn prepend
  "Prepends the values to the content of the selected element."
  [& values]
  (fn [el]
    (assoc el :content (concat (map #(bootleg.utils/convert-to % :hickory) values) (:content el)))))

(defn after
  "Inserts the values after the current selection (node or fragment)."
  [& values]
  (fn [el]
    (net.cgrand.enlive-html/flatten-nodes-coll
     (cons el (map #(bootleg.utils/convert-to % :hickory-seq) values)))))

(defn before
  "Inserts the values before the current selection (node or fragment)."
  [& values]
  (fn [el]
    (net.cgrand.enlive-html/flatten-nodes-coll
     (concat (map #(bootleg.utils/convert-to % :hickory-seq) values) [el]))))

(defn substitute
  "Replaces the current selection (node or fragment)."
  [& values]
  (constantly
   (net.cgrand.enlive-html/flatten-nodes-coll
    (map #(bootleg.utils/convert-to % :hickory-seq) values))))
