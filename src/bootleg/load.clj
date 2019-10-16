(ns bootleg.load
  (:require [bootleg.file :as file]))

(defmulti process-file
  "Takes a path and a filename as a string.
  Returns the loaded object.
  In case of a template, this will return the text of the final markup.
  In case of a data struct, this will return the parsed data struct.
  This is the final format that will be inserted at the var point
  dispatch is based on file extension.
  "
  (fn [path file]
    (file/file-extension file)))

(defmethod process-file :default [path file]
  (slurp (file/path-join path file)))
