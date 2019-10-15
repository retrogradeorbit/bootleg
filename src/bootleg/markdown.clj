(ns bootleg.markdown
  (:require [bootleg.file :as file]
            [bootleg.load :as load]
            [markdown.core :as markdown]
            [clojure.java.io :as io]))

(defn process-markdown [path file]
  (let [body (java.io.ByteArrayOutputStream.)]
    (markdown/md-to-html (io/input-stream (file/path-join path file)) body)
    (.toString body)))

(defmethod load/process-file "markdown" [path file] (process-markdown path file))
(defmethod load/process-file "mdown" [path file] (process-markdown path file))
(defmethod load/process-file "mkdn" [path file] (process-markdown path file))
(defmethod load/process-file "mkd" [path file] (process-markdown path file))
(defmethod load/process-file "md" [path file] (process-markdown path file))
