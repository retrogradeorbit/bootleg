(ns bootleg.pod
  (:require [bencode.core :refer [read-bencode write-bencode]]
            [clojure.edn :as edn]
            [bootleg.glob]
            [bootleg.utils]
            [bootleg.markdown]
            [bootleg.mustache]
            [bootleg.html]
            [bootleg.hiccup]
            [bootleg.selmer]
            [bootleg.yaml]
            [bootleg.json]
            [bootleg.edn]
            [bootleg.file]
            [bootleg.enlive]

            [hickory.convert]
            [hickory.hiccup-utils]
            [hickory.render]
            [hickory.select]
            [hickory.utils]
            [hickory.zip]

            [net.cgrand.enlive-html]
            [net.cgrand.jsoup]
            [net.cgrand.tagsoup]
            [net.cgrand.xml]

            [selmer.filter-parser]
            [selmer.filters]
            [selmer.middleware]
            [selmer.node]
            [selmer.parser]
            [selmer.tags]
            [selmer.template-parser]
            [selmer.util]
            [selmer.validator]

            [clojure.repl]
            )
  (:import [java.io PushbackInputStream])
  )

(def stdin (PushbackInputStream. System/in))

(defn write [v]
  (write-bencode System/out v)
  (.flush System/out))

(defmacro make-lookup [ns]
  (->>
   (for [[k v] (ns-publics ns)]
     (let [{:keys [ns name macro]} (meta v)]
       (when-not macro
         [(list 'quote (symbol (str "pod.retrogradeorbit." (str ns)) (str k)))
          (symbol v)
          ])))
   (filter identity)
   (into {})))

(defmacro make-namespace-def [ns]
  {"name" (str "pod.retrogradeorbit." (str ns))
   "vars" (->>
           (for [[k v] (ns-publics ns)]
             (let [{:keys [ns name macro]} (meta v)]
               (when-not macro
                 {"name" (str name)})))
           (filter identity)
           (into []))}
  )

#_ (macroexpand-1 '(make-namespace-def bootleg.glob))

;; when these namespaces appear in macros, they will be transformed into
;; the pod prefixed namespace
(def translate-ns?
  #{"bootleg.utils"
    "bootleg.selmer"

    "hickory.convert"
    "hickory.hiccup-utils"
    "hickory.render"
    "hickory.select"
    "hickory.utils"
    "hickory.zip"

    "selmer.filter-parser"
    "selmer.filters"
    "selmer.middleware"
    "selmer.node"
    "selmer.parser"
    "selmer.tags"
    "selmer.template-parser"
    "selmer.util"
    "selmer.validator"

    "net.cgrand.enlive-html"
    "net.cgrand.jsoup"
    "net.cgrand.tagsoup"
    "net.cgrand.xml"
    })

(defmacro process-macro-source [sym]
  (->
   (clojure.repl/source-fn sym)
   (edamame.core/parse-string  {:all true})
   (->> (clojure.walk/postwalk
         (fn [form]
           (if (symbol? form)
             (let [ns (namespace form)
                   sym-name (name form)]
               (if (translate-ns? ns)
                 (symbol (str "pod.retrogradeorbit." ns) sym-name)
                 (symbol ns sym-name)))
             form))))
   prn-str))

(bootleg.utils/pprint (process-macro-source bootleg.enlive/deftemplate))

