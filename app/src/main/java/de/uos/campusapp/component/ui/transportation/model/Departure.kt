package de.uos.campusapp.component.ui.transportation.model

import org.joda.time.DateTime

/**
 * Simple implementation of [AbstractDeparture]
 */
class Departure(
    override val direction: String,
    override val symbol: Symbol,
    override val means: String,
    override val departureTime: DateTime
) : AbstractDeparture()
