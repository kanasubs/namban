(ns ^{:doc "Conversion between japanese language scripts and more."
      :author "Carlos C. Fontes"}
  namban.boeki
  (:require
    [clojure.string :refer [split]]
    [clojure.set :refer [union subset?]]))

; TODO support conversion from old versions of hebon and nihon-shiki
(def ^:private syllab-maps
 (let [m
    [; TODO complete yakumono
     {:y "。" :ry "."} {:y "、" :ry ","} {:y "？" :ry "?"} {:y "！" :ry "!"}
     {:y "　" :ry " "} {:y "ゝ" :ry "ヽ"} {:y "ゞ" :ry "ヾ"} {:y "ー" :ry "-"}
     {:y "（" :ry "("} {:y "）" :ry ")"} {:y "｛" :ry "{"} {:y "｝" :ry "}"}
     {:y "［" :ry "["} {:y "］" :ry "]"} {:y "＠" :ry "@"} {:y "・" :ry " "}
     {:y "＃" :ry "#"} {:y "＄" :ry "$"} {:y "％" :ry "%"} {:y "＆" :ry "&"}
     {:y "｀" :ry "`"} {:y "：" :ry ":"} {:y "…" :ry "..."} {:y "＊" :ry "*"}
     {:y "※" :ry "*"} {:y "；" :ry ";"} {:y "￡" :ry "£"}
     
     {:h "あ" :k "ア" :r "a" :s "ａ" :as "a"} {:h "い" :k "イ" :r "i"}
     {:h "う" :k "ウ" :r "u"} {:h "え" :k "エ" :r "e" :as "e"} {:h "お" :k "オ" :r "o"}

     ; numbers go after {:h "あ" :k "ア" :r "a" :s "ａ" :as "a"}
　　　{:s "０" :as "0"} {:s "１" :as "1"} {:s "２" :as "2"} {:s "３" :as "3"}
     {:s "４" :as "4"} {:s "５" :as "5"} {:s "６" :as "6"} {:s "７" :as "7"}
     {:s "８" :as "8"} {:s "９" :as "9"} {:s "ｂ" :as "b"} {:s "ｃ" :as "c"}
     {:s "ｄ" :as "d"} {:s "ｅ" :as "e"} {:s "ｆ" :as "f"} {:s "Ａ" :as "A"}
     {:s "Ｂ" :as "B"} {:s "Ｃ" :as "C"} {:s "Ｄ" :as "D"} {:s "Ｅ" :as "E"}
     {:s "Ｆ" :as "F"}

     ; TODO support wapuro -/nn
     {:h "ん" :k "ン" :r "n'"}
     {:h "ん" :k "ン" :r "n"} ; n => ん/ン
     {:h "ん" :k "ン" :r "n-" :ks "n'"} ; n- => ん/ン/n'
     {:h "ん" :k "ン" :r "m" :ks "n"} ; m => n/ん/ン

     ; as in ちぇっ (chietsu/tietu)
     {:h "っ" :k "ッ" :r "tsu" :ks "tu"}

     ; sources:
     ;   - https://en.wikipedia.org/wiki/Hepburn_romanization
     ;   - https://en.wikipedia.org/wiki/Kunrei-shiki_romanization
     ; gojūon
     {:h "か" :k "カ" :r "ka"} {:h "き" :k "キ" :r "ki"} {:h "く" :k "ク" :r "ku"}
     {:h "け" :k "ケ" :r "ke"} {:h "こ" :k "コ" :r "ko"}

     {:h "さ" :k "サ" :r "sa"} {:h "し" :k "シ" :r "shi" :ks "si"}
     {:h "す" :k "ス" :r "su"} {:h "せ" :k "セ" :r "se"} {:h "そ" :k "ソ" :r "so"}

     {:h "た" :k "タ" :r "ta"} {:h "ち" :k "チ" :r "chi" :ks "ti"}
     {:h "つ" :k "ツ" :r "tsu" :ks "tu"} {:h "て" :k "テ" :r "te"}
     {:h "と" :k "ト" :r "to"}

     {:h "な" :k "ナ" :r "na"} {:h "に" :k "ニ" :r "ni"} {:h "ぬ" :k "ヌ" :r "nu"}
     {:h "ね" :k "ネ" :r "ne"} {:h "の" :k "ノ" :r "no"}

     {:h "は" :k "ハ" :r "ha"} {:h "ひ" :k "ヒ" :r "hi"}
     {:h "ふ" :k "フ" :r "fu" :ks "hu"} {:h "へ" :k "ヘ" :r "he"}
     {:h "ほ" :k "ホ" :r "ho"}

     {:h "ま" :k "マ" :r "ma"} {:h "み" :k "ミ" :r "mi"} {:h "む" :k "ム" :r "mu"}
     {:h "め" :k "メ" :r "me"} {:h "も" :k "モ" :r "mo"}

     {:h "や" :k "ヤ" :r "ya"} {:h "ゆ" :k "ユ" :r "yu"} {:h "よ" :k "ヨ" :r "yo"}
     {:h "ら" :k "ラ" :r "ra"} {:h "り" :k "リ" :r "ri"} {:h "る" :k "ル" :r "ru"}
     {:h "れ" :k "レ" :r "re"} {:h "ろ" :k "ロ" :r "ro"}

     {:h "わ" :k "ワ" :r "wa"} {:h "ゐ" :k "ヰ" :r "wi" :ks "i"}
     {:h "ゑ" :k "ヱ" :r "we" :ks "e"} {:h "を" :k "ヲ" :r "o" :ks "o"}

     ; dakuten no gojūon
     {:h "が" :k "ガ" :r "ga"} {:h "ぎ" :k "ギ" :r "gi"} {:h "ぐ" :k "グ" :r "gu"}
     {:h "げ" :k "ゲ" :r "ge"} {:h "ご" :k "ゴ" :r "go"}

     {:h "ざ" :k "ザ" :r "za"} {:h "じ" :k "ジ" :r "ji" :ks "zi"}
     {:h "ず" :k "ズ" :r "zu"} {:h "ぜ" :k "ゼ" :r "ze"} {:h "ぞ" :k "ゾ" :r "zo"}

     {:h "だ" :k "ダ" :r "da"} {:h "ぢ" :k "ヂ" :r "ji" :ks "zi"}
     {:h "づ" :k "ヅ" :r "zu"} {:h "で" :k "デ" :r "de"} {:h "ど" :k "ド" :r "do"}

     {:h "ば" :k "バ" :r "ba"} {:h "び" :k "ビ" :r "bi"} {:h "ぶ" :k "ブ" :r "bu"}
     {:h "べ" :k "ベ" :r "be"} {:h "ぼ" :k "ボ" :r "bo"}

     {:h "ぱ" :k "パ" :r "pa"} {:h "ぴ" :k "ピ" :r "pi"} {:h "ぷ" :k "プ" :r "pu"}
     {:h "ぺ" :k "ペ" :r "pe"} {:h "ぽ" :k "ポ" :r "po"}

     ; yōon
     {:h "きゃ" :k "キャ" :r "kya"} {:h "きゅ" :k "キュ" :r "kyu"}
     {:h "きょ" :k "キョ" :r "kyo"}

     {:h "しゃ" :k "シャ" :r "sha" :ks "sya"}
     {:h "しゅ" :k "シュ" :r "shu" :ks "syu"}
     {:h "しょ" :k "ショ" :r "sho" :ks "syo"}

     {:h "ちゃ" :k "チャ" :r "cha" :ks "tya"}
     {:h "ちゅ" :k "チュ" :r "chu" :ks "tyu"}
     {:h "ちょ" :k "チョ" :r "cho" :ks "tyo"}

     {:h "にゃ" :k "ニャ" :r "nya"} {:h "にゅ" :k "ニュ" :r "nyu"}
     {:h "にょ" :k "ニョ" :r "nyo"}

     {:h "ひゃ" :k "ヒャ" :r "hya"} {:h "ひゅ" :k "ヒュ" :r "hyu"}
     {:h "ひょ" :k "ヒョ" :r "hyo"}

     {:h "みゃ" :k "ミャ" :r "mya"} {:h "みゅ" :k "ミュ" :r "myu"}
     {:h "みょ" :k "ミョ" :r "myo"}

     {:h "りゃ" :k "リャ" :r "rya"} {:h "りゅ" :k "リュ" :r "ryu"}
     {:h "りょ" :k "リョ" :r "ryo"}

     ; dakuten no yōon
     {:h "ぎゃ" :k "ギャ" :r "gya"} {:h "ぎゅ" :k "ギュ" :r "gyu"}
     {:h "ぎょ" :k "ギョ" :r "gyo"}

     {:h "じゃ" :k "ジャ" :r "ja" :ks "zya"} {:h "じゅ" :k "ジュ" :r "ju" :ks "zyu"}
     {:h "じょ" :k "ジョ" :r "jo" :ks "zyo"}

     ; order counts - ぢ syllab forms must go after じ forms above
     {:h "ぢゃ" :k "ヂャ" :r "ja" :ks "zya"} ; found no way to input this yet
     {:h "ぢゅ" :k "ヂュ" :r "ju" :ks "zyu"} ; found no way to input this yet
     {:h "ぢょ" :k "ヂョ" :r "jo" :ks "zyo"} ; found no way to input this yet

     {:h "びゃ" :k "ビャ" :r "bya"} {:h "びゅ" :k "ビュ" :r "byu"}
     {:h "びょ" :k "ビョ" :r "byo"}

     {:h "ぴゃ" :k "ピャ" :r "pya"} {:h "ぴゅ" :k "ぴュ" :r "pyu"}
     {:h "ぴょ" :k "ピョ" :r "pyo"}
     
     ; extended katakana with some hiragana occurrences
     ; hiragana occurrences for compatibility only appear last in maps
     {:k "イィ" :r "yi" :h "いぃ"} {:k "イェ" :r "ye" :h "いぇ"}

     {:k "ウァ" :r "wa" :h "うぁ"} {:k "ウィ" :r "wi" :h "うぃ"}
     {:k "ウゥ" :r "wu" :h "うぅ"}

     {:k "ウェ" :r "we" :h "うぇ"} {:k "ウォ" :r "wo" :h "うぉ"}

     {:k "ウュ" :r "wyu" :h "うゅ"}

     {:k "ヴァ" :r "va" :h "ゔぁ"} {:k "ヴィ" :r "vi" :h "ゔぃ"}
     {:h "ゔ" :k "ヴ" :r "vu"}

     {:k "ヴェ" :r "ve" :h "ゔぇ"} {:k "ヴォ" :r "vo" :h "ゔぉ"}
     
     {:k "ヴャ" :r "vya" :h "ゔゃ"} {:k "ヴュ" :r "vyu" :h "ゔゅ"}
     
     {:k "ヴィェ" :r "vye" :h "ゔぃぇ"} {:k "ヴョ" :r "vyo" :h "ゔょ"}
     
     {:k "キェ" :r "kye" :h "きぇ"}

     {:k "ギェ" :r "gye" :h "ぎぇ"}

     {:k "クァ" :r "kwa" :h "くぁ"} ; order counts - suggested by Japan's CJMECSST
     {:k "クィ" :r "kwi" :h "くぃ"} {:k "クェ" :r "kwe" :h "くぇ"}
     {:k "クォ" :r "kwo" :h "くぉ"}

     {:k "クヮ" :r "kwa" :h "くゎ"} ; order counts - suggested by US's ANSI

     {:k "グァ" :r "gwa" :h "ぐぁ"} ; order counts - suggested by Japan's CJMECSST
     {:k "グィ" :r "gwi" :h "ぐぃ"} {:k "グェ" :r "gwe" :h "ぐぇ"}
     {:k "グォ" :r "gwo" :h "ぐぉ"}

     {:k "グヮ" :r "gwa" :h "ぐゎ"} ; order counts - suggested by US's ANSI

     {:h "しぇ" :k "シェ" :r "she" :ks "sye"}

     {:h "じぇ" :k "ジェ" :r "je" :ks "zye"}

     {:k "スィ" :r "si" :h "すぃ"}

     {:k "ズィ" :r "zi" :h "ずぃ"}

     {:k "チェ" :r "che" :ks "tye" :h "ちぇ"}

     {:k "ツァ" :r "tsa" :h "つぁ"} {:h "つぃ" :k "ツィ" :r "tsi"}

     {:h "つぇ" :k "ツェ" :r "tse"} {:h "つぉ" :k "ツォ" :r "tso"}

     {:k "ツュ" :r "tsyu" :h "つゅ"}

     {:k "ティ" :r "ti" :h "てぃ"} {:k "トゥ" :r "tu" :h "とぅ"}

     {:k "テュ" :r "tyu" :h "てゅ"}

     {:k "ディ" :r "di" :h "でぃ"} {:k "ドゥ" :r "du" :h "どぅ"}

     {:k "デュ" :r "dyu" :h "でゅ"}

     {:k "ニェ" :r "nye" :h "にぇ"}

     {:k "ヒェ" :r "hye" :h "ひぇ"}

     {:k "ビェ" :r "bye" :h "びぇ"}

     {:k "ピェ" :r "pye" :h "ぴぇ"}

     {:k "ファ" :r "fa" :h "ふぁ"} {:k "フィ" :r "fi" :h "ふぃ"}
     {:k "フェ" :r "fe" :h "ふぇ"} {:k "フォ" :r "fo" :h "ふぉ"}

     {:k "フャ" :r "fya" :h "ふゃ"} {:k "フュ" :r "fyu" :h "ふゅ"}
     {:k "フィェ" :r "fye" :h "ふぃぇ"}

     {:k "フョ" :r "fyo" :h "ふょ"}

     {:k "ホゥ" :r "hu" :h "ほぅ"}

     {:k "ミェ" :r "mye" :h "みぇ"}

     {:k "リェ" :r "rye" :h "りぇ"}

     {:k "ラ゜" :r "la" :h "ら゜"} {:k "リ゜" :r "li" :h "り゜"}
     {:k "ル゜" :r "lu" :h "る゜"} {:k "レ゜" :r "le" :h "れ゜"}
     {:k "ロ゜" :r "lo" :h "ろ゜"}

     {:k "ヷ" :r "va" :h "ゔぁ"} {:k "ヸ" :r "vi" :h "ゔぃ"}
     {:k "ヹ" :r "ve" :h "ゔぇ"} {:k "ヺ" :r "vo" :h "ゔぉ"}

     ; kunrei-shiki exceptions
     ; Limited to international relations and situations with prior precedent in
     ; which a sudden spelling reform would be difficult
     ; kunrei->x only
     {:ks "sha" :h "しゃ" :k "シャ"} {:ks "shi" :h "し" :k "シ"}
     {:ks "shu" :h "しゅ" :k "シュ"} {:ks "sho" :h "しょ" :k "ショ"}

     {:ks "tsu" :h "つ" :k "ツ"}
     
     {:ks "cha" :h "ちゃ" :k "チャ"} {:ks "chi" :h "ち" :k "チ"}
     {:ks "chu" :h "ちゅ" :k "チュ"} {:ks "cho" :h "ちょ" :k "チョ"}

     {:ks "fu" :h "ふ" :k "フ"}
     
     {:ks "ja" :h "じゃ" :k "ジャ"} {:ks "ji" :h "じ" :k "ジ"}
     {:ks "ju" :h "じゅ" :k "ジュ"} {:ks "jo" :h "じょ" :k "ジョ"}

     {:ks "di" :h "ぢ" :k "ヂ" :r "ji"} {:ks "du" :h "づ" :k "ヅ" :r "zu"}

     {:ks "dya" :h "ぢゃ" :k "ヂャ" :r "ja"} {:ks "dyu" :h "ぢゅ" :k "ヂュ" :r "ju"}
     {:ks "dyo" :h "ぢょ" :k "ヂョ" :r "jo"}

     {:ks "kwa" :h "くゎ" :k "クヮ"}

     {:ks "gwa" :h "ぐゎ" :k "グヮ"}

     {:ks "wo" :h "を" :k "ヲ" :r "o"}

     ; ヶ choose from below which version to use, according to use frequency
     {:k "ヶ" :h "か" :r "ka"} ; for counter - tatoeba 一ヶ月
     ;{:k "ヶ" :h "が" :r "ga"} ; for conjuntive particle が
     ;{:k "ヶ" :h "こ" :r "ko"} ; for counter too
     ; ヵ
     {:h "か" :k "ヵ" :r "ka"} ; for counter sometimes when pronounced "ka"
            
     ;other
     ; order counts - ぢ syllab forms must go after じ forms above
     {:k "ヂェ" :r "je" :ks "zye" :h "ぢぇ"} ; ぢぇ/ヂェ => je/zye
     {:k "クョ" :r "kyo" :h "くょ"} ; "kyo" duplicate; cannot input クョ -> low priority 
     {:k "グョ" :r "gyo" :h "ぐょ"} ; "gyo" duplicate; cannot input グョ -> low priority
    ]]
   (into [] (map #(assoc %
                    :h (or (:h %) (:k %))
                    :r (or (:r %) (:ks %))
                    :ks (or (:ks %) (:r %))
                    :w (or (:w %) (:r %) (:ks %)))
              m))))

