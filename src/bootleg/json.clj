(ns bootleg.json
  (:require [bootleg.file :as file]
            [bootleg.load :as load]
            [clojure.data.json :as json]))

(defn load-json [path file]
  (-> (file/path-join path file)
      slurp
      (json/read-str :key-fn keyword)))

(defmethod load/process-file "json" [path file] (load-json path file))
