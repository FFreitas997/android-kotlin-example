package com.example.happyplaces.data

import android.os.Parcel
import android.os.Parcelable

data class MapModel(
    val title: String,
    val location: String,
    val latitude: Double,
    val longitude: Double
): Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readDouble(),
        parcel.readDouble()
    ) {}

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(title)
        parcel.writeString(location)
        parcel.writeDouble(latitude)
        parcel.writeDouble(longitude)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<MapModel> {
        override fun createFromParcel(parcel: Parcel): MapModel {
            return MapModel(parcel)
        }

        override fun newArray(size: Int): Array<MapModel?> {
            return arrayOfNulls(size)
        }
    }
}