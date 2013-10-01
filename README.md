# namban

During the Nanban Trade Period (Nanban Boeki Jidai), western vocabulary was
first introduced in the japanese language as a consequence of the interaction
between europeans and the japanese people.

<img src="http://upload.wikimedia.org/wikipedia/commons/0/00/NanbanCarrack.jpg"
 alt="Namban Carrack" title="Namban Carrack" align="right" height=65 />

> "They eat with their fingers instead of with chopsticks such as we use. They
show their feelings without any self-control. They cannot understand the meaning
of written characters"
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
> (Marcouin, Francis and Keiko Omoto. Quand le Japon s’ouvrit au monde. Paris:
Découvertes Gallimard, 1990. ISBN 2-07-053118-X. Pages 114–116)

namban is a clojure library for conversion between hiragana, katakana, romaji
and more.

### Install

In project.clj: `[namban "0.1.1-alpha"]`

### Tatoeba

```clojure
user=> (use 'namban.boeki)
nil
user=> (hiragana? "ねこ")
true
user=> (hepburn "このパン")
"konopan"
user=> (katakana->romaji "このパン")
"このpan"
user=> (long-vowel-syllab? "shū")
truthy
user=> (scripts "パソコンが難しいです。")
#{:katakana :hiragana :kanji}
user=> (henkan "shuupatsu" :wapuro :kunrei)
"shūpatsu"
```
To check examples and full API, please consult the source and test suite.

### Shortcomings

When the target conversion script is hepburn, and [は へ を] are used as
particles, they will be converted to [ha he wo], instead of the expected [wa e o].
Romaji long vowel "ō" or "ô" conversion to おお/oo when applicable is not
supported. Default are おう/ou, as this is the most common case.
All occurences of ん/ン will be converted to romaji n' regardless of its position in
words. This will change in future releases.

## TODO

- add numbers [0-9]
- support katakana/romaji-upper-case and hiragana/romaji-lower-case
- support hankaku-katanana
- support hyōjun-shiki
- consonant expansion
- clojurescript support
- clojure_py support

## Notes

Tools like MeCab segment text which can improve namban's accuracy.

## Missing something?

Feature requests and tips are welcome!

## License

Copyright (C) 2013 Carlos C. Fontes.

Double licensed under the [Eclipse Public License](http://www.eclipse.org/legal/epl-v10.html) (the same as Clojure) or
the [Apache Public License 2.0](http://www.apache.org/licenses/LICENSE-2.0.html).
