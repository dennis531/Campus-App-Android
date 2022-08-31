package de.uos.campusapp.database

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import de.uos.campusapp.component.ui.chat.model.ChatMember
import de.uos.campusapp.component.ui.messages.model.AbstractMessageMember
import de.uos.campusapp.component.ui.messages.model.MessageMember
import de.uos.campusapp.utils.DateTimeUtils
import de.uos.campusapp.utils.tryOrNull
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
    fun fromMessageMember(member: AbstractMessageMember?): String = Gson().toJson(member)

    @TypeConverter
    fun toMessageMember(member: String): AbstractMessageMember? {
        return tryOrNull { Gson().fromJson(member, MessageMember::class.java) }
    }

    @TypeConverter
    fun fromMessageMemberList(members: List<AbstractMessageMember>?): String = Gson().toJson(members)

    @TypeConverter
    fun toMessageMemberList(member: String): List<AbstractMessageMember>? {
        return tryOrNull {
            val type = object : TypeToken<List<MessageMember>>() {}.type
            Gson().fromJson(member, type)
        }
    }
}
