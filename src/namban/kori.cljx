(ns ; ^{:doc "Utility functions and macros."}
  namban.kori)

(defn cljs?
  "Determines if the file is running in a ClojureScript or Clojure context."
  [] (= (/ 1 2) 0.5))

(defn ffilter
  "Returns the first item of coll for which (pred item) returns logical true.
   Consumes sequences up to the first match, will consume the entire sequence
   and return nil if no match is found.
   Shamelessly copied from: https://github.com/richhickey/clojure-contrib/blob/95dddbbdd748b0cc6d9c8486b8388836e6418848/src/main/clojure/clojure/contrib/seq.clj#L179"
  [pred coll]
  (first (filter pred coll)))

(defn var
  "`var` compatibility for Clojurescript."
  [v] v)

  (defn charcode
    "Returns character code, or nil if input is nil."
    [c] (let [f (or #+cljs #(.charCodeAt %) #+clj int)]
          (some-> c f)))