# Shortcode Compatibility Decision Record

**Status:** Evidence package for issue #551 with Gate 1 architecture decisions
recorded (2026-09-23). Sections 7-9 are normative as amended; section 11 holds
the decision index.
**Scope:** Legacy shortcode retention, parsing, outgoing canonical alias
evidence, and the resulting compatibility contract.
**Measurement note:** Sections 2-6 report the datasets as audited before the
generator merge. Merged-catalog outcomes and exception counts are in section 11.

## 1. Purpose and scope

This record supplies the compatibility evidence for issue #551 and the later
generator, parser, and architecture-review phases. The frozen 1.9.7 source is
`emojify/src/main/assets/emoticons/emoji.json` at commit
`88b707aa8fcb0831c2838a2364998a937711f98c` (tag `1.9.7`). The dataset comes
from the vdurmont/emoji-java era and contains local edits. The fixture in
`scripts/emoji_generator/fixtures/emoji-1.9.7.json` is a byte-for-byte copy of
that file. `cmp` against `git show` passed.

This phase changes no runtime behavior and does not choose an architecture or
canonical alias rule. Full alias-loss and reassignment appendices are emitted
by `compat_check.py` so the evidence can be reproduced without copying a long
generated listing into this record.

## 2. Dataset inventory

| Dataset | Emoji records | Alias or shortcode occurrences | Distinct aliases or shortcodes | Other inventory |
| --- | ---: | ---: | ---: | --- |
| 1.9.7 frozen fixture | 1,603 | 1,969 aliases | 1,969 | 240 multi-alias records; 187 `supports_fitzpatrick` records |
| Current generated data | 1,949 | 2,590 shortcodes | 2,590 | 604 records have multiple shortcodes; maximum 4 |

The expected 1,603 records, 1,969 aliases, 240 multi-alias records, and 187
Fitzpatrick-capable records all match. In both snapshots each alias or
shortcode string is unique across the dataset, so the occurrence and distinct
counts happen to agree.

The current asset was last updated by commit `25d49fb` with the message
`chore(deps): update emoji.json to emojibase-data@17.0.0`.
`scripts/emoji_generator/emoji_generator/sources.py:10-15,19-30` constructs
these URLs from the supplied version:

```text
https://cdn.jsdelivr.net/npm/emojibase-data@{version}/en/data.json
https://cdn.jsdelivr.net/npm/emojibase-data@{version}/en/shortcodes/emojibase.json
```

The generator does not hard-code one version in `sources.py`. It reads
`EMOJI_VERSION` in `generator.py:46-64`; the README example uses `15.1`, while
`.github/workflows/python-cd.yml:33-50` resolves a release version dynamically.
Version `17.0.0` is the current asset's evidenced source version and is the
version used for the preset comparison below.

## 3. Retention analysis

| Required measure | Result |
| --- | ---: |
| 1.9.7 alias count | 1,969 occurrences, 1,969 distinct |
| current shortcode count | 2,590 occurrences, 2,590 distinct |
| exact aliases still represented | 1,185 of 1,969 distinct alias strings |
| legacy aliases no longer represented | 784 distinct alias strings |
| new shortcodes not present in 1.9.7 | 1,405 distinct shortcode strings |
| alias -> multiple emoji collisions | 0 in 1.9.7; 0 in current data |
| emoji -> multiple shortcode cases | 240 legacy records, maximum 7 aliases; 604 current records, maximum 4 shortcodes |
| Fitzpatrick-capable legacy aliases | 187 emoji records, 310 base-alias-to-emoji pairs and 310 distinct base aliases |

Each of the 310 distinct base aliases supports the five historical suffix
spellings, yielding 1,550 concrete `:alias|type_N:` token forms.

New shortcode examples include `1st`, `2nd`, `3rd`, `accordion`,
`afghanistan`, `alien_monster`, and `anatomical_heart`. The script prints all
784 lost alias strings with their legacy emoji and Unicode code points. It also
prints all multi-shortcode examples and every alias form for Fitzpatrick-capable
records.

