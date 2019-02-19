# Android Emojify &nbsp; &nbsp; [![Release](https://jitpack.io/v/wax911/android-emojify.svg?style=flat-square)](https://jitpack.io/#wax911/android-emojify) &nbsp; [![Codacy Badge](https://api.codacy.com/project/badge/Grade/30a8f983c55541cbb504671ecc32786c)](https://www.codacy.com/app/AniTrend/android-emojify?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=wax911/android-emojify&amp;utm_campaign=Badge_Grade) &nbsp; [![Build Status](https://travis-ci.org/AniTrend/android-emojify.svg?branch=master)](https://travis-ci.org/AniTrend/android-emojify) &nbsp; [![Stories in Ready](https://badge.waffle.io/AniTrend/android-emojify.svg?label=ready&title=Ready&style=flat-square)](http://waffle.io/AniTrend/android-emojify) &nbsp; [![license](https://img.shields.io/github/license/mashape/apistatus.svg?style=flat-square)](https://github.com/AniTrend/android-emojify/blob/master/LICENSE)

This project is an android port of [vdurmont/emoji-java](https://github.com/vdurmont/emoji-java) which is a lightweight java library that helps you use Emojis in your java applications re-written in Kotlin.

###### This project is already being used in [AniTrend](https://anitrend.co/)

# Known Issues

- Converting of html entities to emojies may not always display the emoji on a given android device if the target device does not have the suggested emoticons e.g. android 4.3 does not have some emoticons available in android 5.0+

# Suggestions

- From v1.X the project was reworked and should be able to handle conversion from emoji to hexHtml, decHtml or short codes on the main thread with a slight improvement on processing speed (depending on the length of text of course),
however I would highly recommend moving all convention work to a background thread between network requests for a smoother experience for your users (read up on the repository pattern).
- If you are using a markdown library like __[txtmark](https://github.com/rjeschke/txtmark)__ or using just ```Html.fromHtml()```  you can skip
conversion of __HexHtml & HtmlCodes__ to emoji and just pass the returned  __[Spanned](https://developer.android.com/reference/android/text/Spanned.html)__
from the ```Html.fromHtml``` to your text view. (See sample in project)

# Migration

A quick run overview of some of the changes, see the rest of the changes under __Examples__ section. `EmojiManager.initEmojiData` has also been refactored
to throw exceptions rather than consuming them. see __Getting Started__ section, and you can find more example in the library unit tests, e.g. `EmojiUtilTest.kt`

```java
import io.wax911.emojify.EmojiUtils; //becomes -> io.wax911.emojify.parser.EmojiParser;

EmojiUtils.emojify(); //becomes -> EmojiParser.parseToUnicode();
EmojiUtils.htmlify (); //becomes -> EmojiParser.parseToHtmlDecimal();
EmojiUtils.hexHtmlify(); //becomes -> EmojiParser.parseToHtmlHexadecimal();
EmojiUtils.shortCodify(); //becomes -> EmojiParser.parseToAliases();
```

> Starting v1.X conversion is only possible from `emoji -> hexHtml, decHtml or shortCodes` and `hexHtml, decHtml or shortCodes -> emoji`
> unlike in previous versions where you could convert hexHtml to decHtml or shortCodes & vice-versa.
>
>```
>.
>├── io
>│   └── wax911
>│       └── emojify
>│           ├── EmojiManager.kt
>│           ├── model
>│           │   └── Emoji.kt
>│           ├── parser
>│           │   └── EmojiParser.kt
>│           └── util
>│               ├── EmojiTrie.kt
>│               └── Fitzpatrick.kt
>```
__N.B Package names have been changed and would require refactoring, except for `EmojiManager`__

# Use Case

Got a social application but you need someway of having emoji support? Then this library is for you, all your backend stores is the html entities.
Your client application would have to convert all emoji objects in a given string and transmit that to your server.
When the client request status or blog text it has to convert the html entities to emoji objects which your android operating system will resolve. See examples below!

# Getting Started

### Step 1. Add this to your root build.gradle:

```java
allprojects {
	repositories {
		...
		maven { url 'https://jitpack.io' }
	}
}
```

### Step 2. Add the dependency:

```java
dependencies {
	compile 'com.github.wax911:android-emojify:{latest_version}'
}

```

### Step 3. Create an application class in your android project and add:

Don't know how to do that?? Take a look at the [application class example](https://github.com/wax911/android-emojify/blob/master/app/src/main/java/io/wax911/emojifysample/App.java)

```java
public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        try {
            // may throw an exception if init fails
            EmojiManager.initEmojiData(this)
        } catch (e: Exception) {
            e.fillInStackTrace()
        }
    }
}

```

# Screenshots

<img src="https://github.com/wax911/android-emojify/raw/master/screenshots/device-2017-09-25-155538.png" width="365px"/> <img src="https://github.com/wax911/android-emojify/raw/master/screenshots/device-2017-09-25-155600.png" width="365px"/> <img src="https://github.com/wax911/android-emojify/raw/master/screenshots/device-2017-09-25-155617.png" width="365px"/> <img src="https://github.com/wax911/android-emojify/raw/master/screenshots/device-2017-09-25-155644.png" width="365px"/>

# Examples:

### EmojiManager

The `EmojiManager` provides several static methods to search through the emojis database:

* `getForTag` returns all the emojis for a given tag
* `getForAlias` returns the emoji for an alias
* `getAll` returns all the emojis
* `isEmoji` checks if a string is an emoji

You can also query the metadata:

* `getAllTags` returns the available tags

Or get everything:

* `getAll` returns all the emojis

### Emoji model

An `Emoji` is a POJO (plain old java object), which provides the following methods:

* `getUnicode` returns the unicode representation of the emoji
* `getUnicode(Fitzpatrick)` returns the unicode representation of the emoji with the provided Fitzpatrick modifier. If the emoji doesn't support the Fitzpatrick modifiers. If the provided Fitzpatrick is null, this method will return the unicode of the emoji.
* `getDescription` returns the (optional) description of the emoji
* `getAliases` returns a list of aliases for this emoji
* `getTags` returns a list of tags for this emoji
* `getHtmlDecimal` returns an html decimal representation of the emoji
* `getHtmlHexadecimal` returns an html decimal representation of the emoji
* `supportsFitzpatrick` returns true if the emoji supports the Fitzpatrick modifiers, else false

### Fitzpatrick modifiers

Some emojis now support the use of Fitzpatrick modifiers that gives the choice between 5 shades of skin tones:

| Modifier | Type |
| :---: | ------- |
| 🏻 | type_1_2 |
| 🏼 | type_3 |
| 🏽 | type_4 |
| 🏾 | type_5 |
| 🏿 | type_6 |

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

### EmojiParser

#### To unicode

To replace all the aliases and the html representations found in a string by their unicode, use `EmojiParser#parseToUnicode(String)`.

For example:

```java
String str = "An :grinning:awesome :smiley:string &#128516;with a few :wink:emojis!";
String result = EmojiParser.parseToUnicode(str);
// "An 😀awesome 😃string 😄with a few 😉emojis!"
```

#### To aliases

To replace all the emoji's unicodes found in a string by their aliases, use `EmojiParser#parseToAliases(String)`.

For example:

```java
String str = "An 😀awesome 😃string with a few 😉emojis!";
String result = EmojiParser.parseToAliases(str);
// "An :grinning:awesome :smiley:string with a few :wink:emojis!"
```

By default, the aliases will parse and include any Fitzpatrick modifier that would be provided. If you want to remove or ignore the Fitzpatrick modifiers, use `EmojiParser#parseToAliases(String, FitzpatrickAction)`. Examples:

```java
String str = "Here is a boy: \uD83D\uDC66\uD83C\uDFFF!";
EmojiParser.parseToAliases(str);
EmojiParser.parseToAliases(str, FitzpatrickAction.PARSE);
// Returns twice: "Here is a boy: :boy|type_6:!"
EmojiParser.parseToAliases(str, FitzpatrickAction.REMOVE);
// Returns: "Here is a boy: :boy:!"
EmojiParser.parseToAliases(str, FitzpatrickAction.IGNORE);
// Returns: "Here is a boy: :boy:🏿!"
```

#### To html

To replace all the emoji's unicodes found in a string by their html representation, use `EmojiParser#parseToHtmlDecimal(String)` or `EmojiParser#parseToHtmlHexadecimal(String)`.

For example:

```java
String str = "An 😀awesome 😃string with a few 😉emojis!";

String resultDecimal = EmojiParser.parseToHtmlDecimal(str);
// Returns:
// "An &#128512;awesome &#128515;string with a few &#128521;emojis!"

String resultHexadecimal = EmojiParser.parseToHtmlHexadecimal(str);
// Returns:
// "An &#x1f600;awesome &#x1f603;string with a few &#x1f609;emojis!"
```

By default, any Fitzpatrick modifier will be removed. If you want to ignore the Fitzpatrick modifiers, use `EmojiParser#parseToAliases(String, FitzpatrickAction)`. Examples:

```java
String str = "Here is a boy: \uD83D\uDC66\uD83C\uDFFF!";
EmojiParser.parseToHtmlDecimal(str);
EmojiParser.parseToHtmlDecimal(str, FitzpatrickAction.PARSE);
EmojiParser.parseToHtmlDecimal(str, FitzpatrickAction.REMOVE);
// Returns 3 times: "Here is a boy: &#128102;!"
EmojiParser.parseToHtmlDecimal(str, FitzpatrickAction.IGNORE);
// Returns: "Here is a boy: &#128102;🏿!"
```

The same applies for the methods `EmojiParser#parseToHtmlHexadecimal(String)` and `EmojiParser#parseToHtmlHexadecimal(String, FitzpatrickAction)`.

#### Remove emojis

You can easily remove emojis from a string using one of the following methods:

* `EmojiParser#removeAllEmojis(String)`: removes all the emojis from the String
* `EmojiParser#removeAllEmojisExcept(String, Collection<Emoji>)`: removes all the emojis from the String, except the ones in the Collection
* `EmojiParser#removeEmojis(String, Collection<Emoji>)`: removes the emojis in the Collection from the String

For example:

```java
String str = "An 😀awesome 😃string with a few 😉emojis!";
Collection<Emoji> collection = new ArrayList<Emoji>();
collection.add(EmojiManager.getForAlias("wink")); // This is 😉

EmojiParser.removeAllEmojis(str);
EmojiParser.removeAllEmojisExcept(str, collection);
EmojiParser.removeEmojis(str, collection);

// Returns:
// "An awesome string with a few emojis!"
// "An awesome string with a few 😉emojis!"
// "An 😀awesome 😃string with a few emojis!"
```

#### Extract Emojis from a string

You can search a string of mixed emoji/non-emoji characters and have all of the emoji characters returned as a Collection.

* `EmojiParser#extractEmojis(String)`: returns all emojis as a Collection.  This will include duplicates if emojis are present more than once.

## Credits

**emoji-java** originally used the data provided by the [github/gemoji project](https://github.com/github/gemoji). It is still based on it but has evolved since.

__[Supported Emoji List](./SUPPORTED.md)__

# License

```
MIT License

Copyright (c) 2018 Maxwell Mapako

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```
