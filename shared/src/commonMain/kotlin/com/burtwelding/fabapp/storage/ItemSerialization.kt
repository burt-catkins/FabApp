package com.burtwelding.fabapp.storage

import com.burtwelding.fabapp.item.Item
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonPrimitive

/** ItemHandle and concrete item reads share the compiler-generated sealed Item representation. */
@PublishedApi
internal fun <T> storageSerializer(serializer: KSerializer<T>): KSerializer<T> {
    val base = Item.serializer().descriptor
    val subtypes = base.getElementDescriptor(1)
    val name = serializer.descriptor.serialName
    return if (name == base.serialName || subtypes.getElementIndex(name) >= 0) ItemSerializer(serializer)
    else serializer
}

private class ItemSerializer<T>(private val delegate: KSerializer<T>) : KSerializer<T> {
    override val descriptor = Item.serializer().descriptor
    override fun serialize(encoder: Encoder, value: T) = encoder.encodeSerializableValue(Item.serializer(), value as Item)
    override fun deserialize(decoder: Decoder): T {
        val input = decoder as JsonDecoder
        val data = input.decodeJsonElement()
        if (delegate.descriptor.serialName == descriptor.serialName) return input.json.decodeFromJsonElement(delegate, data)
        val tagged = data.jsonArray
        if (tagged[0].jsonPrimitive.content != delegate.descriptor.serialName) throw ItemTypeMismatchException()
        return input.json.decodeFromJsonElement(delegate, tagged[1])
    }
}

internal class ItemTypeMismatchException : SerializationException("Saved item has a different type")
