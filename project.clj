(defproject namban "0.3.0"
  :description "Clojure(Script) Japanese library for trading between Hiragana, Katakana, Romaji, for identifying script types and more."

  :url "https://github.com/kanasubs/namban"

  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :scm {:name "git" :url "https://github.com/kanasubs/namban"}

  :dependencies [[org.clojure/clojure "1.5.1"]]

  :profiles {:dev {:dependencies [[org.clojure/clojurescript "0.0-2014"]
                                  [org.clojure/tools.namespace "0.2.4"]]
                   :plugins [[com.keminglabs/cljx "0.3.2"]
                             [codox "0.8.10"]
                             [com.cemerick/clojurescript.test "0.3.0"]
                             [lein-cljsbuild "1.0.0"]
                             [com.cemerick/austin "0.1.3"]]}}

  :auto-clean false ; 'install' will include sources gen by 'cljx once'

  :repl-options
    {:welcome (do (println "To start Austin, type: (cemerick.piggieback/cljs-repl :repl-env (cemerick.austin/exec-env)). Once in the cljs REPL, you must use/require like, e.g.: (ns cljs.user (:use namban.boeki)).")
                  (println "Type (refresh) to reload all namespaces."))
     :timeout 400000
     :init (require '[clojure.tools.namespace.repl :refer [refresh]])
     :init-ns namban.boeki}

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

  :codox {:src-dir-uri "https://github.com/kanasubs/namban/blob/master/"
          :sources ["target/gen-src" "target/gen-test"]
          :src-uri-mapping {#"target/gen-src" #(str "src/" % "x")
                            #"target/gen-test" #(str "test/" % "x")}
          :include [namban.boeki namban.test.boeki]
          :src-linenum-anchor-prefix "L"}

  :jar-exclusions [#"\.cljx|\.swp|\.swo|\.DS_Store"]

  :aliases {"test" ["do" ["cljx" "once"] ["cljsbuild" "once"]
                         "test" ["cljsbuild" "test"]]
            "repl" ["do" ["cljx" "once"] "repl"]}

  :source-paths ["src" "target/gen-src"]
  :test-paths ["target/gen-test"]
  
  :cljsbuild {:test-commands {"unit" ["phantomjs" :runner "target/unit-test.js"]}
              :builds [{:source-paths ["src" "target/gen-src" "target/gen-test"]
                        :compiler {:output-to "target/unit-test.js"
                                   :optimizations :whitespace
                                   :pretty-print true}}]})