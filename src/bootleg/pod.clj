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
            [bootleg.file])
  (:import [java.io PushbackInputStream])
  )

(def stdin (PushbackInputStream. System/in))

(defn write [v]
  (write-bencode System/out v)
  (.flush System/out))

(defmacro make-looup [ns]
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

(def lookup (merge
             (make-looup bootleg.glob)
             (make-looup bootleg.utils)
             (make-looup bootleg.markdown)
             (make-looup bootleg.mustache)
             (make-looup bootleg.html)
             (make-looup bootleg.hiccup)
             (make-looup bootleg.selmer)
             (make-looup bootleg.yaml)
             (make-looup bootleg.json)
             (make-looup bootleg.edn)
             (make-looup bootleg.file)))

(defn main []
  (try
    (loop []
      (let [{:strs [id op var args]} (read-bencode stdin)]

        #_ (with-open [wrtr (io/writer "./pod-in.txt" :append true)]
          (.write wrtr (prn-str (String. op))))

        (case (String. op)
          "describe" (do (write {"format" "edn"
                                 "namespaces" [(make-namespace-def bootleg.glob)
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
                                               ]
                                 "id" (String. id)})
                         (recur))

          "invoke" (do (try
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
