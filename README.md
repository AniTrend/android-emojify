# Android Emojify &nbsp; &nbsp; [![Release](https://jitpack.io/v/anitrend/android-emojify.svg)](https://jitpack.io/#anitrend/android-emojify) &nbsp; [![Codacy Badge](https://app.codacy.com/project/badge/Grade/6bace5612f8c4799ac86f104f5b2db0f)](https://www.codacy.com/gh/AniTrend/android-emojify/dashboard?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=AniTrend/android-emojify&amp;utm_campaign=Badge_Grade) &nbsp; [![gradle-unit-test](https://github.com/AniTrend/android-emojify/actions/workflows/android-unit-test.yml/badge.svg)](https://github.com/AniTrend/android-emojify/actions/workflows/android-test.yml)

[![FOSSA Status](https://app.fossa.io/api/projects/git%2Bgithub.com%2FAniTrend%2Fandroid-emojify.svg?type=large)](https://app.fossa.io/projects/git%2Bgithub.com%2FAniTrend%2Fandroid-emojify?ref=badge_large)

This project is an android port of [vdurmont/emoji-java](https://github.com/vdurmont/emoji-java)
which is a lightweight java library that helps you use Emojis in your java applications re-written
in Kotlin, with some extra tweaks.

**This project is already being used in [AniTrend](https://anitrend.co/) and only aims to provide
emojis from [emojipedia](https://emojipedia.org/)**

- All class and function documentation on the **emojify** module can be
  found [here](https://anitrend.github.io/android-emojify/)

- All supported emojis can be found [here](./SUPPORTED.md)

## Known Issues

- Converting of html entities to emojis may not always display the emoji on a given android device
  if the target device does not have the suggested emoticons e.g. android 4.3 does not have some
  emoticons available in android 5.0+

## Suggestions

- From v1.X the project was reworked and should be able to handle conversion from emoji to **
  hexHtml**, **decHtml** or short codes on the main thread with a slight improvement on processing
  speed (depending on the length of text of course),
  however I would highly recommend moving all convention work to a background thread (
  consider [Coroutines - Kotlin](https://kotlinlang.org/docs/reference/coroutines/coroutines-guide.html))
  between network requests for a smoother experience for your users (read up on the repository
  pattern).
- If you are using a markdown library like [txtmark](https://github.com/rjeschke/txtmark)
  , [markwon]([GitHub - noties/Markwon: Android markdown library (no WebView)](https://github.com/noties/Markwon))
  or using just `Html.fromHtml()`  you can skip the conversion of __HexHtml__ & __HtmlCodes__ to
  emoji and just pass the
  returned  [Spanned](https://developer.android.com/reference/android/text/Spanned.html) from
  whichever framework you're using. (See sample in project)

## Use Case

Trying to get emoji support in your application in a way that is both compatible with a browser and
mobile, you might even be trying to create a github client with reaction support? Then this library
is for you, your backend stores HTML entities or short codes in text and this library will take
care of everything for you.

## Getting Started

### Step 1. Add this to your root build.gradle:

```groovy
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```

### Step 2. Add the dependencies:

You must use one of our artifacts `kotlinx`, `gson` or `moshi` for deserialization, this should match whatever library you want to use.
e.g.
```groovy
dependencies {
    implementation 'com.github.anitrend.android-emojify:emojify:{latest_version}'
    implementation 'com.github.anitrend.android-emojify:contract:{latest_version}'
    implementation 'com.github.anitrend.android-emojify:kotlinx:{latest_version}'
}
```

### Step 3. Create an application class in your android project and add:

Don't know how to do that?? Take a look at
the [application class example](./app/src/main/java/io/wax911/emojifysample/App.kt)

```kotlin
class App : Application() {

  /**
   * Application scope bound emojiManager, you could keep a reference to this object in a
   * dependency injector framework like as a singleton in `Hilt`, `Dagger` or `Koin`
   */
    internal val emojiManager: EmojiManager by lazy {
        EmojiManager.create(this, KotlinxDeserializer())
    }
}
```

### Step4. Optional - Init EmojiManager with androidx-startup
```kotlin
class EmojiInitializer : Initializer<EmojiManager> {
  private val serializer: IEmojiDeserializer = KotlinxDeserializer()

  /**
   * Initializes and a component given the application [Context]
   *
   * @param context The application context.
   */
  override fun create(context: Context) = EmojiManager.create(context, serializer)

  /**
   * @return A list of dependencies that this [Initializer] depends on. This is
   * used to determine initialization order of [Initializer]s.
   *
   * For e.g. if a [Initializer] `B` defines another
   * [Initializer] `A` as its dependency, then `A` gets initialized before `B`.
   */
  override fun dependencies() = emptyList<Class<out Initializer<*>>>()
}


class App : Application() {

  /**
   * Application scope bound emojiManager, you could keep a reference to this object in a
   * dependency injector framework like as a singleton in `Hilt`, `Dagger` or `Koin`
   */
  internal val startupEmojiManager: EmojiManager by lazy {
    // should already be initialized if we haven't disabled initialization in manifest
    // see: https://developer.android.com/topic/libraries/app-startup#disable-individual
    AppInitializer.getInstance(this)
      .initializeComponent(EmojiInitializer::class.java)
  }
}
```

**AndroidManifest.xml**
```xml
<provider
    android:name="androidx.startup.InitializationProvider"
    android:authorities="${applicationId}.androidx-startup"
    android:exported="false"
    tools:node="merge">
    <meta-data
        android:name="{some_package_name_of_your_choosing}.EmojiInitializer"
        android:value="androidx.startup" />
</provider>
```

## Screenshots

<img src="https://github.com/wax911/android-emojify/raw/master/screenshots/device-2017-09-25-155538.png" width="365px"/> <img src="https://github.com/wax911/android-emojify/raw/master/screenshots/device-2017-09-25-155600.png" width="365px"/> <img src="https://github.com/wax911/android-emojify/raw/master/screenshots/device-2017-09-25-155617.png" width="365px"/> <img src="https://github.com/wax911/android-emojify/raw/master/screenshots/device-2017-09-25-155644.png" width="365px"/>

## Examples:

### EmojiManager

The `EmojiManager` provides several instance methods to search through the emojis database:

* `getForTag` returns all the emojis for a given tag/s
* `getForShortCode(shortCode)` returns the matching `IEmoji` records for that exact key, or `null`
  when no record matches. Pass a short code without `:` delimiters. This lookup does not strip
  colons or otherwise normalize the key.
* `emojiList` list of all the emojis
* `isEmoji` checks if a string is an emoji

You can also query the metadata:

* `getAllTags` returns the available tags

Or get everything:

* `emojiList` list of all the emojis

For example, `getForShortCode` returns a collection because the API allows more than one matching
record:

```kotlin
import io.wax911.emojify.EmojiManager
import io.wax911.emojify.contract.model.IEmoji

fun findWink(emojiManager: EmojiManager): Collection<IEmoji>? =
    emojiManager.getForShortCode("wink")
```

### Emoji model

`IEmoji` provides the following properties:

* `emoji` the Unicode emoji sequence
* `description` the (optional) description of the emoji
* `shortCodes` an optional list of accepted short code strings for this emoji; the first generated
  entry is used for outgoing canonical conversion
* `tags` an optional list of tags for this emoji
* `unicode` the escaped Unicode code-point string stored with the emoji data
* `htmlDec` an HTML decimal character reference for the emoji
* `htmlHex` an HTML hexadecimal character reference for the emoji
* `supportsFitzpatrick` true if the emoji supports the Fitzpatrick modifiers, else false

### Fitzpatrick modifiers

Some emojis support Fitzpatrick modifiers that give a choice between five skin tones:

| Modifier | Type     |
|:--------:| -------- |
| 🏻       | type_1_2 |
| 🏼       | type_3   |
| 🏽       | type_4   |
| 🏾       | type_5   |
| 🏿       | type_6   |

The short code form for a Fitzpatrick modifier is:

```
:code|type_N:
```

A few examples:

```
:boy|type_1_2:
:swimmer|type_4:
:santa|type_6:
```

In the merged catalog, `supportsFitzpatrick` is the OR of the 1.x and current datasets. The three
zombie records retain tone support and 11 records gained it; this policy is intentional and
permanent across regenerations.

For modifiers on emojis without Fitzpatrick support, PARSE emits the base short code followed by
the raw modifier so the output always round-trips.

### EmojiParser

The parser functions are extensions on `EmojiManager`. Import each extension from
`io.wax911.emojify.parser`; for example, use `import io.wax911.emojify.parser.parseToShortCodes`.
Import `FitzpatrickAction` from `io.wax911.emojify.parser.action`. The examples below take an
already initialized `EmojiManager` as a function parameter.

#### 1.x to 2.x short code migration

**Terminology change:** 1.x `aliases` are named `shortCodes` in 2.x.

2.x and later can consume serialized 1.x values with `parseShortCodesToUnicode`, including values
such as `:smile:` and `:boy|type_6:`. The historical modifier suffixes are retained, and their
Unicode output may use the current record's presentation. Matching ignores U+FE0E and U+FE0F when
the merged catalog associates a historical record with its current emoji, so this is presentation
normalization, not preservation of every original code point.

```kotlin
import io.wax911.emojify.EmojiManager
import io.wax911.emojify.parser.parseShortCodesToUnicode

fun readSavedShortCodes(emojiManager: EmojiManager): String =
    emojiManager.parseShortCodesToUnicode(":smile: and :boy|type_6:")
// Returns: 😄 and 👦🏿
```

The 31 reassigned shortcode strings resolve to their modern emoji rather than the different 1.x
emoji that used the same string. The table lists all documented exceptions:

<details>
<summary>31 reassigned shortcode strings</summary>

| Short code | 1.x emoji | 2.x emoji |
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

</details>

For outgoing conversion, 27 records whose first 1.x short code was reassigned emit a documented
alternative canonical: the next retained 1.x short code when available (for example, `:envelope:`),
or the modern canonical when there is no retained alternative (for example,
`:smiling_face_with_sunglasses:`). The malformed 1.x record `♾🏴‍☠️` is omitted because both of its
short codes were reassigned and it has no safe outgoing canonical. See
[`docs/shortcode-compatibility.md`](./docs/shortcode-compatibility.md) for the full compatibility
evidence and exception details.

#### To Unicode from HTML

`parseToUnicode` converts decimal and hexadecimal HTML character references to Unicode emoji. It
does not parse short codes; use `parseShortCodesToUnicode` for those.

```kotlin
import io.wax911.emojify.EmojiManager
import io.wax911.emojify.parser.parseToUnicode

fun decodeHtml(emojiManager: EmojiManager): String =
    emojiManager.parseToUnicode("An &#128516; and &#x1f604;")
// Returns: An 😄 and 😄
```

#### To short codes

`parseToShortCodes` replaces Unicode emoji with the first entry in that emoji's generated
`shortCodes` list. Retained legacy values come first, followed by the current preset order. For
example, `Hello 😄` becomes `Hello :smile:`.

For an emoji that supports Fitzpatrick modifiers, PARSE adds a suffix, REMOVE omits the modifier,
and IGNORE leaves the modifier as Unicode after the short code:

```kotlin
import io.wax911.emojify.EmojiManager
import io.wax911.emojify.parser.action.FitzpatrickAction
import io.wax911.emojify.parser.parseToShortCodes

fun encodeShortCodes(emojiManager: EmojiManager) {
    val greeting = emojiManager.parseToShortCodes("Hello 😄")
    // Hello :smile:

    val parsed = emojiManager.parseToShortCodes("👦🏿", FitzpatrickAction.PARSE)
    // :boy|type_6:
    val removed = emojiManager.parseToShortCodes("👦🏿", FitzpatrickAction.REMOVE)
    // :boy:
    val ignored = emojiManager.parseToShortCodes("👦🏿", FitzpatrickAction.IGNORE)
    // :boy:🏿
}
```

`parseToAliases` is deprecated and should be used only as a migration bridge for callers moving from
1.x. Its signature is `fun EmojiManager.parseToAliases(input: String, fitzpatrickAction: FitzpatrickAction = FitzpatrickAction.PARSE): String`.
It is behaviorally identical to `parseToShortCodes`. It reproduces exact 1.x output for 1,575 of
1,603 legacy emoji (98.3%); 27 conflicted-first records emit their documented alternative
canonical, and one malformed legacy record is dropped. New code should use `parseToShortCodes`.

#### From short codes

`parseShortCodesToUnicode` converts `:code:` and `:code|type_N:` tokens to Unicode. The accepted
case-insensitive suffixes are `type_1_2`, `type_3`, `type_4`, `type_5`, and `type_6`. Unknown or
ambiguous short codes, malformed or incomplete tokens, invalid suffixes, and suffixes on emoji
without Fitzpatrick support are preserved whole. For example, `:not_a_real_shortcode:` and
`:boy|type_1:` remain unchanged. The known but non-capable `:grinning|type_6:` token is also
preserved whole.

```kotlin
import io.wax911.emojify.EmojiManager
import io.wax911.emojify.parser.parseShortCodesToUnicode

fun decodeShortCodes(emojiManager: EmojiManager) {
    val known = emojiManager.parseShortCodesToUnicode(":smile: :boy|type_6:")
    // 😄 👦🏿
    val unchanged = emojiManager.parseShortCodesToUnicode(
        ":not_a_real_shortcode: :boy|type_1: :grinning|type_6:",
    )
    // All three tokens are preserved.
    val nested = emojiManager.parseShortCodesToUnicode("::boy:")
    // :👦
}
```

The one-character scan policy means `::boy:` preserves its leading colon and converts the inner
`:boy:` token. Reassigned short code strings, such as `:cat:`, resolve to their modern emoji rather
than the different emoji associated with the string in 1.x. This also means a historical string
such as `:relaxed:` can resolve to the current Unicode presentation, including its variation
selector.

#### To HTML character references

Use `parseToHtmlDecimal` or `parseToHtmlHexadecimal` to replace Unicode emoji with decimal or
hexadecimal HTML character references.

```kotlin
import io.wax911.emojify.EmojiManager
import io.wax911.emojify.parser.parseToHtmlDecimal
import io.wax911.emojify.parser.parseToHtmlHexadecimal

fun encodeHtml(emojiManager: EmojiManager) {
    val decimal = emojiManager.parseToHtmlDecimal("An 😀awesome 😃string with a few 😉emojis!")
    // An &#128512;awesome &#128515;string with a few &#128521;emojis!

    val hexadecimal = emojiManager.parseToHtmlHexadecimal("An 😀awesome 😃string with a few 😉emojis!")
    // An &#x1f600;awesome &#x1f603;string with a few &#x1f609;emojis!
}
```

With the default PARSE action, or REMOVE, the modifier is omitted from the HTML reference. IGNORE
leaves it as a Unicode character after the reference:

```kotlin
import io.wax911.emojify.EmojiManager
import io.wax911.emojify.parser.action.FitzpatrickAction
import io.wax911.emojify.parser.parseToHtmlDecimal

fun encodeHtmlWithTone(emojiManager: EmojiManager) {
    val parsed = emojiManager.parseToHtmlDecimal("👦🏿", FitzpatrickAction.PARSE)
    // &#128102;
    val removed = emojiManager.parseToHtmlDecimal("👦🏿", FitzpatrickAction.REMOVE)
    // &#128102;
    val ignored = emojiManager.parseToHtmlDecimal("👦🏿", FitzpatrickAction.IGNORE)
    // &#128102;🏿
}
```

#### Remove, replace, and extract emojis

Use `removeAllEmojis(str)` to remove every emoji, `removeAllEmojisExcept(str, emojisToKeep)` to keep
only selected emoji, `removeEmojis(str, emojisToRemove)` to remove a selected set, or
`replaceAllEmojis(str, replacementString)` to replace every emoji with the supplied text. The
collection arguments contain `IEmoji` records.

```kotlin
import io.wax911.emojify.EmojiManager
import io.wax911.emojify.parser.removeAllEmojis
import io.wax911.emojify.parser.removeAllEmojisExcept
import io.wax911.emojify.parser.removeEmojis
import io.wax911.emojify.parser.replaceAllEmojis

fun transformEmojis(emojiManager: EmojiManager) {
    val text = "An 😀awesome 😃string with a few 😉emojis!"
    val wink = emojiManager.getForShortCode("wink").orEmpty()
    emojiManager.removeAllEmojis(text)
    emojiManager.removeAllEmojisExcept(text, wink)
    emojiManager.removeEmojis(text, wink)
    emojiManager.replaceAllEmojis(text, "[emoji]")
}
```

`extractEmojis(input)` returns a `List<String>` containing the emoji substrings found in the input,
including duplicates when an emoji occurs more than once. Import the extension from
`io.wax911.emojify.parser`.

##  

## Credits

**emoji-java** originally used the data provided by
the [github/gemoji project](https://github.com/github/gemoji). It is still based on it but has
evolved since.

# License

```
Copyright 2018 AniTrend

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
