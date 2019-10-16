(ns bootleg.hiccup
  (:require [bootleg.file :as file]
            [bootleg.utils :as utils]
            [bootleg.markdown :as markdown]
            [bootleg.yaml :as yaml]
            [bootleg.json :as json]
            [bootleg.edn :as edn]
            [cljstache.core :as moustache]
            [hickory.core :as hickory]
            [hickory.render :as render]
            [hickory.convert :as convert]
            [clojure.walk :as walk]
            [sci.core :as sci]))

(defn load-file* [ctx file]
  (let [s (slurp file)]
    (sci/eval-string s ctx)))

(declare process-hiccup)

(defn process-hiccup-data [path data]
  (let [ctx {:bindings
             {'markdown (markdown/make-markdown-fn path)

              ;;'html #(load-html (file/path-join path %))

              ;; vars files
              'yaml (partial yaml/load-yaml path)
              'json (partial json/load-json path)
              'edn (partial edn/load-edn path)

              ;; plain files
              'slurp #(slurp (file/path-join path %))

              ;; process html
              'moustache #(moustache/render (slurp (file/path-join path %1)) %2)

              ;; conversions
              'hiccup (partial process-hiccup path)

              ;; conversions
              'hiccup->html utils/hiccup->html
              'hiccup-seq->html utils/hiccup-seq->html
              'html->hiccup utils/html->hiccup
              'html->hiccup-seq utils/html->hiccup-seq
              'hickory->html utils/hickory->html
              'html->hickory utils/html->hickory
              'hiccup->hickory utils/hiccup->hickory
              'hickory->hiccup utils/hickory->hiccup

              ;; debug
              'println println
              }}]
    (-> data
        (sci/eval-string
         (update ctx
                 :bindings assoc 'load-file
                 #(load-file*
                   ctx
                   (file/path-join path %))))

        ;; hickory hiccup->html cant handle numbers
        (->> (walk/postwalk #(if (number? %) (str %) %)))
        ;;enlive/html
        ;;enlive/emit*
        ;;(->> (apply str))
        )))

(defn process-hiccup [path file]
  (->> file
       (file/path-join path)
       slurp
       (process-hiccup-data path)))
