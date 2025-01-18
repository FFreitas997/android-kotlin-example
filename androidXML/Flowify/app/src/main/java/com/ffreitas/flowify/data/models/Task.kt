package com.ffreitas.flowify.data.models

import android.os.Parcel
import android.os.Parcelable

data class Task(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val createdBy: String = "",
    val createdByName: String = "",
    val createdAt: String = "",
    val boardName: String = "",
    val boardImage: String = ""
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(title)
        parcel.writeString(description)
        parcel.writeString(createdBy)
        parcel.writeString(createdByName)
        parcel.writeString(createdAt)
        parcel.writeString(boardName)
        parcel.writeString(boardImage)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Task> {
        override fun createFromParcel(parcel: Parcel): Task {
            return Task(parcel)
        }

        override fun newArray(size: Int): Array<Task?> {
            return arrayOfNulls(size)
        }
    }
}
