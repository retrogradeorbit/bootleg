(ns bootleg.hiccup
  (:require [bootleg.file :as file]
            [bootleg.namespaces :as namespaces]
            [bootleg.context :as context]
            [sci.core :as sci]
            [clojure.walk :as walk]))

(defn load-file* [ctx file]
  (let [s (slurp file)]
    (sci/eval-string s ctx)))

(declare process-hiccup)

(defn realize-all-seqs [form]
  (walk/postwalk
   (fn [f]
     (if (seq? f)
       (doall f)
       f))
   form))

(defn process-hiccup-data [path data]
  (let [ctx {
             :namespaces namespaces/namespaces
             :bindings (assoc namespaces/bindings
                              'hiccup process-hiccup
                              '*command-line-args* (sci/new-dynamic-var '*command-line-args* *command-line-args*)
                              )
             :imports namespaces/imports
             :classes namespaces/classes}]
    (context/with-path path
      (-> data
          (sci/eval-string
           (update ctx
                   :bindings assoc 'load-file
                   #(load-file*
                     ctx
                     (file/path-join path %))))
          realize-all-seqs))))

(defn process-hiccup
  ([file]
   (let [fullpath (file/path-join context/*path* file)
         [path file] (file/path-split fullpath)]
     (process-hiccup path file)))
  ([path file]
   (->> file
        (file/path-join path)
        slurp
        (process-hiccup-data path))))
