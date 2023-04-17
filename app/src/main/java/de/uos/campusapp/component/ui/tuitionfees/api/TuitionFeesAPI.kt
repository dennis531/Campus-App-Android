package de.uos.campusapp.component.ui.tuitionfees.api

import de.uos.campusapp.api.generic.BaseAPI
import de.uos.campusapp.component.ui.tuitionfees.model.AbstractTuition

/**
 * Api interface for the tuition fees component
 */
interface TuitionFeesAPI : BaseAPI {

    /**
     * Fetches tuition information from external system with the outstanding amount of the user
     *
     * @return Tuition information
     */
    fun getTuitionFeesStatus(): AbstractTuition?
}