## 4. Per-emoji mapping preservation

The comparison uses the emoji Unicode string as the key, as requested. The
1,969 distinct legacy alias-to-emoji pairs divide into:

| Mapping result | Pairs |
| --- | ---: |
| Same alias still maps to the same exact emoji string | 849 |
| Alias exists, but maps only to a different exact emoji string | 336 |
| Alias is absent from current shortcodes | 784 |

There are 368 legacy emoji strings with no current shortcode at all, covering
449 legacy alias mappings. The 1,185 legacy alias strings that remain in the
current set are not all preserved mappings: 336 map to a different exact
Unicode string. Of those 336 differences, 305 become the same string after
removing U+FE0E and U+FE0F variation selectors. The other 31 are listed here;
the script appendix prints their full Unicode mappings as well.

| Alias | Legacy emoji | Current mapping |
| --- | --- | --- |
| `beetle` | 🐞 | 🪲 |
| `cat` | 🐱 | 🐈️ |
| `city_sunset` | 🌆 | 🌇 |
| `computer` | 💻 | 🖥️ |
| `cow` | 🐮 | 🐄 |
| `dog` | 🐶 | 🐕️ |
| `email` | ✉ | 📧 |
| `frowning_face` | ☹ | 😦 |
| `horse` | 🐴 | 🐎 |
| `japan` | 🗾 | 🇯🇵 |
| `jar` | 🏺 | 🫙 |
| `jolly_roger` | ♾🏴‍☠️ | 🏴‍☠️ |
| `man_in_tuxedo` | 🤵 | 🤵‍♂️ |
| `mouse` | 🐭 | 🐁 |
| `ng` | 🇳🇬 | 🆖 |
| `no` | 🇳🇴 | 👎️ |
| `o` | ⭕ | 🅾️ |
| `om` | 🇴🇲 | 🕉️ |
| `pencil` | 📝 | ✏️ |
| `pig` | 🐷 | 🐖 |
| `pirate_flag` | ♾🏴‍☠️ | 🏴‍☠️ |
| `point_up` | ☝ | 👆️ |
| `point_up_2` | 👆 | ☝️ |
| `rabbit` | 🐰 | 🐇 |
| `sunglasses` | 😎 | 🕶️ |
| `sunny` | ☀ | 🌤️ |
| `tiger` | 🐯 | 🐅 |
| `train` | 🚋 | 🚆 |
| `umbrella` | ☔ | ☂️ |
| `up` | 🆙 | 🔼 |
| `whale` | 🐳 | 🐋 |

The exact-string classification is intentional: adding a variation selector
still changes the key under this audit. The 31 rows above are not explained by
variation-selector differences alone. Current `shortCodes` has no
alias-to-multiple-emoji collisions, but retaining both sides of these
reassigned alias mappings in one flat lookup could introduce ambiguity. The
complete 336-pair appendix is printed by the script.

## 5. Fitzpatrick token contract in 1.9.7

Historical `EmojiParser.kt:158-178` finds the first closing colon, splits at
the first `|`, requires a known alias and a Fitzpatrick-capable emoji, then
passes the entire token after `|` to `Fitzpatrick.fitzpatrickFromType`. The
historical enum and helper in `Fitzpatrick.kt:26-65` accept these exact
case-insensitive enum spellings after uppercasing:

| Alias suffix | Historical enum | Unicode modifier | Emojibase tone number |
| --- | --- | --- | --- |
| `type_1_2` | `TYPE_1_2` | U+1F3FB | `_tone1` |
| `type_3` | `TYPE_3` | U+1F3FC | `_tone2` |
| `type_4` | `TYPE_4` | U+1F3FD | `_tone3` |
| `type_5` | `TYPE_5` | U+1F3FE | `_tone4` |
| `type_6` | `TYPE_6` | U+1F3FF | `_tone5` |

