(ns ; ^{:doc "Language symbols."}
  namban.shocho)

; TODO support conversion from old versions of hebon and nihon-shiki
(def syllab-maps
 (let [m
    [; TODO complete yakumono
     {:y "。" :ry "."} {:y "、" :ry ","} {:y "？" :ry "?"} {:y "！" :ry "!"}
     {:y "　" :ry " "} {:y "ゝ" :ry "ヽ"} {:y "ゞ" :ry "ヾ"} {:y "ー" :ry "-"}
     {:y "（" :ry "("} {:y "）" :ry ")"} {:y "｛" :ry "{"} {:y "｝" :ry "}"}
     {:y "［" :ry "["} {:y "］" :ry "]"} {:y "＠" :ry "@"} {:y "・" :ry " "}
     {:y "＃" :ry "#"} {:y "＄" :ry "$"} {:y "％" :ry "%"} {:y "＆" :ry "&"}
     {:y "｀" :ry "`"} {:y "：" :ry ":"} {:y "…" :ry "..."} {:y "＊" :ry "*"}
     {:y "※" :ry "*"} {:y "；" :ry ";"} {:y "￡" :ry "£"}
     
     ; this is some kind of yakumono, but can't be included above
     {:k "ヿ" :h "ゟ" :r " "}

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

(def ^:private unpaired-katakana "゠ヷヸヹヺ・ー")
(def ^:private unpaired-hiragana "゙゚゛゜")

(def long-vowel-symbols
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

(def katakana-symbols
  [(str
     "ァィゥェォッャュョアイウエオカガキギクグケゲコゴサザシジスズセゼソゾタダチヂツヅテデトドナニ"
     "ヌネノハバパヒビピフブプヘベペホボポマミムメモヤユヨラリルレロヮワヰヱヲンヴヵヶ")
  "ヽヾヿ" unpaired-katakana])

(def hiragana-symbols
  (set (str 
    "ぁぃぅぇぉっゃゅょあいうえおかがきぎくぐけげこごさざしじすずせぜそぞただちぢつづてでとどなに"
    "ぬねのはばぱひびぴふぶぷへべぺほぼぽまみむめもやゆよらりるれろゎわゐゑをんゔゕゖ"
    "ゝゞゟ" unpaired-hiragana)))

(def consonants (set "bcdfghjklmnprstvwyz"))

(def vowels (set "ĀāĒēĪīŌōŪūÂâÊêÎîÔôÛûaiueoAIUEO"))

(def agyo (set "あいうえお")) ;TODO ー belongs?

(def sokuon-after-symbols
  (set (str "かがきぎくぐけげこごさざしじすずせぜそぞただちぢつづてでと"
            "どなにぬねのばぱびぴぶぷべぺぼぽまみむめもらりるれろゔゕゖ"
            "カガキギクグケゲコゴサザシジスズセゼソゾタダチヂツヅテデト"
            "ドナニヌネノバパビピブプベペボポマミムメモラリルレロヴヵヶ")))

(def romaji-common (set (str "abcdefghijkmnoprstuvwyz"
                             "ABCDEFGHIJKMNOPRSTUVWYZ")))

(def hebon-dake (set "ĀāĒēĪīŌōŪū"))
(def kunrei-dake (set "ÂâÊêÎîÔôÛû"))

(def kana-sokuon (set "っッ"))

; TODO complete this
(def yakumono-symbols
  (str "！？。（）｛｝［］ーヽヾゝゞっ゛・゜…※＊＠＃＄％＆｀｠￠￡＂｢￢｣"
       "､･￥￦＇￨￩：￪＋；［｛￫，＜＼｜￬－＝］｝￭．＞＾～ﾞ￮／＿｟ﾟ"))
