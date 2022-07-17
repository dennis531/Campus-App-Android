package de.tum.`in`.tumcampusapp.component.ui.openinghours.api

import de.tum.`in`.tumcampusapp.component.ui.openinghours.model.Location

interface OpeningHoursApi {
    fun getOpeningHours(): List<Location>
}