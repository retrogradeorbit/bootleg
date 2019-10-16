(ns bootleg.edn
  (:require [bootleg.file :as file]
            [clojure.edn :as edn]))

(defn load-edn [path file]
  (-> (file/path-join path file) slurp edn/read-string))
