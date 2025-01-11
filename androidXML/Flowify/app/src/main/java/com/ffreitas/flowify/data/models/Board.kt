package com.ffreitas.flowify.data.models

import android.os.Parcel
import android.os.Parcelable

data class Board(
    val id: String = "",
    val name: String = "",
    val picture: String = "",
    val createdBy: String = "",
    val createdAt: String = "",
    val assignTo: List<String> = emptyList(),
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.createStringArrayList() ?: emptyList()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(name)
        parcel.writeString(picture)
        parcel.writeString(createdBy)
        parcel.writeString(createdAt)
        parcel.writeStringList(assignTo)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Board> {
        override fun createFromParcel(parcel: Parcel): Board {
            return Board(parcel)
        }

        override fun newArray(size: Int): Array<Board?> {
            return arrayOfNulls(size)
        }
    }
}