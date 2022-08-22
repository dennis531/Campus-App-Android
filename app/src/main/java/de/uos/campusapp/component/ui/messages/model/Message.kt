package de.uos.campusapp.component.ui.messages.model

import android.os.Parcel
import de.uos.campusapp.utils.DateTimeUtils
import org.joda.time.DateTime

/**
 * Simple Message implementation
 */
class Message(
    override val id: String,
    override val subject: String,
    override val text: String,
    override var type: MessageType,
    override val sender: AbstractMessageMember?,
    override val recipients: List<AbstractMessageMember>,
    override val date: DateTime
) : AbstractMessage() {

    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        MessageType.valueOf(parcel.readString()!!),
        parcel.readParcelable(AbstractMessageMember::class.java.classLoader),
        parcel.readParcelableArray(AbstractMessageMember::class.java.classLoader)!!.toList() as List<AbstractMessageMember>,
        DateTimeUtils.getDateTime(parcel.readString()!!)
    )
}