package dev.panwar.projectpulse.models

import android.os.Parcel
import android.os.Parcelable

data class Card(
    val name:String="",
    val createdBy:String="",
    val assignedTo:ArrayList<String> = ArrayList(),
    val labelColor:String=""
):Parcelable {
    constructor(source: Parcel) : this(
        source.readString()!!,
        source.readString()!!,
//    written by me
        source.createStringArrayList()!!
    ) {
    }

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        dest.writeString(name)
        dest.writeString(createdBy)
        dest.writeStringList(assignedTo)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Card> {
        override fun createFromParcel(parcel: Parcel): Card {
            return Card(parcel)
        }

        override fun newArray(size: Int): Array<Card?> {
            return arrayOfNulls(size)
        }
    }
}
