package de.uos.campusapp.api.studip.model.messages

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.github.jasminb.jsonapi.annotations.Id
import com.github.jasminb.jsonapi.annotations.Relationship
import com.github.jasminb.jsonapi.annotations.Type
import de.uos.campusapp.api.studip.model.person.StudipPerson
import de.uos.campusapp.component.ui.messages.model.AbstractMessage
import de.uos.campusapp.component.ui.messages.model.MessageMember
import de.uos.campusapp.component.ui.messages.model.MessageType
import org.joda.time.DateTime

@Type("messages")
@JsonIgnoreProperties("formattedDate", "replyable")
class StudipMessage() : AbstractMessage() {
    @Id
    var apiId: String? = null

    @JsonIgnore
    override var id: String = ""
        get() = apiId ?: ""


    @JsonProperty("subject")
    override var subject: String = ""

    @JsonProperty("message")
    override var text: String = ""
        get() {
            if (!field.startsWith("<!--HTML-->")) {
                // System messages are not html formatted
                return field.replace("\n", "<br>")
            }
            return field
        }

    @JsonProperty("mkdate")
    var mkdate: String? = null

    @Relationship("sender")
    var studipSender: StudipPerson? = null

    @JsonIgnore
    override var sender: MessageMember? = null
        get() = studipSender?.let { MessageMember(it.id, it.fullName) }

    @Relationship("recipients")
    var studipRecipients: List<StudipPerson>? = null

    @JsonIgnore
    override var recipients: List<MessageMember> = emptyList()
        get() = studipRecipients?.map { MessageMember(it.id, it.fullName) } ?: emptyList()

    @JsonIgnore
    override var date: DateTime = DateTime()
        get() = mkdate?.let { DateTime(it) } ?: DateTime()

    @JsonIgnore
    override var type: MessageType = MessageType.INBOX

    constructor(message: AbstractMessage): this() {
        subject = message.subject
        text = message.text
        studipRecipients = message.recipients.map {
            StudipPerson().apply { id = it.id }
        }
    }

}
