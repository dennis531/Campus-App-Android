package de.uos.campusapp.component.ui.transportation.model

import org.joda.time.DateTime
import org.joda.time.Minutes

/**
 * Represents a departure
 *
 * @property direction departure direction, e.g. "Hauptbahnhof"
 * @property symbol Departure symbol, e.g. SimpleSymbol("S8")
 * @property means means of transport, e.g. "Regionalbus"
 * @property departureTime departure time
 */
abstract class AbstractDeparture {
    abstract val direction: String
    abstract val symbol: Symbol
    abstract val means: String
    abstract val departureTime: DateTime

    /**
     * Calculates the countDown with the real departure time and the current time
     *
     * @return The calculated countDown in minutes
     */
    val calculatedCountDown: Int
        get() = Minutes.minutesBetween(DateTime.now(), departureTime).minutes
}
