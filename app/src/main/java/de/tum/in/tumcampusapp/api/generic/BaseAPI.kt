package de.tum.`in`.tumcampusapp.api.generic

import de.tum.`in`.tumcampusapp.component.tumui.person.model.PersonInterface

interface BaseAPI {
    fun getIdentity(): PersonInterface
}