(def ^:private kai-a "カガサザタダナハバパラマワヤャアァかがさざただなはばぱらまわやゃあ")
(def ^:private kai-i "キギシジチヂニヒビピミリイィきぎしじちぢにひびぴみりい")
(def ^:private kai-u "クグスズツヅフブプルムヴヌユュウゥくぐすずつづふぶぷるむぬゆゅうゔ")
(def ^:private kai-e "ケゲセゼテデヘベペメレネエェけげせぜてでへべぺめれねえ")
(def ^:private kai-o "コゴソゾトドノホボポモロヲヨョオォこごそぞとどのほぼぽろをよおょ")

(def ^:private long-vowel-symbols
  [{:o "a" :w "aa" :r "ā" :ks "â" :h "あ" :k "ー" :kai kai-a}
   {:o "i" :w "ii" :r "ī" :ks "î" :h "い" :k "ー" :kai kai-i}
   {:o "e" :w "ee" :r "ē" :ks "ê" :h "え" :k "ー" :kai kai-e}
   {:o "o" :w "ou" :r "ō" :ks "ô" :h "う" :k "ー" :kai kai-o} ; first
   {:o "o" :w "oo" :r "ō" :ks "ô" :h "お" :k "ー" :kai kai-o} ; second
   {:o "u" :w "uu" :r "ū" :ks "û" :h "う" :k "ー" :kai kai-u}
   {:o "A" :w "AA" :r "Ā" :ks "Â" :h "あ" :k "ー" :kai kai-a}
   {:o "I" :w "II" :r "Ī" :ks "Î" :h "い" :k "ー" :kai kai-i}
   {:o "U" :w "UU" :r "Ū" :ks "Û" :h "う" :k "ー" :kai kai-u}
   {:o "E" :w "EE" :r "Ē" :ks "Ê" :h "え" :k "ー" :kai kai-e}
   {:o "O" :w "OU" :r "Ō" :ks "Ô" :h "う" :k "ー" :kai kai-o}]) ; third

