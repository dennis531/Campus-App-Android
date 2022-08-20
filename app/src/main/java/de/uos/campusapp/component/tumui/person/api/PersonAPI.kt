package de.uos.campusapp.component.tumui.person.api

import de.uos.campusapp.api.generic.BaseAPI
import de.uos.campusapp.component.tumui.person.model.PersonInterface

interface PersonAPI: BaseAPI {
    fun searchPerson(query: String): List<PersonInterface>
    fun getPersonDetails(id: String): PersonInterface
}