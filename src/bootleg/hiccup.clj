(ns bootleg.hiccup
  (:require [bootleg.file :as file]
            [bootleg.utils :as utils]
            [bootleg.markdown :as markdown]
            [bootleg.mustache :as mustache]
            [bootleg.html :as html]
            [bootleg.yaml :as yaml]
            [bootleg.json :as json]
            [bootleg.edn :as edn]
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
              'slurp #(slurp (file/path-join path %))
              'html (html/make-html-fn path)
              'hiccup (partial process-hiccup path)

              ;; vars files
              'yaml (partial yaml/load-yaml path)
              'json (partial json/load-json path)
              'edn (partial edn/load-edn path)

              ;; conversions
              'hiccup->html utils/hiccup->html
              'hiccup->hickory utils/hiccup->hickory
              'hiccup-seq->html utils/hiccup-seq->html
              'hiccup-seq->hickory utils/hiccup-seq->hickory

              'html->hiccup utils/html->hiccup
              'html->hiccup-seq utils/html->hiccup-seq
              'html->hickory utils/html->hickory

              'hickory->html utils/hickory->html
              'hickory->hiccup utils/hickory->hiccup

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
