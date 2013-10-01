(ns namban.test.boeki
	(:use midje.sweet namban.boeki)
	(:require [namban.boeki :as boeki]))

(fact "japanese symbol global vars"
	(first @#'boeki/syllab-maps) => map?
	@#'boeki/unpaired-katakana => string?
	@#'boeki/unpaired-hiragana => string?
	@#'boeki/katakana-symbols => vector?
	@#'boeki/hiragana-symbols => set?)

(fact
	\か => hiragana?
	"しょう" => hiragana?
	"shi" => (complement hiragana?)
	nil => (complement hiragana?))

(fact
	\ｶ => half-width-katakana?
	\カ => (complement half-width-katakana?)
	"ﾊﾟｿｺﾝ" => half-width-katakana?
	"ふﾀ" => (complement half-width-katakana?)
	nil => (complement half-width-katakana?))

(fact
	\ジ => full-width-katakana?
	\ｲ => (complement full-width-katakana?)
	"ハナ" => full-width-katakana?
	"ｽとｱ" => (complement full-width-katakana?)
	nil => (complement full-width-katakana?))

(fact
	"ァ" => katakana?
	"デパート" => katakana?
	"デはート" => (complement katakana?)
	\ヒ => katakana?
	\ヾ => katakana?
	\あ => (complement katakana?)
	nil => (complement katakana?))

(fact
  (@#'boeki/romaji-common? \c) => truthy
  (@#'boeki/romaji-common? "c") => falsey
  (@#'boeki/romaji-common? \あ) => falsey
  (@#'boeki/romaji-common? \â) => falsey
  (@#'boeki/romaji-common? \ナ) => falsey
  (@#'boeki/romaji-common? \と) => falsey)

(fact
  (@#'boeki/hepburn-only? \ā) => truthy
  (@#'boeki/hepburn-only? "ā") => falsey
  (@#'boeki/hepburn-only? \d) => falsey
  (@#'boeki/hepburn-only? \あ) => falsey
  (@#'boeki/hepburn-only? \ア) => falsey)

(fact
  (@#'boeki/kunrei-only? \â) => truthy
  (@#'boeki/kunrei-only? "â") => falsey
  (@#'boeki/kunrei-only? \ā) => falsey
  (@#'boeki/kunrei-only? \あ) => falsey
  (@#'boeki/kunrei-only? \ア) => falsey)

(fact
  \â  => kunrei?
  "â" => kunrei?
  "ā" => (complement kunrei?)
  "あ" => (complement kunrei?)
  "ア" => (complement kunrei?)
  nil => (complement kunrei?))

(fact
  "shū" => hepburn?
  "shu" => hepburn?
  "shâ" => (complement hepburn?)
  "しゅ" => (complement hepburn?)
  "シュ" => (complement hepburn?)
  nil => (complement hepburn?))

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

(fact
  "e" => wapuro?
  \r => wapuro?
  \î => (complement wapuro?)
  nil => (complement wapuro?))

(fact
  "〱〵" => nihongo-punctuation?
  "〹〺a" => (complement nihongo-punctuation?)
  nil => (complement nihongo-punctuation?))

(fact
  \ア => kana?
  "はなび" => kana?
  "ソフト" => kana?
  "karasu" => (complement kana?)
  nil => (complement kana?))

(fact
  (#'boeki/jp-syllab? "あ") => fn?
  ((#'boeki/jp-syllab? "あ") {:h "あ" :r "a" :k "ア"})　=> truthy
  ((#'boeki/jp-syllab? "い") {:h "あ" :r "a" :k "ア"})　=> falsey
  ((#'boeki/jp-syllab? nil) {:h "あ" :r "a" :k "ア"})　=> falsey)

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
  (#'boeki/syllab-chunkify nil) => nil)

;(fact
;  (#'boeki/syllab-chunkify2 "a") => ["a"])

(fact
  (#'boeki/internal-script-kw :hepburn) => :romaji
  (#'boeki/internal-script-kw :full-width-katakana) => :katakana
  (#'boeki/internal-script-kw :half-width-katakana) => :half-katakana
  (#'boeki/internal-script-kw :kunrei) => :kunrei-shiki
  (#'boeki/internal-script-kw :wapuro) => :wapuro
  (#'boeki/internal-script-kw :nanika) => :nanika)

(fact
  (#'boeki/shorten-script-kw :hiragana) => :h
  (#'boeki/shorten-script-kw :hepburn) => :r
  (#'boeki/shorten-script-kw :romaji) => :r
  (#'boeki/shorten-script-kw :full-width-katakana) => :k
  (#'boeki/shorten-script-kw :half-width-katakana) => :hk
  (#'boeki/shorten-script-kw :katakana) => :k
  (#'boeki/shorten-script-kw :kunrei-shiki) => :ks
  (#'boeki/shorten-script-kw :kunrei) => :ks
  (#'boeki/shorten-script-kw :wapuro) => :w
  (#'boeki/shorten-script-kw :nanika) => :n)

(fact
  (#'boeki/char-jp-script \a) => :romaji
  (#'boeki/char-jp-script \あ) => :hiragana
  (#'boeki/char-jp-script \ア) => :full-width-katakana
  (#'boeki/char-jp-script \q) => nil
  (#'boeki/char-jp-script nil) => nil)

(fact
  (which-scripts "ぶんしんさば") => (just [:hiragana])
  (which-scripts "shinjitsuハひとつ")
    => (just [:romaji :full-width-katakana :hiragana])
  (which-scripts "パソコンが難しいです。")
    => (just [:full-width-katakana :hiragana :kanji :nihongo-punctuation]))

(fact
  (#'boeki/long-vowel-syllab? "kā") => truthy  
  (#'boeki/long-vowel-syllab? "かあ") => truthy
  (#'boeki/long-vowel-syllab? "シャー") => truthy
  (#'boeki/long-vowel-syllab? "ū") => truthy
  (#'boeki/long-vowel-syllab? "か") => falsey
  (#'boeki/long-vowel-syllab? "ka") => falsey)

(fact
  (#'boeki/find-first-jp-syllab "お" :kunrei) => "o"
  (#'boeki/find-first-jp-syllab "お" :wapuro) => "o")

(fact
  (let [convert-syllab-to-romaji (#'boeki/convert-syllab :oem :romaji)
        convert-syllab-to-wapuro (#'boeki/convert-syllab :oem :wapuro)
        convert-vanilla-syllab-to-hiragana (#'boeki/convert-syllab :final :hiragana)
        convert-vanilla-syllab-to-katakana (#'boeki/convert-syllab :final :katakana)
        convert-vanilla-syllab-to-wapuro (#'boeki/convert-syllab :final :wapuro)
        convert-vanilla-syllab-to-kunrei (#'boeki/convert-syllab :final :kunrei)]
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
    (convert-syllab-to-wapuro "おう") => "ou"))

(fact
  (#'boeki/apply-long-vowel "し" {:h "い"}) => "しい"
  (#'boeki/apply-long-vowel "し" {:h "い"} :hiragana) => "しい"
  (#'boeki/apply-long-vowel "シ" {:k "ー"}) => "シー"
  (#'boeki/apply-long-vowel "shi" {:r "ī"}) => "shī"
  (#'boeki/apply-long-vowel "shi" {:ks "î"} :kunrei) => "shî"
  (#'boeki/apply-long-vowel nil {:k "ー"}) => nil)

(fact
  (#'boeki/find-long-vowel-map "ラー")
  　　=> {:o "a" :w "aa" :r "ā" :ks "â" :h "あ" :k "ー"
         :kc "カガサザタダナハバパラマワヤャアァかがさざただなはばぱらまわやゃあ"}
  (#'boeki/find-long-vowel-map "リー")
  　　=> {:o "i" :w "ii" :r "ī" :ks "î" :h "い" :k "ー"
         :kc "キギシジチヂニヒビピミリイィきぎしじちぢにひびぴみりい"}
  (#'boeki/find-long-vowel-map "ルー")
  　　=> {:o "u" :w "uu" :r "ū" :ks "û" :h "う" :k "ー"
         :kc "クグスズツヅフブプルムヴヌユュウゥくぐすずつづふぶぷるむぬゆゅう"}
  (#'boeki/find-long-vowel-map "レー")
  　　=> {:o "e" :w "ee" :r "ē" :ks "ê" :h "え" :k "ー"
         :kc "ケゲセゼテデヘベペメレネエェけげせぜてでへべぺめれねえ"}
  (#'boeki/find-long-vowel-map "ロー")
  　　=> {:o "o" :w "ou" :r "ō" :ks "ô" :h "う" :k "ー"
         :kc "コゴソゾトドノホボポモロヲヨョオォこごそぞとどのほぼぽろをよおょ"}
  (#'boeki/find-long-vowel-map "shū")
     => {:o "u" :w "uu" :r "ū" :ks "û" :h "う" :k "ー"
         :kc "クグスズツヅフブプルムヴヌユュウゥくぐすずつづふぶぷるむぬゆゅう"})

(fact
  (#'boeki/long-vowel-syllab "おう" :katakana) => "オー"
  (#'boeki/long-vowel-syllab "おう" :romaji) => "ō"
  (#'boeki/long-vowel-syllab "おう" :kunrei) => "ô"
  (#'boeki/long-vowel-syllab "おう" :wapuro) => "ou"

  (#'boeki/long-vowel-syllab "おお" :katakana) => "オー"
  (#'boeki/long-vowel-syllab "おお" :romaji) => "ō"
  (#'boeki/long-vowel-syllab "おお" :kunrei) => "ô"
  (#'boeki/long-vowel-syllab "おお" :wapuro) => "oo"

  (#'boeki/long-vowel-syllab "ō" :hiragana) => "おう"
  (#'boeki/long-vowel-syllab "ō" :katakana) => "オー"
  (#'boeki/long-vowel-syllab "ō" :kunrei) => "ô"
  (#'boeki/long-vowel-syllab "ō" :wapuro) => "ou"

  (#'boeki/long-vowel-syllab "オー" :hiragana) => "おう"
  (#'boeki/long-vowel-syllab "オー" :romaji) => "ō"
  (#'boeki/long-vowel-syllab "オー" :kunrei) => "ô"
  (#'boeki/long-vowel-syllab "オー" :wapuro) => "ou"

  (#'boeki/long-vowel-syllab "うう" :katakana) => "ウー"
  (#'boeki/long-vowel-syllab "うう" :romaji) => "ū"
  (#'boeki/long-vowel-syllab "うう" :kunrei) => "û"
  (#'boeki/long-vowel-syllab "うう" :wapuro) => "uu"

  (#'boeki/long-vowel-syllab "ウー" :hiragana) => "うう"
  (#'boeki/long-vowel-syllab "ウー" :romaji) => "ū"
  (#'boeki/long-vowel-syllab "ウー" :kunrei) => "û"
  (#'boeki/long-vowel-syllab "ウー" :wapuro) => "uu"

  (#'boeki/long-vowel-syllab "ū" :hiragana) => "うう"
  (#'boeki/long-vowel-syllab "ū" :katakana) => "ウー"
  (#'boeki/long-vowel-syllab "ū" :kunrei) => "û"
  (#'boeki/long-vowel-syllab "ū" :wapuro) => "uu"

  (#'boeki/long-vowel-syllab "ou" :katakana) => "オー"
  (#'boeki/long-vowel-syllab "ou" :romaji) => "ō"
  (#'boeki/long-vowel-syllab "ou" :kunrei) => "ô"
  (#'boeki/long-vowel-syllab "ou" :hiragana) => "おう"

  (#'boeki/long-vowel-syllab "oo" :katakana) => "オー"
  (#'boeki/long-vowel-syllab "oo" :romaji) => "ō"
  (#'boeki/long-vowel-syllab "oo" :kunrei) => "ô"
  (#'boeki/long-vowel-syllab "oo" :hiragana) => "おお"

  (#'boeki/long-vowel-syllab "aa" :hiragana) => "ああ")

(fact
  (#'boeki/long-vowel-syllab "しょう" :romaji) => "shō"
  (#'boeki/long-vowel-syllab "チョー" :hiragana) => "ちょう"
  (#'boeki/long-vowel-syllab "shū" :hiragana) => "しゅう"
  (#'boeki/long-vowel-syllab "shū" :katakana) => "シュー"
  (#'boeki/long-vowel-syllab "しゅう" :katakana) => "シュー"
  (#'boeki/long-vowel-syllab "かあ" :romaji) => "kā"
  (#'boeki/long-vowel-syllab "しゅう" :romaji) => "shū"
  (#'boeki/long-vowel-syllab "シュー" :hiragana) => "しゅう"
  (#'boeki/long-vowel-syllab "シュー" :romaji) => "shū")

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
  (let [hiragana-juxt
  	      (juxt hiragana->katakana hiragana->romaji hiragana->hepburn hiragana->kunrei)]
  	(hiragana-juxt "hiしゃあ")) => ["hiシャー" "hishā" "hishā" "hishâ"]

  (katakana->hiragana "hiシャー") => "hiしゃあ"
  (katakana->romaji "hiシャー") => "hishā"
  (katakana->hepburn "hiシャー") => "hishā"
  (katakana->kunrei "hiシャー") => "hishâ"
  (katakana->wapuro "hiシャー") => "hishaa"

  (romaji->hiragana "shūさあ") => "しゅうさあ"
  (romaji->katakana "shūさあ") => "シューさあ"
  (romaji->hepburn "shūさあ") => "shūさあ"
  (romaji->kunrei "shūさあ") => "shûさあ"
  (romaji->wapuro "shūさあ") => "shuuさあ"

  (hepburn->hiragana "shūさあ") => "しゅうさあ"
  (hepburn->katakana "shūさあ") => "シューさあ"
  (hepburn->romaji "shūさあ") => "shūさあ"
  (hepburn->kunrei "shūさあ") => "shûさあ"
  (hepburn->wapuro "shūさあ") => "shuuさあ"

  (kunrei->hiragana "shûさあ") => "しゅうさあ"
  (kunrei->katakana "shûさあ") => "シューさあ"
  (kunrei->romaji "shûさあ") => "shūさあ"
  (kunrei->hepburn "shûさあ") => "shūさあ"
  (kunrei->wapuro "shûさあ") => "shuuさあ"

  (katakana->kunrei nil) => nil)

  (fact
    (hiragana "ドーitashimashite") => "どういたしまして"
    (hiragana nil) => nil)

  (fact
	  (katakana "どうitashimashite") => "ドーイタシマシテ"
    (katakana nil) => nil)

  (fact
	  (romaji "どうイタシマシテ") => "dōitashimashite"
    (romaji nil) => nil)

  (fact
	  (kunrei "shūpatsu") => "shûpatsu"
    (kunrei nil) => nil)