package com.plcoding.bookpedia.book.data.network

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull

object BookDescriptionSerializer : KSerializer<String?> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("BookDescriptionSerializer")

    override fun deserialize(decoder: Decoder): String? {
        val composite = decoder as? JsonDecoder ?: throw SerializationException("Only JSON supported")
        val element = composite.decodeJsonElement()

        return when (element) {
            is JsonPrimitive -> element.contentOrNull
            is JsonObject -> {
                val value = element["value"] as? JsonPrimitive
                value?.contentOrNull
            }
            else -> null
        }
    }

    override fun serialize(encoder: Encoder, value: String?) {
        if (value == null) {
            encoder.encodeNull()
        } else {
            encoder.encodeString(value)
        }
    }
}
