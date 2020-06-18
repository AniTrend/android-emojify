> This project adheres to [Semantic Versioning](http://semver.org/).

Change Log
==========

Version 1.6.0-alpha01 *(2020-06-18)*
----------------------------

* **Update**: Refactor unit tests to reflect recent changes [212a1f](https://github.com/AniTrend/android-emojify/commit/212a1f82aa0ec593766de2ebdf5234468176d61d)
* **Improvement**: Refactor `EmojiParser` from object to extension functions and move out  EmojiTransformer, AliasCandidate and UnicodeCandidate to seperate classes [d18576](https://github.com/AniTrend/android-emojify/commit/d185767b7f66d55d383948ad31d1aa59f2a24911)
* **Improvement**: Rename `EmojiTrie` to `EmojiTree` and move `Matches` and `Node` classes out to seperate classes [aecf00](https://github.com/AniTrend/android-emojify/commit/aecf003153ed3ae6b4a10c4229312e18641e9506) [56d3ac](https://github.com/AniTrend/android-emojify/commit/56d3acf88d83fe189adb08f904310ee44dcc6bf9) [af038b](https://github.com/AniTrend/android-emojify/commit/af038bca2871c7ed7ac0c81b090d8fe82eac7392)
* **New**: Add emoji manager intializer through `androidx.startup` [d94440](https://github.com/AniTrend/android-emojify/commit/d9444085b9b53d9cb871e704129d1efb881edcf2)
* **New**: Add initial proguard-rules for `kotlinx-serialization` [d49f01](https://github.com/AniTrend/android-emojify/commit/d49f01286dd7b3c42f0f39fc351c21eb794a8381)
* **Improvement**: Rewrite `Emoji` class, migrating from gson to `kotlinx-serialization` [14062c](https://github.com/AniTrend/android-emojify/commit/14062ce77866bc4365655d005dc1d0a3b7ee47cb)
* **Improvement**: Rewrite `EmojiManager` as instance class instead of object [a9fe4e](https://github.com/AniTrend/android-emojify/commit/a9fe4ebc7b53cbaf28eedbd2fb50113fc8220e7e)
* **New**: Add `kotlinx-serialization.runtime` dependency [c4edfa](https://github.com/AniTrend/android-emojify/commit/c4edfa04f377fa2dea627a2b0f48077ddacf615e)
* **New**: Add `kotlinx-serialization` to classpath [33ab74](https://github.com/AniTrend/android-emojify/commit/33ab743c6ae03c3ed37451d68b6c4864c9fd6c5d)

Version 1.5.4-alpha01 *(2020-06-06)*
----------------------------

* **Improvement**: Migrate from groovy to kotlin script style [3a2c025](https://github.com/AniTrend/android-emojify/commit/3a2c02530c2fd4ebf4f9a6d4fe8dce5bfbd32557)
* **New**: Add buildSrc and bump gson to 2.8.6 [b7b74ea](https://github.com/AniTrend/android-emojify/commit/b7b74ea6aadb18acd0fbdae483b99c9cd0143d8f)
* **New**: Add `like` alias to thums up emoji [4339063](https://github.com/AniTrend/android-emojify/commit/433906333f5230624d056e425943ace7dce5aa9b)
* **Update**: Bump kotlinVersion from 1.3.71 to 1.3.72 [d869f0d]()
* **Update**: Bump gradle from 3.6.3 to 4.0.0 [#30](https://github.com/AniTrend/android-emojify/pull/30)
* **Update**: Bump junit from 4.12 to 4.13 [#22](https://github.com/AniTrend/android-emojify/pull/22)
* **Update**: Bump gson from 2.8.5 to 2.8.6 [#15](https://github.com/AniTrend/android-emojify/pull/15)

Version 1.5.3 *(2019-10-05)*
----------------------------

* **New**: Add support for emojis up to [Unicode 11.0](https://emojipedia.org/unicode-11.0/) [4288732](https://github.com/AniTrend/android-emojify/commit/428873250dc5865d268aae36bbd459d0306b3167)
* **Fix**: Refactor Emoji Manager Test `containEmoji` [d6ac829](https://github.com/AniTrend/android-emojify/commit/d6ac82975d251c8cd03157c49a3cd712c13f5799)
* **Improvement**: Remove redundant by lazy blocks and update function documentation [b62f3ef](https://github.com/AniTrend/android-emojify/commit/b62f3ef789a1911458db69568bdb27f89c68e5a0) [fcad054](https://github.com/AniTrend/android-emojify/commit/fcad05446f0438cfe0e3284e8aad1ed7e13c0222)
* **Update**: Bump gradle from 3.4.2 to 3.5.0 [#12](https://github.com/AniTrend/android-emojify/pull/12)
* **Update**: Bump `kotlinVersion` from 1.3.41 to 1.3.50 [#13](https://github.com/AniTrend/android-emojify/pull/13)
* **Update**: Bump compile & target SDK to Android Q [4f642eb](https://github.com/AniTrend/android-emojify/commit/4f642eb803c16f3dbdd92ac8a1d9ce068a130ad5)
* **Update:** Bump gradle from 3.4.1 to 3.4.2 [#11](https://github.com/AniTrend/android-emojify/pull/11)
* **Update**: Bump `kotlinVersion` from 1.3.40 to 1.3.41 [#10](https://github.com/AniTrend/android-emojify/pull/10)
* **Update**: Bump kotlinVersion from 1.3.31 to 1.3.40 [391fa47](https://github.com/AniTrend/android-emojify/commit/391fa47974f7831713e43b60f12bfb4df6e4280d)
* **Update**: Bump espresso-core from 3.1.0 to 3.2.0 [#7](https://github.com/AniTrend/android-emojify/pull/7)
* **Update**: Bump runner from 1.1.0 to 1.2.0 [22b10c5](https://github.com/AniTrend/android-emojify/commit/22b10c5b385bf2a4347e1b70daf797787b491317)

Version 1.5.2 *(2019-06-05)*
----------------------------

* **Improvement**: Make usage of `.use` extension to auto close streams [01109a6](https://github.com/AniTrend/android-emojify/commit/01109a664ec943c946bca4b58cd7b7e844c49a2e) 

Version 1.5.1 *(2019-05-24)*
----------------------------

* **Fix**: Remove unused or unneeded manifest declarations [3852a80](https://github.com/AniTrend/android-emojify/commit/3852a80ca9cfa43a5344e3ee774d0b58adc5bf44)
* **Update**: Updated gradle build tools [a293900](https://github.com/AniTrend/android-emojify/commit/a293900ad90f04f9ef6174e7347f974c142cef01)

Version v1.5.0 *(2019-04-19)*
----------------------------

* **New**: Migrate to AndroidX artifacts [a6553b3](https://github.com/AniTrend/android-emojify/commit/a6553b32e2073ea49849b343f2eecd78a02c6174)

Version 1.4.0 *(2018-10-20)*
----------------------------

* **Fix:** Initialisation of EmojiManager properties [40db9d3](https://github.com/AniTrend/android-emojify/commit/40db9d332580d28d67e526296037036d72ff5ba3)
* **Fix**: Refactor EmojiParser, direct `fitzpatrick` initialisation instead of using `init` block and remove redundant non-null assertion [acf80c6](https://github.com/AniTrend/android-emojify/commit/acf80c658b2686fc2df7501c771f6722310505a3)
* **Fix**: Revise null checks and assertion in EmojiTrie [9303882](https://github.com/AniTrend/android-emojify/commit/93038829589dc25f0308d8052291261e3c0839eb)
* **Fix**: Fix missing serialisation field [0818ecd](https://github.com/AniTrend/android-emojify/commit/0818ecd2cd75d7c5b26f56fa07ef22b70413781d)
* **Fix**: Remove exception handling within: EmojiManager [9e2e98](https://github.com/AniTrend/android-emojify/commit/9e2e98a7578af3f4bcf28e2dbdae2245e819d209) [#5](https://github.com/AniTrend/android-emojify/pull/5)
* **Update:** Update Kotlin version & remove Coroutines [a8f2a20](https://github.com/AniTrend/android-emojify/commit/a8f2a20be3ccfcfc7ff5f4047a6b88461786f72d)
* **New**: Added supported emoji list [a8f2a20](https://github.com/AniTrend/android-emojify/commit/a8f2a20be3ccfcfc7ff5f4047a6b88461786f72d)
* **Update**: Rebase library on: vdurmont/emoji-java [e34bfb3](https://github.com/AniTrend/android-emojify/commit/e34bfb31c5f4f3a10535e7cf014e9c1ec4fea0cc)
* **Update**: Update dependencies & add Kotlin support [bce9855](https://github.com/AniTrend/android-emojify/commit/bce9855c9f2f05e11bcbf00548e53a9c1460fce7)