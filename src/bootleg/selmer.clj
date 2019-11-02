(ns bootleg.selmer)

;; add-tag! is a hella nifty macro. Example use:
;; (add-tag! :joined (fn [args context-map] (clojure.string/join "," args)))
(defn add-tag!
  "tag name, fn handler, and maybe tags "
  [k handler & tags]
  `(do
     (selmer.parser/set-closing-tags! ~k ~@tags)
     (swap! selmer.tags/expr-tags assoc ~k (selmer.parser/tag-handler ~handler ~k ~@tags))))

(defn exception [& [param & more :as params]]
  (if (class? param)
    `(throw (new ~param (str ~@more)))
    `(throw (Exception. (str ~@params)))))

(defn with-escaping [& body]
  `(binding [selmer.util/*escape-variables* true]
     ~@body))

(defn without-escaping [& body]
  `(binding [selmer.util/*escape-variables* false]
     ~@body))

(defn ->buf [[buf] & body]
  `(let [~buf (StringBuilder.)]
     (do ~@body)
     (.toString ~buf)))
