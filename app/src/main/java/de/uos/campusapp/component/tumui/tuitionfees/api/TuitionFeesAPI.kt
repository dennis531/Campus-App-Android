package de.uos.campusapp.component.tumui.tuitionfees.api

import de.uos.campusapp.api.generic.BaseAPI
import de.uos.campusapp.component.tumui.tuitionfees.model.AbstractTuition

interface TuitionFeesAPI: BaseAPI {
    /**
     * Fetches the tuition information with the outstanding amount of the user
     */
    fun getTuitionFeesStatus(): AbstractTuition?
}