(ns bootleg.hiccup
  (:require [bootleg.file :as file]
            [bootleg.namespaces :as namespaces]
            [bootleg.context :as context]
            [bootleg.eval :as eval]
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

(defn process-hiccup-data [args path data]
  (context/with-path path
    (-> (eval/evaluate
         args data
         (assoc namespaces/bindings
                process-hiccup (partial process-hiccup args)
                '*command-line-args* (sci/new-dynamic-var '*command-line-args* *command-line-args*)))
        realize-all-seqs)))

(defn process-hiccup
  ([args file]
   (let [fullpath (file/path-join context/*path* file)
         [path file] (file/path-split fullpath)]
     (process-hiccup args path file)))
  ([args path file]
   (->> file
        (file/path-join path)
        slurp
        (process-hiccup-data args path))))
