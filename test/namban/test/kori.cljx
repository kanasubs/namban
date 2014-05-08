(ns #+clj ^{:author "Carlos C. Fontes"} namban.test.kori
   #+cljs namban.test.kori
   
#+cljs (:require-macros [cemerick.cljs.test :refer [is deftest]])
       (:require [namban.kori :refer [ffilter charcode]]
          #+cljs [cemerick.cljs.test :as t]
           #+clj [clojure.test :refer [is deftest]]))

(deftest test-ffilter
  (is (= (ffilter even? [3 2 1 0])
         2))
  (is (nil? (ffilter even? [3 1]))))

(deftest charcode-test
#+clj  (is (= (charcode \〷) 0x3037))
#+cljs (is (= (charcode "〷") 0x3037))
       (is (= (charcode nil) nil)))