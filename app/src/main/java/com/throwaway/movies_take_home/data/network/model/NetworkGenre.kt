package com.throwaway.movies_take_home.data.network.model

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.jsonPrimitive

@Serializable(with = NetworkGenreSerializer::class)
data class NetworkGenre(
    val name: String,
    val count: Int
)

/**
 * Custom serializer for [NetworkGenre] that properly handles the return format.
 */
object NetworkGenreSerializer : KSerializer<NetworkGenre> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("NetworkGenre") {
        element<String>("name")
        element<Int>("count")
    }

    override fun deserialize(decoder: Decoder): NetworkGenre {
        val jsonDecoder = decoder as? JsonDecoder
            ?: throw IllegalArgumentException("This serializer can only be used with JSON")

        val jsonArray = jsonDecoder.decodeJsonElement() as JsonArray

        val name = jsonArray[0].jsonPrimitive.content
        val count = jsonArray[1].jsonPrimitive.content.toInt()

        return NetworkGenre(name, count)
    }

    override fun serialize(encoder: Encoder, value: NetworkGenre) {
        throw UnsupportedOperationException(
            "Serialization for ${NetworkGenreSerializer::class.simpleName} is not supported yet."
        )
    }
}