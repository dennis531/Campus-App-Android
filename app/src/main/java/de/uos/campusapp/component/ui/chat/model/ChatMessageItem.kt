package de.uos.campusapp.component.ui.chat.model

import android.content.Context
import android.os.Parcel
import de.uos.campusapp.utils.DateTimeUtils.getDateTime
import de.uos.campusapp.utils.DateTimeUtils.formatTimeOrDay
import de.uos.campusapp.utils.DateTimeUtils.getDateTimeString
import android.os.Parcelable
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import org.joda.time.DateTime

@Entity(tableName = "chat_message")
open class ChatMessageItem : Parcelable {
    @PrimaryKey
    @ColumnInfo(name = "_id")
    open var id: String = "0"
    open var roomId: String = "0"
    open var text: String = ""
    open var member: ChatMember = ChatMember()
    open var timestamp: DateTime = DateTime()

    @ColumnInfo(name = "sending")
    var sendingStatus = STATUS_SENT

    /**
     * Default constructor: called by gson when parsing an element
     */
    @Ignore
    constructor() {
        sendingStatus = STATUS_SENT
    }

    /**
     * Called when creating a new chat message
     *
     * @param text   ChatNotification message text
     * @param member Member who sent the message
     */
    constructor(text: String, member: ChatMember) : super() {
        this.text = text
        this.member = member
        timestamp = DateTime.now()
        sendingStatus = STATUS_SENDING
    }

    @Ignore
    constructor(id: String, roomId: String, text: String, member: ChatMember, timestamp: DateTime) : super() {
        this.id = id
        this.roomId = roomId
        this.text = text
        this.member = member
        this.timestamp = timestamp
        sendingStatus = STATUS_SENT
    }

    protected constructor(parcel: Parcel) {
        id = parcel.readString()!!
        roomId = parcel.readString()!!
        text = parcel.readString()!!
        timestamp = getDateTime(parcel.readString()!!)
        member = parcel.readParcelable(ChatMember::class.java.classLoader)!!
        sendingStatus = parcel.readInt()
    }

    fun getFormattedTimestamp(context: Context?): String {
        return formatTimeOrDay(timestamp, context!!)
    }

    fun toChatMessage(): AbstractChatMessage {
        return ChatMessage(id, roomId, text, member, timestamp)
    }

    val isNewMessage: Boolean
        get() = id == "0"

    override fun describeContents(): Int = 0

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(roomId)
        parcel.writeString(text)
        parcel.writeString(getDateTimeString(timestamp))
        parcel.writeParcelable(member, flags)
        parcel.writeInt(sendingStatus)
    }

    companion object {
        const val STATUS_SENT = 0
        const val STATUS_SENDING = 1
        const val STATUS_ERROR = 2

        @JvmField
        val CREATOR: Parcelable.Creator<ChatMessageItem> = object : Parcelable.Creator<ChatMessageItem> {
            override fun createFromParcel(parcel: Parcel): ChatMessageItem {
                return ChatMessageItem(parcel)
            }

            override fun newArray(size: Int): Array<ChatMessageItem?> {
                return arrayOfNulls(size)
            }
        }
    }
}