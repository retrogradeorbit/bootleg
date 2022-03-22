(defproject bootleg #= (clojure.string/trim #= (slurp ".meta/VERSION"))
  :description "Simple template processing command line tool to help build static websites"
  :url "https://github.com/retrogradeorbit/bootleg"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.3"]
                 [org.clojure/tools.cli "1.0.206"]
                 [org.clojure/data.json "2.4.0"]
                 [org.clojure/data.xml "0.2.0-alpha6"]
                 [markdown-clj "1.10.8"]
                 [io.forward/yaml "1.0.11" :exclusions [org.yaml/snakeyaml org.flatland/ordered]]
                 [cljstache "2.0.6"]
                 [enlive "1.1.6" :exclusions [org.jsoup/jsoup]]
                 [hickory "0.7.1" :exclusions [org.clojure/tools.reader]]
                 [selmer "1.12.17"]

                 ;; minification
                 [com.github.hazendaz/htmlcompressor "1.7.3"]
                 [com.yahoo.platform.yui/yuicompressor "2.4.8"]
                 [org.slf4j/slf4j-simple "1.7.33"]

                 ;; for clojure eval
                 [org.babashka/sci "0.2.8"]

                 ;; clojures pprint doesn't work under graal native-image
                 [fipp "0.6.23"]
                 [mvxcvi/puget "1.2.0"]

                 ;; pod support
                 [nrepl/bencode "1.1.0"]
                 ]
  :source-paths ["src"]
  :java-source-paths ["src/java" "sci-reflector/src-java"]
  :test-paths ["test/clojure"]

  :main ^:skip-aot bootleg.core
  :target-path "target/%s"

  :aot [bootleg.asciidocresolver]
  :prep-tasks ["compile"]

  :profiles {:uberjar {:aot :all
                       :jvm-opts ["-Dclojure.spec.skip-macros=true"
                                  "-Djava.library.path=./"
                                  ] }})
