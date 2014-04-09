(defproject namban "0.2.0"
  :description "Convert text between different japanese scripts."

  :dependencies [[org.clojure/clojure "1.5.1"]]

  :profiles {:dev {:dependencies [[org.clojure/clojurescript "0.0-2202"]
;                                  [ccfontes/cljs-info "1.0.0-SNAPSHOT"]
                                  [org.clojure/tools.namespace "0.2.4"]]
                   :plugins [[com.keminglabs/cljx "0.3.3-SNAPSHOT"]
                             [codox "0.6.7"]
                             [com.cemerick/clojurescript.test "0.3.0"]
                             [lein-cljsbuild "1.0.3"]
                             [com.cemerick/austin "0.1.4"]]}}

  :repl-options
    {:welcome (do (println "To start Austin, type: (cemerick.piggieback/cljs-repl :repl-env (cemerick.austin/exec-env))")
                  (println "Type (refresh) to reload all namespaces."))
     :timeout 400000
     :init (require '[clojure.tools.namespace.repl :refer [refresh]])}

  :cljx {:builds
          [{:source-paths ["src"]
            :output-path "target/gen-src"
            :rules :clj}

           {:source-paths ["src"]
            :output-path "target/gen-src"
            :rules :cljs}

           {:source-paths ["test"]
            :output-path "target/gen-test"
            :rules :clj}

           {:source-paths ["test"]
            :output-path "target/gen-test"
            :rules :cljs}]}

  :codox {:sources ["target/gen-src" "target/gen-test"]
          :include [namban.boeki namban.test.boeki]
          :src-dir-uri "https://github.com/ccfontes/namban/tree/master/"
          :src-linenum-anchor-prefix "L"}

  :jar-exclusions [#"\.cljx|\.swp|\.swo|\.DS_Store"]

  :aliases {"test" ["do" ["cljx" "once"] ["cljsbuild" "once"]
                         "test" ["cljsbuild" "test"]]}

  :source-paths ["src" "target/gen-src"]
  :test-paths ["target/gen-test"]
  
  :cljsbuild {:test-commands {"unit" ["phantomjs" :runner "target/unit-test.js"]}
              :builds [{:source-paths ["src" "target/gen-src" "target/gen-test"]
                        :compiler {:output-to "target/unit-test.js"
                                   :optimizations :whitespace
                                   :pretty-print true}}]})
