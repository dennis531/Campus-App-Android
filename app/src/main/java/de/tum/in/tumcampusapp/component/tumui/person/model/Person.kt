package de.tum.`in`.tumcampusapp.component.tumui.person.model

import de.tum.`in`.tumcampusapp.component.other.general.model.Recent

class Person : PersonInterface {
    override var id: String = ""
    override var firstName: String = ""
    override var lastName: String = ""
    override var username: String = ""
    override var title: String = ""
    override var fullName: String = ""
    override var gender: String = ""
    override var phoneNumbers: List<String>? = null
    override var mobilephone: String = ""
    override var email: String = ""
    override var fax: String = ""
    override var homepage: String = ""
    override var institutes: List<InstituteInterface>? = null
    override var consultationHours: String = ""
    override var rooms: List<RoomInterface>? = null
    override var address: String = ""
    override var imageUrl: String = ""
    override var additionalInfo: String = ""

    companion object {
        @JvmStatic fun fromRecent(r: Recent): PersonInterface {
            val split = r.name.split("\\$".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val p = Person()
            p.id = split[0]
            p.fullName = split[1]
            return p
        }
    }
}