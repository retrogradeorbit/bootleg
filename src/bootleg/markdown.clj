(ns bootleg.markdown
  (:require [bootleg.file :as file]
            [bootleg.utils :as utils]
            [markdown.core :as markdown]
            [clojure.java.io :as io]))

(defn make-markdown-fn [path]
  (fn [source & options]
    (let [flags (into #{} options)
          markup (if (:data flags)
                   (markdown/md-to-html-string source)
                   (let [body (java.io.ByteArrayOutputStream.)]
                     (markdown/md-to-html (io/input-stream (file/path-join path source)) body)
                     (.toString body)))]
      (utils/coerce-html flags markup))))
