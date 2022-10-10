package de.uos.campusapp.component.ui.openinghours.api

import de.uos.campusapp.component.ui.openinghours.model.AbstractLocation

/**
 * Api interface for the openinghours component
 */
interface OpeningHoursAPI {

    /**
     * Gets information, e.g. opening hours, of institutions from external system
     *
     * @return List of locations
     */
    fun getOpeningHours(): List<AbstractLocation>
}