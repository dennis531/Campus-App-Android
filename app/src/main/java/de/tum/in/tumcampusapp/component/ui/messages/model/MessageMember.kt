package de.tum.`in`.tumcampusapp.component.ui.messages.model

import android.os.Parcel
import android.os.Parcelable

open class MessageMember(
    open val id: String = "",
    open val name: String = ""
): Parcelable {

    override fun toString(): String = name

    override fun equals(other: Any?): Boolean {
        if (other is MessageMember) {
            return id == other.id
        }

        return false
    }

    override fun hashCode(): Int = id.hashCode()

    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(name)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<MessageMember> {
        override fun createFromParcel(parcel: Parcel): MessageMember {
            return MessageMember(parcel)
        }

        override fun newArray(size: Int): Array<MessageMember?> {
            return arrayOfNulls(size)
        }
    }
}