(def ^:private unpaired-katakana "゠ヷヸヹヺ・ーヿ")
(def ^:private unpaired-hiragana "゙゚゛゜ゟ")

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

(def ^:private sokuon-after-symbols
  (set (str "かがきぎくぐけげこごさざしじすずせぜそぞただちぢつづてでと"
            "どなにぬねのばぱびぴぶぷべぺぼぽまみむめもらりるれろゔゕゖ"
            "カガキギクグケゲコゴサザシジスズセゼソゾタダチヂツヅテデト"
            "ドナニヌネノバパビピブプベペボポマミムメモラリルレロヴヵヶ")))

(def ^:private consonants (set "bcdfghjklmnprstvwyz"))

(def ^:private vowels (set "ĀāĒēĪīŌōŪūÂâÊêÎîÔôÛûaiueoAIUEO"))

(def ^:private agyo (set "あいうえお")) ;TODO ー belongs?

(def ^:private romaji-common (set (str "abcdefghijkmnoprstuvwyz"
                                       "ABCDEFGHIJKMNOPRSTUVWYZ")))

(def ^:private hebon-dake (set "ĀāĒēĪīŌōŪū"))
(def ^:private kunrei-only (set "ÂâÊêÎîÔôÛû"))

(def ^:private kana-sokuon (set "っッ"))