Thus legacy `type_1_2` maps to the lightest Emojibase tone, and legacy
`type_6` maps to the darkest Emojibase `tone5`. `type_1` and `type_2` are not
enum values and do not produce a Fitzpatrick modifier. The current
`Fitzpatrick.kt:30-50,62-65` retains the same enum-to-Unicode mapping and
`fitzpatrickFromType` conversion.

The historical enum and conversion are:

```kotlin
TYPE_1_2("\uD83C\uDFFB"),
TYPE_3("\uD83C\uDFFC"),
TYPE_4("\uD83C\uDFFD"),
TYPE_5("\uD83C\uDFFE"),
TYPE_6("\uD83C\uDFFF"),

valueOf(type.uppercase(Locale.ROOT))
```

Source: `Fitzpatrick.kt:30-50,62-65` at commit
`88b707aa8fcb0831c2838a2364998a937711f98c`.

Decisive historical implementation excerpt from
`EmojiParser.kt:162-168`:

```kotlin
val fitzpatrickStart: Int = input.indexOf('|', start + 2)
if (fitzpatrickStart != -1 && fitzpatrickStart < aliasEnd) {
    val emoji = getForAlias(input.substring(start, fitzpatrickStart)) ?: return null
    if (!emoji.supportsFitzpatrick) return null
    val fitzpatrick =
        Fitzpatrick.fitzpatrickFromType(input.substring(fitzpatrickStart + 1, aliasEnd))
    return AliasCandidate(emoji, fitzpatrick, start, aliasEnd)
}
```

Historical tests in `EmojiParseTest.kt:75-84,341-360,418-428` assert
`:boy|type_6:` output/input and `:boy|type_3:` parsing, and assert that
`:grinning|type_6:` is unchanged because that emoji does not support a tone.

The decisive historical assertions are:

```kotlin
assertEquals(":boy|type_6:", result)
assertEquals("\uD83D\uDC66\uD83C\uDFFF", result)
assertEquals(str, result)
assertEquals(Fitzpatrick.TYPE_3, candidate.fitzpatrick)
assertNull(candidate)
candidate = emojiManager.aliasCandidateAt(str, 6)
```

Sources: `EmojiParseTest.kt:83,349,360,427,408-410` at commit
`88b707aa8fcb0831c2838a2364998a937711f98c`.

For other cases, the implementation distinguishes:

- Unknown alias: `getForAlias` returns null, so the candidate is rejected and
  the input remains unchanged.
- Unknown suffix such as `type_1` on a known, capable alias: the alias
  candidate is still returned with a null modifier. `parseToUnicode` therefore
  replaces it with the base emoji and drops the unknown suffix. The old helper
  also prints the failed enum conversion stack trace.
- Any suffix on a non-Fitzpatrick emoji: candidate is rejected and input stays
  unchanged, as covered by the `grinning` test.
- Missing opening or closing colon: no candidate is returned and text stays
  unchanged. `aliasCandidateAt` requires a colon at the scan position and a
  later closing colon.
- Nested colons: the test at `EmojiParseTest.kt:403-415` shows the first
  colon in `::boy:` is rejected and the second can start a valid `:boy:`. The
  old scanner can therefore preserve the leading colon while consuming the
  valid inner alias. A trailing colon after a valid token is likewise left as
  ordinary text.

The normative later-phase contract in section 9 intentionally requires
unknown and malformed tokens to be preserved, rather than copying the old
unknown-suffix behavior.

## 6. Canonical outgoing selection

The historical implementation selects:

```kotlin
val alias = unicodeCandidate.emoji?.aliases?.get(0)
```

(`EmojiParser.kt:66` at the cited commit). The 1.9.7 test expects
`😀 -> :grinning:` and `👦🏿 -> :boy|type_6:`
(`EmojiParseTest.kt:42-55,74-84`). Candidate comparison against the historic
first alias gives:

