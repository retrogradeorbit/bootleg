(ns bootleg.mustache
  (:require [bootleg.file :as file]
            [bootleg.utils :as utils]
            [cljstache.core :as mustache]))

(defn make-mustache-fn [path]
  (fn [source vars & options]
    (let [flags (into #{} options)
          pre-markup (if (:data flags)
                       source
                       (slurp (file/input-stream path source)))
          markup (mustache/render pre-markup vars)]
      (utils/coerce-html flags markup))))
