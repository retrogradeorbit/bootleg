(ns bootleg.edn
  (:require [bootleg.file :as file]
            [bootleg.context :as context]
            [clojure.edn :as edn]))

(defn load-edn
  ([file] (load-edn context/*path* file))
  ([path file]
   (-> (file/path-join path file) slurp edn/read-string)))