| Rule | Exact matches | Availability | Result |
| --- | ---: | ---: | --- |
| A. `legacy-first` | 1,603 / 1,603 | 1,603 available | 100.00% |
| B. `current-first-deterministic` | 688 / 1,235 | 1,235 available; 368 unavailable | 55.71% of available; 915 total drift or unavailable |
| C. `emojibase-preset-first` | 939 / 1,584 | 1,584 available; 19 unavailable | 59.28% of available; 664 total drift or unavailable |

Rule A uses the 1.9.7 first alias for every emoji in this comparison. It
reproduces the historic first-alias catalog only if that legacy canonical value
remains available to the implementation. Rule B means a stable deduplication
that preserves the current JSON array order. It is a snapshot comparison, not
a guarantee of deterministic regeneration: the current generator merges with
`list(set(...))` in `generator.py:32-35`. Rule C uses the first value in the
Emojibase preset order. Its exact comparison is incomplete for 19 legacy emoji
that have no preset entry.

The requested samples produce:

| Input | Rule A | Rule B | Rule C |
| --- | --- | --- | --- |
| 😄 | `:smile:` | `:grinning_face_with_closed_eyes:` | `:grinning_face_with_closed_eyes:` |
| 😀 | `:grinning:` | `:grinning:` | `:grinning:` |
| 👦🏿 | `:boy|type_6:` | `:boy|type_6:` | `:boy|type_6:` |

Only Rule A meets both target samples `😄 -> :smile:` and
`👦🏿 -> :boy|type_6:`. It also matches all 1,603 historic first aliases in
the frozen fixture by construction. The full mismatch examples are printed by
the script.

### Pinned upstream preset evidence

`sources.py:19-30` describes the shortcode preset as a hexcode-to-string-or-list
mapping. The fetched file at
`https://cdn.jsdelivr.net/npm/emojibase-data@17.0.0/en/shortcodes/emojibase.json`
contains 3,979 entries and represents these samples as follows:

| Hexcode | Preset value in order |
| --- | --- |
| `1f604` | `["grinning_face_with_closed_eyes", "smile"]` |
| `1f600` | `["grinning", "grinning_face"]` |
| `1f44b` | `["wave", "waving_hand"]` |
| `1f466` | `"boy"` |
| `2602` | `"umbrella"` |
| `2614` | `"umbrella_with_rain"` |

Array value order is preserved as returned by the preset. The local
`sources.py` docstring documents the mapping shape and an array example, but
does not state a separate ordering guarantee. This observed upstream order
does not make Rule C reproduce the historic `😄 -> :smile:` output because
`smile` is second for `1f604`.

## 7. `parseToAliases` shim satisfiability

A pure forwarder using the current-list-first result cannot reproduce the full
historic canonical output: 547 of the 1,235 available current choices differ,
and 368 legacy emoji have no current shortcode, for 915 of 1,603 cases that
drift or are unavailable. Using pinned preset-first instead leaves 645
available mismatches and 19 unavailable cases, or 664 of 1,603 not matching.

A deprecated forwarder can reproduce the historic first alias for all 1,603
fixture emoji only if a legacy-canonical preference/index remains available,
as Rule A demonstrates. This does not itself settle how aliases that were
lost, reassigned, or attached to absent current emoji strings should resolve.
The architecture review must compare these options without assuming a
forwarder is behaviorally exact:

1. Pure forwarder to the new implementation using its selected canonical
   rule.
2. Forwarder with legacy-canonical preference and the necessary legacy
   accepted-alias mapping.
3. Do not restore `parseToAliases`; use the new API and document the output
   change.

Gate 1 decision (2026-09-23): option 2 is realized without a separate
preference index. The merge decision in section 8 bakes the legacy canonical
into `shortCodes[0]`, so `parseToAliases` is restored as a deprecated
single-forward bridge to `parseToShortCodes` with `ReplaceWith`. One parser
implementation, no copied logic (criterion 12). The bridge reproduces exact
1.x output for 1,574 of 1,603 legacy emoji (98.2%). The 28 whose first alias
is semantically conflicted emit their documented alternative canonical: the
next retained legacy alias if one exists (for example `:envelope:` for the
envelope emoji), otherwise the modern canonical (for example
`:smiling_face_with_sunglasses:`, since the reassigned `sunglasses` string is
excluded). This deviation is required: emitting a reassigned string would
break round-tripping and reintroduce the ambiguity criterion 9 forbids.

