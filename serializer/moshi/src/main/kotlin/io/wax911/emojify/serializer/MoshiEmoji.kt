package io.wax911.emojify.serializer

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import io.wax911.emojify.contract.model.AbstractEmoji

/**
 * Default implementation of AbstractEmoji for moshi
 */
@JsonClass(generateAdapter = true)
data class MoshiEmoji(
    @Json(name = "aliases") override val aliases: List<String>? = null,
    @Json(name = "description") override val description: String? = null,
    @Json(name = "emoji") override val emoji: String,
    @Json(name = "emojiChar") override val emojiChar: String,
    @Json(name = "supports_fitzpatrick") override val supportsFitzpatrick: Boolean = false,
    @Json(name = "supports_gender") override val supportsGender: Boolean = false,
    @Json(name = "tags") override val tags: List<String>? = null,
): AbstractEmoji()
