package com.example.flagquiz.data

import android.os.Parcel
import android.os.Parcelable

data class Statistic(
    val username: String,
    val score: Int,
    val time: String
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readInt(),
        parcel.readString()!!
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(username)
        parcel.writeInt(score)
        parcel.writeString(time)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Statistic> {
        override fun createFromParcel(parcel: Parcel): Statistic {
            return Statistic(parcel)
        }

        override fun newArray(size: Int): Array<Statistic?> {
            return arrayOfNulls(size)
        }
    }
}
