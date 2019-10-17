(ns bootleg.html
  (:require [bootleg.file :as file]
            [bootleg.utils :as utils]))

(defn make-html-fn [path]
  (fn html [source & options]
    (let [flags (into #{} options)
          markup (if (:data flags)
                   source
                   (slurp (file/input-stream path source)))]
      (utils/html-output-to flags markup))))
