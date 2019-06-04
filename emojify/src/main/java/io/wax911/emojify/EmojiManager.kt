package io.wax911.emojify

import android.content.Context
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import io.wax911.emojify.model.Emoji
import io.wax911.emojify.parser.EmojiParser
import io.wax911.emojify.util.EmojiTrie
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.*


/**
 * Holds the loaded emojis and provides search functions.
 *
 * @author Vincent DURMONT [vdurmont@gmail.com]
 */
object EmojiManager {

    private val EMOJIS_BY_ALIAS by lazy {
        val aliasMap = HashMap<String, Emoji>()
        ALL_EMOJIS.forEach { emoji ->
            emoji.aliases?.forEach { alias ->
                aliasMap[alias] = emoji
            }
        }
        aliasMap
    }

    private val EMOJIS_BY_TAG by lazy {
        val emojiTagMap = HashMap<String, MutableSet<Emoji>>()
        ALL_EMOJIS.forEach { emoji ->
            emoji.tags?.forEach { tag ->
                if (emojiTagMap[tag] == null)
                    emojiTagMap[tag] = HashSet()
                emojiTagMap[tag]?.add(emoji)
            }
        }
        emojiTagMap
    }

    private val EMOJI_TRIE: EmojiTrie by lazy {
        EmojiTrie(ALL_EMOJIS)
    }

    private val ALL_EMOJIS by lazy {
        ArrayList<Emoji>()
    }

    private const val PATH = "emoticons/emoji.json"

    /**
     * Initializes emoji objects from an asset file in the library directory
     *
     * @param context any valid application context
     * @throws Exception may throw but not limited to [java.io.IOException] when an error occurs
     */
    @Throws(Exception::class)
    fun initEmojiData(context: Context) {
        if (ALL_EMOJIS.isEmpty()) {
            val gson = GsonBuilder()
                    .enableComplexMapKeySerialization()
                    .setLenient().create()
            InputStreamReader(context.assets.open(PATH)).use { streamReader ->
                BufferedReader(streamReader).use {
                    ALL_EMOJIS.apply {
                        addAll(gson.fromJson(it, object : TypeToken<ArrayList<Emoji>>() {}.type))
                        forEach { emoji -> emoji.initProperties() }
                    }
                }
            }
        }
    }

    /**
     * Returns all the [Emoji]s for a given tag.
     *
     * @param tag the tag
     *
     * @return the associated [Emoji]s, null if the tag
     * is unknown
     */
    fun getForTag(tag: String?): Set<Emoji>? {
        return tag.let {
            EMOJIS_BY_TAG[it]
        }
    }

    /**
     * Returns the [Emoji] for a given alias.
     *
     * @param alias the alias
     *
     * @return the associated [Emoji], null if the alias
     * is unknown
     */
    fun getForAlias(alias: String?): Emoji? {
        return alias?.let {
            EMOJIS_BY_ALIAS[trimAlias(it)]
        }
    }

    private fun trimAlias(alias: String): String {
        var result = alias
        if (result.startsWith(":")) {
            result = result.substring(1, result.length)
        }
        if (result.endsWith(":")) {
            result = result.substring(0, result.length - 1)
        }
        return result
    }


    /**
     * Returns the [Emoji] for a given unicode.
     *
     * @param unicode the the unicode
     *
     * @return the associated [Emoji], null if the
     * unicode is unknown
     */
    fun getByUnicode(unicode: String?): Emoji? {
        return if (unicode == null) {
            null
        } else EMOJI_TRIE.getEmoji(unicode)
    }

    /**
     * Returns all the [Emoji]s
     *
     * @return all the [Emoji]s
     */
    fun getAll(): Collection<Emoji> {
        return ALL_EMOJIS
    }

    /**
     * Tests if a given String is an emoji.
     *
     * @param string the string to test
     *
     * @return true if the string is an emoji's unicode, false else
     */
    fun isEmoji(string: String?): Boolean {
        if (string == null) return false

        val unicodeCandidate = EmojiParser
                .getNextUnicodeCandidate(string.toCharArray(), 0)
        return unicodeCandidate != null &&
                unicodeCandidate.emojiStartIndex == 0 &&
                unicodeCandidate.fitzpatrickEndIndex == string.length
    }

    /**
     * Tests if a given String only contains emojis.
     *
     * @param string the string to test
     *
     * @return true if the string only contains emojis, false else
     */
    fun isOnlyEmojis(string: String?): Boolean {
        return string != null && EmojiParser.removeAllEmojis(string).isEmpty()
    }

    /**
     * Checks if sequence of chars contain an emoji.
     * @param sequence Sequence of char that may contain emoji in full or
     * partially.
     *
     * @return
     * &lt;li&gt;
     * Matches.EXACTLY if char sequence in its entirety is an emoji
     * &lt;/li&gt;
     * &lt;li&gt;
     * Matches.POSSIBLY if char sequence matches prefix of an emoji
     * &lt;/li&gt;
     * &lt;li&gt;
     * Matches.IMPOSSIBLE if char sequence matches no emoji or prefix of an
     * emoji
     * &lt;/li&gt;
     */
    fun isEmoji(sequence: CharArray): EmojiTrie.Matches {
        return EMOJI_TRIE.isEmoji(sequence)
    }

    /**
     * Returns all the tags in the database
     *
     * @return the tags
     */
    fun getAllTags(): Collection<String> {
        return EMOJIS_BY_TAG.keys
    }
}