; TODO complete this
(def ^:private yakumono-symbols
  (str "！？。（）｛｝［］ーヽヾゝゞっ゛・゜…※＊＠＃＄％＆｀｠￠￡＂｢￢｣"
       "､･￥￦＇￨￩：￪＋；［｛￫，＜＼｜￬－＝］｝￭．＞＾～ﾞ￮／＿｟ﾟ"))

(defn- ffilter
  "Returns the first item of coll for which (pred item) returns logical true.
   Consumes sequences up to the first match, will consume the entire sequence
   and return nil if no match is found.
   Shamelessly copied from: https://github.com/richhickey/clojure-contrib/blob/95dddbbdd748b0cc6d9c8486b8388836e6418848/src/main/clojure/clojure/contrib/seq.clj#L179"
  [pred coll]
  (first (filter pred coll)))

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
  "Checks if all character(s) in string have predicate f."
  [f s]
  (when s
    (if (char? s)
      (f s)
      (not (some (comp not f) s)))))

(defn- kw-subgroups
  "Returns the scripts belonging to the script keyword."
  [kw]
  (case kw
    :romaji [:romaji :kunrei-shiki :wapuro]
    [kw]))

(defn some-kana-sokuon-comp
  "Returs kana sokuon compound or nil if it doesn't exist."
  [s]
  (let [f #(when (kana-sokuon %2)
             (some->> %1 inc (nthrest s) first sokuon-after-symbols (str %2)))]
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

(def あぎょう？ agyo?)

(defn hiragana?
  "Checks if all character(s) in string are hiragana."
  [s] (str-pred hiragana-symbols s))

(def ひらがな？ hiragana?)

(defn hankaku-katakana?
  "Checks if all character(s) in string are hankaku-katakana."
  [s]
  (when s
    (if (char? s)
      (let [c (int s)]
        (and (<= 0xff61 c) (<= c 0xff9f)))
      (let [f #(let [c (some-> % int)]
              (and (<= 0xff61 c) (<= c 0xff9f)))]
        (not (some (comp not f) s))))))

