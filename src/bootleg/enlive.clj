(ns bootleg.enlive
  (:require [bootleg.file :as file]
            [bootleg.context :as context]
            [net.cgrand.enlive-html]
            [clojure.java.io :as io]
            ))

(defmethod net.cgrand.enlive-html/get-resource String
  [path loader]
  (-> path file/path-relative io/input-stream loader))

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

(defn template [source args & forms]
  (let [[options source & body]
        (#'net.cgrand.enlive-html/pad-unless map? {} (list* source args forms))]
    `(let [opts# (merge (net.cgrand.enlive-html/ns-options (extra-core/find-ns '~(ns-name *ns*)))
                        ~options)
           source# ~source]
       (net.cgrand.enlive-html/register-resource! source#)
       (comp net.cgrand.enlive-html/emit* (net.cgrand.enlive-html/snippet* (net.cgrand.enlive-html/html-resource source# opts#) ~@body)))))

(defn deftemplate [name source args & forms]
  `(def ~name (net.cgrand.enlive-html/template ~source ~args ~@forms)))

(defn snippet* [nodes & body]
  (let [nodesym (gensym "nodes")]
    `(let [~nodesym (map net.cgrand.enlive-html/annotate ~nodes)]
       (fn ~@(do
               (let [res (for [[args & forms] (#'net.cgrand.enlive-html/bodies body)]
                           `(~args
                             (extra-core/doall (net.cgrand.enlive-html/flatmap (net.cgrand.enlive-html/transformation ~@forms) ~nodesym))))]
                 res
                 ))))))

(defn snippet
 "A snippet is a function that returns a seq of nodes."
 [source selector args & forms]
  (let [[options source selector args & forms]
         (#'net.cgrand.enlive-html/pad-unless map? {} (list* source selector args forms))]
    `(let [opts# (merge (net.cgrand.enlive-html/ns-options (extra-core/find-ns '~(ns-name *ns*)))
                   ~options)
           source# ~source]
       (net.cgrand.enlive-html/register-resource! source#)
       (net.cgrand.enlive-html/snippet* (net.cgrand.enlive-html/select (net.cgrand.enlive-html/html-resource source# opts#) ~selector) ~args ~@forms))))

(defn defsnippet
 "Define a named snippet -- equivalent to (def name (snippet source selector args ...))."
 [name source selector args & forms]
 `(def ~name (net.cgrand.enlive-html/snippet ~source ~selector ~args ~@forms)))

(defn transformation
 ([] `identity)
 ([form] form)
 ([form & forms] `(fn [node#] (net.cgrand.enlive-html/at node# ~form ~@forms))))

(defn lockstep-transformation
 [& forms] `(fn [node#] (net.cgrand.enlive-html/at node# :lockstep ~(apply array-map forms))))

(defn clone-for
 [comprehension & forms]
  `(fn [node#]
     (net.cgrand.enlive-html/flatten-nodes-coll (for ~comprehension ((net.cgrand.enlive-html/transformation ~@forms) node#)))))

(defn defsnippets
 [source & specs]
 (let [xml-sym (gensym "xml")]
   `(let [~xml-sym (net.cgrand.enlive-html/html-resource ~source)]
      ~@(for [[name selector args & forms] specs]
               `(def ~name (net.cgrand.enlive-html/snippet ~xml-sym ~selector ~args ~@forms))))))

(defn transform-content [& body]
 `(let [f# (net.cgrand.enlive-html/transformation ~@body)]
    (fn [elt#]
      (assoc elt# :content (net.cgrand.enlive-html/flatmap f# (:content elt#))))))

(defn strict-mode
 "Adds xhtml-transitional DTD to switch browser in 'strict' mode."
 [& forms]
  `(net.cgrand.enlive-html/do-> (net.cgrand.enlive-html/transformation ~@forms) strict-mode*))

(defn let-select
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

(defn sniptest
 "A handy macro for experimenting at the repl"
 [source-string & forms]
  `(net.cgrand.enlive-html/sniptest*
    (net.cgrand.enlive-html/html-snippet ~source-string)
    (net.cgrand.enlive-html/transformation ~@forms)))
