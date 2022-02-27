(ns bootleg.minify
  (:require [clojure.string :as string])
  (:import ;;com.yahoo.platform.yui.compressor.CssCompressor
           [com.googlecode.htmlcompressor.compressor HtmlCompressor]
           [com.google.javascript.jscomp
            CompilationLevel
            CompilerOptions
            SourceFile
            CompilerOptions$LanguageMode
            CommandLineRunner
            ]))

(defn html-compressor
  [{:keys [
           ;; compress css contained inside <style> tags
           compress-css

           ;; compress javascript inside <script> tags
           compress-javascript

           ;; js compression method to use. set to :yui or :google-closure
           javascript-compressor

           ;; the options for the javascript compressor chosen.
           ;; for yui, hashmap with keys:
           ;;  :css-line-break
           ;;  :error-reporter
           ;;  :disable-optimizations
           ;;  :js-line-break
           ;;  :js-no-munge
           ;;  :js-preserve-all-semicolons
           ;;
           ;; for :google-closure
           ;;  :mode
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

           ;; Tells Yahoo YUI Compressor to break lines after the specified number of symbols during CSS compression. pass in integer
           ;; yui-css-line-break

           ;; Sets ErrorReporter that YUI Compressor will use for reporting errors
           ;; during JavaScript compression. If no ErrorReporter was provided
           ;; YuiJavaScriptCompressor.DefaultErrorReporter will be used. which
           ;; reports errors to *err*
           ;;yui-error-reporter

           ;; Tells Yahoo YUI Compressor to disable all the built-in micro optimizations during JavaScript compression.
           ;; yui-js-disable-optimizations

           ;; Tells Yahoo YUI Compressor to break lines after the specified number of symbols during JavaScript compression. pass in an integer
           ;; yui-js-line-break

           ;; Tells Yahoo YUI Compressor to only minify javascript without obfuscating local symbols.
           ;; yui-js-no-munge

           ;; Tells Yahoo YUI Compressor to preserve unnecessary semicolons during JavaScript compression.
           ;; yui-js-preserve-all-semicolons
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
         ;; yui-css-line-break -1
         ;; yui-error-reporter nil
         ;; yui-js-disable-optimizations false
         ;; yui-js-line-break -1
         ;; yui-js-no-munge false
         ;; yui-js-preserve-all-semicolons false
         }}]

  (doto (HtmlCompressor.)
    (.setCompressCss compress-css)
    (.setCssCompressor nil)
    (.setCompressJavaScript compress-javascript)
    (.setJavaScriptCompressor
     (case javascript-compressor
       :yui nil
       :google-closure (com.google.javascript.jscomp.Compiler.)))
    (.setEnabled true)
    (.setGenerateStatistics false)
    (.setPreserveLineBreaks preserve-line-breaks)
    (.setPreservePatterns preserve-patterns)
    (.setRemoveComments remove-comments)
    (.setRemoveFormAttributes remove-form-attributes)
    (.setRemoveHttpProtocol remove-http-protocol)
    (.setRemoveHttpsProtocol remove-https-protocol)
    (.setRemoveInputAttributes remove-input-attributes)
    (.setRemoveIntertagSpaces remove-intertag-spaces)
    (.setRemoveJavaScriptProtocol remove-javascript-protocol)
    (.setRemoveLinkAttributes remove-link-attributes)
    (.setRemoveMultiSpaces remove-multi-spaces)
    (.setRemoveQuotes remove-quotes)
    (.setRemoveScriptAttributes remove-script-attributes)
    (.setRemoveStyleAttributes remove-style-attributes)
    (.setRemoveSurroundingSpaces
     (->> remove-surrounding-spaces
          (map (comp str name))
          (string/join ",")))
    (.setSimpleBooleanAttributes simple-boolean-attributes)
    (.setSimpleDoctype simple-doctype)

    ;; (.setYuiCssLineBreak yui-css-line-break)
    ;; (.setYuiErrorReporter yui-error-reporter)
    ;; (.setYuiJsDisableOptimizations yui-js-disable-optimizations)
    ;; (.setYuiJsLineBreak yui-js-line-break)
    ;; (.setYuiJsNoMunge yui-js-no-munge)
    ;; (.setYuiJsPreserveAllSemiColons yui-js-preserve-all-semicolons)

    ))

(defn compress-html [html & [config]]
  (-> config
      (or {})
      html-compressor
      (.compress html)))

#_ (compress-html "<div  arg=\" foo \" >\n\n  test  </div>")
