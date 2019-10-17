(ns bootleg.hiccup
  (:require [bootleg.file :as file]
            [bootleg.utils :as utils]
            [bootleg.markdown :as markdown]
            [bootleg.mustache :as mustache]
            [bootleg.html :as html]
            [bootleg.yaml :as yaml]
            [bootleg.json :as json]
            [bootleg.edn :as edn]
            [net.cgrand.enlive-html :as enlive]
            [clojure.walk :as walk]
            [sci.core :as sci]
            [fipp.edn :refer [pprint]]))

(defn load-file* [ctx file]
  (let [s (slurp file)]
    (sci/eval-string s ctx)))

(declare process-hiccup)

(defn process-hiccup-data [path data]
  (let [ctx {:bindings
             {
              ;; file loading
              'markdown (markdown/make-markdown-fn path)
              'mustache (mustache/make-mustache-fn path)
              'slurp #(slurp (file/input-stream path %))
              'html (html/make-html-fn path)
              'hiccup (partial process-hiccup path)

              ;; vars files
              'yaml (partial yaml/load-yaml path)
              'json (partial json/load-json path)
              'edn (partial edn/load-edn path)

              ;; testing
              'is-hiccup? utils/is-hiccup?
              'is-hiccup-seq? utils/is-hiccup-seq?
              'is-hickory? utils/is-hickory?
              'is-hickory-seq? utils/is-hickory-seq?

              ;; conversions
              'convert-to utils/convert-to
              'as-html utils/as-html

              ;; enlive
              'at (fn at [node-or-nodes & selector-transform-pairs]
                    (let [input-type (utils/markup-type node-or-nodes)
                          converted-nodes (utils/convert-to node-or-nodes :hickory-seq)]
                      (->
                       (reduce
                        (fn [node [selector transform]]
                          (enlive/at node selector transform))
                        converted-nodes
                        (partition 2 selector-transform-pairs))

                       (utils/convert-to input-type))))
              'content enlive/content
              'html-content enlive/html-content
              'wrap enlive/wrap
              'unwrap enlive/unwrap
              'set-attr enlive/set-attr
              'remove-attr enlive/remove-attr
              'add-class enlive/add-class
              'remove-class enlive/remove-class
              'do-> enlive/do->
              'append enlive/append
              'prepend enlive/prepend
              'after enlive/after
              'before enlive/before
              'substitute enlive/substitute
              'move enlive/move

              ;; macro
              ;;'clone-for enlive/clone-for

              ;; debug
              'println println
              'pprint pprint
              }}]
    (-> data
        (sci/eval-string
         (update ctx
                 :bindings assoc 'load-file
                 #(load-file*
                   ctx
                   (file/path-join path %))))

        ;; hickory hiccup->html cant handle numbers
        (->> (walk/postwalk #(if (number? %) (str %) %))))))

(defn process-hiccup [path file]
  (->> file
       (file/path-join path)
       slurp
       (process-hiccup-data path)))
