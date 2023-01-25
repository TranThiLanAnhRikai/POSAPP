package com.example.pos.data.entity

data class Item (
    val name: String,
    val quantity: Int?,
    val price: String,
    val image: ByteArray
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Item

        if (quantity != other.quantity) return false
        if (price != other.price) return false
        if (!image.contentEquals(other.image)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = quantity.hashCode()
        result = 31 * result + price.hashCode()
        result = 31 * result + image.contentHashCode()
        return result
    }
}