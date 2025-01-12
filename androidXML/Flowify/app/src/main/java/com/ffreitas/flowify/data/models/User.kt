package com.ffreitas.flowify.data.models

import android.os.Parcelable

data class User(
    val id: String = "",
    var name: String = "",
    val email: String = "",
    var picture: String = "",
    var mobile: Long = 0
) : Parcelable {

    constructor(parcel: android.os.Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readLong()
    )

    override fun writeToParcel(parcel: android.os.Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(name)
        parcel.writeString(email)
        parcel.writeString(picture)
        parcel.writeLong(mobile)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<User> {
        override fun createFromParcel(parcel: android.os.Parcel): User {
            return User(parcel)
        }

        override fun newArray(size: Int): Array<User?> {
            return arrayOfNulls(size)
        }
    }
}
