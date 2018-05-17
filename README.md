# Android Emojify &nbsp; &nbsp; [![Release](https://jitpack.io/v/wax911/android-emojify.svg?style=flat-square)](https://jitpack.io/#wax911/android-emojify) &nbsp; [![Codacy Badge](https://api.codacy.com/project/badge/Grade/30a8f983c55541cbb504671ecc32786c)](https://www.codacy.com/app/AniTrend/android-emojify?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=wax911/android-emojify&amp;utm_campaign=Badge_Grade) &nbsp; [![Build Status](https://travis-ci.org/AniTrend/android-emojify.svg?branch=master)](https://travis-ci.org/AniTrend/android-emojify) &nbsp; [![Stories in Ready](https://badge.waffle.io/AniTrend/android-emojify.svg?label=ready&title=Ready&style=flat-square)](http://waffle.io/AniTrend/android-emojify) &nbsp; [![license](https://img.shields.io/github/license/mashape/apistatus.svg?style=flat-square)](https://github.com/AniTrend/android-emojify/blob/master/LICENSE)

This project is an android port of the [emoji4j](https://github.com/kcthota/emoji4j) which is a java library to convert short codes, html entities to emojis and vice-versa. Also supports parsing emoticons, surrogate html entities.

Inspired by [vdurmont/emoji-java](https://github.com/vdurmont/emoji-java), emoji4j adds more goodies and helpers to deal with emojis. The emoji data is based on the database from [github/gemoji](https://github.com/github/gemoji) and ASCII emoticons data from [wooorm/emoticon](https://github.com/wooorm/emoticon).

###### This project is already being used in [AniTrend](https://anitrend.co/)

<a href='https://ko-fi.com/A3772XCL' target='_blank'><img height='36' style='border:0px;height:36px;' src='https://az743702.vo.msecnd.net/cdn/kofi5.png?v=0' border='0' alt='Buy Me a Coffee at ko-fi.com' /></a>

# Known Issues

- Converting of html entities to emojies may not always display the emoji on a given android device if the target device does not have the suggested emoticons e.g. android 4.3 does not have some emoticons available in android 5.0+

# Suggestions

- Depending on the complexity of the string passed the conversion may take a second or two which may make your application jitter, I strongly suggest doing conversions in a background thread. This could be between sending a network request or recieving it.
- If you are using a markdown library like __[txtmark](https://github.com/rjeschke/txtmark)__ or using just ```Html.fromHtml()```  you can skip convertion of __HexHtml & HtmlCodes__ to emoji and just pass the returned  __[Spanned](https://developer.android.com/reference/android/text/Spanned.html)__ from the ```Html.fromHtml``` to your text view. (See sample in project)

# Use Case

Got a social application but you need someway of having emoji support? Then this library is for you, all your backend stores is the html entities. Your client application would have to convert all emoji objects in a given string and transmit that to your server. When the client request status or blog text it has to convert the html entities to emoji objects which your android operating system will resolve. See examples below!

# Getting Started

### Step 1. Add this to your root build.gradle:

```
allprojects {
	repositories {
		...
		maven { url 'https://jitpack.io' }
	}
}
```

### Step 2. Add the dependency:

```
dependencies {
	compile 'com.github.wax911:android-emojify:{latest_version}'
}

```

### Step 3. Create an application class in your android project and add:

Don't know how to do that?? Take a look at the [application class example](https://github.com/wax911/android-emojify/blob/master/app/src/main/java/io/wax911/emojifysample/App.java)

```
public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        EmojiManager.initEmojiData(getApplicationContext());
    }
}

```

# Screenshots

<img src="https://github.com/wax911/android-emojify/raw/master/screenshots/device-2017-09-25-155538.png" width="365px"/> <img src="https://github.com/wax911/android-emojify/raw/master/screenshots/device-2017-09-25-155600.png" width="365px"/> <img src="https://github.com/wax911/android-emojify/raw/master/screenshots/device-2017-09-25-155617.png" width="365px"/> <img src="https://github.com/wax911/android-emojify/raw/master/screenshots/device-2017-09-25-155644.png" width="365px"/>

# Examples:

## getEmoji

Get emoji by unicode, short code, decimal or hexadecimal html entity

```
Emoji emoji = EmojiUtils.getEmoji("🐭"); //get emoji by unicode character

EmojiUtils.getEmoji("blue_car").getEmoji(); //returns 🚙

EmojiUtils.getEmoji(":blue_car:").getEmoji(); //also returns 🚙

EmojiUtils.getEmoji("&#x1f42d;").getEmoji(); //returns 🐭

EmojiUtils.getEmoji("&#128045;").getEmoji(); //also returns 🐭

EmojiUtils.getEmoji("&#55357;&#56833;").getEmoji(); //returns 😁

```

## The Emoji Object

Conversion from unicode, short code, hexadecimal and decimal html entities is pretty easy.

```
Emoji emoji = EmojiUtils.getEmoji("🐭");

emoji.getEmoji(); //returns 🐭

emoji.getDecimalHtml(); //returns &#128045;

emoji.getHexHtml(); //return &#x1f42d;

emoji.getAliases(); //returns a collection of aliases. ["mouse"]

```

## isEmoji

Verifies if the passed string is an emoji character

```
EmojiUtils.isEmoji("🐭"); //returns true

EmojiUtils.isEmoji("blue_car"); //returns true

EmojiUtils.isEmoji(":coyote:"); //returns false

EmojiUtils.isEmoji("&#x1f42d;"); //returns true

EmojiUtils.isEmoji("&#128045;"); //returns true

```

## emojify

Emojifies the passed string

```
String text = "A :cat:, :dog: and a :mouse: became friends<3. For :dog:'s birthday party, they all had :hamburger:s, :fries:s, :cookie:s and :cake:.";

EmojiUtils.emojify(text); //returns A 🐱, 🐶 and a 🐭 became friends❤️. For 🐶's birthday party, they all had 🍔s, 🍟s, 🍪s and 🍰.

String text = "A &#128049;, &#x1f436; and a :mouse: became friends. For the :dog:'s birthday party, they all had :hamburger:s, :fries:s, :cookie:s and :cake:."

EmojiUtils.emojify(text); //returns A 🐱, 🐶 and a 🐭 became friends. For the 🐶's birthday party, they all had 🍔s, 🍟s, 🍪s and 🍰.

```

## htmlify
Converts unicode characters in text to corresponding decimal html entities

```
String text = "A :cat:, :dog: and a :mouse: became friends. For the :dog:'s birthday party, they all had :hamburger:s, :fries:s, :cookie:s and :cake:.";

EmojiUtils.htmlify(text); //returns A &#128049;, &#128054; and a &#128045; became friends. For the &#128054;'s birthday party, they all had &#127828;s, &#127839;s, &#127850;s and &#127856;.

String text = "A 🐱, 🐶 and a 🐭 became friends. For the 🐶's birthday party, they all had 🍔s, 🍟s, 🍪s and 🍰."

EmojiUtils.htmlify(text); //also returns A &#128049;, &#128054; and a &#128045; became friends. For the &#128054;'s birthday party, they all had &#127828;s, &#127839;s, &#127850;s and &#127856;.

```

## hexHtmlify

Converts unicode characters in text to corresponding decimal hexadecimal html entities

```
String text = "A :cat:, :dog: and a :mouse: became friends. For the :dog:'s birthday party, they all had :hamburger:s, :fries:s, :cookie:s and :cake:.";

EmojiUtils.hexHtmlify(text); //returns A &#x1f431;, &#x1f436; and a &#x1f42d; became friends. For the &#x1f436;'s birthday party, they all had &#x1f354;s, &#x1f35f;s, &#x1f36a;s and &#x1f370;.

String text = "A 🐱, 🐶 and a 🐭 became friends. For the 🐶's birthday party, they all had 🍔s, 🍟s, 🍪s and 🍰."

EmojiUtils.hexHtmlify(text); //returns A &#x1f431;, &#x1f436; and a &#x1f42d; became friends. For the &#x1f436;'s birthday party, they all had &#x1f354;s, &#x1f35f;s, &#x1f36a;s and &#x1f370;.

```

## htmlify as Surrogate Entities

Converts unicode characters in text to corresponding decimal surrogate html entities

```
String text = "😃";

EmojiUtils.htmlify(text, true); //returns &#55357;&#56835;

```

## shortCodify

```
String text = "A 🐱, 🐶 and a 🐭 became friends❤️. For 🐶's birthday party, they all had 🍔s, 🍟s, 🍪s and 🍰.";

EmojiUtils.shortCodify(text); //returns A :cat:, :dog: and a :mouse: became friends:heart:. For :dog:'s birthday party, they all had :hamburger:s, :fries:s, :cookie:s and :cake:.

```

## removeAllEmojis
Removes unicode emoji characters from the passed string

```
String emojiText = "A 🐱, 🐱 and a 🐭 became friends❤️. For 🐶's birthday party, they all had 🍔s, 🍟s, 🍪s and 🍰.";

EmojiUtils.removeAllEmojis(emojiText);//"A ,  and a  became friends. For 's birthday party, they all had s, s, s and .

```

## countEmojis

Counts emojis in a String

```
String text = "A &#128049;, &#x1f436;,&nbsp;:coyote: and a :mouse: became friends. For :dog:'s birthday party, they all had 🍔s, :fries:s, :cookie:s and :cake:.";

EmojiUtils.countEmojis(text); //returns 8

```

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
