(ns bootleg.eval
  (:require [bootleg.utils :as utils]
            [bootleg.namespaces :as namespaces]
            [sci.core :as sci]
            [sci.addons :as addons]
            [clojure.string :as string]
            [clojure.java.io :as io]))

(defn make-target [clj-path]
  (let [path (butlast clj-path)
        filename (str (last clj-path) ".clj")
        full-path (concat path [filename])]
    (.getPath (apply io/file full-path))))

(defn make-target-file-possiblities [target-file]
  (if (.contains target-file "-")
    [target-file (string/replace target-file #"-" "_")]
    [target-file]))

(defn find-file [target-file search-path]
  (let [target-files (make-target-file-possiblities target-file)
        searches (->> search-path
                      (map (fn [path]
                             (map #(io/file path %) target-files)))
                      (apply concat))]
    (->> searches
         (map (fn [f] (when (.isFile f) f)))
         (filter identity)
         first)))

(defn make-search-path [spire-path]
  (let [cwd (utils/current-file-parent)]
    (if spire-path
      (concat (string/split spire-path #":") [cwd])
      [cwd])))

(defn load-fn
  "callback used by sci's require handler to find the source
  for a required namespace. Uses os evironment SPIREPATH to locate
  the sourcefile"
  [{:keys [namespace]}]
  (let [path (-> namespace
                 str
                 (string/split #"\."))
        spire-path (System/getenv "BOOTLEGPATH")
        target (make-target path)
        search-path (make-search-path spire-path)
        source-file (find-file target search-path)]
    (when source-file
      {:file (.getPath source-file)
       :source (str (format "(ns %s)" namespace)
                    (slurp source-file))})))

(defn load-file*
  "loads and evaluates a file, merging its root var defs into the
  present namespace"
  [sci-opts path]
  (let [file-path (io/file (utils/current-file-parent) path)
        source (slurp file-path)]
    (sci/with-bindings {sci/ns @sci/ns
                        sci/file file-path}
      (sci/eval-string* sci-opts source))))

(defn setup-sci-context [args bindings]
  (let [env (atom {})
        ctx-ref (atom nil)
        namespaces (update namespaces/namespaces
                           'clojure.core
                           assoc
                           '*command-line-args* (sci/new-dynamic-var '*command-line-args* args)
                           'load-file #(load-file* @ctx-ref %))
        opts (-> {:env env
                  :namespaces namespaces
                  :bindings bindings
                  :imports namespaces/imports
                  :features #{:bb :clj}
                  :classes namespaces/classes
                  :load-fn load-fn}
                 addons/future)
        sci-ctx (sci/init opts)]
    (reset! ctx-ref sci-ctx)
    sci-ctx))

(defn evaluate [args script bindings]
  (sci/eval-string*
   (setup-sci-context args bindings)
   script))