(def はんかくカタカナ？ hankaku-katakana?)

(defn zenkaku-katakana?
  "Checks if all character(s) in string are zenkaku-katakana."
  [s]
  (when s
    (if (char? s)
      (let [c (int s)]
        (and (<= 0x30a0 c) (<= c 0x30ff)))
      (let [f #(let [c (some-> % int)]
              (and (<= 0x30a0 c) (<= c 0x30ff)))]
        (not (some (comp not f) s))))))

(def ぜんかくカタカナ？ zenkaku-katakana?)

(defn katakana?
  "Checks if all character(s) in string are katakana."
  [s] (str-pred (some-fn zenkaku-katakana? hankaku-katakana?) s))

(def カタカナ？ katakana?)

(defn kana?
  "Checks if all character(s) in string are kana."
  [s] (str-pred (some-fn katakana? hiragana?) s))

(def かな？ kana?)

(defn kunrei?
  "Checks if all character(s) in string are kunrei-shiki romaji."
  [s] (str-pred (some-fn romaji-common kunrei-only) s))

(def kunrei-shiki? kunrei?)
(def くんれい？ kunrei?)
(def くんれいしき？ kunrei?)

(defn hebon?
  "Checks if all character(s) in string are hebon romaji."
  [s] (str-pred (some-fn romaji-common hebon-dake) s))

(def hebon-shiki? hebon?)
(def ヘボン？ hebon?)
(def ヘボンしき？ hebon?)

(defn wapuro?
  "Checks if all character(s) in string are wāpuro romaji."
  [s] (str-pred romaji-common s))

(def ワープロ？ wapuro?)

(defn romaji?
  "Checks if all character(s) in string are any kind of romaji."
  [s] (str-pred (some-fn romaji-common hebon-dake kunrei-only) s))

(def ローマじ？ romaji?)

