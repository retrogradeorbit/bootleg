(ns bootleg.selmer
  (:require [bootleg.utils :as utils]
            [selmer.parser]))

;; add-tag! is a hella nifty macro. Example use:
;; (add-tag! :joined (fn [args context-map] (clojure.string/join "," args)))
(defn add-tag!
  "tag name, fn handler, and maybe tags "
  [_ _ k handler & tags]
  `(do
     (selmer.parser/set-closing-tags! ~k ~@tags)
     (swap! selmer.tags/expr-tags assoc ~k (selmer.parser/tag-handler ~handler ~k ~@tags))))

(defn exception [_ _ & [param & more :as params]]
  (if (class? param)
    `(throw (new ~param (str ~@more)))
    `(throw (Exception. (str ~@params)))))

(defn with-escaping [_ _ & body]
  `(binding [selmer.util/*escape-variables* true]
     ~@body))

(defn without-escaping [_ _ & body]
  `(binding [selmer.util/*escape-variables* false]
     ~@body))

(defn ->buf [_ _ [buf] & body]
  `(let [~buf (StringBuilder.)]
     (do ~@body)
     (.toString ~buf)))

(defn selmer [source vars & options]
  (let [flags (into #{} options)
        pre-markup (if (:data flags)
                     source
                     (utils/slurp-relative source))
        markup (selmer.parser/render pre-markup vars)]
    (utils/html-output-to flags markup)))
