package de.tum.`in`.tumcampusapp.component.ui.transportation.api.mvv

import de.tum.`in`.tumcampusapp.component.ui.transportation.model.Departure
import org.joda.time.DateTime

data class MvvDeparture(
    val servingLine: MvvServingLine = MvvServingLine(),
    val dateTime: DateTime = DateTime(),
    val countdown: Int = 0
) {
    fun toDeparture(): Departure {
        return Departure(
            servingLine.direction
                .replace(",", ", ")
                .replace("\\s+".toRegex(), " "),
            MVVSymbol(servingLine.symbol),
            servingLine.name,
            dateTime
        )
    }
}
