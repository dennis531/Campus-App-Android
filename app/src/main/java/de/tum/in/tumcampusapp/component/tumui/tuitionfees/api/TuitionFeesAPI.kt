package de.tum.`in`.tumcampusapp.component.tumui.tuitionfees.api

import de.tum.`in`.tumcampusapp.api.generic.BaseAPI
import de.tum.`in`.tumcampusapp.component.tumui.tuitionfees.model.AbstractTuition

interface TuitionFeesAPI: BaseAPI {
    /**
     * Fetches the tuition information with the outstanding amount of the user
     */
    fun getTuitionFeesStatus(): AbstractTuition?
}