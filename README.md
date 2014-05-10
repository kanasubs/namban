# namban [![Build Status](https://travis-ci.org/ccfontes/namban.png?branch=master)](https://travis-ci.org/ccfontes/namban)

During the Nanban Trade Period (Nanban Boeki Jidai), western vocabulary was
first introduced in the Japanese language as a result of the interaction
between Europeans and the Japanese people.

<img src="http://upload.wikimedia.org/wikipedia/commons/0/00/NanbanCarrack.jpg"
 alt="Namban Carrack" title="Namban Carrack" align="right" height=65 />

> "They eat with their fingers instead of with chopsticks such as we use. They
show their feelings without any self-control. They cannot understand the meaning
of written characters."
(from Boxer, Christian Century).

<img src="http://upload.wikimedia.org/wikipedia/commons/b/b4/Hasekura_in_Rome.JPG"
 alt="Hasekura in Rome" title="Hasekura in Rome" align="right" height=125 />

> “They never touch food with their fingers, but instead use two small sticks
that they hold with three fingers.” “They blow their noses in soft silky papers
the size of a hand, which they never use twice, so that they throw them on the
ground after usage, and they were delighted to see our people around them
precipitate themselves to pick them up.” 
> “Their swords cut so well that they can cut a soft paper just by putting it on
the edge and by blowing on it.”
> (Marcouin, Francis and Keiko Omoto. Quand le Japon s’ouvrit au monde.)

namban is a Clojure(Script) Japanese library for trading between Hiragana, Katakana, Romaji, for identifying script types and more.

### Install

[![clojars version](https://clojars.org/namban/latest-version.svg?raw=true)](https://clojars.org/namban)

### Tatoeba
API is available both in Romaji and Kana.
```clj
user=> (use 'namban.boeki)
nil
user=> (hiragana? "ねこがかわいいでしょう")
true
user=> (hebon "このパンがおいしい") ; use tools like Kuromoji to segment the text
"konopan'gaoishii"
user=> (katakana->romaji "このパンがおいしい")
"このpan'がおいしい"
user=> (ローマじ->ひらがな "wareware wa uchūjin desu")
"われわれ わ うちゅうじん です" ; no way to know wa is a particle
user=> (scripts "こいつぁなあ、南蛮渡来の品もんだぜぇ！")
#{:kanji :yakumono :hiragana}
user=> (henkan "nihon e syûpatu" :wapuro :kunrei)
"nihon e syûpatu"
```

### Tests and full API

[![](http://bks4.books.google.com/books?id=GB8YAAAAYAAJ&pg=PA1&img=1&zoom=1&sig=ACfU3U3D9xbQ0qUfE9twRVILvEuPm1vskQ)](http://ccfontes.github.io/namban/namban.boeki.html)

### Shortcomings

When the target conversion script is `:hebon`, and `は`, `へ`, `を` are used as
particles, they will be converted to `ha`, `he`, `wo`, instead of the expected `wa` `e` `o`, respectively.
Romaji long vowels `ō`, `ô` conversions to `おお`, `oo`, respectively, when applicable is not supported. Defaults are `おう`, `ou` - as this is the most common case.
All occurrences of `ん`, `ン` will be converted to romaji `n'` regardless of their position in words, although this behaviour will be improved in future releases.

### Improving conversions

Tools like [Kuromoji](http://www.atilika.org) segment text which can improve namban's accuracy.

### Wishlist

- kana->X conversions
- katakana/romaji-upper-case and hiragana/romaji-lower-case formats
- support hankaku-katanana script
- support hyōjun-shiki script
- consonant expansion

### Missing something?

Feature requests are welcome!

### Using namban?

I'll be happy to add your project using namban to this list.

[kanasubs.com](http://www.kanasubs.com) — Convert subtitles in Kanji/Raw to Kana online.

### Japanese language Q&A by

Megumi Imai

### License

Copyright (C) 2013 Carlos C. Fontes.

Double licensed under the [Eclipse Public License](http://www.eclipse.org/legal/epl-v10.html) (the same as Clojure) or
the [Apache Public License 2.0](http://www.apache.org/licenses/LICENSE-2.0.html).
