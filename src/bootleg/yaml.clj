(ns bootleg.yaml
  (:require [bootleg.file :as file]
            [yaml.core :as yaml]))

(defn load-yaml [path file]
  (-> (file/path-join path file) yaml/from-file))
