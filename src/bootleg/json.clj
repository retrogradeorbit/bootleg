(ns bootleg.json
  (:require [bootleg.file :as file]
            [bootleg.context :as context]
            [clojure.data.json :as json]))

(defn load-json
  ([file] (load-json context/*path* file))
  ([path file]
   (-> (file/path-join path file)
       slurp
       (json/read-str :key-fn keyword))))
