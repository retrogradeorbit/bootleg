(ns bootleg.core
  (:require [bootleg.transform :as transform]
            [bootleg.file :as file]

            ;; loaders
            [bootleg.load :as load]
            [bootleg.markdown]
            [bootleg.json]
            [bootleg.yaml]

            [yaml.core :as yaml]
            [cljstache.core :as moustache]
            [net.cgrand.enlive-html :as enlive]
            [clojure.tools.cli :refer [parse-opts]]
            [clojure.walk :as walk]
            [clojure.string :as string])
  (:gen-class))

(def version "0.1.1")

(def cli-options
  [
   ["-h" "--help" "Print the command line help"]
   ["-v" "--version" "Print the version string and exit"]])

(defn usage [options-summary]
  (->> ["Static site generator"
        ""
        "Usage: bootleg [options] yaml-file"
        ""
        "Options:"
        options-summary]
       (string/join \newline)))

(defn load-and-process [{:keys [path load process vars] :as context}]
  (let [content (load/process-file path load)]
    (if (string? content)
      (->
       (if process
         (->
          (reduce
           (fn [acc {:keys [selector] :as step}]
             (let [selector-vec (into [] (map keyword (string/split selector #"\s+")))]
               (enlive/transform acc selector-vec (transform/make-transform context step))))
           (enlive/as-nodes
            (-> content
                java.io.StringReader.
                enlive/html-resource))
           process)

          enlive/emit*
          (->> (apply str)))

         content)
       (moustache/render vars))
      content)))

(defn process-yaml-result [data path]
  (walk/postwalk
   #(if (:load %)
      (load-and-process (assoc % :path path))
      %)
   data))

(defn process [filename]
  (let [[path _] (file/path-split filename)]
    (-> filename
        yaml/from-file
        (process-yaml-result path))))

(defn -main
  "main entry point for site generation"
  [& args]
  (let [{:keys [options summary arguments]} (parse-opts args cli-options)]
    (cond
      (:help options)
      (println (usage summary))

      (:version options)
      (println "Version:" version)

      (= 1 (count arguments))
      (-> arguments first process println)

      :else
      (println (usage summary)))))
