package de.uos.campusapp.component.tumui.person.model

import java.io.Serializable

/**
 * Represents a person
 *
 * @property id
 *
 * @property firstName First name
 * @property lastName Last name
 * @property username Username, e.g. RZID
 * @property title Title (optional)
 * @property fullName Complete name
 *
 * @property phoneNumbers Phone numbers (optional)
 * @property mobilephone Mobile number (optional)
 * @property email Email address (optional)
 * @property fax Fax address (optional)
 * @property homepage Homepage (optional)
 *
 * @property institutes Related institutes (optional)
 * @property consultationHours Consultation hours (optional)
 * @property rooms Rooms (optional)
 *
 * @property imageUrl Profile image url (optional)
 *
 * @property additionalInfo More information (optional)
 */
interface PersonInterface : Serializable {
    val id: String

    var firstName: String
    var lastName: String
    var username: String
    var title: String
    var fullName: String

    var phoneNumbers: List<String>? // Work phone numbers
    var mobilephone: String
    var email: String
    var fax: String
    var homepage: String

    var institutes: List<InstituteInterface>?
    var consultationHours: String
    var rooms: List<RoomInterface>?

    var imageUrl: String // profile image url

    var additionalInfo: String

    companion object {
        private const val serialVersionUID = 6906210675497296501L
    }
}