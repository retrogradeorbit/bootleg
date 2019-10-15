(ns bootleg.yaml
  (:require [bootleg.file :as file]
            [bootleg.load :as load]
            [yaml.core :as yaml]))

(defn load-yaml [path file]
  (-> (file/path-join path file) yaml/from-file))

(defmethod load/process-file "yml" [path file] (load-yaml path file))
(defmethod load/process-file "yaml" [path file] (load-yaml path file))
