# Android Emojify &nbsp; &nbsp; [![Release](https://jitpack.io/v/wax911/android-emojify.svg?style=flat-square)](https://jitpack.io/#wax911/android-emojify) &nbsp; [![Codacy Badge](https://app.codacy.com/project/badge/Grade/6bace5612f8c4799ac86f104f5b2db0f)](https://www.codacy.com/gh/AniTrend/android-emojify/dashboard?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=AniTrend/android-emojify&amp;utm_campaign=Badge_Grade) &nbsp; [![gradle-unit-test](https://github.com/AniTrend/android-emojify/actions/workflows/android-test.yml/badge.svg)](https://github.com/AniTrend/android-emojify/actions/workflows/android-test.yml)

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

## Migration

### From v0.x.x - v1.x.x

A quick run overview of some of the changes, see the rest of the changes under __Examples__
section. `EmojiManager.initEmojiData` has also been refactored
to throw exceptions rather than consuming them.

> See the __Getting Started__ section, and you can find more example in the library unit tests,
> e.g. `EmojiUtilTest.kt`

```java
import io.wax911.emojify.EmojiUtils; //becomes -> io.wax911.emojify.parser.EmojiParser;

EmojiUtils.emojify(); //becomes -> EmojiParser.parseToUnicode();
EmojiUtils.htmlify (); //becomes -> EmojiParser.parseToHtmlDecimal();
EmojiUtils.hexHtmlify(); //becomes -> EmojiParser.parseToHtmlHexadecimal();
EmojiUtils.shortCodify(); //becomes -> EmojiParser.parseToAliases();
```

> Starting v1.X conversion is only possible from `emoji -> hexHtml, decHtml or shortCodes`
> and `hexHtml, decHtml or shortCodes -> emoji`
> unlike in previous versions where you could convert hexHtml to decHtml or shortCodes & vice-versa.
>
> ```
> .
> ├── io
> │   └── wax911
> │       └── emojify
> │           ├── EmojiManager.kt
> │           ├── model
> │           │   └── Emoji.kt
> │           ├── parser
> │           │   └── EmojiParser.kt
> │           └── util
> │               ├── EmojiTrie.kt
> │               └── Fitzpatrick.kt
> ```
>
> __N.B Package names have been changed and would require refactoring, except for `EmojiManager`__

###  

### From v1.x.x - v1.6.0

