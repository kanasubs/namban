(ns ; ^{:author "Carlos C. Fontes"}
  namban.test.boeki
#+cljs (:require-macros [cemerick.cljs.test :refer [is deftest]])
  (:require
       [namban.boeki :as boeki]
       [namban.shocho :as shocho]
       [namban.boeki :refer
         [some-sokuon-comp some-romaji-sokuon-comp some-kana-sokuon-comp
          hiragana hiragana? kana? kanji? scripts henkan
          hankaku-katakana? zenkaku-katakana? katakana katakana?
          kunrei kunrei? hebon? romaji romaji? wapuro?
          yakumono? yakumono romaji-yakumono? romaji-yakumono
          suji suji? arabia-suji arabia-suji?   
          hiragana->katakana hiragana->romaji hiragana->hebon hiragana->kunrei
          katakana->hiragana katakana->romaji katakana->hebon
          katakana->kunrei katakana->wapuro
          romaji->hiragana romaji->katakana romaji->hebon romaji->kunrei
          romaji->wapuro hebon->hiragana hebon->katakana hebon->romaji
          hebon->kunrei hebon->wapuro kunrei->hiragana kunrei->katakana
          kunrei->romaji kunrei->hebon kunrei->wapuro kunrei->hiragana]]
#+cljs [namban.kori :refer [var]]
#+cljs [cemerick.cljs.test :as t] ; or else cemerick no where to be found :D
 #+clj [clojure.test :refer [is deftest]]))

(deftest internal-script-kw-test
  (is (= ((var boeki/internal-script-kw) :hebon)
         :romaji))
  (is (= ((var boeki/internal-script-kw) :zenkaku-katakana)
         :katakana))
  (is (= ((var boeki/internal-script-kw) :hankaku-katakana)
         :hankaku-katakana))
  (is (= ((var boeki/internal-script-kw) :kunrei)
         :kunrei-shiki))
  (is (= ((var boeki/internal-script-kw) :wapuro)
         :wapuro))
  (is (= ((var boeki/internal-script-kw) :nanika)
         :nanika)))

(deftest kw-initials-test
  (is (= ((var boeki/kw-initials) :hiragana) :h))
  (is (= ((var boeki/kw-initials) :romaji) :r))
  (is (= ((var boeki/kw-initials) :hankaku-katakana) :hk))
  (is (= ((var boeki/kw-initials) :katakana) :k))
  (is (= ((var boeki/kw-initials) :kunrei-shiki) :ks))
  (is (= ((var boeki/kw-initials) :wapuro) :w))
  (is (= ((var boeki/kw-initials) :nanika) :n)))

(deftest kw-supergroup-test
  (is (= ((var boeki/kw-supergroup) :kunrei-shiki) :romaji)))

(deftest insert-pipe-test
  (is (fn? ((var boeki/insert-pipe) #(identity "何か"))))
  (is (= (((var boeki/insert-pipe) #(identity "何か")))
         "|何か")))

(deftest join-chunk-patterns-test
  (is (string? ((var boeki/join-chunk-patterns) #(str "|[ァ]ー?" (:kai %))))))

(deftest ffilter-syllab-test
  (is (map? ((var boeki/ffilter-syllab) "お")))
  (is (not ((var boeki/ffilter-syllab) "abc"))))

(deftest some-X-sokuon-test
  (is (= (some-romaji-sokuon-comp "kappa")
         "pp"))
  (is (-> "arigatou" some-romaji-sokuon-comp nil?))
  (is (= (some-kana-sokuon-comp "トイレット")
         "ット"))
  (is (-> "ぎんこう" some-kana-sokuon-comp nil?))
  (is (= (some-sokuon-comp "がんばって")
         "って"))
  (is (-> "oo" some-sokuon-comp nil?)))

(deftest hiragana?-test
#+clj (is (hiragana? \か))
      (is (hiragana? "しょう"))
      (is (-> "shi" hiragana? not))
      (is (-> nil hiragana? nil?)))

(deftest hankaku-katakana?-test
#+clj (is (hankaku-katakana? \ｶ))
#+clj (is (-> \カ hankaku-katakana? not))
      (is (hankaku-katakana? "ﾊﾟｿｺﾝ"))
      (is (-> "ふﾀ" hankaku-katakana? not))
      (is (-> nil hankaku-katakana? nil?)))

(deftest zenkaku-katakana?-test
#+clj (is (zenkaku-katakana? \ジ))
#+clj (is (-> \ｲ zenkaku-katakana? not))
      (is (zenkaku-katakana? "ハナ"))
      (is (-> "ｽとｱ" zenkaku-katakana? not))
      (is (-> nil zenkaku-katakana? nil?)))

(deftest katakana?-test
      (is (katakana? "ァ"))
      (is (katakana? "デパート"))
      (is (-> "デはート" katakana? not))
#+clj (is (katakana? \ヴ))
#+clj (is (katakana? \ヾ))
#+clj (is (-> \あ katakana? not))
#+clj (is (-> nil katakana? nil?)))

(deftest long-vowel-syllab?-test
  (is ((var boeki/long-vowel-syllab?) "kā"))
  (is ((var boeki/long-vowel-syllab?) "かあ"))
  (is ((var boeki/long-vowel-syllab?) "シャー"))
  (is ((var boeki/long-vowel-syllab?) "ū"))
  (is (not ((var boeki/long-vowel-syllab?) "か")))
  (is (not ((var boeki/long-vowel-syllab?) "ka"))))

(deftest kunrei?-test
#+clj (is (kunrei? \â))
      (is (kunrei? "â"))
      (is (-> "ā" kunrei? not))
      (is (-> "あ" kunrei? not))
      (is (-> "ア" kunrei? not))
      (is (-> nil kunrei? nil?)))

(deftest hebon?-test
  (is (hebon? "shū"))
  (is (hebon? "shu"))
  (is (-> "shâ" hebon? not))
  (is (-> "しゅ" hebon? not))
  (is (-> "シュ" hebon? not))
  (is (-> nil hebon? nil?)))

(deftest romaji?-test
#+clj (is (romaji? \ā))
      (is (romaji? "a"))
      (is (romaji? "âa"))
      (is (-> "âあa" romaji? not))
      (is (-> nil romaji? not))
#+clj (is (-> \` romaji? not))
#+clj (is (-> \{ romaji? not))
#+clj (is (-> \@ romaji? not))
#+clj (is (-> \[ romaji? not)))

(deftest yakumono-and-punctuation-test
  (is (yakumono?
        (str "　。、？！（）｛｝［］ー【】〔〕〈〉《》「」『』「」『』々ヽヾゝゞ〃っ゛・゜〆"
             "〜…※＊〓〄〇〒〖〗〘〙〚〛〝〞〟〠〡〢〣〤〥〦〧〨〩〪〭〮〯〫〬〰〱〲〳〴〵〶"
            "〷〸〹〺〻〼〽〾〿")))
  (is (yakumono? "〱〵〹〺。？！"))
  (is (-> nil yakumono? nil?))
  (is (romaji-yakumono? " ."))
  (is (= (yakumono "San byaku yen desu.") "San　byaku　yen　desu。"))
  (is (= (romaji-yakumono "三　百円　です。") "三 百円 です.")))

(deftest numerals-test
  (is (suji? "９ａＢ"))
  (is (arabia-suji? "x10aB"))
  (is (arabia-suji? "1"))
  (is (-> "0aB" arabia-suji? not))
  (is (= (suji "9aB") "９aB"))
  (is (= (suji "0x9eab") "０x９ａｂ"))
  (is (= (suji "っと") "っと"))
  (is (= (arabia-suji "９ａＢ") "9aB")))

(deftest CJK-Unified-Ideographs-test
  (is (kanji? "人")) ; basic
  (is (kanji? "㐰")) ; extension A
  (is (kanji? "爫"))) ; compatibility
; support for 16 bits only in java and those are off range:
;  (kanji? "𠃵") => truthy ; extension B
; can't test extension C - no fonts installed
;  (kanji? "𫝆") => truthy ; extension D
;  (kanji? "杓") => truthy ; compatibility supplement

(deftest wapuro?-test
      (is (wapuro? "e"))
#+clj (is (wapuro? \r))
#+clj (is (-> \î wapuro? not))
      (is (-> nil wapuro? nil?)))

(deftest kana?-test
#+clj (is (kana? \ア))
      (is (kana? "はなび"))
      (is (kana? "ソフト"))
      (is (-> "karasu" kana? not))
      (is (-> nil kana? nil?)))

(deftest syllab-chunkify-test
      (is (= ((var boeki/syllab-chunkify) " あ") [" " "あ"]))
      (is (= ((var boeki/syllab-chunkify) "あ ") ["あ" " "]))
#+clj (is (= ((var boeki/syllab-chunkify) \あ) ["あ"]))
      (is (= ((var boeki/syllab-chunkify) "かたなaei") ["か" "た" "な" "a" "e" "i"]))
      (is (= ((var boeki/syllab-chunkify) "hiシャー") ["hi" "シャー"]))
      (is (= ((var boeki/syllab-chunkify) "ou") ["ou"]))
      (is (= ((var boeki/syllab-chunkify) (str "a bu na i de su ka ra ki ro i se n no "
                                "u chi ga wa de o ma chi ku da sa i"))
       ["a" " " "bu" " " "na" " " "i" " " "de" " " "su" " " "ka" " " "ra" " "
        "ki" " " "ro" " " "i" " " "se" " " "n" " " "no" " " "u" " " "chi" " "
        "ga" " " "wa" " " "de" " " "o" " " "ma" " " "chi" " " "ku" " " "da" " "
        "sa" " " "i"]))
  (is (= ((var boeki/syllab-chunkify) "どうイタシマシテ")
         ["どう" "イ" "タ" "シ" "マ" "シ" "テ"]))
  (is (= ((var boeki/syllab-chunkify) "hiしゃあ")
         ["hi" "しゃあ"]))
  (is (= ((var boeki/syllab-chunkify) "くい")
         ["く" "い"]))
  (is (= ((var boeki/syllab-chunkify) "カップ")
         ["カ" "ップ"]))
  (is (= ((var boeki/syllab-chunkify) "いらっしゃいませ")
         ["い" "ら" "っしゃ" "い" "ま" "せ"]))
  (is (= ((var boeki/syllab-chunkify) "kappa")
         ["ka" "ppa"]))
  (is (= ((var boeki/syllab-chunkify) "tsu")
         ["tsu"]))
  (is (= ((var boeki/syllab-chunkify) "kan'i")
         ["ka" "n'" "i"]))
  (is (= ((var boeki/syllab-chunkify) "kan-i")
         ["ka" "n-" "i"]))
  (is (= ((var boeki/syllab-chunkify)
    (str
      "うぅイィyiいぃイェyeいぇウァうぁウィwiうぃウゥwuウェweうぇウォwoうぉウュwyuうゅヴァva"
      "ゔぁヴィviゔぃvuヴェveゔぇヴォvoゔぉヴャvyaゔゃヴュvyuゔゅヴィェvyeゔぃぇヴョvyo"
      "ゔょキェkyeきぇギェgyeぎぇクァkwaくぁクィkwiくぃクェkweくぇクォkwoくぉクヮkwaグァ"
      "gwaぐぁグィgwiぐぃグェgweぐぇグォgwoぐぉグヮしぇシェじぇジェjezyeスィsiすぃズィzi"
      "ずぃちぇチェchetyeツァtsaつぁつぃツィtsiつぇツェtseつぉツォtsoツュtsyuつゅティti"
      "てぃトゥtuテュtyuてゅディdiでぃドゥduデュdyuでゅニェnyeにぇヒェhyeひぇビェbyeびぇ"
      "ピェpyeぴぇファfaふぁフィfiふぃフェfeふぇフォfoふぉフャfyaふゃフュfyuふゅフィェfye"
      "ふぃぇフョfyoふょホゥミェmyeみぇリェryeりぇラ゜laら゜リ゜liり゜ル゜luる゜レ゜leれ゜"
      "ロ゜loろ゜vavivevoぢゃヂャjadyaぢょヂョjodyoぢぇヂェjezyeクョ"
      "kyoくょグョgyoぐょくゎ"))

         ["うぅ" "イィ" "yi" "いぃ" "イェ" "ye" "いぇ" "ウァ" "うぁ" "ウィ" "wi" "うぃ"
          "ウゥ" "wu" "ウェ" "we" "うぇ" "ウォ" "wo" "うぉ" "ウュ" "wyu" "うゅ" "ヴァ"
          "va" "ゔぁ" "ヴィ" "vi" "ゔぃ" "vu" "ヴェ" "ve" "ゔぇ" "ヴォ" "vo" "ゔぉ"
          "ヴャ" "vya" "ゔゃ" "ヴュ" "vyu" "ゔゅ" "ヴィェ" "vye" "ゔぃぇ" "ヴョ" "vyo"
          "ゔょ" "キェ" "kye" "きぇ" "ギェ" "gye" "ぎぇ" "クァ" "kwa" "くぁ" "クィ"
          "kwi" "くぃ" "クェ" "kwe" "くぇ" "クォ" "kwo" "くぉ" "クヮ" "kwa" "グァ"
          "gwa" "ぐぁ" "グィ" "gwi" "ぐぃ" "グェ" "gwe" "ぐぇ" "グォ" "gwo" "ぐぉ"
          "グヮ" "しぇ" "シェ" "じぇ" "ジェ" "je" "zye" "スィ" "si" "すぃ" "ズィ" "zi"
          "ずぃ" "ちぇ" "チェ" "che" "tye" "ツァ" "tsa" "つぁ" "つぃ" "ツィ" "tsi"
          "つぇ" "ツェ" "tse" "つぉ" "ツォ" "tso" "ツュ" "tsyu" "つゅ" "ティ" "ti"
          "てぃ" "トゥ" "tu" "テュ" "tyu" "てゅ" "ディ" "di" "でぃ" "ドゥ" "du" "デュ"
          "dyu" "でゅ" "ニェ" "nye" "にぇ" "ヒェ" "hye" "ひぇ" "ビェ" "bye" "びぇ"
          "ピェ" "pye" "ぴぇ" "ファ" "fa" "ふぁ" "フィ" "fi" "ふぃ" "フェ" "fe" "ふぇ"
          "フォ" "fo" "ふぉ" "フャ" "fya" "ふゃ" "フュ" "fyu" "ふゅ" "フィェ" "fye"
          "ふぃぇ" "フョ" "fyo" "ふょ" "ホゥ" "ミェ" "mye" "みぇ" "リェ" "rye" "りぇ"
          "ラ゜" "la" "ら゜" "リ゜" "li" "り゜" "ル゜" "lu" "る゜" "レ゜" "le" "れ゜"
          "ロ゜" "lo" "ろ゜" "va" "vi" "ve" "vo" "ぢゃ" "ヂャ" "ja"
          "dya" "ぢょ" "ヂョ" "jo" "dyo" "ぢぇ" "ヂェ" "je" "zye" "クョ" "kyo"
          "くょ" "グョ" "gyo" "ぐょ" "くゎ"]))
  ;(\backspace \tab \newline \formfeed \return \space)
  (is (= ((var boeki/syllab-chunkify) \newline) ["\n"])) ; cannot use [\b\t\n\f\r] directly
  (is (= ((var boeki/syllab-chunkify) "\b\t\n\f\r") ["\b" "\t" "\n" "\f" "\r"]))
  (is (nil? ((var boeki/syllab-chunkify) nil))))

(deftest char-jp-script-test
  (is (= ((var boeki/char-jp-script) \a) :romaji))
  (is (= ((var boeki/char-jp-script) \あ) :hiragana))
  (is (= ((var boeki/char-jp-script) \ア) :zenkaku-katakana))
  (is (nil? ((var boeki/char-jp-script) \q)))
  (is (nil? ((var boeki/char-jp-script) nil))))

(deftest kw-subgroups-of-test
  (is (= ((var boeki/kw-subgroups-of) "どうしたの？") #{:hiragana :yakumono})))

(deftest zenkaku-katakana-test
  (is (= (scripts "ぶんしんさば") #{:hiragana}))
  (is (= (scripts "shinjitsuハひとつ") #{:romaji :zenkaku-katakana :hiragana}))
  (is (= (scripts "パソコンが難しいです。")
         #{:zenkaku-katakana :hiragana :kanji :yakumono})))

(deftest sokuon-for-test
  (is (= ((var boeki/sokuon-for) "ss" :hiragana) "っ"))
  (is (= ((var boeki/sokuon-for) "っし" :romaji) "s"))
  (is (= ((var boeki/sokuon-for) "tt" :romaji) "t")))

(deftest convert-syllab-to-romaji-test
  (let [convert-syllab-to-romaji ((var boeki/convert-syllab) :romaji)
        convert-syllab-to-wapuro ((var boeki/convert-syllab) :wapuro)
        convert-vanilla-syllab-to-hiragana ((var boeki/convert-syllab) :hiragana)
        convert-vanilla-syllab-to-katakana ((var boeki/convert-syllab) :katakana)
        convert-vanilla-syllab-to-wapuro ((var boeki/convert-syllab) :wapuro)
        convert-vanilla-syllab-to-kunrei ((var boeki/convert-syllab) :kunrei-shiki)]
    (is (= (convert-syllab-to-romaji "st") "st"))
    (is (= (convert-syllab-to-romaji "sta") "sta"))
    (is (= (convert-syllab-to-romaji "shi") "shi"))
    (is (= (convert-syllab-to-romaji "し") "shi"))
    (is (= (convert-syllab-to-romaji "シャー") "shā"))
    (is (= (convert-syllab-to-romaji "お") "o"))
    (is (= (convert-vanilla-syllab-to-hiragana "o") "お"))
    (is (= (convert-vanilla-syllab-to-katakana "o") "オ"))
    (is (= (convert-vanilla-syllab-to-wapuro "お") "o"))
    (is (= (convert-vanilla-syllab-to-kunrei "お") "o"))
    (is (= (convert-syllab-to-wapuro "おう") "ou"))
    (is (= (convert-vanilla-syllab-to-kunrei "tsu") "tu"))))

(deftest apply-long-vowel-test
  (is (= ((var boeki/apply-long-vowel) "し" {:h "い"}) "しい"))
  (is (= ((var boeki/apply-long-vowel) "し" {:h "い"} :hiragana) "しい"))
  (is (= ((var boeki/apply-long-vowel) "シ" {:k "ー"}) "シー"))
  (is (= ((var boeki/apply-long-vowel) "shi" {:r "ī"}) "shī"))
  (is (= ((var boeki/apply-long-vowel) "shi" {:ks "î"} :kunrei-shiki) "shî"))
  (is (nil? ((var boeki/apply-long-vowel) nil {:k "ー"}))))

(deftest find-long-vowel-map-test
  (is (= ((var boeki/find-long-vowel-map) "ラー")
         {:o "a" :w "aa" :r "ā" :ks "â" :h "あ" :k "ー" :kai (-> shocho/kai-a var #+clj deref)}))
  (is (= ((var boeki/find-long-vowel-map) "リー")
         {:o "i" :w "ii" :r "ī" :ks "î" :h "い" :k "ー" :kai (-> shocho/kai-i var #+clj deref)}))
  (is (= ((var boeki/find-long-vowel-map) "ルー")
         {:o "u" :w "uu" :r "ū" :ks "û" :h "う" :k "ー" :kai (-> shocho/kai-u var #+clj deref)}))
  (is (= ((var boeki/find-long-vowel-map) "レー")
         {:o "e" :w "ee" :r "ē" :ks "ê" :h "え" :k "ー" :kai (-> shocho/kai-e var #+clj deref)}))
  (is (= ((var boeki/find-long-vowel-map) "ロー")
         {:o "o" :w "ou" :r "ō" :ks "ô" :h "う" :k "ー" :kai (-> shocho/kai-o var #+clj deref)}))
  (is (= ((var boeki/find-long-vowel-map) "shū")
         {:o "u" :w "uu" :r "ū" :ks "û" :h "う" :k "ー" :kai (-> shocho/kai-u var #+clj deref)})))

(deftest long-vowel-conversions-test
  (is (= (((var boeki/convert-syllab) :katakana) "おう") "オー"))
  (is (= (((var boeki/convert-syllab) :romaji) "おう") "ō"))
  (is (= (((var boeki/convert-syllab) :kunrei-shiki) "おう") "ô"))
  (is (= (((var boeki/convert-syllab) :wapuro) "おう") "ou"))

  (is (= (((var boeki/convert-syllab) :katakana) "おお") "オー"))
  (is (= (((var boeki/convert-syllab) :romaji) "おお") "ō"))
  (is (= (((var boeki/convert-syllab) :kunrei-shiki) "おお") "ô"))
  (is (= (((var boeki/convert-syllab) :wapuro) "おお") "oo"))

  (is (= (((var boeki/convert-syllab) :hiragana) "ō") "おう"))
  (is (= (((var boeki/convert-syllab) :katakana) "ō") "オー"))
  (is (= (((var boeki/convert-syllab) :kunrei-shiki) "ō") "ô"))
  (is (= (((var boeki/convert-syllab) :wapuro) "ō") "ou"))

  (is (= (((var boeki/convert-syllab) :hiragana) "オー") "おう"))
  (is (= (((var boeki/convert-syllab) :romaji) "オー") "ō"))
  (is (= (((var boeki/convert-syllab) :kunrei-shiki) "オー") "ô"))
  (is (= (((var boeki/convert-syllab) :wapuro) "オー") "ou"))

  (is (= (((var boeki/convert-syllab) :katakana) "うう") "ウー"))
  (is (= (((var boeki/convert-syllab) :romaji) "うう") "ū"))
  (is (= (((var boeki/convert-syllab) :kunrei-shiki) "うう") "û"))
  (is (= (((var boeki/convert-syllab) :wapuro) "うう") "uu"))

  (is (= (((var boeki/convert-syllab) :hiragana) "ウー") "うう"))
  (is (= (((var boeki/convert-syllab) :romaji) "ウー") "ū"))
  (is (= (((var boeki/convert-syllab) :kunrei-shiki) "ウー") "û"))
  (is (= (((var boeki/convert-syllab) :wapuro) "ウー") "uu"))

  (is (= (((var boeki/convert-syllab) :hiragana) "ū") "うう"))
  (is (= (((var boeki/convert-syllab) :katakana) "ū") "ウー"))
  (is (= (((var boeki/convert-syllab) :kunrei-shiki) "ū") "û"))
  (is (= (((var boeki/convert-syllab) :wapuro) "ū") "uu"))

  (is (= (((var boeki/convert-syllab) :katakana) "ou") "オー"))
  (is (= (((var boeki/convert-syllab) :romaji) "ou") "ō"))
  (is (= (((var boeki/convert-syllab) :kunrei-shiki) "ou") "ô"))
  (is (= (((var boeki/convert-syllab) :hiragana) "ou") "おう"))

  (is (= (((var boeki/convert-syllab) :katakana) "oo") "オー"))
  (is (= (((var boeki/convert-syllab) :romaji) "oo") "ō"))
  (is (= (((var boeki/convert-syllab) :kunrei-shiki) "oo") "ô"))
  (is (= (((var boeki/convert-syllab) :hiragana) "oo") "おお"))

  (is (= (((var boeki/convert-syllab) :hiragana) "aa") "ああ")))

(deftest long-vowel-syllab-conversions-test
  (is (= (((var boeki/convert-syllab) :romaji) "しょう") "shō"))
  (is (= (((var boeki/convert-syllab) :hiragana) "チョー") "ちょう"))
  (is (= (((var boeki/convert-syllab) :hiragana) "shū") "しゅう"))
  (is (= (((var boeki/convert-syllab) :katakana) "shū") "シュー"))
  (is (= (((var boeki/convert-syllab) :katakana) "しゅう") "シュー"))
  (is (= (((var boeki/convert-syllab) :romaji) "かあ") "kā"))
  (is (= (((var boeki/convert-syllab) :romaji) "しゅう") "shū"))
  (is (= (((var boeki/convert-syllab) :hiragana) "シュー") "しゅう"))
  (is (= (((var boeki/convert-syllab) :romaji) "シュー") "shū")))

(deftest henkan-test
      (is (= (henkan "ō" :hiragana) "おう"))
      (is (= (henkan "ō" :romaji :hiragana) "おう"))
      (is (= (henkan " あ" :romaji) " a"))
      (is (= (henkan "あ " :romaji) "a "))
      (is (= (henkan "ア オ エ ウ イ あ お え う い" :romaji) "a o e u i a o e u i"))
#+clj (is (= (henkan \あ :romaji) \a))
      (is (= (henkan "あ" :romaji) "a"))
      (is (= (henkan "アオエウイあおえうい" :katakana :romaji) "aoeuiあおえうい"))
      (is (= (henkan "アオエウイあおえうい" :katakana :hiragana) "あおえういあおえうい"))
      (is (= (henkan "abunaidesukarakiroisen no uchigawadeomachikudasai" :hiragana)
             "あぶないですからきろいせん の うちがわでおまちください")))

(deftest hiragana-test
  (is (= (hiragana "ドーitashimashite") "どういたしまして"))
  (is (= (hiragana "namban") "なんばん"))
  (is (= (hiragana "kan'i") "かんい"))
  (is (= (hiragana "kan-i") "かんい"))
  (is (= (hiragana "ヷヸヹヺ") "ゔぁゔぃゔぇゔぉ"))
  (is (-> nil hiragana nil?)))

(deftest katakana-test
  (is (= (katakana "どうitashimashite") "ドーイタシマシテ"))
  (is (-> nil katakana nil?)))

(deftest romaji-test
  (is (= (romaji "どうイタシマシテ") "dōitashimashite"))
  (is (-> nil romaji nil?)))

(deftest kunrei-test
  (is (= (kunrei "shūpatsu") "syûpatu"))
  (is (-> nil kunrei nil?)))

(deftest source-target-conversions-test
  (let [hiragana-juxt
          (juxt hiragana->katakana hiragana->romaji hiragana->hebon hiragana->kunrei)]
    (is (= (hiragana-juxt "hiしゃあ") ["hiシャー" "hishā" "hishā" "hisyâ"])))

  (is (= (katakana->hiragana "hiシャー") "hiしゃあ"))
  (is (= (katakana->romaji "カッパ") "kappa"))
  (is (= (katakana->hebon "hiシャー") "hishā"))
  (is (= (hiragana->hebon "まっ") "matsu"))
  (is (= (katakana->hebon "ッ") "tsu"))
  (is (= (katakana->kunrei "hiシャー") "hisyâ"))
  (is (= (katakana->wapuro "hiシャー") "hishaa"))

  (is (= (romaji->hiragana "koppu") "こっぷ"))
  (is (= (romaji->katakana "shūさあ") "シューさあ"))
  (is (= (romaji->hebon "shūさあ") "shūさあ"))
  (is (= (romaji->kunrei "shūさあ") "syûさあ"))
  (is (= (romaji->wapuro "shūさあ") "shuuさあ"))

  (is (= (hebon->hiragana "shūさあ") "しゅうさあ"))
  (is (= (hebon->katakana "shūさあ") "シューさあ"))
  (is (= (hebon->romaji "shūさあ") "shūさあ"))
  (is (= (hebon->kunrei "shūさあ") "syûさあ"))
  (is (= (hebon->wapuro "shūさあ") "shuuさあ"))

  (is (= (kunrei->hiragana "shûさあ") "しゅうさあ"))
  (is (= (kunrei->katakana "shûさあ") "シューさあ"))
  (is (= (kunrei->romaji "shûさあ") "shūさあ"))
  (is (= (kunrei->hebon "shûさあ") "shūさあ"))
  (is (= (kunrei->wapuro "shûさあ") "shuuさあ"))
  (is (= (kunrei->hiragana "sha") "しゃ"))

  (is (-> nil katakana->kunrei nil?)))

(deftest ヿ-ゟ-space-test
  (is (= (hiragana "ヿ") "ゟ"))
  (is (= (katakana "ゟ") "ヿ"))
  (is (= (romaji "ヿゟ") "  ")))
