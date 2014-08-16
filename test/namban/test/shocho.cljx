(ns #+clj ^{:author "Carlos C. Fontes"} namban.test.shocho
   #+cljs namban.test.shocho
   
#+cljs (:require-macros [cemerick.cljs.test :refer [is deftest]])
       (:require [namban.shocho :as shocho]
                 [namban.shocho :refer
                   [syllab-maps long-vowel-symbols katakana-symbols
                    hiragana-symbols consonants vowels agyo
                    sokuon-after-symbols romaji-common hebon-dake kunrei-dake
                    kana-sokuon]]
          #+cljs [namban.jitsuyo :refer [var]]
          #+cljs [cemerick.cljs.test :as t]
           #+clj [clojure.test :refer [is deftest]]))

(deftest shocho-test
       (is (-> syllab-maps first map?))
       (is (-> long-vowel-symbols first map?))
       (is (-> shocho/unpaired-katakana var #+clj deref string?))
       (is (-> shocho/unpaired-hiragana var #+clj deref string?))
       (is (vector? katakana-symbols))
       (is (set? hiragana-symbols))
       (is (set? consonants))
       (is (set? vowels))
       (is (set? agyo))
       (is (set? sokuon-after-symbols))
#+clj  (is (-> \c romaji-common boolean))
#+cljs (is (romaji-common "c")) ; for hexadecimal numbers
#+clj  (is (-> \あ romaji-common not))
#+clj  (is (-> \â romaji-common not))
#+clj  (is (-> \ナ romaji-common not))
#+clj  (is (-> \と romaji-common not))
#+clj  (is (-> \ā hebon-dake boolean))
#+cljs (is (hebon-dake "ā"))
#+clj  (is (-> \d hebon-dake not))
#+clj  (is (-> \あ hebon-dake not))
#+clj  (is (-> \ア hebon-dake not))
#+clj  (is (-> \â kunrei-dake boolean))
#+cljs (is (kunrei-dake "â"))
#+clj  (is (-> \ā kunrei-dake not))
#+clj  (is (-> \あ kunrei-dake not))
#+clj  (is (-> \ア kunrei-dake not))
       (is (set? kana-sokuon)))