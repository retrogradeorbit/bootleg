(ns bootleg.enlive
  (:require [net.cgrand.enlive-html]))

(defn at [node-or-nodes & rules]
  `(-> ~node-or-nodes net.cgrand.enlive-html/as-nodes
       ~@(for [[s t] (partition 2 rules)]
           (if (= :lockstep s)
             `(net.cgrand.enlive-html/lockstep-transform
               ~(into {} (for [[s t] t]
                           [(if (#'net.cgrand.enlive-html/static-selector? s) (net.cgrand.enlive-html/cacheable s) s) t])))
             `(net.cgrand.enlive-html/transform ~(if (#'net.cgrand.enlive-html/static-selector? s) (net.cgrand.enlive-html/cacheable s) s) ~t)))))

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
       (fn ~@(for [[args & forms] (#'net.cgrand.enlive-html/bodies body)]
           `(~args
              (doall (net.cgrand.enlive-html/flatmap (net.cgrand.enlive-html/transformation ~@forms) ~nodesym))))))))
