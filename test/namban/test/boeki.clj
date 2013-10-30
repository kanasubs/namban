(ns namban.test.boeki
	(:use midje.sweet namban.boeki)
	(:require [namban.boeki :as boeki]))

(fact "japanese symbol global vars"
	(first @#'boeki/syllab-maps) => map?
  (first @#'boeki/long-vowel-symbols) => map?
	@#'boeki/unpaired-katakana => string?
	@#'boeki/unpaired-hiragana => string?
	@#'boeki/katakana-symbols => vector?
	@#'boeki/hiragana-symbols => set?
  @#'boeki/consonants => set?
  @#'boeki/vowels => set?
  @#'boeki/agyo => set?
  @#'boeki/sokuon-after-symbols => set?)

(fact
  (@#'boeki/romaji-common \c) => truthy
  (@#'boeki/romaji-common "c") => falsey
  (@#'boeki/romaji-common \あ) => falsey
  (@#'boeki/romaji-common \â) => falsey
  (@#'boeki/romaji-common \ナ) => falsey
  (@#'boeki/romaji-common \と) => falsey)

(fact
  (@#'boeki/hebon-dake \ā) => truthy
  (@#'boeki/hebon-dake "ā") => falsey
  (@#'boeki/hebon-dake \d) => falsey
  (@#'boeki/hebon-dake \あ) => falsey
  (@#'boeki/hebon-dake \ア) => falsey)

(fact
  (@#'boeki/kunrei-only \â) => truthy
  (@#'boeki/kunrei-only "â") => falsey
  (@#'boeki/kunrei-only \ā) => falsey
  (@#'boeki/kunrei-only \あ) => falsey
  (@#'boeki/kunrei-only \ア) => falsey)

(fact
  (@#'boeki/ffilter even? [3 2 1 0]) => 2
  (@#'boeki/ffilter even? [3 1]) => nil)

(fact
  (#'boeki/internal-script-kw :hebon) => :romaji
  (#'boeki/internal-script-kw :zenkaku-katakana) => :katakana
  (#'boeki/internal-script-kw :hankaku-katakana) => :hankaku-katakana
  (#'boeki/internal-script-kw :kunrei) => :kunrei-shiki
  (#'boeki/internal-script-kw :wapuro) => :wapuro
  (#'boeki/internal-script-kw :nanika) => :nanika)

(fact
  (#'boeki/kw-initials :hiragana) => :h
  (#'boeki/kw-initials :romaji) => :r
  (#'boeki/kw-initials :hankaku-katakana) => :hk
  (#'boeki/kw-initials :katakana) => :k
  (#'boeki/kw-initials :kunrei-shiki) => :ks
  (#'boeki/kw-initials :wapuro) => :w
  (#'boeki/kw-initials :nanika) => :n)

(fact
  (#'boeki/kw-supergroup :kunrei-shiki) => :romaji)

(fact
  (#'boeki/insert-pipe #(identity "何か")) => fn?
  ((#'boeki/insert-pipe #(identity "何か"))) => "|何か")

(fact
  (#'boeki/join-chunk-patterns #(str "|[ァ]ー?" (:kai %))) => string?)

(fact
  @#'boeki/kana-sokuon => set?
  (some-romaji-sokuon-comp "kappa") => "pp"
  (some-romaji-sokuon-comp "arigatou") => nil
  (some-kana-sokuon-comp "トイレット") => "ット"
  (some-kana-sokuon-comp "ぎんこう") => nil
  (some-sokuon-comp "がんばって") => "って"
  (some-sokuon-comp "oo") => nil)

(fact
  \か => hiragana?
  "しょう" => hiragana?
  "shi" => (complement hiragana?)
  nil => (complement hiragana?))

(fact
  \ｶ => hankaku-katakana?
  \カ => (complement hankaku-katakana?)
  "ﾊﾟｿｺﾝ" => hankaku-katakana?
  "ふﾀ" => (complement hankaku-katakana?)
  nil => (complement hankaku-katakana?))

(fact
  \ジ => zenkaku-katakana?
  \ｲ => (complement zenkaku-katakana?)
  "ハナ" => zenkaku-katakana?
  "ｽとｱ" => (complement zenkaku-katakana?)
  nil => (complement zenkaku-katakana?))

(fact
  "ァ" => katakana?
  "デパート" => katakana?
  "デはート" => (complement katakana?)
  \ヾ => katakana?
  \ヴ => katakana?
  \あ => (complement katakana?)
  nil => (complement katakana?))


(fact
  (#'boeki/long-vowel-syllab? "kā") => truthy  
  (#'boeki/long-vowel-syllab? "かあ") => truthy
  (#'boeki/long-vowel-syllab? "シャー") => truthy
  (#'boeki/long-vowel-syllab? "ū") => truthy
  (#'boeki/long-vowel-syllab? "か") => falsey
  (#'boeki/long-vowel-syllab? "ka") => falsey)

(fact
  \â  => kunrei?
  "â" => kunrei?
  "ā" => (complement kunrei?)
  "あ" => (complement kunrei?)
  "ア" => (complement kunrei?)
  nil => (complement kunrei?))

(fact
  "shū" => hebon?
  "shu" => hebon?
  "shâ" => (complement hebon?)
  "しゅ" => (complement hebon?)
  "シュ" => (complement hebon?)
  nil => (complement hebon?))

(fact
  \ā => romaji?
  "a" => romaji?
  "âa" => romaji?
  "âあa" => (complement romaji?)
  nil => (complement romaji?)
  \` => (complement romaji?)
  \{ => (complement romaji?)
  \@ => (complement romaji?)
  \[ => (complement romaji?))

(fact "yakumono and punctuation"
  (yakumono?
    (str "　。、？！（）｛｝［］ー【】〔〕〈〉《》「」『』「」『』々ヽヾゝゞ〃っ゛・゜〆〜…※＊〓"
         "〄〇〒〖〗〘〙〚〛〝〞〟〠〡〢〣〤〥〦〧〨〩〪〭〮〯〫〬〰〱〲〳〴〵〶〷〸〹〺〻〼〽〾〿"))
    => truthy
  (romaji-yakumono? " .") => truthy
  (yakumono "San byaku yen desu.") => "San　byaku　yen　desu。"
  (romaji-yakumono "三　百円　です。") => "三 百円 です.")

(fact "numerals"
  (suji? "９ａＢ") => truthy
  (arabia-suji? "x10aB") => truthy
  (arabia-suji? "1") => truthy
  (arabia-suji? "0aB") => falsey
  (suji "9aB") => "９aB"
  (suji "0x9eab") => "０x９ａｂ"
  (suji "っと") => "っと"
  (arabia-suji "９ａＢ") => "9aB")

(fact
  "e" => wapuro?
  \r => wapuro?
  \î => (complement wapuro?)
  nil => (complement wapuro?))

(fact
  "〱〵〹〺。？！" => yakumono?
  nil => (complement yakumono?))

(fact
  \ア => kana?
  "はなび" => kana?
  "ソフト" => kana?
  "karasu" => (complement kana?)
  nil => (complement kana?))

(fact
  (#'boeki/syllab-chunkify " あ") => (just [" " "あ"])
  (#'boeki/syllab-chunkify "あ ") => (just ["あ" " "])
  (#'boeki/syllab-chunkify \あ) => ["あ"]
  (#'boeki/syllab-chunkify "かたなaei") => ["か" "た" "な" "a" "e" "i"]
  (#'boeki/syllab-chunkify "hiシャー") => ["hi" "シャー"]
  (#'boeki/syllab-chunkify "ou") => ["ou"]
  (#'boeki/syllab-chunkify (str "a bu na i de su ka ra ki ro i se n no "
                                "u chi ga wa de o ma chi ku da sa i"))
    => ["a" " " "bu" " " "na" " " "i" " " "de" " " "su" " " "ka" " " "ra" " "
        "ki" " " "ro" " " "i" " " "se" " " "n" " " "no" " " "u" " " "chi" " "
        "ga" " " "wa" " " "de" " " "o" " " "ma" " " "chi" " " "ku" " " "da" " "
        "sa" " " "i"]
  (#'boeki/syllab-chunkify "どうイタシマシテ") => ["どう" "イ" "タ" "シ" "マ" "シ" "テ"]
  (#'boeki/syllab-chunkify "hiしゃあ") => ["hi" "しゃあ"]
  (#'boeki/syllab-chunkify "くい") => ["く" "い"]
  (#'boeki/syllab-chunkify "カップ") => ["カ" "ップ"]
  (#'boeki/syllab-chunkify "いらっしゃいませ") => ["い" "ら" "っしゃ" "い" "ま" "せ"]  
  (#'boeki/syllab-chunkify "kappa") => ["ka" "ppa"]
  (#'boeki/syllab-chunkify "tsu") => ["tsu"]
  (#'boeki/syllab-chunkify "kan'i") => ["ka" "n'" "i"]
  (#'boeki/syllab-chunkify "kan-i") => ["ka" "n-" "i"]
  (#'boeki/syllab-chunkify
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
    => (just
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
          "くょ" "グョ" "gyo" "ぐょ" "くゎ"])
  ;(\backspace \tab \newline \formfeed \return \space)
  (#'boeki/syllab-chunkify \newline) => ["\n"] ; cannot use [\b\t\n\f\r] directly
  (#'boeki/syllab-chunkify "\n") => ["\n"]
  (#'boeki/syllab-chunkify nil) => nil)

;(fact
;  (#'boeki/syllab-chunkify2 "a") => ["a"])

(fact
  (#'boeki/char-jp-script \a) => :romaji
  (#'boeki/char-jp-script \あ) => :hiragana
  (#'boeki/char-jp-script \ア) => :zenkaku-katakana
  (#'boeki/char-jp-script \q) => nil
  (#'boeki/char-jp-script nil) => nil)

(fact
  (#'boeki/kw-subgroups-of "どうしたの？") => #{:hiragana :yakumono})

(fact
  (scripts "ぶんしんさば") => #{:hiragana}
  (scripts "shinjitsuハひとつ")
    => #{:romaji :zenkaku-katakana :hiragana}
  (scripts "パソコンが難しいです。")
    => #{:zenkaku-katakana :hiragana :kanji :yakumono})

(fact
  (#'boeki/ffilter-syllab "お") => map?
  (#'boeki/ffilter-syllab "abc") => falsey)

(fact
  (#'boeki/sokuon-for "ss" :hiragana) => \っ
  (#'boeki/sokuon-for "っし" :romaji) => \s
  (#'boeki/sokuon-for "tt" :romaji) => \t)

(fact
  (let [convert-syllab-to-romaji (#'boeki/convert-syllab :romaji)
        convert-syllab-to-wapuro (#'boeki/convert-syllab :wapuro)
        convert-vanilla-syllab-to-hiragana (#'boeki/convert-syllab :hiragana)
        convert-vanilla-syllab-to-katakana (#'boeki/convert-syllab :katakana)
        convert-vanilla-syllab-to-wapuro (#'boeki/convert-syllab :wapuro)
        convert-vanilla-syllab-to-kunrei (#'boeki/convert-syllab :kunrei-shiki)]
    (convert-syllab-to-romaji "st") => "st"
    (convert-syllab-to-romaji "sta") => "sta"
    (convert-syllab-to-romaji "shi") => "shi"
    (convert-syllab-to-romaji "し") => "shi"
    (convert-syllab-to-romaji "シャー") => "shā"
    (convert-syllab-to-romaji "お") => "o"
    (convert-vanilla-syllab-to-hiragana "o") => "お"
    (convert-vanilla-syllab-to-katakana "o") => "オ"
    (convert-vanilla-syllab-to-wapuro "お") => "o"
    (convert-vanilla-syllab-to-kunrei "お") => "o"
    (convert-syllab-to-wapuro "おう") => "ou"
    (convert-vanilla-syllab-to-kunrei "tsu") => "tu"))

(fact
  (#'boeki/apply-long-vowel "し" {:h "い"}) => "しい"
  (#'boeki/apply-long-vowel "し" {:h "い"} :hiragana) => "しい"
  (#'boeki/apply-long-vowel "シ" {:k "ー"}) => "シー"
  (#'boeki/apply-long-vowel "shi" {:r "ī"}) => "shī"
  (#'boeki/apply-long-vowel "shi" {:ks "î"} :kunrei-shiki) => "shî"
  (#'boeki/apply-long-vowel nil {:k "ー"}) => nil)

(fact
  (#'boeki/find-long-vowel-map "ラー")
    => {:o "a" :w "aa" :r "ā" :ks "â" :h "あ" :k "ー" :kai @#'boeki/kai-a}
  (#'boeki/find-long-vowel-map "リー")
    => {:o "i" :w "ii" :r "ī" :ks "î" :h "い" :k "ー" :kai @#'boeki/kai-i}
  (#'boeki/find-long-vowel-map "ルー")
    => {:o "u" :w "uu" :r "ū" :ks "û" :h "う" :k "ー" :kai @#'boeki/kai-u}
  (#'boeki/find-long-vowel-map "レー")
    => {:o "e" :w "ee" :r "ē" :ks "ê" :h "え" :k "ー" :kai @#'boeki/kai-e}
  (#'boeki/find-long-vowel-map "ロー")
    => {:o "o" :w "ou" :r "ō" :ks "ô" :h "う" :k "ー" :kai @#'boeki/kai-o}
  (#'boeki/find-long-vowel-map "shū")
     => {:o "u" :w "uu" :r "ū" :ks "û" :h "う" :k "ー" :kai @#'boeki/kai-u})

(fact "long vowel conversions"
  ((#'boeki/convert-syllab :katakana) "おう") => "オー"
  ((#'boeki/convert-syllab :romaji) "おう") => "ō"
  ((#'boeki/convert-syllab :kunrei-shiki) "おう") => "ô"
  ((#'boeki/convert-syllab :wapuro) "おう") => "ou"

  ((#'boeki/convert-syllab :katakana) "おお") => "オー"
  ((#'boeki/convert-syllab :romaji) "おお") => "ō"
  ((#'boeki/convert-syllab :kunrei-shiki) "おお") => "ô"
  ((#'boeki/convert-syllab :wapuro) "おお") => "oo"

  ((#'boeki/convert-syllab :hiragana) "ō") => "おう"
  ((#'boeki/convert-syllab :katakana) "ō") => "オー"
  ((#'boeki/convert-syllab :kunrei-shiki) "ō") => "ô"
  ((#'boeki/convert-syllab :wapuro) "ō") => "ou"

  ((#'boeki/convert-syllab :hiragana) "オー") => "おう"
  ((#'boeki/convert-syllab :romaji) "オー") => "ō"
  ((#'boeki/convert-syllab :kunrei-shiki) "オー") => "ô"
  ((#'boeki/convert-syllab :wapuro) "オー") => "ou"

  ((#'boeki/convert-syllab :katakana) "うう") => "ウー"
  ((#'boeki/convert-syllab :romaji) "うう") => "ū"
  ((#'boeki/convert-syllab :kunrei-shiki) "うう") => "û"
  ((#'boeki/convert-syllab :wapuro) "うう") => "uu"

  ((#'boeki/convert-syllab :hiragana) "ウー") => "うう"
  ((#'boeki/convert-syllab :romaji) "ウー") => "ū"
  ((#'boeki/convert-syllab :kunrei-shiki) "ウー") => "û"
  ((#'boeki/convert-syllab :wapuro) "ウー") => "uu"

  ((#'boeki/convert-syllab :hiragana) "ū") => "うう"
  ((#'boeki/convert-syllab :katakana) "ū") => "ウー"
  ((#'boeki/convert-syllab :kunrei-shiki) "ū") => "û"
  ((#'boeki/convert-syllab :wapuro) "ū") => "uu"

  ((#'boeki/convert-syllab :katakana) "ou") => "オー"
  ((#'boeki/convert-syllab :romaji) "ou") => "ō"
  ((#'boeki/convert-syllab :kunrei-shiki) "ou") => "ô"
  ((#'boeki/convert-syllab :hiragana) "ou") => "おう"

  ((#'boeki/convert-syllab :katakana) "oo") => "オー"
  ((#'boeki/convert-syllab :romaji) "oo") => "ō"
  ((#'boeki/convert-syllab :kunrei-shiki) "oo") => "ô"
  ((#'boeki/convert-syllab :hiragana) "oo") => "おお"

  ((#'boeki/convert-syllab :hiragana) "aa") => "ああ")

(fact
  ((#'boeki/convert-syllab :romaji) "しょう") => "shō"
  ((#'boeki/convert-syllab :hiragana) "チョー") => "ちょう"
  ((#'boeki/convert-syllab :hiragana) "shū") => "しゅう"
  ((#'boeki/convert-syllab :katakana) "shū") => "シュー"
  ((#'boeki/convert-syllab :katakana) "しゅう") => "シュー"
  ((#'boeki/convert-syllab :romaji) "かあ") => "kā"
  ((#'boeki/convert-syllab :romaji) "しゅう") => "shū"
  ((#'boeki/convert-syllab :hiragana) "シュー") => "しゅう"
  ((#'boeki/convert-syllab :romaji) "シュー") => "shū")

(fact
  (henkan "ō" :hiragana) => "おう"
  (henkan "ō" :romaji :hiragana) => "おう"
  (henkan " あ" :romaji) => " a"
  (henkan "あ " :romaji) => "a "
  (henkan "ア オ エ ウ イ あ お え う い" :romaji) => "a o e u i a o e u i"
  (henkan \あ :romaji) => \a
  (henkan "あ" :romaji) => "a"
  (henkan "アオエウイあおえうい" :katakana :romaji) => "aoeuiあおえうい"
  (henkan "アオエウイあおえうい" :katakana :hiragana) => "あおえういあおえうい"
  (henkan "abunaidesukarakiroisen no uchigawadeomachikudasai" :hiragana)
    => "あぶないですからきろいせん の うちがわでおまちください")

(fact
  (hiragana "ドーitashimashite") => "どういたしまして"
  (hiragana "namban") => "なんばん"
  (hiragana "kan'i") => "かんい"
  (hiragana "kan-i") => "かんい"
  (hiragana "ヷヸヹヺ") => "ゔぁゔぃゔぇゔぉ"
  (hiragana nil) => nil)

(fact
  (katakana "どうitashimashite") => "ドーイタシマシテ"
  (katakana nil) => nil)

(fact
  (romaji "どうイタシマシテ") => "dōitashimashite"
  (romaji nil) => nil)

(fact
  (kunrei "shūpatsu") => "syûpatu"
  (kunrei nil) => nil)

(fact
  (let [hiragana-juxt
  	      (juxt hiragana->katakana hiragana->romaji hiragana->hebon hiragana->kunrei)]
  	(hiragana-juxt "hiしゃあ")) => ["hiシャー" "hishā" "hishā" "hisyâ"]

  (katakana->hiragana "hiシャー") => "hiしゃあ"
  (katakana->romaji "カッパ") => "kappa"
  (katakana->hebon "hiシャー") => "hishā"
  (hiragana->hebon "まっ") => "matsu"
  (katakana->hebon "ッ") => "tsu"
  (katakana->kunrei "hiシャー") => "hisyâ"
  (katakana->wapuro "hiシャー") => "hishaa"

  (romaji->hiragana "koppu") => "こっぷ"
  (romaji->katakana "shūさあ") => "シューさあ"
  (romaji->hebon "shūさあ") => "shūさあ"
  (romaji->kunrei "shūさあ") => "syûさあ"
  (romaji->wapuro "shūさあ") => "shuuさあ"

  (hebon->hiragana "shūさあ") => "しゅうさあ"
  (hebon->katakana "shūさあ") => "シューさあ"
  (hebon->romaji "shūさあ") => "shūさあ"
  (hebon->kunrei "shūさあ") => "syûさあ"
  (hebon->wapuro "shūさあ") => "shuuさあ"

  (kunrei->hiragana "shûさあ") => "しゅうさあ"
  (kunrei->katakana "shûさあ") => "シューさあ"
  (kunrei->romaji "shûさあ") => "shūさあ"
  (kunrei->hebon "shûさあ") => "shūさあ"
  (kunrei->wapuro "shûさあ") => "shuuさあ"
  (kunrei->hiragana "sha") => "しゃ"

  (katakana->kunrei nil) => nil)