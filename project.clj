(defproject namban "0.1.4"
  :description "Convert text between different japanese scripts."
  :dependencies [[org.clojure/clojure "1.5.1"]]
  :profiles {:dev {:dependencies [[midje "1.6-beta1"]]
                   :plugins [[codox "0.6.6"] [lein-midje "3.1.2"] [lein-clojars "0.9.1"]]
                   :codox {:sources ["src"]
                           :output-dir "doc/api"}}}
  :jvm-opts ["-Dfile.encoding=utf-8"]
  :target-path "target"
  :source-paths ["src"])
