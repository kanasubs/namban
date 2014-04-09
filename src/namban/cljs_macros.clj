(ns ^{:doc "Both clojure and clojurescript macros must be implemented in clojure."}
  namban.cljs-macros
  (:require [namban.kori :refer [cljs?]]
            ;[cljs-info.ns :refer [cljs-ns-resolve]]
            ))

(defmacro defcljx [lvalue rvalue]
  `(def ~lvalue ~rvalue))