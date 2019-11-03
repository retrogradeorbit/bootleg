(ns bootleg.html
  (:require [bootleg.file :as file]
            [bootleg.utils :as utils]
            [clojure.java.io :as io]))

(defn html [source & options]
  (let [flags (into #{} options)
        markup (if (:data flags)
                 source
                 (slurp (io/input-stream (file/path-relative source))))]
    (utils/html-output-to flags markup)))
