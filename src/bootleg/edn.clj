(ns bootleg.edn
  (:require [bootleg.file :as file]
            [bootleg.load :as load]
            [clojure.edn :as edn]))

(defn load-edn [path file]
  (-> (file/path-join path file) edn/read-string))

(defmethod load/process-file "eden" [path file] (load-edn path file))
(defmethod load/process-file "edn" [path file] (load-edn path file))
