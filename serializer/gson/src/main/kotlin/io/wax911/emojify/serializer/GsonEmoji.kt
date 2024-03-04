package io.wax911.emojify.serializer

import com.google.gson.annotations.SerializedName
import io.wax911.emojify.contract.model.AbstractEmoji

/**
 * Default implementation of AbstractEmoji for gson
 */
data class GsonEmoji(
    @SerializedName(value = "aliases") override val aliases: List<String>?,
    @SerializedName(value = "description") override val description: String?,
    @SerializedName(value = "emoji") override val emoji: String,
    @SerializedName(value = "emojiChar") override val emojiChar: String,
    @SerializedName(value = "supports_fitzpatrick") override val supportsFitzpatrick: Boolean,
    @SerializedName(value = "supports_gender") override val supportsGender: Boolean,
    @SerializedName(value = "tags") override val tags: List<String>?,
): AbstractEmoji()
