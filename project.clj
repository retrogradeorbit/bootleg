(defproject bootleg "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.0"]
                 [org.clojure/tools.cli "0.4.2"]
                 [markdown-clj "1.10.0"]

                 ;; https://github.com/owainlewis/yaml/issues/35
                 [io.forward/yaml "1.0.9"
                  :exclusions [[org.yaml/snakeyaml]]]
                 [org.yaml/snakeyaml "1.25"]

                 [cljstache "2.0.4"]
                 [enlive "1.1.6"]

                 ;; https://github.com/cgrand/enlive/issues/110
                 ;; TODO: use hickory to parse html cause tagsoup is broken
                 [hickory "0.7.1"]
                 ]
  :main ^:skip-aot bootleg.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
