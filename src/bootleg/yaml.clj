(ns bootleg.yaml
  (:require [bootleg.file :as file]
            [bootleg.context :as context]
            [yaml.core :as yaml]))

(defn load-yaml
  ([file] (load-yaml context/*path* file))
  ([path file]
   (-> (file/path-join path file)
       slurp
       (yaml/parse-string :keywords true))))
