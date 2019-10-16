(ns bootleg.hiccup
  (:require [bootleg.file :as file]
            [bootleg.load :as load]
            [bootleg.markdown :as markdown]
            [bootleg.yaml :as yaml]
            [bootleg.json :as json]
            [bootleg.edn :as edn]
            [net.cgrand.enlive-html :as enlive]
            [cljstache.core :as moustache]
            [hickory.core :as hickory]
            [sci.core :as sci]))

(defn load-file* [ctx file]
  (let [s (slurp file)]
    (sci/eval-string s ctx)))

(defn load-hiccup [path file]
  (let [ctx {:bindings
        {'markdown (partial markdown/process-markdown path)
         'yaml (partial yaml/load-yaml path)
         'json (partial json/load-json path)
         'edn (partial edn/load-edn path)
         'slurp #(slurp (file/path-join path %))
         'moustache #(moustache/render (file/path-join path %1) %2)
         'hiccup (partial load-hiccup path)
         'as-hickory hickory/as-hickory
         'as-hiccup hickory/as-hiccup
         'parse hickory/parse
         'parse-fragment hickory/parse-fragment
         }}]
    (-> (file/path-join path file)
        slurp
        (sci/eval-string
         (update ctx :bindings assoc 'load-file
                 #(load-file*
                   ctx
                   (file/path-join path %))))
        enlive/html
        enlive/emit*
        (->> (apply str)))))

(defmethod load/process-file "clj" [path file] (load-hiccup path file))
(defmethod load/process-file "cljs" [path file] (load-hiccup path file))
