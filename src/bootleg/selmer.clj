(ns bootleg.selmer
  (:require [bootleg.utils :as utils]
            [bootleg.file :as file]
            [selmer.parser]
            [selmer.util]
            [clojure.java.io :as io]))

;; add-tag! is a hella nifty macro. Example use:
;; (add-tag! :joined (fn [args context-map] (clojure.string/join "," args)))
(defmacro add-tag!
  "tag name, fn handler, and maybe tags "
  [k handler & tags]
  `(do
     (selmer.parser/set-closing-tags! ~k ~@tags)
     (swap! selmer.tags/expr-tags assoc ~k (selmer.parser/tag-handler ~handler ~k ~@tags))))

(defmacro exception [& [param & more :as params]]
  (if (class? param)
    `(throw (new ~param (str ~@more)))
    `(throw (Exception. (str ~@params)))))

(defmacro with-escaping [& body]
  `(binding [selmer.util/*escape-variables* true]
     ~@body))

(defmacro without-escaping [& body]
  `(binding [selmer.util/*escape-variables* false]
     ~@body))

(defmacro ->buf [[buf] & body]
  `(let [~buf (StringBuilder.)]
     (do ~@body)
     (.toString ~buf)))

(defn selmer [source vars & options]
  (let [flags (into #{} options)]
    (utils/html-output-to
     flags
     (if (:data flags)
       (selmer.parser/render source vars)
       (let [[path basename] (file/path-split source)
             basepath (file/path-relative (or path "."))]
         (with-redefs [selmer.util/resource-path (fn [template]
                                                   (.toURL (io/file basepath template)))]
           (selmer.parser/render-file basename vars)))))))
