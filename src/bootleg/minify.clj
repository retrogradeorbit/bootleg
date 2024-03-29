(ns bootleg.minify
  (:require [clojure.string :as string])
  (:import ;;com.yahoo.platform.yui.compressor.CssCompressor
   [java.util.logging Level]
   [com.googlecode.htmlcompressor.compressor
    HtmlCompressor
    ClosureJavaScriptCompressor
    YuiJavaScriptCompressor]
   [com.google.javascript.jscomp
    CompilationLevel
    CompilerOptions
    SourceFile
    CompilerOptions$LanguageMode
    CommandLineRunner
    WarningLevel]
   [java.nio.charset Charset]))


(defn make-externs-source-files [{:keys [default files content] :as externs}]
  (let [file-set (->> (for [file files]
                       (if (string? file)
                         ;; string file definition
                         (if (string/ends-with? file ".zip")
                           (SourceFile/fromZipFile file (Charset/defaultCharset))
                           [(SourceFile/fromFile file (Charset/defaultCharset))])

                         ;; hashmap file definition
                         (if (string/ends-with? (:filename file) ".zip")
                           (SourceFile/fromZipFile
                            (:filename file)
                            (if (:encoding file)
                              (Charset/forName (:encoding file))
                              (Charset/defaultCharset)))
                           [(SourceFile/fromFile
                             (:filename file)
                             (if (:encoding file)
                               (Charset/forName (:encoding file))
                               (Charset/defaultCharset)))])))
                     (apply concat))
        content-set (for [[n data] (map vector (range) content)]
                      (SourceFile/fromCode
                       (str "inline-extern-content-" n ".js")
                       data))
        default-zip (if default
                      [(SourceFile/fromInputStream
                        "default-externs.js"
                        (clojure.java.io/input-stream (clojure.java.io/resource "default-externs.js"))
                        )]
                      [])]
    (into [] (concat file-set content-set default-zip))))

