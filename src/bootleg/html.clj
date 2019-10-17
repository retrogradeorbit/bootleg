(ns bootleg.html
  (:require [bootleg.file :as file]
            [bootleg.utils :as utils]))

(defn make-html-fn [path]
  (fn [source & options]
    (let [flags (into #{} options)
          markup (if (:data flags)
                   source
                   (slurp (file/input-stream path source)))]
      (utils/coerce-html flags markup))))
