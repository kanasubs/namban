(ns ^{:doc "Conversion between japanese language scripts and more."
      :author "Carlos C. Fontes"}
  namban.boeki
  (:require [clojure.string :refer [split]]))

(defn- find-first
  [pred coll]
  (first (filter pred coll)))

(defn- every-truthy? [f s]
  (every? identity (map f s)))

(def ^:private syllab-maps
 (let [m
    [{:h "ゝ" :k "ヽ"} {:h "ゞ" :k "ヾ"}
     {:h "ゝ" :k "ヽ"} {:h "ゞ" :k "ヾ"}
     {:h "ゔ" :r "vu" :k "ヴ"} {:h "か" :r "ka" :k "カ"} {:h "が" :r "ga" :k "ガ"}
     {:h "あ" :r "a" :k "ア"} {:h "い" :r "i" :k "イ"} {:h "う" :r "u" :k "ウ"}
     {:h "え" :r "e" :k "エ"} {:h "お" :r "o" :k "オ"} {:h "ん" :r "n" :k "ン"}
     {:h "ゔ" :r "vu" :k "ヴ"} {:h "か" :r "ka" :k "カ"} {:h "が" :r "ga" :k "ガ"}
     {:h "き" :r "ki" :k "キ"} {:h "ぎ" :r "gi" :k "ギ"} {:h "く" :r "ku" :k "ク"}
     {:h "ぐ" :r "gu" :k "グ"} {:h "け" :r "ke" :k "ケ"} {:h "げ" :r "ge" :k "ゲ"}
     {:h "こ" :r "ko" :k "コ"} {:h "ご" :r "go" :k "ゴ"} {:h "さ" :r "sa" :k "サ"}
     {:h "ざ" :r "za" :k "ザ"} {:h "じ" :r "ji" :k "ジ"} {:h "す" :r "su" :k "ス"}
     {:h "ず" :r "zu" :k "ズ"} {:h "せ" :r "se" :k "セ"} {:h "ぜ" :r "ze" :k "ゼ"}
     {:h "そ" :r "so" :k "ソ"} {:h "ぞ" :r "zo" :k "ゾ"} {:h "た" :r "ta" :k "タ"}
     {:h "だ" :r "da" :k "ダ"} {:h "ぢ" :r "ji" :k "ヂ"} {:h "づ" :r "zu" :k "ヅ"}
     {:h "て" :r "te" :k "テ"} {:h "で" :r "de" :k "デ"} {:h "と" :r "to" :k "ト"}
     {:h "ど" :r "do" :k "ド"} {:h "な" :r "na" :k "ナ"} {:h "に" :r "ni" :k "ニ"}
     {:h "ぬ" :r "nu" :k "ヌ"} {:h "ね" :r "ne" :k "ネ"} {:h "の" :r "no" :k "ノ"}
     {:h "は" :r "ha" :k "ハ"} {:h "ば" :r "ba" :k "バ"} {:h "ぱ" :r "pa" :k "パ"}
     {:h "ひ" :r "hi" :k "ヒ"} {:h "び" :r "bi" :k "ビ"} {:h "ぴ" :r "pi" :k "ピ"}
     {:h "ふ" :r "hu" :k "フ"} {:h "ぶ" :r "bu" :k "ブ"} {:h "ぷ" :r "pu" :k "プ"}
     {:h "へ" :r "he" :k "ヘ"} {:h "べ" :r "be" :k "ベ"} {:h "ぺ" :r "pe" :k "ペ"}
     {:h "ほ" :r "ho" :k "ホ"} {:h "ぼ" :r "bo" :k "ボ"} {:h "ぽ" :r "po" :k "ポ"}
     {:h "ま" :r "ma" :k "マ"} {:h "み" :r "mi" :k "ミ"} {:h "む" :r "mu" :k "ム"}
     {:h "め" :r "me" :k "メ"} {:h "も" :r "mo" :k "モ"} {:h "や" :r "ya" :k "ヤ"}
     {:h "ゆ" :r "yu" :k "ユ"} {:h "よ" :r "yo" :k "ヨ"} {:h "ら" :r "ra" :k "ラ"}
     {:h "り" :r "ri" :k "リ"} {:h "る" :r "ru" :k "ル"} {:h "れ" :r "re" :k "レ"}
     {:h "ろ" :r "ro" :k "ロ"} {:h "わ" :r "wa" :k "ワ"} {:h "ゐ" :r "wi" :k "ヰ"}
     {:h "ゑ" :r "we" :k "ヱ"} {:h "を" :r "wo" :k "ヲ"}
     {:h "し" :r "shi" :k "シ"} {:h "ち" :r "chi" :k "チ"} {:h "つ" :r "tsu" :k "ツ"}
     {:h "ちゅ" :r "chu" :k "チュ"} ; found no way to input this yet
     {:h "ちゃ" :r "cha" :k "チャ"} ; found no way to input this yet
     {:h "ちょ" :r "cho" :k "チョ"} ; found no way to input this yet
     {:h "にゃ" :r "nya" :k "ニャ"} {:h "ぎゃ" :r "gya" :k "ギャ"}
     {:h "ひゃ" :r "hya" :k "ヒャ"} {:h "びゃ" :r "bya" :k "ビャ"}
     {:h "ぴゃ" :r "pya" :k "ピャ"} {:h "みゃ" :r "mya" :k "ミャ"}
     {:h "りゃ" :r "rya" :k "リャ"} {:h "きゅ" :r "kyu" :k "キュ"}
     {:h "ぎゅ" :r "gyu" :k "ギュ"} {:h "しゅ" :r "shu" :k "シュ"}  
     {:h "にゅ" :r "nyu" :k "ニュ"} {:h "びゅ" :r "byu" :k "ビュ"}
     {:h "ひゅ" :r "hyu" :k "ヒュ"} {:h "りゅ" :r "ryu" :k "リュ"}
     {:h "みゅ" :r "myu" :k "ミュ"} {:h "ぎょ" :r "gyo" :k "ギョ"}
     {:h "きょ" :r "kyo" :k "キョ"} {:h "にょ" :r "nyo" :k "ニョ"}
     {:h "しょ" :r "sho" :k "ショ"} {:h "びょ" :r "byo" :k "ビョ"}
     {:h "ひょ" :r "hyo" :k "ヒョ"} {:h "りょ" :r "ryo" :k "リョ"}
     {:h "みょ" :r "myo" :k "ミョ"} {:h "じゅ" :r "ju" :k "ジュ"}
     {:h "きゃ" :r "kya" :k "キャ"} {:h "じゃ" :r "ja" :k "ジャ"}
     {:h "しゃ" :r "sha" :k "シャ"} {:h "じょ" :r "jo" :k "ジョ"}
     {:h "ぴゅ" :r "pyu" :k "ぴュ"} 
     {:k "ウォ" :r "wo"}  ; "wo" duplicate; cannot input ウォ -> low priority
     {:k "ウィ" :r "wi"}  ; "wi" duplicate; cannot input ウィ -> low priority
     {:k "ウェ" :r "we"}  ; "we" duplicate; cannot input ウェ -> low priority
     {:k "グョ" :r "gyo"} ; "gyo" duplicate; cannot input グョ -> low priority
     {:k "クョ" :r "kyo"} ; "kyo" duplicate; cannot input クョ -> low priority
     {:k "ヴァ" :r "va"} {:k "ファ" :r "fa"} {:k "ヴィ" :r "vi"}
     {:k "フィ" :r "fi"} {:k "ティ" :r "ti"} {:k "ヴェ" :r "ve"}
     {:k "イェ" :r "ye"} {:k "フェ" :r "fe"} {:k "ヴォ" :r "vo"}
     {:k "フォ" :r "fo"}
     {:k "ヂュ" :r "dyu"} {:k "ドゥ" :r "dwu"} {:k "トゥ" :r "twu"}
     {:k "チェ" :r "tye"} {:k "ツェ" :r "tse"} {:k "ジェ" :r "zye"}
     {:k "シェ" :r "sye"} {:k "グェ" :r "gwe"} {:k "クェ" :r "kwe"}
     {:k "ヂョ" :r "dyo"} {:k "ツォ" :r "tso"} {:k "グォ" :r "gwo"}
     {:k "クォ" :r "kwo"} {:k "ヴュ" :r "vyu"} {:k "フュ" :r "fyu"}
     {:k "デュ" :r "dju"} {:k "テュ" :r "tju"} {:k "ヂャ" :r "dya"}
     {:k "ツァ" :r "tsa"}
     {:k "グァ" :r "gwa"} {:k "クァ" :r "kwa"} {:k "ディ" :r "dji"}
     {:k "ツィ" :r "tsi"} {:k "グィ" :r "gwi"} {:k "クィ" :r "kwi"}]]
   (into [] (map #(assoc % :ks (:r %) :w (:r %)) m))))

(def ^:private long-vowel-symbols
  [{:o "a" :w "aa" :r "ā" :ks "â" :h "あ" :k "ー"
    :kc "カガサザタダナハバパラマワヤャアァかがさざただなはばぱらまわやゃあ"}
   {:o "i" :w "ii" :r "ī" :ks "î" :h "い" :k "ー"
    :kc "キギシジチヂニヒビピミリイィきぎしじちぢにひびぴみりい"}
   {:o "e" :w "ee" :r "ē" :ks "ê" :h "え" :k "ー"
    :kc "ケゲセゼテデヘベペメレネエェけげせぜてでへべぺめれねえ"}
   {:o "o" :w "ou" :r "ō" :ks "ô" :h "う" :k "ー" ; order matters here - first
    :kc "コゴソゾトドノホボポモロヲヨョオォこごそぞとどのほぼぽろをよおょ"}
   {:o "o" :w "oo" :r "ō" :ks "ô" :h "お" :k "ー" ; order matters here - second
  　:kc "コゴソゾトドノホボポモロヲヨョオォこごそぞとどのほぼぽろをよおょ"}
   {:o "u" :w "uu" :r "ū" :ks "û" :h "う" :k "ー"
　  :kc "クグスズツヅフブプルムヴヌユュウゥくぐすずつづふぶぷるむぬゆゅう"}
   {:o "A" :w "AA" :r "Ā" :ks "Â" :h "あ" :k "ー"
    :kc "カガサザタダナハバパラマワヤャアァかがさざただなはばぱらまわやゃあ"}
   {:o "I" :w "II" :r "Ī" :ks "Î" :h "い" :k "ー"
    :kc "キギシジチヂニヒビピミリイィきぎしじちぢにひびぴみりい"}
   {:o "U" :w "UU" :r "Ū" :ks "Û" :h "う" :k "ー"
    :kc "クグスズツヅフブプルムヴヌユュウゥくぐすずつづふぶぷるむぬゆゅう"}
   {:o "E" :w "EE" :r "Ē" :ks "Ê" :h "え" :k "ー"
    :kc "ケゲセゼテデヘベペメレネエェけげせぜてでへべぺめれねえ"}
   {:o "O" :w "OU" :r "Ō" :ks "Ô" :h "う" :k "ー" ; order matters here - third
    :kc "コゴソゾトドノホボポモロヲヨョオォこごそぞとどのほぼぽろをよおょ"}])

(defn ^:private long-vowel-syllab? [s]
  "depends on syllab-chunkify correctness. not proper for API"
  (or
    (and (> (count s) 1) ((set "あいうえおー") (last s)))
    ((set "ĀāĪīŪūĒēŌōÂâÎîÛûÊêÔô") (last s))
    (= (last s) (-> s butlast last))
    (and (= (-> s butlast last) \o) (= (last s) \u))))

(def ^:private unpaired-katakana "゠ヷヸヹヺ・ーヿ")
(def ^:private unpaired-hiragana "゙゚゛゜ゟ")

; TODO ヶ (ka/ko/ga) not supported yet
(def ^:private katakana-symbols
  [(str
     "ァィゥェォッャュョアイウエオカガキギクグケゲコゴサザシジスズセゼソゾタダチヂツヅテデトドナニ"
     "ヌネノハバパヒビピフブプヘベペホボポマミムメモヤユヨラリルレロヮワヰヱヲンヴヵヶ")
  "ヽヾ" unpaired-katakana])

(def ^:private hiragana-symbols
  (set (str 
    "ぁぃぅぇぉっゃゅょあいうえおかがきぎくぐけげこごさざしじすずせぜそぞただちぢつづてでとどなに"
    "ぬねのはばぱひびぴふぶぷへべぺほぼぽまみむめもやゆよらりるれろゎわゐゑをんゔゕゖ"
    "ゝゞ" unpaired-hiragana)))

(def ^:private romaji-common? (set (str "abcdefghijkmnoprstuvwyz"
                        "ABCDEFGHIJKMNOPRSTUVWYZ")))

(def ^:private hepburn-only? (set "ĀāĒēĪīŌōŪū"))

(def ^:private consonants (set "bcdfghjkmnprstvwyz"))

(defn consonants? [s]
  (when s
    (if (char? s)
      (consonants s)
      (not (find-first (comp not consonants) s)))))

(def vowel? (set "ĀāĒēĪīŌōŪūÂâÊêÎîÔôÛûaiueoAIUEO"))

(defn hiragana?
  "source: http://jrgraphix.net/r/Unicode/3040-309F"
  [s]
  (when s
    (if (char? s)
      (hiragana-symbols s)
      (not (find-first (comp not hiragana-symbols) s)))))

(defn half-width-katakana?
  "source: http://www.sljfaq.org/afaq/half-width-katakana.html"
  [s]
  (when s
    (if (char? s)
      (let [c (int s)]
        (and (<= 0xff61 c) (<= c 0xff9f)))
      (let [f #(let [c (some-> % int)]
              (and (<= 0xff61 c) (<= c 0xff9f)))]
        (not (find-first (comp not f) s))))))

(defn full-width-katakana?
  "source: http://jrgraphix.net/r/Unicode/30A0-30FF"
  [s]
  (when s
    (if (char? s)
      (let [c (int s)]
        (and (<= 0x30a0 c) (<= c 0x30ff)))
      (let [f #(let [c (some-> % int)]
              (and (<= 0x30a0 c) (<= c 0x30ff)))]
        (not (find-first (comp not f) s))))))

(defn katakana? [s]
  (when s
    (let [f #(or (full-width-katakana? %) (half-width-katakana? %))]
      (if (char? s)
        (f s)
        (not (find-first (comp not f) s))))))

(defn kana? [s]
  (when s
    (let [f #(or (katakana? %) (hiragana? %))]
      (if (char? s)
        (f s)
        (not (find-first (comp not f) s))))))

(def kunrei-only? (set "ÂâÊêÎîÔôÛû"))

(defn kunrei? [s]
  (when s
    (let [f #(or (romaji-common? %) (kunrei-only? %))]
      (if (char? s)
        (f s)
        (not (find-first (comp not f) s))))))

(defn hepburn? [s]
  (when s
    (let [f #(or (romaji-common? %) (hepburn-only? %))]
      (if (char? s)
        (f s)
        (not (find-first (comp not f) s))))))

(defn wapuro? [s]
  (when s
    (let [f #(romaji-common? %)]
      (if (char? s)
        (f s)
        (not (find-first (comp not f) s))))))

(defn romaji? [s]
  (when s
    (let [f #(or (romaji-common? %)
           (hepburn-only? %)
           (kunrei-only? %))]
      (if (char? s)
        (f s)
        (not (find-first (comp not f) s))))))

(defn nihongo-punctuation? [s]
  (when s
    (let [f #(and (>= (int %) 0x3000) (<= (int %) 0x303f))]
      (if (char? s)
        (f s)
        (not (find-first (comp not f) s))))))

(defn- jp-syllab? [s]
  (fn [m] (find-first (fn [[_ v]] (= s v)) m)))

(defn- internal-script-kw [f]
  (case f
    :hepburn :romaji
    :full-width-katakana :katakana
    :half-width-katakana :half-katakana
    :kunrei :kunrei-shiki
    f))

(defn- shorten-script-kw [f]
  (when f
    (->> (split (name (internal-script-kw f)) #"-") (map first) (apply str) keyword)))

(defn- char-jp-script [c]
  (cond
    (half-width-katakana? c) :half-width-katakana
    (full-width-katakana? c) :full-width-katakana
    (hiragana? c) :hiragana
    (kunrei-only? c) :kunrei-shiki
    (romaji? c) :romaji
    (nihongo-punctuation? c) :nihongo-punctuation
    (when-let [c (some-> c int)]
      (and (>= c 0x4e00) (<= c 0x9faf))) :kanji))

(defn which-scripts [s]
  (some->> s (map char-jp-script) (into #{})))

(defn- insert-pipe [f] (comp (partial str "|") f))

(defn- apply-chunk-patterns [f]
  (subs
    (apply str (map f long-vowel-symbols))
    1))

(defn- kana-kc-fns [f?]
  (let [f (fn [c] (f? c))]
   (fn [m]
     (let [kc (:kc m)]
       (str "[" (apply str (filter f kc)) "]")))))

(def ^:private hiragana-kc (kana-kc-fns hiragana?))
(def ^:private katakana-kc (kana-kc-fns katakana?))

(defn- kana-chunk-re [kc-f k]
  (let [small-w-vowel (if (= k :h) "ゃあ?|ゅう?|ょ[おう]" "[ァャェィォョゥュ]ー?")
        kana-f #(str (kc-f %) "(?:" small-w-vowel "|" (k %) ")")
        kana-f (insert-pipe kana-f)]
    (apply-chunk-patterns kana-f)))

(def ^:private hiragana-chunk-re (kana-chunk-re hiragana-kc :h))
(def ^:private katakana-chunk-re (kana-chunk-re katakana-kc :k))

(def romaji-chunk-re
  (let [consonant-re-pattern "[bcdfghjkmnprstvwyzBCDFGHJKMNPRSTVWYZ]"
        vowel-re-pattern #(str "(?:" (:w %) "|" (:o %) "|" (:r %) "|" (:ks %) ")")
        f #(str (vowel-re-pattern %) "|" ; to avoid pattern like "aha" (in "[hy]?")
                consonant-re-pattern "[hy]?" (vowel-re-pattern %))
        f (insert-pipe f)]
    (apply-chunk-patterns f)))

(def ^:private chunk-re-pattern
  (re-pattern
    (str
      romaji-chunk-re "|"
      katakana-chunk-re "|"
      hiragana-chunk-re "|.")))

(defn- syllab-chunkify [s]
  (let [s (str s)]
    (re-seq chunk-re-pattern s)))

(defn- apply-long-vowel [s long-vowel-map & [target]]
  (let [target-long-vowel
          (-> (or target (-> s which-scripts first))
              shorten-script-kw long-vowel-map)]
    (cond
      (kana? s) (str s target-long-vowel)
      (romaji? s) (str (apply str (butlast s)) target-long-vowel))))

(declare convert-syllab)

(defmulti ^:private find-long-vowel-map
  (fn [s] 
    (cond
      (kana? s) :kana
      (romaji? s) :romaji)))

(defmethod find-long-vowel-map :kana [s]
  (let [kc-map (find-first #(some #{(->> s butlast last)} (:kc %)) long-vowel-symbols)]
      (cond
        (katakana? s) kc-map
        (hiragana? s)
          (let [vowel-map (find-first #(some #{(last s)} (:h %)) long-vowel-symbols)]
            (if (or (= kc-map vowel-map) (not= (:o vowel-map) (:o kc-map)))
              kc-map
              vowel-map)))))

(defmethod find-long-vowel-map :romaji [s]
  (let [lower-vowels? #(every-truthy? (set "aiueo") %)
        upper-vowels? #(every-truthy? (set "AIUEO") %)
        last-vowels (apply str [(-> s butlast last) (last s)])
        long-vowels (or (lower-vowels? last-vowels) (upper-vowels? last-vowels))
        long-vowel? #(find-first (fn [[_ v]] (= (-> s last str) v)) %)
        long-vowels? #(find-first (fn [[_ v]] (= last-vowels v)) %)]
    (find-first (if long-vowels long-vowels? long-vowel?) long-vowel-symbols)))

(defn- long-vowel-syllab [syllab target]
  (let [convert-syllab (convert-syllab :final target)
        long-vowel-map (find-long-vowel-map syllab)
        cut-syllab (apply str (butlast syllab))]
    (if (kana? syllab)
      (apply-long-vowel (convert-syllab cut-syllab) long-vowel-map target)
      (let [vanilla-syllab
              (str cut-syllab
                   (when-not (-> cut-syllab last vowel?) (:o long-vowel-map)))
            converted-syllab (convert-syllab vanilla-syllab)]
        (apply-long-vowel converted-syllab long-vowel-map target)))))

(defn- find-first-jp-syllab [s target]
  (let [s (str s)
     　 t (shorten-script-kw target)]
    (t (find-first (jp-syllab? s) syllab-maps))))

(defmulti ^:private convert-syllab (fn [type _] type))

(defmethod convert-syllab :oem [_ target]
  (fn [s]
    (let [s (str s)]
      (or
        (find-first-jp-syllab s target)
        (when (long-vowel-syllab? s) (long-vowel-syllab s target))
        s))))

(defmethod convert-syllab :final [_ target]
  (fn [s]
    (or (find-first-jp-syllab s target) s)))

(defn henkan
  ([s target]
    (let [convert-syllab (convert-syllab :oem target)]
      (if (char? s)
        (some-> s convert-syllab first)
        (some->> s syllab-chunkify (map convert-syllab) (reduce str)))))

  ([s source target]
    (let [source? (->> (str (name source) \?) symbol (ns-resolve *ns*))
          convert-syllab (convert-syllab :oem target)
          convert-syllab #(if (source? %) (convert-syllab %) %)]
      (if (char? s)
        (some-> s convert-syllab first)
        (some->> s syllab-chunkify (map convert-syllab) (reduce str))))))

(defn hiragana [s] (henkan s :hiragana))
(defn katakana [s] (henkan s :katakana))
(defn romaji [s] (henkan s :romaji))
(defn kunrei [s] (henkan s :kunrei))
(defn wapuro [s] (henkan s :wapuro))

(defn hiragana->katakana [s] (henkan s :hiragana :katakana))
(defn hiragana->romaji [s] (henkan s :hiragana :romaji))
(defn hiragana->hepburn [s] (henkan s :hiragana :hepburn))
(defn hiragana->kunrei [s] (henkan s :hiragana :kunrei))
(defn hiragana->wapuro [s] (henkan s :hiragana :wapuro))

(defn katakana->hiragana [s] (henkan s :katakana :hiragana))
(defn katakana->romaji [s] (henkan s :katakana :romaji))
(defn katakana->hepburn [s] (henkan s :katakana :hepburn))
(defn katakana->kunrei [s] (henkan s :katakana :kunrei))
(defn katakana->wapuro [s] (henkan s :katakana :wapuro))

(defn romaji->hiragana [s] (henkan s :romaji :hiragana))
(defn romaji->katakana [s] (henkan s :romaji :katakana))
(defn romaji->hepburn [s] (henkan s :romaji :hepburn))
(defn romaji->kunrei [s] (henkan s :romaji :kunrei))
(defn romaji->wapuro [s] (henkan s :romaji :wapuro))

(defn hepburn->hiragana [s] (henkan s :hepburn :hiragana))
(defn hepburn->katakana [s] (henkan s :hepburn :katakana))
(defn hepburn->romaji [s] (henkan s :hepburn :romaji))
(defn hepburn->kunrei [s] (henkan s :hepburn :kunrei))
(defn hepburn->wapuro [s] (henkan s :hepburn :wapuro))

(defn kunrei->hiragana [s] (henkan s :kunrei :hiragana))
(defn kunrei->katakana [s] (henkan s :kunrei :katakana))
(defn kunrei->romaji [s] (henkan s :kunrei :romaji))
(defn kunrei->hepburn [s] (henkan s :kunrei :hepburn))
(defn kunrei->wapuro [s] (henkan s :kunrei :wapuro))

;TODO desambiguation of long vowels
; methods:
;   1- segmentation
;   2- dictionary entries
;
; add docstrigs
; complete test coverage
; what happens when there isn't hiragana version? - like イェ