## 8. Legacy ingestion options

### Option A: merge 1.9.7 aliases into generated data

- **Generator impact:** Read the frozen fixture while generating current
  records and merge legacy accepted aliases. Preserve a deliberate order and
  resolve the 336 same-alias/different-Unicode mappings. There are 368 legacy
  emoji strings with no current shortcode, so the treatment of those aliases
  also needs an explicit policy.
- **Runtime lookup impact:** Reuse the current shortcode lookup. Combining
  both mappings without collision policy could make a single alias ambiguous;
  preserving both exact Unicode destinations would create up to 336 such
  cross-snapshot conflicts.
- **Serializer parity impact:** If aliases are added to existing `shortCodes`,
  the JSON schema and `IEmoji` model need not change. All three serializer
  modules still need regression checks for equivalent parsed data. If a new
  field is introduced instead, `kotlinx`, `gson`, and `moshi` must implement the
  same field and defaults.
- **FlatBuffers migration (#540) impact:** The merged legacy mapping is
  naturally part of each catalog record, but the catalog migration must retain
  it and its canonical-order semantics.

### Option B: generate a separate legacy index from the frozen fixture

- **Generator impact:** Generate and validate a separate alias-to-legacy-emoji
  index from the immutable fixture. Current generated data remains untouched.
- **Runtime lookup impact:** Consult current and legacy indexes under an
  explicit precedence and ambiguity rule. The separate map can represent the
  historical strings without silently overwriting a current mapping.
- **Serializer parity impact:** A runtime-built or separately loaded index
  does not require changing the `IEmoji` JSON schema, though equivalent
  behavior must be tested with each serializer path. If the index becomes a
  serialized model field or asset, all three serializer implementations must
  agree on that representation.
- **FlatBuffers migration (#540) impact:** The legacy map must be included in
  the catalog abstraction or equivalent FlatBuffers catalog payload. It must
  not be a JSON-only parser side table that disappears during migration.

**Gate 1 decision (2026-09-23): refined Option A.** The generator merges
legacy aliases into the generated catalog's per-record `shortCodes`. No second
asset, no second runtime index, no schema change, no `EmojiManager` loading
change. Merge rules, applied at generation time:

1. Match each legacy record to a current record by exact emoji string, else by
   variation-selector-normalized string (strip U+FE0E and U+FE0F from both
   sides). Observed: 1,235 exact matches plus 366 normalized matches of 1,603,
   with a unique deterministic target in every case.
2. Merge the legacy record's aliases into the target's `shortCodes`, EXCLUDING
   the 31 semantically conflicted alias strings of section 4. Merged order:
   retained legacy aliases in fixture order, then current shortcodes in preset
   order, deduplicated order-preserving. This also replaces the generator's
   `list(set(...))` merge, so ordering becomes deterministic.
3. A legacy emoji that matches no current record even after normalization and
   retains at least one alias becomes its own catalog record. Exactly one case:
   the Texas flag `🏴󠁵󠁳󠁴󠁸󠁿` (alias `ustx`). The 1.9.7 malformed `♾🏴‍☠️` record
   matches nothing and both of its aliases are conflicted, so the record is
   dropped and documented as an exception.
4. `supportsFitzpatrick` of the current record is authoritative for merged
   aliases. Observed drift: 14 records, all legacy-incapable to
   current-capable (for example `couple`); documented as an exception list.

Coverage of the measured gaps: the 784 lost aliases resolve again as 650 merged
into exact-present records, 133 into normalized-matched records, and 1 via the
new Texas record. The 368 exact-absent legacy strings normalize into 366
current records, 1 Texas record, and 1 dropped malformed record. The 305
variation-selector differences merge into single records (presentation
normalization), leaving the 31 semantic conflicts as the complete exclusion
set. The merged catalog is collision-free by construction: both snapshots had
zero intra-dataset collisions and conflicted strings are excluded from the
legacy side.

Rejected alternative (Option B): variation-selector normalization dissolves
almost the entire conflict surface that motivated a separate index. A second
asset would add a loading path, two runtime indexes, and a FlatBuffers (#540)
migration obligation for state that fits in the existing per-record field. With
the merge, the compatibility state lives entirely in `shortCodes` vectors, so
the catalog migration carries it by construction.

Binding invariant: the merged catalog is the single source of truth for both
directions. Every shortcode string resolves to exactly one record, and index 0
of `shortCodes` is the outgoing canonical. No runtime structure may hold legacy
alias knowledge outside the loaded catalog.

## 9. Normative compatibility contract for later phases

The following is the required compatibility target for implementation and
tests, not a claim that every detail matches the old parser:

- Accept the token shapes `:code:` and `:code|type_N:`. Supported legacy
  modifier spellings are exactly `type_1_2`, `type_3`, `type_4`, `type_5`, and
  `type_6`, with the Unicode and Emojibase tone mapping in section 5.
- Preserve unknown aliases, unknown modifier values, ambiguous aliases, and
  malformed or incomplete tokens unchanged. Do not choose a destination by map
  iteration order, and do not drop an unrecognized modifier suffix.
  Deviation from 1.x, deliberate and documented: `:boy|type_1:` (valid base,
  invalid suffix) is preserved whole; 1.x emitted the base emoji and dropped
  the suffix. Historical data written by the 1.x emitter never contains
  invalid suffixes, so no serialized value depends on the old behavior.
- A supported modifier on an emoji that does not support Fitzpatrick tones is
  not converted. Preserve that entire token unchanged.
- Canonical outgoing selection (decided): `":" + shortCodes[0] + ":"` of the
  resolved record in the merged generated catalog. Generated order is retained
  legacy aliases in fixture order, then current shortcodes in preset order,
  deduplicated order-preserving. Never infer the canonical from an unordered
  set or from a runtime preference map.
- Incoming resolution (decided): the merged catalog maps every shortcode string
  to exactly one record. The 31 conflicted strings resolve to the modern
  mapping (for example `:cat:` resolves to the current `cat` record,
  U+1F408). Variation-selector drift is presentation normalization to the
  current string (for example `:relaxed:` resolves to the current `relaxed`
  presentation). `getForShortCode` keeps its exact-key contract and agrees
  with the parser on every string.
- Fitzpatrick composition (decided): compose output as `IEmoji.emoji` plus
  `Fitzpatrick.unicode`. Do not compose through `IEmoji.getUnicode` as
  currently implemented: the `unicode` field holds literal escaped ASCII text,
  so `getUnicode` returns escaped base text plus modifier rather than the
  emoji. (Known issue recorded in section 11.)
- Scanner policy (decided): single left-to-right scan; at each position attempt
  one complete token (opening colon, non-empty code, optional `|suffix`,
  closing colon with the historic `start + 2` minimum length); on success
  consume through the closing colon; on failure advance exactly one character.
  This reproduces 1.x nested behavior (`::boy:` becomes `:👦`) without nesting
  special cases and preserves failed tokens whole. This line supersedes the
  earlier draft wording about not partially consuming nested tokens. A
  supported suffix on a non-capable emoji preserves the whole token (1.x
  agreed).
- Generator tests must enforce the frozen fixture counts, legacy alias
  preservation or an explicit exception for every lost/reassigned mapping,
  same-emoji mapping invariants, collision behavior, stable canonical ordering,
  and the `😄 -> :smile:` and `👦🏿 -> :boy|type_6:` target examples if the
  selected rule claims to support them.
- If the JSON schema changes, add parity coverage proving that `kotlinx`,
  `gson`, and `moshi` deserialize the same compatibility catalog. Catalog
  behavior must also remain present through FlatBuffers migration #540.

## 10. Reproduction

From the repository root, rerun the analysis and print the complete lost-alias,
reassignment, Fitzpatrick-form, and mismatch appendices with:

```bash
python3 scripts/emoji_generator/compat_check.py
```

The script uses only Python standard-library modules. It reads the frozen
fixture and current generated dataset from their default repository paths and
fetches the pinned 17.0.0 shortcode preset. To rerun without network access,
download that exact URL to a local file and pass `--preset-json <file>`.

To regenerate the frozen fixture byte-for-byte from the source commit:

```bash
mkdir -p scripts/emoji_generator/fixtures
git show 88b707aa8fcb0831c2838a2364998a937711f98c:emojify/src/main/assets/emoticons/emoji.json > scripts/emoji_generator/fixtures/emoji-1.9.7.json
```

The analysis does not rewrite either dataset. The full generated report is
printed to standard output and can be redirected when archiving a run.

## 11. Gate 1 decision record (2026-09-23)

Binding decisions for work-unit `shortcode-restore`. Sections 7-9 above state
the operative contract; this table is the index.

| Decision | Outcome |
| --- | --- |
| D1 legacy ingestion | Refined Option A: merge into per-record `shortCodes` (section 8 rules). Single catalog, no schema change, FlatBuffers-safe by construction. |
| D2 canonical outgoing | `shortCodes[0]` of the merged record: retained legacy aliases in fixture order, then current shortcodes in preset order. Fitzpatrick: PARSE `:base\|type_N:`, REMOVE `:base:`, IGNORE `:base:` plus raw modifier. |
| D3 conflict policy | Modern wins for the 31 conflicted strings (legacy side excludes them). Variation-selector drift is presentation normalization to the current string. |
| D4 scanner edges | Invalid suffix (`:boy\|type_1:`) preserves the whole token (documented deviation from 1.x). Nested colons (`::boy:`) keep 1.x one-character-advance behavior: the inner token converts. |
| D5 public API | `parseToShortCodes` / `parseShortCodesToUnicode` extensions in `io.wax911.emojify.parser`; internal `ShortCodeCandidate`; deprecated `parseToAliases` as a single-forward bridge (criterion 12). Composition is `IEmoji.emoji` plus `Fitzpatrick.unicode`, never `IEmoji.getUnicode` as currently implemented. |

Forbidden approaches (binding): a second asset, second runtime index, or
parser-held legacy map; a second transformer for `parseToAliases`; duplicated
Fitzpatrick tables; `shortCodes.first()` on unordered data or `.first()` on
`getForShortCode` sets; folding shortcode scanning into `parseToUnicode`;
composing output from the `unicode` field or `getUnicode`; emitting a
conflicted alias as canonical; discarding unknown tokens.

Acceptance implications (honest): criterion 3 holds for 1,938 of 1,969 legacy
alias strings (98.4%); the 31 reassigned strings are the documented exception
set under criterion 3's "explicitly documented impossible mappings". The
deprecated bridge reproduces exact 1.x output for 98.2% of legacy emoji,
demoting 28 conflicted first aliases to documented alternates. Criterion 11
documentation obligations: the canonical policy, the presentation-normalization
note, and exception tables covering the 31 reassigned aliases (section 4), the
28 canonical demotions (section 7), the 14 Fitzpatrick-capability drifts, and
the dropped malformed `♾🏴‍☠️` record with its 2 conflicted aliases.

Follow-up (out of scope): `IEmoji.getUnicode` composes from the escaped
`unicode` text field, and `extractEmojis` returns malformed Fitzpatrick forms
through it. Repair separately; the new parser must not reuse it.

Named uncertainty: preset order for future Emojibase dataset versions is
upstream-controlled, so canonical strings for current-only emoji may change on
dataset updates. That is deterministic per shipped asset (generator
byte-stability tests); legacy-preferring canonicals are pinned by the frozen
fixture.
