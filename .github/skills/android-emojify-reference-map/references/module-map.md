# Module Reference Map

Use this map to place code before searching for a specific file.

| Module | Depends on | Package roots | Use for | Dokka |
| --- | --- | --- | --- | --- |
| `:contract` | none | `io.wax911.emojify.contract.model/`, `io.wax911.emojify.contract.serializer/`, `io.wax911.emojify.contract.util.trie/` | Shared interfaces: `IEmoji`, `IEmojiDeserializer`, trie `Matches` | `https://anitrend.github.io/android-emojify/contract/index.html` |
| `:serializer:kotlinx` | `:contract` | root serializer package | kotlinx.serialization-based `IEmojiDeserializer` implementation | `https://anitrend.github.io/android-emojify/serializer/kotlinx/index.html` |
| `:serializer:gson` | `:contract` | root serializer package | Gson-based `IEmojiDeserializer` implementation | `https://anitrend.github.io/android-emojify/serializer/gson/index.html` |
| `:serializer:moshi` | `:contract` | root serializer package | Moshi-based `IEmojiDeserializer` implementation | `https://anitrend.github.io/android-emojify/serializer/moshi/index.html` |
| `:emojify` | `:contract` | `manager/`, `parser/`, `parser/action/`, `parser/candidate/`, `parser/transformer/`, `util/`, `util/trie/` | `EmojiManager`, `EmojiParser`, Fitzpatrick modifiers, trie lookup, emoji extensions | `https://anitrend.github.io/android-emojify/emojify/index.html` |
| `:app` | `:emojify`, serializers | — | Sample application; excluded from CI; not a dependency target | — |

## Dependency Direction

```
:contract
   ↑
:serializer:kotlinx  :serializer:gson  :serializer:moshi
   ↑                        ↑                  ↑
                        :emojify
                            ↑
                          :app (local only)
```

Never invert this direction. Serializers must not depend on `:emojify`; `:emojify` must not depend on a specific serializer at compile time (only in `testImplementation` for the test suite).

## Placement Heuristics

- New shared model or interface used by both `:emojify` and serializers: `:contract`.
- New serialization format or JSON library adapter: new or existing `:serializer:*` module.
- New parsing behavior, emoji lookup logic, or trie helper: `:emojify`.
- New emoji data or schema change: `emojify/src/main/assets/emoticons/emoji.json`; verify all three serializers still parse correctly.
- Sample usage or integration demo: `:app` (not committed to CI).

## Consumer Notes

- Consumers add `:emojify` as their primary dependency and one `:serializer:*` module for JSON parsing.
- `:contract` types (`IEmoji`, `IEmojiDeserializer`) are what consumers implement or pass in; they rarely need to depend on `:contract` directly unless writing a custom serializer.
- When adding a new public API in `:emojify` or `:contract`, assume the Dokka page is part of the deliverable.
- Emoji assets shipped in `emojify/src/main/assets/emoticons/emoji.json` are the source of truth; the `scripts/emoji_generator` utility is used to regenerate them from upstream sources.
