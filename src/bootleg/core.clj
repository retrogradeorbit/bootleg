(ns bootleg.core
  (:require [bootleg.file :as file]
            [bootleg.utils :as utils]
            [bootleg.hiccup :as hiccup]
            [bootleg.minify :as minify]
            [bootleg.config :as config]
            [bootleg.context :as context]
            [bootleg.pod :as pod]
            [clojure.tools.cli :refer [parse-opts]]
            [clojure.string :as string]
            [clojure.java.io :as io])
  (:gen-class))

(def version (utils/embed ".meta/VERSION"))

(def cli-options
  [
   ["-h" "--help" "Print the command line help"]
   ["-v" "--version" "Print the version string and exit"]
   ["-e" "--evaluate CODE" "Pass in the hiccup to evaluate on the command line"]
   ["-d" "--data" "Output the rendered template as a clojure form instead of html"]
   ["-m" "--minify" "When outputing html minify html, css <style> blocks and js <script> blocks"]
   [nil  "--minify-html" "minify html in the output"]
   [nil  "--minify-css" "minify css contained within <style> tags"]
   [nil  "--minify-js" "minify javascript contained within <script> tags"]
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

(defn process [args filename]
  (let [[path file] (file/path-split filename)]
    (hiccup/process-hiccup args (or path ".") file)))

(defn output-result [options result]
  (let [out (if (:output options)
              (io/writer (:output options))
              *out*)]
    (try
      (if (:data options)
        (-> result (utils/pprint {:writer out}))
        (let [html (utils/as-html result)
              {:keys [minify minify-html minify-css minify-js]} options
              ]
          (.write
           out
           (cond
             minify
             (minify/compress-html
              html
              {
               :compress-css true
               :compress-javascript true
               :javascript-compressor :closure
               :javascript-compressor-options {:level :simple}
               :remove-comments true
               :remove-multiple-spaces true
               }
              )

             (or minify-html minify-js minify-css)
             (minify/compress-html
              html
              {
               :compress-css minify-css
               :compress-javascript minify-js
               :javascript-compressor :closure
               :javascript-compressor-options (if minify-js {:level :simple} {})
               :remove-comments minify-html
               :remove-multiple-spaces minify-html
               })

             :else
             html)
           )))
      (finally
        (if (:output options)
          (.close out)
          (.flush out))))))

(defn -main
  "main entry point for site generation"
  [& args]
  (config/init!)
  (binding [*command-line-args* args]
    (if (System/getenv "BABASHKA_POD")
      (pod/main)
      (let [{:keys [options summary arguments]} (parse-opts args cli-options)]
        (context/with-colour (or (:colour options) (:color options))
          (try
            (cond
              (:help options)
              (println (usage summary))

              (:version options)
              (println "Version:" version)

              (:evaluate options)
              (let [result (->> options :evaluate (hiccup/process-hiccup-data args "."))]
                (output-result options result))

              (= 1 (count arguments))
              (let [result (->> arguments first (process args))]
                (output-result options result))

              (zero? (count arguments))
              (let [result (->> *in* slurp (hiccup/process-hiccup-data args "."))]
                (output-result options result))

              :else
              (println (usage summary)))
            (catch java.io.FileNotFoundException e
              (if (:traceback options)
                (throw e)
                (binding [*out* *err*]
                  (println
                   (str (utils/colour :red)
                        "bootleg: File not found: " (.getMessage e)
                        (utils/colour)))))
              (System/exit 1))
            (catch clojure.lang.ExceptionInfo e
              (if (:traceback options)
                (throw e)
                (let [{:keys [type] :as data} (ex-data e)]
                  (if (#{:sci/error :edamame/error} type)
                    (let [{:keys [row col]} (ex-data e)
                          message (.getMessage e)
                          cause (.getCause e)
                          cause-message (when cause (.getMessage cause))
                          nice-name (utils/exception-nice-name cause)]
                      (binding [*out* *err*]
                        (println
                         (str
                          (utils/colour :red)
                          "bootleg: script error at line " row ", column " col ": "
                          (if cause
                            (str nice-name ": " cause-message)
                            message)
                          (utils/colour)))))
                    (throw e))))
              (System/exit 2))))))))
