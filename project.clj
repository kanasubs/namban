(defproject namban "0.1.5"
  :description "Convert text between different japanese scripts."
  :dependencies [[org.clojure/clojure "1.5.1"]]
  :profiles {:dev {:dependencies [[midje "1.6.2"]]
                   :plugins [[codox "0.6.7"]
                             [lein-midje "3.1.3"]
                             [lein-clojars "0.9.1"]]}})
