package de.uos.campusapp.component.ui.openinghours.api

import de.uos.campusapp.component.ui.openinghours.model.Location

interface OpeningHoursAPI {
    fun getOpeningHours(): List<Location>
}