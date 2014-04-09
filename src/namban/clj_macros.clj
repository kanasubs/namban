(ns ^{:doc "Both clojure and clojurescript macros must be implemented in clojure."}
  namban.clj-macros
  (:require [namban.kori :refer [cljs?]]
            ;[cljs-info.ns :refer [cljs-ns-resolve]]
            ))

(defmacro defcopy
  "Defines a copy of a var: a new var with the same root binding (if
   any) and similar metadata. The metadata of the copy is its initial
   metadata (as provided by def) merged into the metadata of the original.
   source: same as defalias from clojure 1.2 and downwards."
  ([name orig]
  `(do
     (alter-meta!
      (if (.hasRoot (var ~orig))
        (def ~name (.getRawRoot (var ~orig)))
        (def ~name))
      ;; When copying metadata, disregard {:macro false}.
      ;; Workaround for http://www.assembla.com/spaces/clojure/tickets/273
      #(conj (dissoc % :macro)
             (apply dissoc (meta (var ~orig)) (remove #{:macro} (keys %)))))
     (var ~name)))
  ([name orig doc]
   (list `defcopy (with-meta name (assoc (meta name) :doc doc)) orig)))

(defmacro defcljx
  "`defcopy` for Clojure, and `def` for Clojurescript."
  [lvalue rvalue] `(defcopy ~lvalue ~rvalue))