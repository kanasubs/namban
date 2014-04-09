(ns ;^{:doc "Conversion between japanese language scripts and more."
    ;  :author "Carlos C. Fontes"}
  namban.boeki
#+cljs (:require-macros [namban.cljs-macros :refer [defcljx]])
       (:require [clojure.string :refer [split]]
                 [namban.kori :refer [ffilter charcode]]
                 [namban.shocho :refer [syllab-maps
                                        long-vowel-symbols
                                        katakana-symbols
                                        hiragana-symbols
                                        consonants
                                        vowels
                                        agyo
                                        sokuon-after-symbols
                                        romaji-common
                                        hebon-dake
                                        kunrei-dake
                                        kana-sokuon
                                        yakumono-symbols]]
           #+clj [namban.clj-macros :refer [defcopy defcljx]]))

(defn- long-vowel-syllab?
  "Checks if string ifs a long vowel syllab.
   Depends on syllab-chunkify correctness."
  [s]
  (or
    (and (> (count s) 1) ((set "あいうえおー") (last s)))
    ((set "ĀāĪīŪūĒēŌōÂâÎîÛûÊêÔô") (last s))
    (= (last s) (-> s butlast last))
    (and (= (-> s butlast last) \o) (= (last s) \u))))

(defn- internal-script-kw
  "Converts a valid user input script keyword to a valid library script keyword."
  [f]
  (case f
    :hebon :romaji
    :zenkaku-katakana :katakana
    :kunrei :kunrei-shiki
    f))

(defn- kw-initials
  "Shorten keyword to its initial(s).
   For every dash character, the following character is an initial too."
  [f]
  (when f
    (->> (split (name f) #"-") (map first) (apply str) keyword)))

(defn- kw-supergroup [t]
  "Converts the child script keyword to
   the parent script keyword it belongs to."
  (case t
    :kunrei-shiki :romaji
    :wapuro :romaji
    t))

(defn- insert-pipe
  "Creates composed function which adds a pipe character
   to the input function's return value."
  [f] (comp (partial str "|") f))

(defn- join-chunk-patterns
  "Join into one string all japanese syllab
   chunk regex patterns for input function."
  [f]
  (subs
    (apply str (map f long-vowel-symbols))
    1))

(defn- kana-chunk-re
  "Receives a kana consonant fn and
   returns its full kana regex pattern string."
  [kai-f k]
  (let [small-w-vowel
          (if (= k :h)
            "ぃぇえ?|[ゃぁゎ]あ?|[ゅぅ]う?|[ょぉ][おう]?|ぃい?|ぇえ?|゜"
            "(?:ィェ|[ァャェィォョゥュヮ]|゜)ー?")
        kana-sokuon (str (k {:h \っ :k \ッ}))
        kana-f #(str
                  kana-sokuon "?" (kai-f %) "(?:" small-w-vowel "|" (k %) ")|"
                  kana-sokuon (kai-f %))
        kana-f (insert-pipe kana-f)]
    (join-chunk-patterns kana-f)))

(def ^:private consonant-re-pattern
 (str
  "(?:ts|kw|zy|gw|bb?|cc?|dd?|ff?|gg?|hh?|jj?|kk?"
    "|ll?|mm?|nn?|pp?|rr?|ss?|tt?|vv?|ww?|yy?|zz?"
    "|TS|KW|ZY|GW|BB?|CC?|DD?|FF?|GG?|HH?|JJ?|KK?"
    "|LL?|MM?|NN?|PP?|RR?|SS?|TT?|VV?|WW?|YY?|ZZ?)"))

(def ^:private romaji-chunk-re
  (let [vowel-re-pattern #(str "(?:" (:w %) "|" (:o %) "|" (:r %) "|" (:ks %) ")")
        f #(str (vowel-re-pattern %) ; to avoid pattern like "aha" (in "[hy]?")
                "|n'|n-|N'|N-|" consonant-re-pattern "[hy]?" (vowel-re-pattern %))
        f (insert-pipe f)]
    (join-chunk-patterns f)))

(def ^:private hex-re-pattern "(?:0?x|U\\+)[0-9a-zA-Z]{4}")

; TODO find in accordance with source script. for cases of duplicate syllab
;      but of different scripts why may result in different syllab maps
(defn- ffilter-syllab [s]
  (ffilter #(some #{(str s)} (vals %)) syllab-maps))

(defn- str-pred
  "Checks if all characters in string have predicate f."
  [f s]
  (when s
    (if (string? s)
      (not-any? (comp not f) s)
      (f s))))

(defmulti ^:private str-in-range-pred
  "Checks if all characters in string satisfy predicate."
  (fn [_ s]
    (when s
      (if (and (string? s) (> (count s) 1))
        :string
        :char))))

(defmethod str-in-range-pred :char [[lower higher] c]
  (<= lower (charcode c) higher))

(defmethod str-in-range-pred :string [unicode-range s]
  (not-any?
    (complement (partial str-in-range-pred unicode-range))
    s))

(defmethod str-in-range-pred :default [_ _])

(defn- kw-subgroups
  "Returns the scripts belonging to the script keyword."
  [kw]
  (case kw
    :romaji [:romaji :kunrei-shiki :wapuro]
    [kw]))

(defn some-kana-sokuon-comp
  "Returns kana sokuon compound or nil if it doesn't exist."
  [s]
  (let [drop-from-s #(drop % s)
        f #(when (kana-sokuon %2)
            (some->> %1 inc drop-from-s first sokuon-after-symbols (str %2)))]
    (first (keep-indexed f s))))

(defn consonants?
  "Checks if all character(s) in string are romaji consonants."
  [s] (str-pred consonants s))

(defn vowels?
  "Checks if all character(s) in string are romaji vowels."
  [s] (str-pred vowels s))

(defn agyo?
  "Checks if all character(s) in string are agyo."
  [s] (str-pred agyo s))

(defcljx あぎょう? agyo?)

(defn hiragana?
  "Checks if all character(s) in string are hiragana."
  [s] (str-pred hiragana-symbols s))

(defcljx ひらがな? hiragana?)

(defn hankaku-katakana?
  "Checks if all character(s) in string are hankaku-katakana."
  [s] (str-in-range-pred [0xff61 0xff9f] s))

(defcljx はんかくカタカナ? hankaku-katakana?)

(defn zenkaku-katakana?
  "Checks if all character(s) in string are zenkaku-katakana."
  [s] (str-in-range-pred [0x30a0 0x30ff] s))

(defcljx ぜんかくカタカナ? zenkaku-katakana?)

(defn katakana?
  "Checks if all character(s) in string are katakana."
  [s] (str-pred (some-fn zenkaku-katakana? hankaku-katakana?) s))

(defcljx カタカナ? katakana?)

(defn kana?
  "Checks if all character(s) in string are kana."
  [s] (str-pred (some-fn katakana? hiragana?) s))

(defcljx かな? kana?)

(defn kunrei?
  "Checks if all character(s) in string are kunrei-shiki romaji."
  [s] (str-pred (some-fn romaji-common kunrei-dake) s))

(defcljx kunrei-shiki? kunrei?)
(defcljx くんれい? kunrei?)
(defcljx くんれいしき? kunrei?)

(defn hebon?
  "Checks if all character(s) in string are hebon romaji."
  [s] (str-pred (some-fn romaji-common hebon-dake) s))

(defcljx hebon-shiki? hebon?)
(defcljx ヘボン? hebon?)
(defcljx ヘボンしき? hebon?)

(defn wapuro?
  "Checks if all character(s) in string are wāpuro romaji."
  [s] (str-pred romaji-common s))

(defcljx ワープロ? wapuro?)

(defn romaji?
  "Checks if all character(s) in string are any kind of romaji."
  [s] (str-pred (some-fn romaji-common hebon-dake kunrei-dake) s))

(defcljx ローマじ? romaji?)

; TODO complete this
(defn yakumono?
  "Checks if all character(s) in string are japanese punctuation."
  [s]
  (str-pred
    #(or
       ; source: http://www.fontspace.com/unicode/block/CJK+Symbols+and+Punctuation
       (<= 0x3000 (charcode %) 0x303f)
       ; source: http://unicode.org/charts/PDF/UFF00.pdf
       (some #{%} yakumono-symbols))
    s))

(defcljx やくもの? yakumono?)

; TODO complete this
(defn romaji-yakumono?
  "Checks if all character(s) in string are romaji punctuation."
  [s] (str-pred #(some #{%} " .,?!-(){}[]@#$%&`:*;£") s))

(defcljx ローマじやくもの? romaji-yakumono?)

(defn arabia-suji?
  "Checks if string is arabian numeral."
  [s]
  (re-matches
    (re-pattern (str hex-re-pattern "|[0-9]"))
    (str s)))

(defcljx アラビアすうじ? arabia-suji?)

(defn suji?
  "Checks if all character(s) in string are arabian numbers."
  [s] (str-pred #(some #{%} "０１２３４５６７８９ａｂｃｄｅｆＡＢＣＤＥＦ") s))

(defcljx すうじ? suji?)

(defn kanji?
  "Checks if all character(s) in string are kanji.
   Source: http://en.wikipedia.org/wiki/CJK_Unified_Ideographs"
  [s]
  (str-pred
    #(or ; huh yes there are chinese and korean only here too - what a mess!
      (<= 0x4e00 (charcode %) 0x9faf) ; CJK
      (<= 0x3400 (charcode %) 0x4dbf) ; CJK extension A (rare)
      (<= 0xf900 (charcode %) 0xfaff)) ; CJK compat. ideographs
      ; support for 16 bits only in java and those are off range:
      ;(and (>= (int %) 0x20000) (<= (int %) 0x2a6d6)) ; CJK ext. B (very rare)
      ;(and (>= (int %) 0x2a700) (<= (int %) 0x2b73f)) ; CJK ext. C
      ;(and (>= (int %) 0x2b740) (<= (int %) 0x2b81f)) ; CJK ext. D
      ;(and (>= (int %) 0x2f800) (<= (int %) 0x2fa1f)) ; CJK supplement
    s))

(defcljx 漢字? kanji?)

(defn some-romaji-sokuon-comp
  "Returs romaji sokuon compound or nil if it doesn't exist in string.
   Compound refers to sokuon followed by 'consonant'"
  [s]
  (when (and (romaji? s) (not (re-find #"aa|ii|uu|ee|oo" s)))
    (let [=2 (comp (partial = 2) count)]
      (some->> (partition-by identity s)
               (ffilter =2)
               first
               (repeat 2)
               (apply str)))))

(defn some-sokuon-comp
  "Returs truthy if there is a sokuon compound or falsey otherwise.
   Compound refers to sokuon followed by 'consonant'"
  [s] ((some-fn some-kana-sokuon-comp some-romaji-sokuon-comp) s))

(defn- kana-agyo-igai-fns
  "Receives a kana predicate and returns its corresponding consonant fn."
  [f?]
  (let [f (fn [c] (f? c))]
   (fn [m]
     (let [kana-agyo-igai (:kai m)]
       (str "[" (apply str (filter f kana-agyo-igai)) "]")))))

(def ^:private hiragana-agyo-igai (kana-agyo-igai-fns hiragana?))
(def ^:private katakana-agyo-igai (kana-agyo-igai-fns katakana?))

(def ^:private hiragana-chunk-re (kana-chunk-re hiragana-agyo-igai :h))
(def ^:private katakana-chunk-re (kana-chunk-re katakana-agyo-igai :k))

(def ^:private chunk-re-pattern
  (re-pattern
    (str
      hex-re-pattern "|"
      romaji-chunk-re "|"
      katakana-chunk-re "|"
      hiragana-chunk-re "|"
      "[\\n\\r]" ; isn't caught by (re-pattern "."). can't be [\n\r] because of cljs
      "|.")))

; TODO redesign in terms of [{:type value} ...] no predicate needed - faster, cleaner
(defn- syllab-chunkify
  "Given a string, returns ordered coll of potential japanese syllabs."
  [s]
  (let [s (str s)]
    (re-seq chunk-re-pattern s)))

(defn- char-jp-script
  "Returns script keyword corresponding to input character."
  [c]
  (cond
    (hankaku-katakana? c) :hankaku-katakana
    (zenkaku-katakana? c) :zenkaku-katakana
    (hiragana? c) :hiragana
    (kunrei-dake c) :kunrei-shiki
    (romaji? c) :romaji
    (yakumono? c) :yakumono
    (romaji-yakumono? c) :romaji-yakumono
    (suji? c) :suji
    (arabia-suji? c) :arabia-suji
    (kanji? c) :kanji))

(defn scripts
  "Returns a set of script keywords
   corresponding to every character in input string."
  [s] (some->> s (map char-jp-script) (into #{})))

(defn- internal-scripts
  "Returns a set of internal script keywords
   corresponding to every character in input string."
  [s]
  (let [char-jp-script (comp internal-script-kw char-jp-script)]
    (some->> s (map char-jp-script) (into #{}))))

(defn- kw-subgroups-of [s]
  (->> (internal-scripts s) (map kw-subgroups) flatten (into #{})))

(defn- apply-long-vowel
  "Applies target long vowel supplied in long-vowel-map to a vanilla syllab.
   Target long vowel is calculated or supplied as param."
  [syllab long-vowel-map & [target]]
  (let [target-long-vowel
          (-> (or target (-> syllab internal-scripts first))
              kw-initials long-vowel-map)]
    (cond
      (kana? syllab) (str syllab target-long-vowel)
      (romaji? syllab) (str (apply str (butlast syllab)) target-long-vowel))))

(defmulti ^:private find-long-vowel-map
  "Find long vowel map corresponding to a valid long vowel syllab."
  (fn [s] 
    (cond
      (kana? s) :kana
      (romaji? s) :romaji)))

(defmethod find-long-vowel-map :kana [s]
  (let [kai-map (ffilter
                 #(some #{(->> s butlast last)} (:kai %))
                 long-vowel-symbols)]
    (cond
      (katakana? s) kai-map
      (hiragana? s)
        (let [vowel-map (ffilter #(some #{(last s)} (:h %)) long-vowel-symbols)]
          (if (or (= kai-map vowel-map) (not= (:o vowel-map) (:o kai-map)))
            kai-map
            vowel-map)))))

(defmethod find-long-vowel-map :romaji [s]
  (let [lower-vowels? #(every? identity (map (set "aiueo") %))
        upper-vowels? #(every? identity (map (set "AIUEO") %))
        last-vowels (apply str [(-> s butlast last) (last s)])
        long-vowels ((some-fn lower-vowels? upper-vowels?) last-vowels)
        long-vowel? #(some (fn [[_ v]] (= (-> s last str) v)) %)
        long-vowels? #(some (fn [[_ v]] (= last-vowels v)) %)]
    (ffilter (if long-vowels long-vowels? long-vowel?) long-vowel-symbols)))

(defn- sokuon-for
  "Convert source sokuon for target script."
  [s target]
  (let [t (-> target kw-supergroup kw-initials)]
    (if (= t :r)
      (-> (or (-> s last ffilter-syllab :r) s) first str)
      (t {:h "っ" :k "ッ"}))))

;OPTIMIZE in case source already is target, don't make lookup
(defmulti ^:private convert-syllab
  "Convert syllab to target script."
  (fn [target & [s]]
    (when s
      (cond
        (some #{target} #{:katakana :hiragana :kunrei-shiki :romaji :wapuro})
          (cond
            (some-sokuon-comp s) :sokuon
            (long-vowel-syllab? s) :long-vowel
            :else :vanilla)
        (= target :suji)
          (if (arabia-suji? s) :suji :identity)
        :else :vanilla))))

(defmethod convert-syllab :sokuon [target & [s]]
  (str
    (sokuon-for (take 2 s) target) ; also takes lookahead char
    (convert-syllab target (apply str (drop 1 s)))))

(defmethod convert-syllab :long-vowel [target & [s]]
  (let [convert-syllab (convert-syllab target)
        long-vowel-map (find-long-vowel-map s)
        cut-syllab (apply str (butlast s))]
    (if (kana? s)
      (apply-long-vowel (convert-syllab cut-syllab) long-vowel-map target)
      (let [vanilla-syllab
              (str cut-syllab
                   (when-not (-> cut-syllab last vowels?) (:o long-vowel-map)))
            converted-syllab (convert-syllab vanilla-syllab)]
        (apply-long-vowel converted-syllab long-vowel-map target)))))

(defmethod convert-syllab :suji [target & [s]]
  (let [t (kw-initials target)
        f #(if (some #{%} #{\U \x}) % (-> % ffilter-syllab t))]
    (apply str (map f s))))

(defmethod convert-syllab :vanilla [target & [s]]
  (let [t (kw-initials target)]
    (or (-> s ffilter-syllab t) s)))

(defmethod convert-syllab :identity [_ & [s]] s)

(defmethod convert-syllab :default [target & [_]]
  (fn [s]
    (let [s (str s)]
      (convert-syllab target s))))

(defn- henkan-helper [s target convert-syllab]
  (if (string? s)
    (some->> s syllab-chunkify (map convert-syllab) (reduce str))
    (some-> s convert-syllab first)))

(defn henkan
  "'henkan' means 'conversion'. Converts a string into target script.
   Converts only syllabs from source script when it's supplied."
  ([s target]
   (let [target (internal-script-kw target)
         convert-syllab (convert-syllab target)]
     (henkan-helper s target convert-syllab)))

  ([s source target]
   (let [[source target] (map internal-script-kw [source target])
         convert-syllab (convert-syllab target)
         convert-syllab
           #(if (some #{source} (kw-subgroups-of %)) (convert-syllab %) %)]
     (henkan-helper s target convert-syllab))))

(defcljx へんかん henkan)

(defn hiragana "Converts syllabs of string to hiragana."
  [s] (henkan s :hiragana))

(defcljx ひらがな hiragana)

(defn zenkaku-katakana "Converts syllabs of string to zenkaku katakana."
  [s] (henkan s :katakana))

(defcljx katakana zenkaku-katakana)
(defcljx カタカナ zenkaku-katakana)
(defcljx ゼンカクカタカナ zenkaku-katakana)

(defn romaji "Converts syllabs of string to romaji."
  [s] (henkan s :romaji))

(defcljx ローマじ romaji)

(defn hebon "Converts syllabs of string to hebon."
  [s] (henkan s :hebon))

(defcljx hebon-shiki hebon)
(defcljx ヘボン hebon)
(defcljx ヘボンしき hebon)

(defn kunrei "Converts syllabs of string to kunrei."
  [s] (henkan s :kunrei-shiki))

(defcljx kunrei-shiki kunrei)
(defcljx くんれい kunrei)
(defcljx くんれいしき kunrei)

(defn wapuro "Converts syllabs of string to wāpuro."
  [s] (henkan s :wapuro))

(defcljx ワープロ wapuro)

(defn romaji-yakumono "Returns punctuation in romaji."
  [s] (henkan s :romaji-yakumono))

(defcljx ローマじやくもの romaji-yakumono)

(defn yakumono
  "Returns punctuation in japanese."
  [s] (henkan s :yakumono))

(defcljx やくもの yakumono)

(defn suji
  "Returns japanese numerals."
  [s] (henkan s :suji))

(defcljx すうじ suji)

(defn arabia-suji
  "Returns arabic numerals."
  [s] (henkan s :arabia-suji))

(defcljx アラビアすうじ arabia-suji)

(defn hiragana->zenkaku-katakana
  "Converts syllabs in hiragana to katakana.
   Leaves the rest of the string intact."
  [s] (henkan s :hiragana :katakana))

(defcljx hiragana->katakana hiragana->zenkaku-katakana)
(defcljx ひらがな->カタカナ hiragana->zenkaku-katakana)
(defcljx ひらがな->ぜんかくカタカナ hiragana->zenkaku-katakana)

(defn hiragana->romaji
  "Converts syllabs in hiragana to romaji.
   Leaves the rest of the string intact."
  [s] (henkan s :hiragana :romaji))

(defcljx ひらがな->ローマじ hiragana->romaji)

(defn hiragana->hebon
  "Converts syllabs in hiragana to hebon.
   Leaves the rest of the string intact."
  [s] (henkan s :hiragana :hebon))

(defcljx hiragana->hebon-shiki hiragana->hebon)
(defcljx ひらがな->ヘボン hiragana->hebon)
(defcljx ひらがな->ヘボンしき hiragana->hebon)

(defn hiragana->kunrei
  "Converts syllabs in hiragana to kunrei.
   Leaves the rest of the string intact."
  [s] (henkan s :hiragana :kunrei-shiki))

(defcljx hiragana->kunrei-shiki hiragana->kunrei)
(defcljx ひらがな->くんれい hiragana->kunrei)
(defcljx ひらがな->くんれいしき hiragana->kunrei)

(defn hiragana->wapuro
  "Converts syllabs in hiragana to wāpuro.
   Leaves the rest of the string intact."
  [s] (henkan s :hiragana :wapuro))

(defcljx ひらがな->ワープロ hiragana->wapuro)

(defn zenkaku-katakana->hiragana
  "Converts syllabs in katakana to hiragana.
   Leaves the rest of the string intact."
  [s] (henkan s :katakana :hiragana))

(defcljx katakana->hiragana zenkaku-katakana->hiragana)
(defcljx ぜんかくカタカナ->ひらがな zenkaku-katakana->hiragana)
(defcljx カタカナ->ひらがな zenkaku-katakana->hiragana)

(defn zenkaku-katakana->romaji
  "Converts syllabs in katakana to romaji.
   Leaves the rest of the string intact."
  [s] (henkan s :katakana :romaji))

(defcljx katakana->romaji zenkaku-katakana->romaji)
(defcljx ぜんかくカタカナ->ローマじ zenkaku-katakana->romaji)
(defcljx カタカナ->ローマじ zenkaku-katakana->romaji)

(defn zenkaku-katakana->hebon
  "Converts syllabs in katakana to hebon.
   Leaves the rest of the string intact."
  [s] (henkan s :katakana :hebon))

(defcljx katakana->hebon zenkaku-katakana->hebon)
(defcljx katakana->hebon-shiki zenkaku-katakana->hebon)
(defcljx zenkaku-katakana->hebon-shiki zenkaku-katakana->hebon)
(defcljx ぜんかくカタカナ->ヘボン zenkaku-katakana->hebon)
(defcljx カタカナ->ヘボン zenkaku-katakana->hebon)
(defcljx カタカナ->ヘボンしき zenkaku-katakana->hebon)
(defcljx ぜんかくカタカナ->ヘボンしき zenkaku-katakana->hebon)

(defn zenkaku-katakana->kunrei
  "Converts syllabs in katakana to kunrei.
   Leaves the rest of the string intact."
  [s] (henkan s :katakana :kunrei-shiki))

(defcljx katakana->kunrei-shiki zenkaku-katakana->kunrei)
(defcljx katakana->kunrei zenkaku-katakana->kunrei)
(defcljx zenkaku-katakana->kunrei-shiki zenkaku-katakana->kunrei)
(defcljx ぜんかくカタカナ->くんれい zenkaku-katakana->kunrei)
(defcljx カタカナ->くんれいしき zenkaku-katakana->kunrei)
(defcljx カタカナ->くんれい zenkaku-katakana->kunrei)
(defcljx ぜんかくカタカナ->ヘボンしき zenkaku-katakana->kunrei)

(defn zenkaku-katakana->wapuro
  "Converts syllabs in katakana to wāpuro.
   Leaves the rest of the string intact."
  [s] (henkan s :katakana :wapuro))

(defcljx katakana->wapuro zenkaku-katakana->wapuro)
(defcljx ぜんかくカタカナ->ワープロ zenkaku-katakana->wapuro)
(defcljx カタカナ->ワープロ zenkaku-katakana->wapuro)

(defn romaji->hiragana
  "Converts syllabs in romaji to hiragana.
   Leaves the rest of the string intact."
  [s] (henkan s :romaji :hiragana))

(defcljx ローマじ->ひらがな romaji->hiragana)

(defn romaji->zenkaku-katakana
  "Converts syllabs in romaji to hiragana.
   Leaves the rest of the string intact."
  [s] (henkan s :romaji :katakana))

(defcljx romaji->katakana romaji->zenkaku-katakana)
(defcljx ローマじ->ぜんかくカタカナ romaji->zenkaku-katakana)
(defcljx ローマじ->カタカナ romaji->zenkaku-katakana)

(defn romaji->hebon
  "Converts syllabs in romaji to hebon.
   Leaves the rest of the string intact."
  [s] (henkan s :romaji :hebon))

(defcljx romaji->hebon-shiki romaji->hebon)
(defcljx ローマじ->ヘボン romaji->hebon)
(defcljx ローマじ->ヘボンしき romaji->hebon)

(defn romaji->kunrei
  "Converts syllabs in romaji to kunrei.
   Leaves the rest of the string intact."
  [s] (henkan s :romaji :kunrei-shiki))

(defcljx romaji->kunrei-shiki romaji->kunrei)
(defcljx ローマじ->くんれい romaji->kunrei)
(defcljx ローマじ->くんれいしき romaji->kunrei)

(defn romaji->wapuro
  "Converts syllabs in romaji to wāpuro.
   Leaves the rest of the string intact."
  [s] (henkan s :romaji :wapuro))

(defcljx ローマじ->ワープロ romaji->wapuro)

(defn hebon->hiragana
  "Converts syllabs in hebon to hiragana.
   Leaves the rest of the string intact."
  [s] (henkan s :hebon :hiragana))

(defcljx hebon-shiki->hiragana hebon->hiragana)
(defcljx ヘボン->ひらがな hebon->hiragana)
(defcljx ヘボンしき->ひらがな hebon->hiragana)

(defn hebon->zenkaku-katakana
  "Converts syllabs in hebon to katakana.
   Leaves the rest of the string intact."
  [s] (henkan s :hebon :katakana))

(defcljx hebon->katakana hebon->zenkaku-katakana)
(defcljx hebon-shiki->katakana hebon->zenkaku-katakana)
(defcljx hebon-shiki->zenkaku-katakana hebon->zenkaku-katakana)
(defcljx ヘボン->ぜんかくカタカナ hebon->zenkaku-katakana)
(defcljx ヘボン->カタカナ hebon->zenkaku-katakana)
(defcljx ヘボンしき->カタカナ hebon->zenkaku-katakana)
(defcljx ヘボンしき->ぜんかくカタカナ hebon->zenkaku-katakana)

(defn hebon->romaji
  "Converts syllabs in hebon to romaji.
   Leaves the rest of the string intact."
  [s] (henkan s :hebon :romaji))

(defcljx hebon-shiki->romaji hebon->romaji)
(defcljx ヘボン->ローマじ hebon->romaji)
(defcljx ヘボンしき->ローマじ hebon->romaji)

(defn hebon->kunrei
  "Converts syllabs in hebon to kunrei.
   Leaves the rest of the string intact."
  [s] (henkan s :hebon :kunrei-shiki))

(defcljx hebon->kunrei-shiki hebon->kunrei)
(defcljx hebon-shiki->kunrei hebon->kunrei)
(defcljx hebon-shiki->kunrei-shiki hebon->kunrei)
(defcljx ヘボン->くんれい hebon->kunrei)
(defcljx ヘボン->くんれいしき hebon->kunrei)
(defcljx ヘボンしき->くんれい hebon->kunrei)
(defcljx ヘボンしき->くんれいしき hebon->kunrei)

(defn hebon->wapuro
  "Converts syllabs in hebon to wāpuro.
   Leaves the rest of the string intact."
  [s] (henkan s :hebon :wapuro))

(defcljx hebon-shiki->wapuro hebon->wapuro)
(defcljx ヘボン->ワープロ hebon->wapuro)
(defcljx ヘボンしき->ワープロ hebon->wapuro)

(defn kunrei->hiragana
  "Converts syllabs in kunrei to hiragana.
   Leaves the rest of the string intact."
  [s] (henkan s :kunrei-shiki :hiragana))

(defcljx kunrei-shiki->hiragana kunrei->hiragana)
(defcljx くんれい->ひらがな kunrei->hiragana)
(defcljx くんれいしき->ひらがな kunrei->hiragana)

(defn kunrei->zenkaku-katakana
  "Converts syllabs in kunrei to katakana.
   Leaves the rest of the string intact."
  [s] (henkan s :kunrei-shiki :katakana))

(defcljx kunrei->katakana kunrei->zenkaku-katakana)
(defcljx kunrei-shiki->katakana kunrei->zenkaku-katakana)
(defcljx kunrei-shiki->zenkaku-katakana kunrei->zenkaku-katakana)
(defcljx くんれい->ぜんかくカタカナ kunrei->zenkaku-katakana)
(defcljx くんれい->カタカナ kunrei->zenkaku-katakana)
(defcljx くんれいしき->カタカナ kunrei->zenkaku-katakana)
(defcljx くんれいしき->ぜんかくカタカナ kunrei->zenkaku-katakana)

(defn kunrei->romaji
  "Converts syllabs in kunrei to romaji.
   Leaves the rest of the string intact."
  [s] (henkan s :kunrei-shiki :romaji))

(defcljx kunrei-shiki->romaji kunrei->romaji)
(defcljx くんれい->ローマじ kunrei->romaji)
(defcljx くんれいしき->ローマじ kunrei->romaji)

(defn kunrei->hebon
  "Converts syllabs in kunrei to hebon.
   Leaves the rest of the string intact."
  [s] (henkan s :kunrei-shiki :hebon))

(defcljx kunrei->hebon-shiki kunrei->hebon)
(defcljx kunrei-shiki->hebon kunrei->hebon)
(defcljx kunrei-shiki->hebon-shiki kunrei->hebon)
(defcljx くんれい->ヘボン kunrei->hebon)
(defcljx くんれい->ヘボンしき kunrei->hebon)
(defcljx くんれいしき->ヘボン kunrei->hebon)
(defcljx くんれいしき->ヘボンしき kunrei->hebon)

(defn kunrei->wapuro
  "Converts syllabs in kunrei to wāpuro.
   Leaves the rest of the string intact."
  [s] (henkan s :kunrei-shiki :wapuro))

(defcljx kunrei-shiki->wapuro kunrei->wapuro)
(defcljx くんれい->ワープロ kunrei->wapuro)
(defcljx くんれいしき->ワープロ kunrei->wapuro)

;TODO desambiguation of long vowels
; methods:
;   1- segmentation
;   2- dictionary entries
;
; add ignore wapuro chunking option
; create wapuro-syllabs? / hebon-syllabs? / kunrei-syllabs?
; what category are: "＝" ?
