package de.uos.campusapp.component.ui.openinghours.api

import de.uos.campusapp.component.ui.openinghours.model.AbstractLocation

interface OpeningHoursAPI {
    fun getOpeningHours(): List<AbstractLocation>
}