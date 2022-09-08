package de.uos.campusapp.api.studip.model.lectures

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import com.github.jasminb.jsonapi.annotations.Id
import com.github.jasminb.jsonapi.annotations.Relationship
import com.github.jasminb.jsonapi.annotations.Type
import de.uos.campusapp.api.studip.model.person.StudipPerson
import de.uos.campusapp.component.ui.lectures.model.FileInterface
import org.joda.time.DateTime

@Type("file-refs")
class StudipLectureFile : FileInterface {
    @Id
    override val id: String = ""

    @JsonProperty("name")
    override val name: String = ""

    @JsonProperty("mime-type")
    override val mimeType: String = ""

    @JsonProperty("chdate")
    private val chdate: String = ""

    @JsonIgnore
    override var date: DateTime? = null
        get() {
            return if (chdate.isEmpty()) {
                null
            } else {
                DateTime(chdate)
            }
        }

    @Relationship("owner")
    private val ownerRelationship: StudipPerson? = null

    @JsonIgnore
    override var author: String? = null
        get() = ownerRelationship?.fullName

    @JsonProperty("filesize")
    override val size: Long? = null

    @JsonProperty("is-downloadable")
    val isDownloadable: Boolean = true
}