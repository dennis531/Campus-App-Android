package de.uos.campusapp.component.tumui.person.model

import java.io.Serializable

interface PersonInterface : Serializable {
    val id: String

    var firstName: String
    var lastName: String
    var username: String
    var title: String
    var fullName: String

    var gender: String

    var phoneNumbers: List<String>? // Work phone numbers
    var mobilephone: String
    var email: String
    var fax: String
    var homepage: String

    var institutes: List<InstituteInterface>?

    var consultationHours: String

    var rooms: List<RoomInterface>?
    var address: String

    var imageUrl: String // profile image url

    var additionalInfo: String

    companion object {
        private const val serialVersionUID = 6906210675497296501L

//        @JvmStatic fun fromRecent(r: Recent): PersonInterface {
//            val split = r.name.split("\\$".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
//            val p = Class.forName(r.clazz).newInstance() as PersonInterface
//            p.id = split[0]
//            p.fullName = split[1]
//            return p
//        }
    }
}