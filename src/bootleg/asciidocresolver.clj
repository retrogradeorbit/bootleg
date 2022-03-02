(ns bootleg.asciidocresolver
  (:import [java.nio.file Paths])
  (:gen-class
   :name bootleg.asciidocresolver.Resolver
   :methods [
             [read [String] String]
             [pwd [] String]]))

(defn -read [^String path]
  (let [f (.toFile (Paths/get path))]
    (if (.exists f)
      (slurp f)
      "")))

(defn -pwd [this]
  (-> (Paths/get "" (make-array String 0))
      (.toAbsolutePath)
      (.toString)))
