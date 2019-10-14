(ns bootleg.file
  (:require [clojure.java.io :as io])
  )

(defn path-split
  "give a full path filename, return a tuple of
  [path basename]

  eg \"blog/posts/1/main.yml\" -> [\"blog/posts/1\" \"main.yml\"]
  "
  [filename]
  (let [file (io/file filename)
        parent (.getParent file)
        name (.getName file)]
    [parent name]))

(defn path-join [& parts]
  (.getPath (apply io/file parts)))
