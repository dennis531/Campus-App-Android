package de.uos.campusapp.api.studip.model.chat

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.github.jasminb.jsonapi.annotations.Id
import com.github.jasminb.jsonapi.annotations.Relationship
import com.github.jasminb.jsonapi.annotations.Type
import de.uos.campusapp.api.studip.model.person.StudipPerson
import de.uos.campusapp.component.ui.chat.model.AbstractChatMessage
import de.uos.campusapp.component.ui.chat.model.ChatMember
import org.joda.time.DateTime

@Type("blubber-comments")
@JsonIgnoreProperties("sendingStatus")
class StudipBlubberComment() : AbstractChatMessage() {
    @Id
    var apiId: String? = null

    @JsonIgnore
    override var id: String = "0"
        get() = apiId ?: "0"

    @JsonIgnore
    override var roomId: String = "0"
        get() = thread?.id ?: "0"

    @Relationship("thread")
    val thread: StudipBlubberThreadRelationship? = null

    @JsonProperty("content")
    override var text: String = ""

    @JsonIgnore
    override var timestamp: DateTime = DateTime()
        get() = DateTime(timestampIso)

    @JsonProperty("mkdate")
    private var timestampIso: String = ""

    @Relationship("author")
    private val author: StudipPerson? = null

    @JsonIgnore
    override var member: ChatMember = ChatMember()
        get() {
            return author?.let { ChatMember(it.id, it.username, it.fullName) } ?: ChatMember()
        }

    constructor(message: AbstractChatMessage) : this() {
        text = message.text
        timestampIso = message.timestamp.toString()
    }
}

@Type("blubber-threads")
class StudipBlubberThreadRelationship {
    @Id
    val id: String = ""
}
