(ns bootleg.edn
  (:require [bootleg.file :as file]
            [bootleg.utils :as utils]
            [bootleg.context :as context]
            [clojure.edn :as edn]))

(defn edn [source & options]
  (let [flags (into #{} options)
        data (if (:data flags)
               source
               (utils/slurp-relative source))]
    (edn/read-string data)))
