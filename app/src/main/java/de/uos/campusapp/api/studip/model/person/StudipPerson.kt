package de.uos.campusapp.api.studip.model.person

import com.fasterxml.jackson.annotation.*
import com.github.jasminb.jsonapi.annotations.Id
import com.github.jasminb.jsonapi.annotations.Meta
import com.github.jasminb.jsonapi.annotations.Type
import de.uos.campusapp.component.ui.person.model.InstituteInterface
import de.uos.campusapp.component.ui.person.model.PersonInterface
import de.uos.campusapp.component.ui.person.model.RoomInterface
import de.uos.campusapp.component.ui.onboarding.model.Identity
import java.io.Serializable

@Type("users")
class StudipPerson : PersonInterface {
    @Id
    override var id: String = ""
    @JsonProperty("given-name")
    override var firstName: String = ""
    @JsonProperty("family-name")
    override var lastName: String = ""
    @JsonProperty("username")
    override var username: String = ""
    @JsonProperty("name-prefix")
    override var title: String = ""
    @JsonProperty("formatted-name")
    override var fullName: String = ""

    @JsonIgnore
    override var phoneNumbers: List<String>? = null
        get() = institutes?.mapNotNull { (it as? StudipInstitute)?.phone }
    @JsonIgnore
    override var mobilephone: String = ""
    @JsonProperty("email")
    override var email: String = ""
    @JsonIgnore
    override var fax: String = ""
    @JsonProperty("homepage")
    override var homepage: String = ""

    @JsonIgnore
    override var institutes: List<InstituteInterface>? = null // needs to be fetched from a separate call

    @JsonIgnore
    override var consultationHours: String = ""

    @JsonIgnore
    override var rooms: List<RoomInterface>? = null
        get() = institutes?.mapNotNull { (it as? StudipInstitute)?.location?.let { l -> StudipRoom(l) } }

    @JsonIgnore
    override var imageUrl: String = ""
        get() = meta?.avatar?.get("normal") ?: ""

    @JsonIgnore
    override var additionalInfo: String = ""

    @Meta
    var meta: PersonMeta? = null

    fun toIdentity(): Identity {
        return Identity(id, username, fullName, email, imageUrl)
    }
}

class PersonMeta : Serializable {
    @JsonProperty("avatar")
    var avatar: Map<String, String>? = null

    companion object {
        private const val serialVersionUID = 97819484892745790L
    }
}