; TODO complete this
(defn yakumono?
  "Checks if all character(s) in string are japanese punctuation."
  [s]
  (str-pred
    #(or
       ; source: http://www.fontspace.com/unicode/block/CJK+Symbols+and+Punctuation
       (and (>= (int %) 0x3000) (<= (int %) 0x303f))
       ; source: http://unicode.org/charts/PDF/UFF00.pdf
       (some #{%} yakumono-symbols))
    s))

(def ^:private やくもの？ yakumono?)

; TODO complete this
(defn romaji-yakumono?
  "Checks if all character(s) in string are romaji punctuation."
  [s] (str-pred #(some #{%} " .,?!-(){}[]@#$%&`:*;£") s))

(def ローマじやくもの？ romaji-yakumono?)

(defn arabia-suji?
  "Checks if string is arabian numeral."
  [s]
  (re-matches
    (re-pattern (str hex-re-pattern "|[0-9]"))
    (str s)))

(def アラビアすうじ？ arabia-suji?)

(defn suji?
  "Checks if all character(s) in string are arabian numbers."
  [s] (str-pred #(some #{%} "０１２３４５６７８９ａｂｃｄｅｆＡＢＣＤＥＦ") s))

(def すうじ？ suji?)

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
      hiragana-chunk-re "|[\n\b\t\f\r]|.")))

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
    (kunrei-only c) :kunrei-shiki
    (romaji? c) :romaji
    (yakumono? c) :yakumono
    (romaji-yakumono? c) :romaji-yakumono
    (suji? c) :suji
    (arabia-suji? c) :arabia-suji
    (when-let [c (some-> c int)]
      (and (>= c 0x4e00) (<= c 0x9faf))) :kanji))

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
      (first
        (or (-> s last ffilter-syllab :r) s))
      (t {:h \っ :k \ッ}))))

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
;(suji "9aB") => "９あＢ"
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

