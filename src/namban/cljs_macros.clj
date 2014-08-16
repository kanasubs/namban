(ns ^{:doc "Both clojure and clojurescript macros must be implemented in clojure."}
  namban.cljs-macros
  (:require [namban.jitsuyo :refer [cljs?]]))

(defmacro defcljx [lvalue rvalue] `(def ~lvalue ~rvalue))