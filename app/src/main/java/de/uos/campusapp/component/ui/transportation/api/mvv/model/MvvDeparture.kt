package de.uos.campusapp.component.ui.transportation.api.mvv.model

import de.uos.campusapp.component.ui.transportation.model.AbstractDeparture
import de.uos.campusapp.component.ui.transportation.model.Departure
import org.joda.time.DateTime

data class MvvDeparture(
    val servingLine: MvvServingLine = MvvServingLine(),
    val dateTime: DateTime = DateTime(),
    val countdown: Int = 0
) {
    fun toDeparture(): AbstractDeparture {
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
