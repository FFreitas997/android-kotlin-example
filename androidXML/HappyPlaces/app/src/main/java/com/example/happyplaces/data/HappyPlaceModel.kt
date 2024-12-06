package com.example.happyplaces.data

data class HappyPlaceModel(
    var id: Int? = null,
    val title: String,
    val description: String,
    val date: String,
    val location: String,
    val image: ByteArray,
    val latitude: Double,
    val longitude: Double
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as HappyPlaceModel

        if (id != other.id) return false
        if (title != other.title) return false
        if (description != other.description) return false
        if (date != other.date) return false
        if (location != other.location) return false
        if (!image.contentEquals(other.image)) return false
        if (latitude != other.latitude) return false
        if (longitude != other.longitude) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id ?: 0
        result = 31 * result + title.hashCode()
        result = 31 * result + description.hashCode()
        result = 31 * result + date.hashCode()
        result = 31 * result + location.hashCode()
        result = 31 * result + image.contentHashCode()
        result = 31 * result + latitude.hashCode()
        result = 31 * result + longitude.hashCode()
        return result
    }

}