Starting from **v1.6.0** the project has had some parts rewritten, specifically `EmojiParser`
, `EmojiManager` and added support
for **[androidx.startup](https://developer.android.com/topic/libraries/app-startup#kotlin)** please
take the time to read the short description on what this does.

> __See [CHANGELOG.md](./CHANGELOG.md) for a detailed list of changes__

#### Summary of changes

- `EmojiManager` has been converted from an `object` to a class with the following
  signature: `class EmojiManager(val emojiList: Collection<Emoji>)`. This has been done to allow **
  you** to use your own emoji list and no longer required to explicitly initialize anything as
  previously with a call to `EmojiManager.initEmojiData`
- `EmojiParser` has been converted to a set of extension function that are applied on
  the `EmojiManager` see examples below:

  ```kotlin
  // getting our emoji manager from our application class through an extension function
  // see ./app/src/main/java/io/wax911/emojifysample/App.kt
  val emojiManager = context.emojiManager()
  EmojiParser.parseToUnicode(); //becomes -> emojiManager.parseToUnicode();
  EmojiParser.parseToHtmlDecimal (); //becomes -> emojiManager.parseToHtmlDecimal();
  EmojiParser.parseToHtmlHexadecimal(); //becomes -> emojiManager.parseToHtmlHexadecimal();
  EmojiParser.parseToAliases(); //becomes -> emojiManager.parseToAliases();
  ```

##### New project structure

> ```sh
> .
> └── io
>     └── wax911
>         └── emojify
>             ├── EmojiManager.kt
>             ├── initializer
>             │   └── EmojiInitializer.kt
>             ├── manager
>             │   └── IEmojiManager.kt
>             ├── model
>             │   └── Emoji.kt
>             ├── parser
>             │   ├── action
>             │   │   └── FitzpatrickAction.kt
>             │   ├── candidate
>             │   │   ├── AliasCandidate.kt
>             │   │   └── UnicodeCandidate.kt
>             │   ├── common
>             │   │   └── EmojiTransformer.kt
>             │   ├── EmojiParser.kt
>             │   └── transformer
>             │       └── EmojiTransformer.kt
>             └── util
>                 ├── EmojiTree.kt
>                 ├── Fitzpatrick.kt
>                 └── tree
>                     ├── Matches.kt
>                     └── Node.kt
> ```
> __N.B `EmojiManger` has been converted to class, thus an instance needs to be obtained
from `EmojiInitializer`
through [androidx.startup](https://developer.android.com/topic/libraries/app-startup#disable-individual#kotlin)
or manually create an instance of the class on your own__.

## Use Case

Trying to get emoji support in your application in a way that is both compatible with a browser and
mobile, you might even be trying to create a github client with reaction support? Then this library
is for you, your backend stores is the html entities or aliases in text and this library will take
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

### Step 2. Add the dependency:

```groovy
dependencies {
    implementation 'com.github.anitrend:android-emojify:{latest_version}'
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
        // should already be initialized if we haven't disabled initialization in manifest
        // see: https://developer.android.com/topic/libraries/app-startup#disable-individual
        AppInitializer.getInstance(this)
            .initializeComponent(EmojiInitializer::class.java)
    }
}
```

## Screenshots

<img src="https://github.com/wax911/android-emojify/raw/master/screenshots/device-2017-09-25-155538.png" width="365px"/> <img src="https://github.com/wax911/android-emojify/raw/master/screenshots/device-2017-09-25-155600.png" width="365px"/> <img src="https://github.com/wax911/android-emojify/raw/master/screenshots/device-2017-09-25-155617.png" width="365px"/> <img src="https://github.com/wax911/android-emojify/raw/master/screenshots/device-2017-09-25-155644.png" width="365px"/>

## Examples:

### EmojiManager

The `EmojiManager` provides several instance methods to search through the emojis database:

* `getForTag` returns all the emojis for a given tag
* `getForAlias` returns the emoji for an alias
* `emojiList` list of all the emojis
* `isEmoji` checks if a string is an emoji

You can also query the metadata:

* `getAllTags` returns the available tags

Or get everything:

* `emojiList` list of all the emojis

### Emoji model

An `Emoji` is a data class, which provides the following methods:

* `unicode` the unicode representation of the emoji
* `description` the (optional) description of the emoji
* `aliases` a list of aliases for this emoji
* `tags` a list of tags for this emoji
* `htmlDec` an html decimal representation of the emoji
* `htmlHex` an html decimal representation of the emoji
* `supportsFitzpatrick` true if the emoji supports the Fitzpatrick modifiers, else false
* `getUnicode(fitzpatrick: Fitzpatrick?): String` Returns the unicode representation of the emoji
  associated with the provided Fitzpatrick modifier.

### Fitzpatrick modifiers

Some emojis now support the use of Fitzpatrick modifiers that gives the choice between 5 shades of
skin tones:

| Modifier | Type     |
|:--------:| -------- |
| 🏻       | type_1_2 |
| 🏼       | type_3   |
| 🏽       | type_4   |
| 🏾       | type_5   |
| 🏿       | type_6   |

We defined the format of the aliases including a Fitzpatrick modifier as:

```
:ALIAS|TYPE:
```

A few examples:

```
:boy|type_1_2:
:swimmer|type_4:
:santa|type_6:
```

###  

### EmojiParser

Is a set of extension methods to act on `EmojiManager`, so given an instance of `EmojiManger` we can
achieve the following:

```kotlin
val emojiManager: EmojiManger = ...
```

#### To unicode

To replace all the aliases and the html representations found in a string by their unicode,
use `EmojiParser#parseToUnicode(String)`.

For example:

```kotlin
val str = "An :+1:awesome :smiley:string " + "😄with a few :wink:emojis!"
val result = emojiManager.parseToUnicode(str)
// An 😀awesome 😃string 😄with a few 😉emojis!
```

#### To aliases

To replace all the emoji's unicodes found in a string by their aliases,
use `EmojiParser#parseToAliases(String)`.

For example:

```kotlin
val str = "An 😀awesome 😃string with a few 😉emojis!"
val result = emojiManager.parseToAliases(str)
// "An :grinning:awesome :smiley:string with a few :wink:emojis!"
```

By default, the aliases will parse and include any Fitzpatrick modifier that would be provided. If
you want to remove or ignore the Fitzpatrick modifiers,
use `EmojiParser#parseToAliases(String, FitzpatrickAction)`. Examples:

```kotlin
val str = "Here is a boy: \uD83D\uDC66\uD83C\uDFFF!"
emojiManager.parseToAliases(str, FitzpatrickAction.PARSE)
// Returns twice: "Here is a boy: :boy|type_6:!"
emojiManager.parseToAliases(str, FitzpatrickAction.REMOVE)
// Returns: "Here is a boy: :boy:!"
emojiManager.parseToAliases(str, FitzpatrickAction.IGNORE)
// Returns: "Here is a boy: :boy:🏿!"
```

#### To html

To replace all the emoji's unicodes found in a string by their html representation,
use `EmojiParser#parseToHtmlDecimal(String)` or `EmojiParser#parseToHtmlHexadecimal(String)`.

For example:

```kotlin
val str = "An 😀awesome 😃string with a few 😉emojis!"
val resultHtmlDecimal = emojiManager.parseToHtmlDecimal(str)
// Returns:
// "An &#128512;awesome &#128515;string with a few &#128521;emojis!"

val resultHexadecimal = emojiManager.parseToHtmlHexadecimal(str)
// Returns:
// "An 😀awesome 😃string with a few 😉emojis!"
```

By default, any Fitzpatrick modifier will be removed. If you want to ignore the Fitzpatrick
modifiers, use `emojiManager.parseToAliases(String, FitzpatrickAction)`. Examples:

```kotlin
val str = "Here is a boy: \uD83D\uDC66\uD83C\uDFFF!"

emojiManager.parseToHtmlDecimal(str, FitzpatrickAction.PARSE)
emojiManager.parseToHtmlDecimal(str, FitzpatrickAction.REMOVE)
// Returns: "Here is a boy: 👦!"
emojiManager.parseToHtmlDecimal(str, FitzpatrickAction.IGNORE)
// Returns: "Here is a boy: 👦🏿!"
```

The same applies for the methods `emojiManager.parseToHtmlHexadecimal(String)`
and `emojiManager.parseToHtmlHexadecimal(String, FitzpatrickAction)`.

#### Remove emojis

You can easily remove emojis from a string using one of the following methods:

* `emojiManager.removeAllEmojis(String)`: removes all the emojis from the String
* `emojiManager.removeAllEmojisExcept(String, Collection<Emoji>)`: removes all the emojis from the
  String, except the ones in the Collection
* `emojiManager.removeEmojis(String, Collection<Emoji>)`: removes the emojis in the Collection from
  the String

For example:

```kotlin
val str = "An 😀awesome 😃string with a few 😉emojis!"
val collection = ArrayList<Emoji>()
collection.add(emojiManager.getForAlias("wink")); // This is 😉

emojiManager.removeAllEmojis(str);
emojiManager.removeAllEmojisExcept(str, collection);
emojiManager.removeEmojis(str, collection);

// Returns:
// "An awesome string with a few emojis!"
// "An awesome string with a few 😉emojis!"
// "An 😀awesome 😃string with a few emojis!"
```

#### Extract Emojis from a string

You can search a string of mixed emoji/non-emoji characters and have all of the emoji characters
returned as a Collection.

* `emojiManager.extractEmojis(String)`: returns all emojis as a Collection. This will include
  duplicates if emojis are present more than once.

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
