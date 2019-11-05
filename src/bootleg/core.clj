(ns bootleg.core
  (:require [bootleg.file :as file]
            [bootleg.utils :as utils]
            [bootleg.hiccup :as hiccup]
            [bootleg.config :as config]
            [bootleg.context :as context]
            [clojure.tools.cli :refer [parse-opts]]
            [clojure.string :as string]
            [clojure.java.io :as io])
  (:gen-class))

(def version "0.1.5-SNAPSHOT")

(def cli-options
  [
   ["-h" "--help" "Print the command line help"]
   ["-v" "--version" "Print the version string and exit"]
   ["-e" "--evaluate CODE" "Pass in the hiccup to evaluate on the command line"]
   ["-d" "--data" "Output the rendered template as a clojure form instead of html"]
   ["-o" "--output FILE" "Write the output to the specified file instead of stdout"]
   ["-t" "--traceback" "Print the full exception traceback"]
   ["-c" "--colour" "Print outputs in colour where appropriate"]
   [nil  "--color" "Alias for --colour"]
   ])

(defn usage [options-summary]
  (->> ["Static website generation made simple. A powerful, fast, clojure html templating solution."
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
  (let [out (if (:output options)
              (io/writer (:output options))
              *out*)]
    (try
      (if (:data options)
        (-> result (utils/pprint {:writer out}))
        (->> result utils/as-html (.write out)))
      (finally
        (if (:output options)
          (.close out)
          (.flush out))))))

(defn -main
  "main entry point for site generation"
  [& args]
  (config/init!)
  (let [{:keys [options summary arguments]} (parse-opts args cli-options)]
    (context/with-colour (or (:colour options) (:color options))
      (try
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
          (println (usage summary)))
        (catch java.io.FileNotFoundException e
          (if (:traceback options)
            (throw e)
            (binding [*out* *err*]
              (println
               (str (utils/colour :red)
                    "bootleg: " (.getMessage e)
                    (utils/colour)))))
          (System/exit 1))
        (catch clojure.lang.ExceptionInfo e
          (if (:traceback options)
            (throw e)
            (let [{:keys [type] :as data} (ex-data e)]
              (if (= type :sci/error)
                (let [{:keys [row col]} (ex-data e)
                      message (.getMessage (.getCause e))]
                  (binding [*out* *err*]
                    (println
                     (str
                      (utils/colour :red)
                      "bootleg: script error at line " row ", column " col ": " message
                      (utils/colour)))))
                (throw e))))
          (System/exit 2))))))
