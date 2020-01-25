(ns bootleg.json
  (:require [bootleg.utils :as utils]
            [clojure.data.json :as json]))

(defn json
  [source & options]
  (let [flags (into #{} options)
        data (if (:data flags)
               source
               (utils/slurp-relative source))]
    (json/read-str data :key-fn keyword)))
