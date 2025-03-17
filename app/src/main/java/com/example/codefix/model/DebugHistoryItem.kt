package com.example.codefix.model

import android.os.Parcel
import android.os.Parcelable

data class DebugHistoryItem(
    val id: String = "", // Ensure ID is part of Firestore document
    val originalCode: String,
    val debuggedCode: String,
    val timestamp: Long
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readLong()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(originalCode)
        parcel.writeString(debuggedCode)
        parcel.writeLong(timestamp)
    }

    override fun describeContents(): Int = 0

    fun timestampFormatted(): String {
        // Convert timestamp to readable format
        return java.text.SimpleDateFormat("yyyy-MM-dd HH:mm", java.util.Locale.getDefault())
            .format(java.util.Date(timestamp))
    }

    companion object CREATOR : Parcelable.Creator<DebugHistoryItem> {
        override fun createFromParcel(parcel: Parcel): DebugHistoryItem {
            return DebugHistoryItem(parcel)
        }

        override fun newArray(size: Int): Array<DebugHistoryItem?> {
            return arrayOfNulls(size)
        }
    }
}