(def lookup (merge
             (make-lookup bootleg.glob)
             (make-lookup bootleg.utils)
             (make-lookup bootleg.markdown)
             (make-lookup bootleg.mustache)
             (make-lookup bootleg.html)
             (make-lookup bootleg.hiccup)
             (make-lookup bootleg.selmer)
             (make-lookup bootleg.yaml)
             (make-lookup bootleg.json)
             (make-lookup bootleg.edn)
             (make-lookup bootleg.file)
             (make-lookup bootleg.enlive)

             (make-lookup hickory.convert)
             (make-lookup hickory.hiccup-utils)
             (make-lookup hickory.render)
             (make-lookup hickory.select)
             (make-lookup hickory.utils)
             (make-lookup hickory.zip)

             (make-lookup net.cgrand.enlive-html)
             (make-lookup net.cgrand.jsoup)
             (make-lookup net.cgrand.tagsoup)
             (make-lookup net.cgrand.xml)

             (make-lookup selmer.filter-parser)
             (make-lookup selmer.filters)
             (make-lookup selmer.middleware)
             (make-lookup selmer.node)
             (make-lookup selmer.parser)
             (make-lookup selmer.tags)
             (make-lookup selmer.template-parser)
             (make-lookup selmer.util)
             (make-lookup selmer.validator)

             ))

(defn main []
  (try
    (loop []
      (let [{:strs [id op var args]} (read-bencode stdin)]

        #_ (with-open [wrtr (io/writer "./pod-in.txt" :append true)]
             (.write wrtr (prn-str (String. op))))

        (case (String. op)
          "describe"
          (do
            (write {"format" "edn"
                    "namespaces"
                    [(make-namespace-def bootleg.glob)
                     (make-namespace-def bootleg.utils)
                     (make-namespace-def bootleg.markdown)
                     (make-namespace-def bootleg.mustache)
                     (make-namespace-def bootleg.html)
                     (make-namespace-def bootleg.hiccup)
                     (make-namespace-def bootleg.selmer)
                     (make-namespace-def bootleg.yaml)
                     (make-namespace-def bootleg.json)
                     (make-namespace-def bootleg.edn)
                     (make-namespace-def bootleg.file)
                     (make-namespace-def bootleg.enlive)
                     (make-namespace-def hickory.convert)
                     (make-namespace-def hickory.hiccup-utils)
                     (make-namespace-def hickory.render)
                     (make-namespace-def hickory.select)
                     (make-namespace-def hickory.utils)
                     (make-namespace-def hickory.zip)

                     (make-namespace-def hickory.convert)
                     (make-namespace-def hickory.hiccup-utils)
                     (make-namespace-def hickory.render)
                     (make-namespace-def hickory.select)
                     (make-namespace-def hickory.utils)
                     (make-namespace-def hickory.zip)

                     (make-namespace-def net.cgrand.enlive-html)
                     (make-namespace-def net.cgrand.jsoup)
                     (make-namespace-def net.cgrand.tagsoup)
                     (make-namespace-def net.cgrand.xml)

                     (make-namespace-def selmer.filter-parser)
                     (make-namespace-def selmer.filters)
                     (make-namespace-def selmer.middleware)
                     (make-namespace-def selmer.node)
                     (make-namespace-def selmer.parser)
                     (make-namespace-def selmer.tags)
                     (make-namespace-def selmer.template-parser)
                     (make-namespace-def selmer.util)
                     (make-namespace-def selmer.validator)

                     ]
                    "id" (String. id)})
            (recur))

          "invoke"
          (do
            (try
              (let [var (-> var
                            String.
                            symbol)
                    args (String. args)
                    args (edn/read-string args)]
                #_(with-open [wrtr (io/writer "./pod-in.txt" :append true)]
                    (.write wrtr (prn-str var args)))

                (if-let [f (lookup var)]
                  (let [value (pr-str (apply f args))
                        reply {"value" value
                               "id" id
                               "status" ["done"]}]
                    (write reply))
                  (throw (ex-info (str "Var not found: " var) {})))
                )
              (catch Throwable e
                (binding [*out* *err*]
                  (println e))
                (let [reply {"ex-message" (.getMessage e)
                             "ex-data" (pr-str
                                        (assoc (ex-data e)
                                               :type (class e)))
                             "id" id
                             "status" ["done" "error"]}]
                  (write reply))))
            (recur))
          (do
            (write {"err" (str "unknown op:" (name op))})
            (recur)))))
    (catch java.io.EOFException _ nil)))
