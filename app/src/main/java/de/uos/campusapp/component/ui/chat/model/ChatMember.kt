package de.uos.campusapp.component.ui.chat.model

import android.os.Parcel
import android.os.Parcelable

/**
 * Holds information about a chat member
 *
 * @property id
 * @property username User name, e.g. RZID
 * @property displayName Name displayed in chat
 */
open class ChatMember() : Parcelable {

    open var id: String = "0"
    open var username: String = ""
    open var displayName: String = ""

    /**
     * Create a chat member with the required information
     */
    constructor(id: String, username: String, displayName: String) : this() {
        this.id = id
        this.username = username
        this.displayName = displayName
    }

    constructor(parcel: Parcel) : this() {
        id = parcel.readString()!!
        username = parcel.readString()!!
        displayName = parcel.readString()!!
    }

    override fun toString(): String = displayName

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(username)
        parcel.writeString(displayName)
    }

    override fun describeContents() = 0

    companion object CREATOR : Parcelable.Creator<ChatMember> {
        override fun createFromParcel(parcel: Parcel): ChatMember {
            return ChatMember(parcel)
        }

        override fun newArray(size: Int): Array<ChatMember?> {
            return arrayOfNulls(size)
        }
    }
}
