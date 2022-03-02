(ns bootleg.markdown
  (:require [bootleg.file :as file]
            [bootleg.utils :as utils]
            [markdown.core :as markdown]
            [clojure.java.io :as io]))

(defn markdown
  "Converts markdown source (file name) to hiccup-seq (default, see options)
  available options:

  :data - treat source as html string instead

  One of:
  :html, :hiccup, :hiccup-seq, :hickory, :hickory-seq

  markdown-clj options:
  :footnotes?
  :reference-links?
  :parse-meta? - will not return meta but can be used to strip it out at top of file"

  [source & options]
  (let [flags (into #{} options)
        input (if (:data flags) source (slurp (file/path-relative source)))
        markup (apply markdown/md-to-html-string input (interleave options (repeat true)))]
    (utils/html-output-to flags markup)))
