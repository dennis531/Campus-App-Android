package de.uos.campusapp.component.ui.person.api

import de.uos.campusapp.api.generic.BaseAPI
import de.uos.campusapp.component.ui.person.model.PersonInterface

/**
 * Api interface for the person component
 */
interface PersonAPI : BaseAPI {

    /**
     * Searches a person in the external system
     *
     * @param query Person search string
     * @return List of found persons
     */
    fun searchPerson(query: String): List<PersonInterface>

    /**
     * Gets details of a person
     *
     * @param id Person id
     * @return Person information
     */
    fun getPersonDetails(id: String): PersonInterface
}