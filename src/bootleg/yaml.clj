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
