package de.uos.campusapp.component.ui.messages.model

import android.content.Context
import android.content.Intent
import android.os.Parcel
import android.os.Parcelable
import de.uos.campusapp.component.ui.messages.activity.MessagesDetailsActivity
import de.uos.campusapp.utils.DateTimeUtils
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat

/**
 * Represents an abstract message
 *
 * All fields are required
 * @property id Identifier
 * @property subject Message subject
 * @property text Message content (Html formatted)
 * @property type Message type
 * @property sender Message sender
 * @property recipients List of recipients (required for outgoing messages)
 * @property date Message date
 */
abstract class AbstractMessage: Parcelable {
    abstract val id: String
    abstract val subject: String
    abstract val text: String
    abstract var type: MessageType
    abstract val sender: AbstractMessageMember?
    abstract val recipients: List<AbstractMessageMember>
    abstract val date: DateTime

    val formattedDate: String
        get() = DATE_FORMAT.print(date)

    val replyable: Boolean
        get() = sender != null && type != MessageType.OUTBOX

    fun getIntent(context: Context): Intent {
        return Intent(context, MessagesDetailsActivity::class.java).also {
            it.putExtra(MessagesDetailsActivity.EXTRA_MESSAGE, this)
        }
    }

    fun toMessageItem(): MessageItem {
        return MessageItem(id, subject, text, type.id, sender, recipients, date)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(subject)
        parcel.writeString(text)
        parcel.writeString(type.name)
        parcel.writeParcelable(sender, flags)
        parcel.writeParcelableArray(recipients.toTypedArray(), flags)
        parcel.writeString(DateTimeUtils.getDateTimeString(date))
    }

    override fun describeContents(): Int = 0

    companion object {
        private val DATE_FORMAT = DateTimeFormat.shortDateTime()

        @JvmField
        val CREATOR: Parcelable.Creator<AbstractMessage> = object : Parcelable.Creator<AbstractMessage> {
            override fun createFromParcel(parcel: Parcel): AbstractMessage {
                return Message(parcel)
            }

            override fun newArray(size: Int): Array<AbstractMessage?> {
                return arrayOfNulls(size)
            }
        }
    }
}