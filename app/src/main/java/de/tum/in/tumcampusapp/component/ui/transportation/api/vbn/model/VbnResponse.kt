package de.tum.`in`.tumcampusapp.component.ui.transportation.api.vbn.model

import com.google.gson.annotations.SerializedName
import de.tum.`in`.tumcampusapp.component.ui.transportation.model.Departure
import de.tum.`in`.tumcampusapp.component.ui.transportation.model.SimpleSymbol

data class VbnResponse(
    @SerializedName("pattern")
    val pattern: VbnPattern = VbnPattern(),
    @SerializedName("times")
    val times: List<VbnTime> = emptyList()
) {
    fun toDepartureList(): List<Departure> {
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
