(ns bootleg.markdown
  (:require [bootleg.file :as file]
            [bootleg.utils :as utils]
            [markdown.core :as markdown]
            [clojure.java.io :as io]))

(defn markdown [source & options]
  (let [flags (into #{} options)
        markup (if (:data flags)
                 (markdown/md-to-html-string source)
                 (let [body (java.io.ByteArrayOutputStream.)]
                   (markdown/md-to-html (io/input-stream (file/path-relative source)) body)
                   (.toString body)))]
    (utils/html-output-to flags markup)))
