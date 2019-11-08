(ns bootleg.mustache
  (:require [bootleg.file :as file]
            [bootleg.utils :as utils]
            [cljstache.core :as mustache]
            [clojure.java.io :as io]))

(defn mustache [source vars & options]
  (let [flags (into #{} options)
        pre-markup (if (:data flags)
                     source
                     (slurp (io/input-stream (file/path-relative source))))
        markup (mustache/render pre-markup vars)]
    (utils/html-output-to flags markup)))
