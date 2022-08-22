package de.uos.campusapp.component.ui.messages.model

import android.os.Parcel

/**
 * Simple implementation of [AbstractMessageMember]
 */
class MessageMember(
    override val id: String,
    override val name: String
) : AbstractMessageMember() {

    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!
    )
}
