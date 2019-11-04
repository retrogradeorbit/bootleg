(ns bootleg.file
  (:require [bootleg.context :as context]
            [clojure.java.io :as io]
            [clojure.string :as string])
  (:import [java.nio.file Paths]))

(defn path-split
  "give a full path filename, return a tuple of
  [path basename]

  eg \"blog/posts/1/main.yml\" -> [\"blog/posts/1\" \"main.yml\"]
  "
  [filename]
  (let [file (io/file filename)]
    [(.getParent file) (.getName file)]))

(defn path-join
  "given multiple file path parts, join them all together with the
  file separator"
  [& parts]
  (.getPath (apply io/file parts)))

(defn file-extension [file]
  (->  file
       (string/split #"\.")
       last))

(defn file-input-stream [path file]
  (io/input-stream (path-join path file)))

(defn url-input-stream [url]
  (io/input-stream (io/as-url url)))

(defmulti input-stream
  (fn [_ url-or-file]
    ;; dispatch on schema
    (when-let [[_ schema] (re-find #"(\S+):\/\/" url-or-file)]
      schema)))

(defmethod input-stream :default [path file] (file-input-stream path file))
(defmethod input-stream "http" [_ url] (url-input-stream url))
(defmethod input-stream "https" [_ url] (url-input-stream url))

(defn path-relative [filename]
  (path-join context/*path* filename))

(defn relativise
  "return the relative path that gets you from a working directory
  `source` to the file or directory `target`"
  [source target]
  (let [source-path (Paths/get (.toURI (io/as-file (.getCanonicalPath (io/as-file source)))))
        target-path (Paths/get (.toURI (io/as-file (.getCanonicalPath (io/as-file target)))))]
    (-> source-path
        (.relativize target-path)
        .toFile
        .getPath)))
