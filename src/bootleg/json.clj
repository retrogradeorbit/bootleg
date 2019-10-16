(ns bootleg.json
  (:require [bootleg.file :as file]
            [clojure.data.json :as json]))

(defn load-json [path file]
  (-> (file/path-join path file)
      slurp
      (json/read-str :key-fn keyword)))
