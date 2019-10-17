(ns bootleg.utils
  (:require [clojure.string :as string]
            [hickory.core :as hickory]
            [hickory.render :as render]
            [hickory.convert :as convert]))

(defn- i-starts-with?
  "efficient case insensitive string start-with?"
  [haystack needle]
  (let [uneedle (string/upper-case needle)
        uhaystack-start (string/upper-case (subs haystack 0 (min (count needle) (count haystack))))]
    (= uneedle uhaystack-start)))

(defn html? [markup]
  (or (i-starts-with? markup "<!DOCTYPE HTML")
      (i-starts-with? markup "<html")))

(defn html->hiccup-seq [markup]
  (if (html? markup)
    (hickory/as-hiccup (hickory/parse markup))
    (map hickory/as-hiccup (hickory/parse-fragment markup))))

(defn html->hiccup [markup]
  (last (html->hiccup-seq markup)))

(def hiccup-seq->html render/hiccup-to-html)

(defn hiccup->html [hiccup]
  (hiccup-seq->html [hiccup]))

(def hiccup-seq->hickory convert/hiccup-to-hickory)

(defn hiccup->hickory [hiccup]
  (hiccup-seq->hickory [hiccup]))

(def hickory->hiccup convert/hickory-to-hiccup)
(defn html->hickory [markup]
  (-> markup html->hiccup-seq hiccup-seq->hickory))

(def hickory->html render/hickory-to-html)

(defn coerce-html [flags html]
  (cond
    (:hiccup flags)
    (html->hiccup-seq html)

    (:hickory flags)
    (html->hickory html)

    (:html flags)
    html

    :else
    (html->hiccup-seq html)))

;; hiccup is a single form. However, html snippets can be multiple sequential forms
;; resulting in a sequence of hiccup. The following function converts either of these
;; to html without throwing an error for one of them
(defn hiccup*->html [hiccup-or-hiccup-seq]
  (if (vector? (first hiccup-or-hiccup-seq))
    (hiccup-seq->html hiccup-or-hiccup-seq)
    (hiccup->html hiccup-or-hiccup-seq)))

;; full html
#_ (hickory/parse "<html><body><h1>foo</h1></body></html>")
#_ (hickory/parse "<h1>foo</h1>")

;; fragment
#_ (hickory/parse-fragment "<html><body><h1>foo</h1></body></html>")
#_ (hickory/parse-fragment "<h1>foo</h1>")

;; smart-parse
#_ (smart-parse "<html><body><h1>foo</h1></body></html>")
#_ (smart-parse "<h1>foo</h1>")

;; html->hiccup-seq
#_ (html->hiccup-seq "<html><body><h1>foo</h1></body></html>")
#_ (html->hiccup-seq "<h1>foo</h1><p>b</p>")
#_ (html->hiccup-seq "<h1>foo</h1>")

;; html->hickory
#_ (html->hickory "<html><body><h1>foo</h1></body></html>")
#_ (html->hickory "<h1>foo</h1><p>b</p>")
#_ (html->hickory "<h1>foo</h1>")

;; hiccup->html
#_ (hiccup->html [:div])

;; hiccup->hickory
#_ (hiccup->hickory [:div])

;; hiccup-seq->hickory
#_ (hiccup-seq->hickory '([:div] [:p]))

;; hickup*->html
#_ (hiccup*->html [:div])
#_ (hiccup*->html '([:div]))
