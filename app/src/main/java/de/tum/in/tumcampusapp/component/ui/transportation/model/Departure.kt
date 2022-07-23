package de.tum.`in`.tumcampusapp.component.ui.transportation.model

import org.joda.time.DateTime
import org.joda.time.Minutes

open class Departure(
    open val direction: String, // e.g. "Hauptbahnhof"
    open val symbol: Symbol, // e.g. SimpleSymbol("S8")
    open val means: String, // e.g. "Regionalbus"
    open val departureTime: DateTime
) {

    /**
     * Calculates the countDown with the real departure time and the current time
     *
     * @return The calculated countDown in minutes
     */
    val calculatedCountDown: Int
        get() = Minutes.minutesBetween(DateTime.now(), departureTime).minutes
}
