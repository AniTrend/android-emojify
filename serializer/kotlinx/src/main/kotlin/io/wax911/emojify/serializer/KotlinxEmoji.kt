package io.wax911.emojify.serializer

import io.wax911.emojify.contract.model.AbstractEmoji
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Default implementation of AbstractEmoji for kotlinx-serialization
 */
@Serializable
data class KotlinxEmoji(
    @SerialName("aliases") override val aliases: List<String>? = null,
    @SerialName("description") override val description: String? = null,
    @SerialName("emoji") override val emoji: String,
    @SerialName("emojiChar") override val emojiChar: String,
    @SerialName("supports_fitzpatrick") override val supportsFitzpatrick: Boolean = false,
    @SerialName("supports_gender") override val supportsGender: Boolean = false,
    @SerialName("tags") override val tags: List<String>? = null,
) : AbstractEmoji()
