package de.uos.campusapp.api.uosbackend.model.transportation

import com.google.gson.annotations.SerializedName
import de.uos.campusapp.component.ui.transportation.model.AbstractDeparture
import de.uos.campusapp.component.ui.transportation.model.Departure
import de.uos.campusapp.component.ui.transportation.model.SimpleSymbol

data class VbnResponse(
    @SerializedName("pattern")
    val pattern: VbnPattern = VbnPattern(),
    @SerializedName("times")
    val times: List<VbnTime> = emptyList()
) {
    fun toDepartureList(): List<AbstractDeparture> {
        return times.map {
            Departure(
                it.headsign,
                SimpleSymbol(pattern.desc.substringBefore(" ") ?: ""),
                "",
                it.getDatetime()
            )
        }
    }
}
