package de.uos.campusapp.api.studip.model.chat

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import com.github.jasminb.jsonapi.annotations.Id
import com.github.jasminb.jsonapi.annotations.Relationship
import com.github.jasminb.jsonapi.annotations.Type
import de.uos.campusapp.api.studip.model.lectures.StudipLecture
import de.uos.campusapp.api.studip.model.person.StudipPerson
import de.uos.campusapp.component.ui.chat.model.AbstractChatRoom

@Type("blubber-threads")
class StudipBlubberThread : AbstractChatRoom() {
    @Id
    override var id: String = "0"

    @JsonProperty("context-type")
    private var contextType: String = ""

    @JsonIgnore
    override var title: String = ""
        get() {
            return when (contextType) {
                CONTEXT_PUBLIC -> "Globaler Chat"
                CONTEXT_PRIVATE -> {
                    val names = mentions?.joinToString(limit = 3) { it.fullName } ?: ""
                    "Privater Chat: $names"
                }
                CONTEXT_COURSE -> "Veranstaltung: ${lecture?.title ?: ""}"
                else -> "Kein Name angegeben"
            }
        }

    @JsonIgnore
    override var joined: Boolean = true // Delivered Stud.IP blubber threads are always joined

    @JsonIgnore
    override var members: Int? = null
        get() {
            return when (contextType) {
                CONTEXT_PRIVATE -> mentions?.size
                else -> null
            }
        }

    @Relationship("context")
    private val lecture: StudipLecture? = null

    @Relationship("mentions")
    private val mentions: List<StudipPerson>? = null

    companion object {
        const val CONTEXT_PRIVATE = "private"
        const val CONTEXT_PUBLIC = "public"
        const val CONTEXT_COURSE = "course"
    }
}