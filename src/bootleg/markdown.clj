(ns bootleg.markdown
  (:require [bootleg.file :as file]
            [bootleg.utils :as utils]
            [markdown.core :as markdown]))

(defn make-markdown-fn [path]
  (fn markdown [source & options]
    (let [flags (into #{} options)
          markup (if (:data flags)
                   (markdown/md-to-html-string source)
                   (let [body (java.io.ByteArrayOutputStream.)]
                     (markdown/md-to-html (file/input-stream path source) body)
                     (.toString body)))]
      (utils/coerce-html flags markup))))
