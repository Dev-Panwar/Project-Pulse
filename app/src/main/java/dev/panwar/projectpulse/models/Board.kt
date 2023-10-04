package dev.panwar.projectpulse.models

import android.os.Parcel
import android.os.Parcelable

// Board is a Group where number of People are Contributing towards a Project
data class Board(
    val name:String="",
    val image:String="",
    val createdBy:String="",
    val assignedTo:ArrayList<String>,
    var documentId:String=""
):Parcelable {
//    Below lines of code generated by Generate Parcelable code plugin

//    this is a default constructor added by us....By adding this no-argument constructor, you satisfy Firestore's requirements for deserialization. Firestore will use this constructor to create instances of your Board class when retrieving data from the database.
     constructor() : this("", "", "", ArrayList(), "")
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
//       added by me...for Assigned to
        parcel.createStringArrayList()!!,
        parcel.readString()!!
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) = with(parcel) {
        parcel.writeString(name)
        parcel.writeString(image)
        parcel.writeString(createdBy)
        //       added by me...for Assigned to
        parcel.writeStringList(assignedTo)
        parcel.writeString(documentId)
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
