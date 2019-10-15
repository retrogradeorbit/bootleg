(ns bootleg.transform
  (:require [bootleg.file :as file]
            [net.cgrand.enlive-html :as enlive]))

(defn prefix-path-fn [path]
  (fn [attr]
    (fn [node]
      (update-in node [:attrs (keyword attr)] #(file/path-join path %)))))

(defmacro make-enlive-map [names]
  (->> (for [n names] [n (symbol "enlive" n)])
       (into {})))

(def transform-map
  (make-enlive-map ["content" "html-content"
                    "add-class" "remove-class"
                    "set-attr" "remove-attr"]))

(defn make-transform-map [{:keys [path] :as context} step]
  (assoc transform-map
         "prefix-path-to-attr" (prefix-path-fn path)))

(defn make-transform [context {:keys [transform args] :as step}]
  (let [tmap (make-transform-map context step)
        t-fn (get tmap transform)]
    (apply t-fn args)))
