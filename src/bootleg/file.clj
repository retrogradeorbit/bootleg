(ns bootleg.file
  (:require [bootleg.context :as context]
            [clojure.java.io :as io]
            [clojure.string :as string])
  (:import [java.nio.file Paths Files LinkOption]
           [java.nio.file.attribute FileAttribute]))

(defn path-split
  "give a full path filename, return a tuple of
  [path basename]

  eg \"blog/posts/1/main.yml\" -> [\"blog/posts/1\" \"main.yml\"]

  If filename is just a bare filename, path is returned as `nil`
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
  (if (or (string/starts-with? filename "http://")
          (string/starts-with? filename "https://"))
    filename
    (path-join context/*path* filename)))

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

(def empty-string-array
  (make-array String 0))

(def empty-file-attribute-array
  (make-array FileAttribute 0))

(def empty-link-options
  (make-array LinkOption 0))

(def no-follow-links
  (into-array LinkOption [LinkOption/NOFOLLOW_LINKS]))

(defn symlink [link target]
  (let [link-path (Paths/get (path-relative link) empty-string-array)]
    (when (Files/exists link-path no-follow-links)
      ;; link path exists
      (if (Files/isSymbolicLink link-path)
        ;; and its a symlink
        (Files/delete link-path)

        ;; its something else
        (throw (ex-info (str link " already exists and is not a symlink")
                        {:path link}))))

    (.toString
     (Files/createSymbolicLink
      link-path
      (Paths/get target empty-string-array)
      empty-file-attribute-array))))

(defn mkdir [directory]
  (let [path (Paths/get (path-relative directory) empty-string-array)]
    (if (Files/exists path empty-link-options)
      ;; file exists
      (if (Files/isDirectory path empty-link-options)
        ;; directory already exists
        (.toString path)

        ;; exists but isnt a directory
        (throw (ex-info (str directory " already exists and is not a directory")
                        {:path directory})))

      ;; nothing exists. create it
      (.toString
       (Files/createDirectory path empty-file-attribute-array)))))

(defn mkdirs [directory]
  (let [path (Paths/get (path-relative directory) empty-string-array)]
    (if (Files/exists path empty-link-options)
      ;; file exists
      (if (Files/isDirectory path empty-link-options)
        ;; directory already exists
        (.toString path)

        ;; exists but isnt a directory
        (throw (ex-info (str directory " already exists and is not a directory")
                        {:path directory})))

      ;; nothing exists. create it
      (.toString
       (Files/createDirectories path empty-file-attribute-array)))))
