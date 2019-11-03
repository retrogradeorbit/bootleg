(ns bootleg.glob
  (:require [bootleg.file :as file]
            [bootleg.context :as context]
            [clojure.string :as string]
            [clojure.java.io :as io]))

(defn bracket-processor [contents]
  (-> contents
      (string/replace #"\!" "^")))

#_ (bracket-processor "[!abc]")

(defn replace-inside [s re func]
  (let [surrounds (string/split s re -1)
        founds (re-seq re s)]
    (->> surrounds last list
         (concat (interleave surrounds (map func founds)))
         (apply str))))

#_ (replace-inside "foo[A-Z]bar[!a-z0-9]bing" #"\[[^]]*\]" bracket-processor)

(defn glob->regex
  "Takes a glob-format string and returns a regex."
  [pattern]
  (-> pattern
      (replace-inside #"\[[^]]*\]" bracket-processor)
      (string/replace #"\." "[.]")
      (string/replace #"\*" ".+")
      (string/replace #"\?" ".")
      (str "$")
      (->> (str "^"))))

(defn glob-match
  [pattern s]
  (let [re (re-pattern (glob->regex pattern))]
    (re-matches re s)))

#_ (glob-match "foo*bin?" "foobarbing")
#_ (glob->regex "*.[cC]?j")
#_ (re-matches (re-pattern (glob->regex "*.[!d-z]?j")) "myfile.clj")

(defn glob-pattern? [s]
  (or
   (.contains s "*")
   (.contains s "?")
   (re-find #"\[.+\]" s)))

#_ (glob-pattern? "[a-z].clj")
#_ (glob-pattern? "file.*")

(defn find-matching
  "path must be a directory"
  [path glob]
  (assert (.isDirectory path))
  (let [files  (.listFiles path)
        names (map #(.getName %) files)
        matcher (re-pattern (glob->regex glob))
        match? (partial re-matches matcher)]
    (filter match? names)))

#_ (find-matching (io/file ".") "s??")
#_ (find-matching (io/file ".") "*.clj")

(defn glob-walk [path [glob & remains]]
  (if (= "**" glob)
    (let [matching (filter #(and (not= path %) (.isDirectory %)) (file-seq path))]
      (for [f matching]
        (glob-walk f remains)))

    (let [matching (find-matching path glob)]
      (for [f matching]
        (let [file (io/file path f)]
          (cond
            (and (.isDirectory file) (seq remains))
            (filter identity (glob-walk file remains))

            (.isDirectory file)
            file

            (and (.isFile file) (not (seq remains)))
            file))))))

#_ (flatten (glob-walk (io/file ".") ["src" "bootleg" "*.clj"]))
#_ (flatten (glob-walk (io/file ".") ["src" "bootle*"]))
#_ (flatten (glob-walk (io/file ".") ["**" "bootleg" "*.clj"]))
#_ (flatten (glob-walk (io/file ".") ["**" "*.clj"]))
#_ (def fset (flatten (glob-walk (io/file ".") ["**" "foo" "**" "a.txt"])))
#_ (count fset)
#_ (count (distinct fset))

(defn glob-files
  "walk the path for files matching glob and return
  a seq of java.io.File objects"
  [path glob]
  (let [;; resolve . and .. into canonical path
        glob (.getCanonicalPath (io/file (io/as-file path) glob))
        sep (java.io.File/separator)
        glob-parts (rest (string/split glob (re-pattern sep)))]
    (distinct (flatten (glob-walk (io/file "/") glob-parts)))))

#_ (glob-files "." "./src/../../*/*/bootleg/*.clj")
#_ (glob-files "." "./src/../../**/bootleg/*.clj")
#_ (glob-files "." "**/foo/**/*.txt")

(defn glob
  ([pattern] (glob context/*path* pattern))
  ([path pattern]
   (->> (glob-files path pattern)
        (map #(.getPath %))
        (map (partial file/relativise path)))))

#_ (glob "." "**/foo/**/*.txt")
