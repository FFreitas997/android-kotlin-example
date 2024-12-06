package com.example.happyplaces.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "happy_places")
data class HappyPlaceEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,

    @ColumnInfo(name = "title")
    val title: String,

    @ColumnInfo(name = "description")
    val description: String,

    @ColumnInfo(name = "date")
    val date: String,

    @ColumnInfo(name = "location")
    val location: String,

    @ColumnInfo(name = "image")
    val image: ByteArray,

    @ColumnInfo(name = "latitude")
    val latitude: Double,

    @ColumnInfo(name = "longitude")
    val longitude: Double
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as HappyPlaceEntity

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
