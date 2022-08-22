package de.uos.campusapp.component.ui.messages.model

import android.os.Parcel
import android.os.Parcelable

/**
 * Represents a message member
 *
 * @property id
 * @property name Name of member
 */
abstract class AbstractMessageMember(): Parcelable {
    abstract val id: String
    abstract val name: String

    override fun toString(): String = name

    override fun equals(other: Any?): Boolean {
        if (other is AbstractMessageMember) {
            return id == other.id
        }

        return false
    }

    override fun hashCode(): Int = id.hashCode()

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(name)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<AbstractMessageMember> {
        override fun createFromParcel(parcel: Parcel): AbstractMessageMember {
            return MessageMember(parcel)
        }

        override fun newArray(size: Int): Array<AbstractMessageMember?> {
            return arrayOfNulls(size)
        }
    }
}
