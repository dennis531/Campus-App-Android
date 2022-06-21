package de.tum.`in`.tumcampusapp.component.tumui.person.api

import de.tum.`in`.tumcampusapp.api.generic.BaseAPI
import de.tum.`in`.tumcampusapp.component.tumui.person.model.PersonInterface

interface PersonAPI: BaseAPI {
    fun searchPerson(query: String): List<PersonInterface>
    fun getPersonDetails(id: String): PersonInterface
}