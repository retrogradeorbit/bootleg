(ns bootleg.hiccup
  (:require [bootleg.file :as file]
            [bootleg.utils :as utils]
            [bootleg.markdown :as markdown]
            [bootleg.mustache :as mustache]
            [bootleg.selmer :as selmer]
            [bootleg.html :as html]
            [bootleg.yaml :as yaml]
            [bootleg.json :as json]
            [bootleg.edn :as edn]
            [bootleg.namespaces :as namespaces]
            [bootleg.context :as context]
            [bootleg.glob :as glob]
            [sci.core :as sci]
            [fipp.edn :refer [pprint]]))

(defn load-file* [ctx file]
  (let [s (slurp file)]
    (sci/eval-string s ctx)))

(declare process-hiccup)

(defn process-hiccup-data [path data]
  (let [ctx {
             :namespaces namespaces/namespaces
             :bindings
             {
              ;; file loading
              'markdown (markdown/make-markdown-fn path)
              'mustache (mustache/make-mustache-fn path)
              'slurp #(slurp (file/input-stream path %))
              'html (html/make-html-fn path)
              'hiccup (partial process-hiccup path)
              'selmer selmer/selmer

              ;; vars files
              'yaml (partial yaml/load-yaml path)
              'json (partial json/load-json path)
              'edn (partial edn/load-edn path)

              ;; directories and filenames
              'glob (partial glob/glob path)

              ;; testing
              'is-hiccup? utils/is-hiccup?
              'is-hiccup-seq? utils/is-hiccup-seq?
              'is-hickory? utils/is-hickory?
              'is-hickory-seq? utils/is-hickory-seq?

              ;; conversions
              'convert-to utils/convert-to
              'markup-type utils/markup-type
              'as-html utils/as-html

              ;; debug
              'println println
              'pprint pprint
              }}]
    (context/with-path path
      (-> data
          (sci/eval-string
           (update ctx
                   :bindings assoc 'load-file
                   #(load-file*
                     ctx
                     (file/path-join path %))))))))

(defn process-hiccup [path file]
  (->> file
       (file/path-join path)
       slurp
       (process-hiccup-data path)))
