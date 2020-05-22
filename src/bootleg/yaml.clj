(ns bootleg.yaml
  (:require [bootleg.utils :as utils]
            [yaml.core :as yaml]))

(defn yaml
  [source & options]
  (let [flags (into #{} options)
        data (if (:data flags)
               source
               (utils/slurp-relative source))]
    (yaml/parse-string data :keywords true)))

(defn yaml*
  [& args]
  (->> (apply yaml args)
       (clojure.walk/postwalk
        (fn [form]
          (if (map? form)
            (into {} form)
            form)))))