(defn html-compressor
  [{:keys [
           ;; compress css contained inside <style> tags
           compress-css

           ;; compress javascript inside <script> tags
           compress-javascript

           ;; js compression method to use. set to :yui or :closure
           javascript-compressor

           ;; the options for the javascript compressor chosen.
           ;; for :yui, hashmap with keys:
           ;;  :disable-optimizations
           ;;  :line-break
           ;;  :no-munge
           ;;  :preserve-all-semicolons
           ;;
           ;; for :closure
           ;;  :level
           ;;  :externs
           javascript-compressor-options

           ;; preserve line breaks
           preserve-line-breaks

           ;; pass a sequence of regexps to preserve in output
           preserve-patterns

           ;; remove all  html comments
           remove-comments

           ;; remove method="get" attributes from <form>
           remove-form-attributes

           ;; remove http: from href, src, cite and action attributes. Makes
           ;; the browser use the document's current protocol.
           remove-http-protocol

           ;; remove https: from href, src, cite and action attributes. Makes
           ;; the browser use the document's current protocol.
           remove-https-protocol

           ;; type="text" attributes will be removed from <input>
           remove-input-attributes

           ;; all inter-tag whitespace characters will be removed
           remove-intertag-spaces

           ;; javascript: pseudo-protocol will be removed from inline event handlers
           remove-javascript-protocol

           ;; the attributes type="text/css" and type="text/plain" will be removed
           ;; from <link rel="stylesheet"> and <link rel="alternate stylesheet"> tags.
           remove-link-attributes

           ;; all multiple whitespace characters will be replaced with single spaces.
           remove-multi-spaces

           ;; all unnecessary quotes will be removed from tag attributes.
           remove-quotes

           ;; remove type="text/javascript", type="application/javascript" and
           ;; language="javascript" attributes will be removed from <script> tags.
           remove-script-attributes

           ;; type="text/style" attributes will be removed from <style> tags
           remove-style-attributes

           ;; provided a list of tags, removes surrounding white space just from
           ;; within those listed tags. Pass in a sequence of keywords or strings.
           ;; eg ["p" "br"] or [:p :br]
           remove-surrounding-spaces

           ;; any values of following boolean attributes names will be removed:
           ;; checked, selected, disabled, readonly.
           ;; eg <input readonly="readonly"> becomes <input readonly>
           simple-boolean-attributes

           ;; existing DOCTYPE declaration will be replaced with simple
           ;; <!DOCTYPE html> declaration.
           simple-doctype
           ]

    ;; defaults
    :or {compress-css false
         compress-javascript false
         javascript-compressor :yui
         preserve-line-breaks false
         preserve-patterns nil
         remove-comments true
         remove-form-attributes false
         remove-http-protocol false
         remove-https-protocol false
         remove-input-attributes false
         remove-intertag-spaces false
         remove-javascript-protocol false
         remove-link-attributes false
         remove-multi-spaces true
         remove-quotes false
         remove-script-attributes false
         remove-style-attributes false
         remove-surrounding-spaces nil
         simple-boolean-attributes false
         simple-doctype false
         }}]

  (doto (HtmlCompressor.)
    (.setCompressCss (boolean compress-css))
    (.setCssCompressor nil)
    (.setCompressJavaScript (boolean compress-javascript))
    (.setJavaScriptCompressor
     (when compress-javascript
       (case javascript-compressor
         :yui (doto (YuiJavaScriptCompressor.)
                (.setNoMunge (:no-munge javascript-compressor-options false))
                (.setPreserveAllSemiColons (:preserve-all-semicolons javascript-compressor-options false))
                (.setDisableOptimizations (:disable-optimizations javascript-compressor-options false))
                (.setLineBreak (:line-break javascript-compressor-options -1))
                ;;(.setLoggingLevel Level/OFF)
                )

         :closure (doto (ClosureJavaScriptCompressor.
                         (case (:level javascript-compressor-options)
                           :whitespace CompilationLevel/WHITESPACE_ONLY
                           :simple CompilationLevel/SIMPLE_OPTIMIZATIONS
                           :advanced CompilationLevel/ADVANCED_OPTIMIZATIONS
                           ))
                    (.setCustomExternsOnly true)
                    (.setExterns (make-externs-source-files (:externs javascript-compressor-options)))
                    ;;(.setLoggingLevel Level/OFF)
                    ))))
    (.setEnabled true)
    (.setGenerateStatistics false)
    (.setPreserveLineBreaks (boolean preserve-line-breaks))
    (.setPreservePatterns preserve-patterns)
    (.setRemoveComments (boolean remove-comments))
    (.setRemoveFormAttributes (boolean remove-form-attributes))
    (.setRemoveHttpProtocol (boolean remove-http-protocol))
    (.setRemoveHttpsProtocol (boolean remove-https-protocol))
    (.setRemoveInputAttributes (boolean remove-input-attributes))
    (.setRemoveIntertagSpaces (boolean remove-intertag-spaces))
    (.setRemoveJavaScriptProtocol (boolean remove-javascript-protocol))
    (.setRemoveLinkAttributes (boolean remove-link-attributes))
    (.setRemoveMultiSpaces (boolean remove-multi-spaces))
    (.setRemoveQuotes (boolean remove-quotes))
    (.setRemoveScriptAttributes (boolean remove-script-attributes))
    (.setRemoveStyleAttributes (boolean remove-style-attributes))
    (.setRemoveSurroundingSpaces
     (->> remove-surrounding-spaces
          (map (comp str name))
          (string/join ",")))
    (.setSimpleBooleanAttributes (boolean simple-boolean-attributes))
    (.setSimpleDoctype (boolean simple-doctype))
    ;; (.setYuiErrorReporter yui-error-reporter)
    ))

(defn compress-html [html & [config]]
  (let [compressor (-> config
                       (or {})
                       html-compressor)]
    (.compress ^HtmlCompressor compressor html)))

;; babashka call endpoint
(defn compress-html*bb [html & [config]]
  (compress-html
   html
   (if (:preserve-patterns config)
     (update config :preserve-patterns (fn [ps] (mapv #(re-pattern %) ps)))
     config)
   )
  )

#_ (compress-html "<div  arg=\" foo \" >\n\n  test  </div>")
