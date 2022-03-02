(ns bootleg.asciidoc
  (:require [clojure.data.json :as json]
            [bootleg.asciidocresolver]
            [bootleg.utils :as utils]
            [bootleg.file :as file])
  (:import
   [bootleg.asciidocresolver Resolver]
   [org.graalvm.polyglot Context Value]))

(set! *warn-on-reflection* true)

(def adoc "
Book Title Goes Here
====================
Author's Name
v1.0, 2003-12
:doctype: book


[dedication]
Example Dedication
------------------
Optional dedication.

This document is an AsciiDoc book skeleton containing briefly
annotated example elements plus a couple of example index entries and
footnotes.

Books are normally used to generate DocBook markup and the titles of
the preface, appendix, bibliography, glossary and index sections are
significant ('specialsections').

")

(def context
  (->
   (Context/newBuilder (into-array ["js"]))
   ;;(.allowIO true)
   ;;(.allowHostAccess true)
   (.allowAllAccess true) ;; include polyglot
   (.build))
  )

(defn setup [^Context context]
  (-> context
      (.getPolyglotBindings)
      (.putMember
       "IncludeResolver"
       ;; used by asciidoc to read ::include asciidoc directives
       (Resolver.)))

  (.eval context "js" "var IncludeResolver = Polyglot.import('IncludeResolver');")
  (.eval context "js" (slurp (clojure.java.io/resource "asciidoctor.js")))
  (.eval context "js" (str "var asciidoctor = Asciidoctor()")))

#_ (setup context)

(defn asciidoctor-convert [^Context context asciidoc & [options]]
  (str
   (.eval context "js"
          (format "asciidoctor.convert(%s, %s)"
                  (json/write-str asciidoc)
                  (json/write-str (or options nil))))))

#_ (asciidoctor-convert context "Hello, _Asciidoc_ \"glad\" 'to' \"see\" 'you' " nil)

(defonce context-setup? (atom false))

(defn convert [doc & [options]]
  (swap! context-setup?
         (fn [setup?]
           (if setup?
             true
             (do
               (setup context)
               true))))

  #_(setup context)
  #_(println "setup done")

  (asciidoctor-convert context doc options)
  )

#_ (convert)

(defn asciidoc [source & options]
  (let [flags (into #{} options)]
    (utils/html-output-to
     flags
     (let [input (if (:data flags) source (slurp (file/path-relative source)))]
       (convert input)))))