(defn henkan
  "'henkan' means 'conversion'. Converts a string into target script.
   Converts only syllabs from source script when it's supplied."
  ([s target]
    (let [target (internal-script-kw target)
          convert-syllab (convert-syllab target)]
      (if (char? s)
        (some-> s convert-syllab first)
        (some->> s syllab-chunkify (map convert-syllab) (reduce str)))))

  ([s source target]
    (let [source (internal-script-kw source)
          target (internal-script-kw target)
          convert-syllab (convert-syllab target)
          convert-syllab
            #(if (some #{source} (kw-subgroups-of %)) (convert-syllab %) %)]
      (if (char? s)
        (some-> s convert-syllab first)
        (some->> s syllab-chunkify (map convert-syllab) (reduce str))))))

(def へんかん henkan)

(defn hiragana "Converts syllabs of string to hiragana."
  [s] (henkan s :hiragana))

(def ひらがな hiragana)

(defn zenkaku-katakana "Converts syllabs of string to zenkaku katakana."
  [s] (henkan s :katakana))

(def katakana zenkaku-katakana)
(def カタカナ zenkaku-katakana)
(def ゼンカクカタカナ zenkaku-katakana)

(defn romaji "Converts syllabs of string to romaji."
  [s] (henkan s :romaji))

(def ローマじ romaji)

(defn hebon "Converts syllabs of string to hebon."
  [s] (henkan s :hebon))

(def hebon-shiki hebon)
(def ヘボン hebon)
(def ヘボンしき hebon)

(defn kunrei "Converts syllabs of string to kunrei."
  [s] (henkan s :kunrei-shiki))

(def kunrei-shiki kunrei)
(def くんれい kunrei)
(def くんれいしき kunrei)

(defn wapuro "Converts syllabs of string to wāpuro."
  [s] (henkan s :wapuro))

(def ワープロ wapuro)

(defn romaji-yakumono "Returns romaji punctuation."
  [s] (henkan s :romaji-yakumono))

(def ローマじやくもの romaji-yakumono)

(defn yakumono
  [s] (henkan s :yakumono))

(def やくもの yakumono)

(defn suji
  "Returns japanese numerals."
  [s] (henkan s :suji))

(def すうじ suji)

(defn arabia-suji
  "Returns arabic numerals."
  [s] (henkan s :arabia-suji))

(def アラビアすうじ arabia-suji)

(defn hiragana->zenkaku-katakana
  "Converts syllabs in hiragana to katakana.
   Leaves the rest of the string intact."
  [s] (henkan s :hiragana :katakana))

(def hiragana->katakana hiragana->zenkaku-katakana)
(def ひらがな→カタカナ hiragana->zenkaku-katakana)
(def ひらがな→ぜんかくカタカナ hiragana->zenkaku-katakana)

(defn hiragana->romaji
  "Converts syllabs in hiragana to romaji.
   Leaves the rest of the string intact."
  [s] (henkan s :hiragana :romaji))

(def ひらがな→ローマじ hiragana->romaji)

(defn hiragana->hebon
  "Converts syllabs in hiragana to hebon.
   Leaves the rest of the string intact."
  [s] (henkan s :hiragana :hebon))

(def hiragana->hebon-shiki hiragana->hebon)
(def ひらがな→ヘボン hiragana->hebon)
(def ひらがな→ヘボンしき hiragana->hebon)

(defn hiragana->kunrei
  "Converts syllabs in hiragana to kunrei.
   Leaves the rest of the string intact."
  [s] (henkan s :hiragana :kunrei-shiki))

(def hiragana->kunrei-shiki hiragana->kunrei)
(def ひらがな→くんれい hiragana->kunrei)
(def ひらがな→くんれいしき hiragana->kunrei)

(defn hiragana->wapuro
  "Converts syllabs in hiragana to wāpuro.
   Leaves the rest of the string intact."
  [s] (henkan s :hiragana :wapuro))

(def ひらがな→ワープロ hiragana->wapuro)

(defn zenkaku-katakana->hiragana
  "Converts syllabs in katakana to hiragana.
   Leaves the rest of the string intact."
  [s] (henkan s :katakana :hiragana))

(def katakana->hiragana zenkaku-katakana->hiragana)
(def ぜんかくカタカナ→ひらがな zenkaku-katakana->hiragana)
(def カタカナ→ひらがな zenkaku-katakana->hiragana)

(defn zenkaku-katakana->romaji
  "Converts syllabs in katakana to romaji.
   Leaves the rest of the string intact."
  [s] (henkan s :katakana :romaji))

(def katakana->romaji zenkaku-katakana->romaji)
(def ぜんかくカタカナ→ローマじ zenkaku-katakana->romaji)
(def カタカナ→ローマじ zenkaku-katakana->romaji)

(defn zenkaku-katakana->hebon
  "Converts syllabs in katakana to hebon.
   Leaves the rest of the string intact."
  [s] (henkan s :katakana :hebon))

(def katakana->hebon zenkaku-katakana->hebon)
(def katakana->hebon-shiki zenkaku-katakana->hebon)
(def zenkaku-katakana->hebon-shiki zenkaku-katakana->hebon)
(def ぜんかくカタカナ→ヘボン zenkaku-katakana->hebon)
(def カタカナ→ヘボン zenkaku-katakana->hebon)
(def カタカナ→ヘボンしき zenkaku-katakana->hebon)
(def ぜんかくカタカナ→ヘボンしき zenkaku-katakana->hebon)

(defn zenkaku-katakana->kunrei
  "Converts syllabs in katakana to kunrei.
   Leaves the rest of the string intact."
  [s] (henkan s :katakana :kunrei-shiki))

(def katakana->kunrei-shiki zenkaku-katakana->kunrei)
(def katakana->kunrei zenkaku-katakana->kunrei)
(def zenkaku-katakana->kunrei-shiki zenkaku-katakana->kunrei)
(def ぜんかくカタカナ→くんれい zenkaku-katakana->kunrei)
(def カタカナ→くんれいしき zenkaku-katakana->kunrei)
(def カタカナ→くんれい zenkaku-katakana->kunrei)
(def ぜんかくカタカナ→ヘボンしき zenkaku-katakana->kunrei)

(defn zenkaku-katakana->wapuro
  "Converts syllabs in katakana to wāpuro.
   Leaves the rest of the string intact."
  [s] (henkan s :katakana :wapuro))

(def katakana->wapuro zenkaku-katakana->wapuro)
(def ぜんかくカタカナ→ワープロ zenkaku-katakana->wapuro)
(def カタカナ→ワープロ zenkaku-katakana->wapuro)

(defn romaji->hiragana
  "Converts syllabs in romaji to hiragana.
   Leaves the rest of the string intact."
  [s] (henkan s :romaji :hiragana))

(def ローマじ→ひらがな romaji->hiragana)

(defn romaji->zenkaku-katakana
  "Converts syllabs in romaji to hiragana.
   Leaves the rest of the string intact."
  [s] (henkan s :romaji :katakana))

(def romaji->katakana romaji->zenkaku-katakana)
(def ローマじ→ぜんかくカタカナ romaji->zenkaku-katakana)
(def ローマじ→カタカナ romaji->zenkaku-katakana)

(defn romaji->hebon
  "Converts syllabs in romaji to hebon.
   Leaves the rest of the string intact."
  [s] (henkan s :romaji :hebon))

(def romaji->hebon-shiki romaji->hebon)
(def ローマじ→ヘボン romaji->hebon)
(def ローマじ→ヘボンしき romaji->hebon)

(defn romaji->kunrei
  "Converts syllabs in romaji to kunrei.
   Leaves the rest of the string intact."
  [s] (henkan s :romaji :kunrei-shiki))

(def romaji->kunrei-shiki romaji->kunrei)
(def ローマじ→くんれい romaji->kunrei)
(def ローマじ→くんれいしき romaji->kunrei)

(defn romaji->wapuro
  "Converts syllabs in romaji to wāpuro.
   Leaves the rest of the string intact."
  [s] (henkan s :romaji :wapuro))

(def ローマじ→ワープロ romaji->wapuro)

(defn hebon->hiragana
  "Converts syllabs in hebon to hiragana.
   Leaves the rest of the string intact."
  [s] (henkan s :hebon :hiragana))

(def hebon-shiki->hiragana hebon->hiragana)
(def ヘボン→ひらがな hebon->hiragana)
(def ヘボンしき→ひらがな hebon->hiragana)

(defn hebon->zenkaku-katakana
  "Converts syllabs in hebon to katakana.
   Leaves the rest of the string intact."
  [s] (henkan s :hebon :katakana))

(def hebon->katakana hebon->zenkaku-katakana)
(def hebon-shiki->katakana hebon->zenkaku-katakana)
(def hebon-shiki->zenkaku-katakana hebon->zenkaku-katakana)
(def ヘボン→ぜんかくカタカナ hebon->zenkaku-katakana)
(def ヘボン→カタカナ hebon->zenkaku-katakana)
(def ヘボンしき→カタカナ hebon->zenkaku-katakana)
(def ヘボンしき→ぜんかくカタカナ hebon->zenkaku-katakana)

(defn hebon->romaji
  "Converts syllabs in hebon to romaji.
   Leaves the rest of the string intact."
  [s] (henkan s :hebon :romaji))

(def hebon-shiki->romaji hebon->romaji)
(def ヘボン→ローマじ hebon->romaji)
(def ヘボンしき→ローマじ hebon->romaji)

(defn hebon->kunrei
  "Converts syllabs in hebon to kunrei.
   Leaves the rest of the string intact."
  [s] (henkan s :hebon :kunrei-shiki))

(def hebon->kunrei-shiki hebon->kunrei)
(def hebon-shiki->kunrei hebon->kunrei)
(def hebon-shiki->kunrei-shiki hebon->kunrei)
(def ヘボン→くんれい hebon->kunrei)
(def ヘボン→くんれいしき hebon->kunrei)
(def ヘボンしき→くんれい hebon->kunrei)
(def ヘボンしき→くんれいしき hebon->kunrei)

(defn hebon->wapuro
  "Converts syllabs in hebon to wāpuro.
   Leaves the rest of the string intact."
  [s] (henkan s :hebon :wapuro))

(def hebon-shiki->wapuro hebon->wapuro)
(def ヘボン→ワープロ hebon->wapuro)
(def ヘボンしき→ワープロ hebon->wapuro)

(defn kunrei->hiragana
  "Converts syllabs in kunrei to hiragana.
   Leaves the rest of the string intact."
  [s] (henkan s :kunrei-shiki :hiragana))

(def kunrei-shiki->hiragana kunrei->hiragana)
(def くんれい→ひらがな kunrei->hiragana)
(def くんれいしき→ひらがな kunrei->hiragana)

(defn kunrei->zenkaku-katakana
  "Converts syllabs in kunrei to katakana.
   Leaves the rest of the string intact."
  [s] (henkan s :kunrei-shiki :katakana))

(def kunrei->katakana kunrei->zenkaku-katakana)
(def kunrei-shiki->katakana kunrei->zenkaku-katakana)
(def kunrei-shiki->zenkaku-katakana kunrei->zenkaku-katakana)
(def くんれい→ぜんかくカタカナ kunrei->zenkaku-katakana)
(def くんれい→カタカナ kunrei->zenkaku-katakana)
(def くんれいしき→カタカナ kunrei->zenkaku-katakana)
(def くんれいしき→ぜんかくカタカナ kunrei->zenkaku-katakana)

(defn kunrei->romaji
  "Converts syllabs in kunrei to romaji.
   Leaves the rest of the string intact."
  [s] (henkan s :kunrei-shiki :romaji))

(def kunrei-shiki->romaji kunrei->romaji)
(def くんれい→ローマじ kunrei->romaji)
(def くんれいしき→ローマじ kunrei->romaji)

(defn kunrei->hebon
  "Converts syllabs in kunrei to hebon.
   Leaves the rest of the string intact."
  [s] (henkan s :kunrei-shiki :hebon))

(def kunrei->hebon-shiki kunrei->hebon)
(def kunrei-shiki->hebon kunrei->hebon)
(def kunrei-shiki->hebon-shiki kunrei->hebon)
(def くんれい→ヘボン kunrei->hebon)
(def くんれい→ヘボンしき kunrei->hebon)
(def くんれいしき→ヘボン kunrei->hebon)
(def くんれいしき→ヘボンしき kunrei->hebon)

(defn kunrei->wapuro
  "Converts syllabs in kunrei to wāpuro.
   Leaves the rest of the string intact."
  [s] (henkan s :kunrei-shiki :wapuro))

(def kunrei-shiki->wapuro kunrei->wapuro)
(def くんれい→ワープロ kunrei->wapuro)
(def くんれいしき→ワープロ kunrei->wapuro)

;TODO desambiguation of long vowels
; methods:
;   1- segmentation
;   2- dictionary entries
;
; add ignore wapuro chunking option
; create wapuro-syllabs? / hebon-syllabs? / kunrei-syllabs?
; what category are: "＝" ?