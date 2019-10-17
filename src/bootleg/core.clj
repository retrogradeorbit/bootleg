(ns bootleg.core
  (:require [bootleg.file :as file]
            [bootleg.utils :as utils]
            [bootleg.hiccup :as hiccup]
            [clojure.tools.cli :refer [parse-opts]]
            [clojure.string :as string]
            [fipp.edn :refer [pprint]]
            )
  (:gen-class))

(def version "0.1.1")

(def cli-options
  [
   ["-h" "--help" "Print the command line help"]
   ["-v" "--version" "Print the version string and exit"]
   ["-e" "--evaluate CODE" "Pass in the hiccup to evaluate on the command line"]
   ["-d" "--data" "Output the rendered template as a clojure form instead of html"]
   ])

(defn usage [options-summary]
  (->> ["Static site generator"
        ""
        "Usage: bootleg [options] [clj-file]"
        ""
        "Options:"
        options-summary]
       (string/join \newline)))

(defn process [filename]
  (let [[path file] (file/path-split filename)]
    (hiccup/process-hiccup path file)))

(defn output-result [options result]
  (if (:data options)
    (-> result pprint)
    (-> result utils/as-html println)))

(defn -main
  "main entry point for site generation"
  [& args]
  (let [{:keys [options summary arguments]} (parse-opts args cli-options)]
    (cond
      (:help options)
      (println (usage summary))

      (:version options)
      (println "Version:" version)

      (:evaluate options)
      (let [result (->> options :evaluate (hiccup/process-hiccup-data "."))]
        (output-result options result))

      (= 1 (count arguments))
      (let [result (-> arguments first process)]
        (output-result options result))

      :else
      (println (usage summary)))))
