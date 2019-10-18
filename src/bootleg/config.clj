(ns bootleg.config
  (:require [bootleg.file :as file]
            [clojure.java.io :as io])
  (:import [java.util Base64]))

(def home-dir (System/getenv "HOME"))
(def config-dir (file/path-join home-dir ".bootleg"))
(def libs-dir (file/path-join config-dir "libs"))
(def libsunec-path (file/path-join libs-dir "libsunec.so"))

(defn chunk-file [file-path]
  (->> (let [file (io/as-file file-path)
             file-array (byte-array (.length file))
             stream (io/input-stream file)]
         (.read stream file-array)
         (.close stream)
         file-array)
       (.encodeToString (Base64/getEncoder))

       ;; embedded macro literals have a 64k utf8 char limit.
       ;; so me chunk it into 32000 long chunks
       (partition 32000 32000 nil)
       (mapv #(apply str %))))

(defn compile-time-jvm-lib-path [filename]
  (file/path-join
   (or (System/getenv "GRAALVM_HOME")
       (System/getenv "JAVA_HOME"))
   filename))

(defmacro embed-length [file-path]
  (.length (io/as-file (compile-time-jvm-lib-path file-path))))

(defmacro embed [file-path]
  `(apply str ~(chunk-file (compile-time-jvm-lib-path file-path))))

(defmacro write-embedded-file-to [output-path compile-time-path]
  `(with-open [w# (io/output-stream ~output-path)]
    (->> (embed ~compile-time-path)
         (.decode (Base64/getDecoder))
         (.write w#))))

(defn setup []
  ;; https://blog.taylorwood.io/2018/10/04/graalvm-https.html
  (System/setProperty "java.library.path" libs-dir)
  (write-embedded-file-to libsunec-path "jre/lib/amd64/libsunec.so"))

(defn init! []
  (.mkdirs (io/as-file libs-dir))
  (let [check-size (embed-length "jre/lib/amd64/libsunec.so")
        exists? (.exists (io/as-file libsunec-path))
        file-size (when exists? (.length (io/as-file libsunec-path)))]
    (when (or (not exists?)
              (not= check-size file-size))
      (setup))))
