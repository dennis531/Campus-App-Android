package de.uos.campusapp.api.studip.model.person

import com.fasterxml.jackson.annotation.JsonIgnore
import com.github.jasminb.jsonapi.annotations.Id
import com.github.jasminb.jsonapi.annotations.Relationship
import com.github.jasminb.jsonapi.annotations.Type
import de.uos.campusapp.component.ui.person.model.InstituteInterface
import java.io.Serializable

@Type("institute-memberships")
class StudipInstitute : InstituteInterface {
    @Id
    var id: String = ""
    var location: String = ""
    var phone: String = ""

    @JsonIgnore
    override var name: String = ""
        get() = institute?.name ?: ""

    @Relationship("institute")
    var institute: InstituteRelationship? = null
}

@Type("institutes")
class InstituteRelationship : Serializable {
    @Id
    var id: String = ""

    var name: String = ""

    companion object {
        private const val serialVersionUID = 1120604090486967901L
    }
}
