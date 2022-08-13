package de.tum.`in`.tumcampusapp.database

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import de.tum.`in`.tumcampusapp.component.ui.chat.model.ChatMember
import de.tum.`in`.tumcampusapp.component.ui.messages.model.MessageMember
import de.tum.`in`.tumcampusapp.utils.DateTimeUtils
import de.tum.`in`.tumcampusapp.utils.tryOrNull
import org.joda.time.DateTime

class Converters {
    @TypeConverter
    fun isoToDateTime(str: String?): DateTime? {
        return if (str == null) null else DateTimeUtils.getDateTime(str)
    }

    @TypeConverter
    fun fromDateTime(date: DateTime?): String? {
        return if (date == null) null else DateTimeUtils.getDateTimeString(date)
    }

    @TypeConverter
    fun fromChatMember(member: ChatMember?): String = Gson().toJson(member)

    @TypeConverter
    fun toChatMember(member: String): ChatMember? {
        return tryOrNull { Gson().fromJson(member, ChatMember::class.java) }
    }

    @TypeConverter
    fun fromMessageMember(member: MessageMember?): String = Gson().toJson(member)

    @TypeConverter
    fun toMessageMember(member: String): MessageMember? {
        return tryOrNull { Gson().fromJson(member, MessageMember::class.java) }
    }

    @TypeConverter
    fun fromMessageMemberList(members: List<MessageMember>?): String = Gson().toJson(members)

    @TypeConverter
    fun toMessageMemberList(member: String): List<MessageMember>? {
        return tryOrNull {
            val type = object : TypeToken<List<MessageMember>>() {}.type
            Gson().fromJson(member, type)
        }
    }
}
