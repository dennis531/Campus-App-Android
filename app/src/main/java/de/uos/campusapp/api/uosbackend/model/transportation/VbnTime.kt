package de.uos.campusapp.api.uosbackend.model.transportation

import com.google.gson.annotations.SerializedName
import org.joda.time.DateTime

data class VbnTime(
    @SerializedName("headsign")
    val headsign: String = "",
    @SerializedName("scheduledDeparture")
    val scheduledDeparture: Int = 0,
    @SerializedName("serviceDay")
    val serviceDay: Long = 0
) {
    fun getDatetime(): DateTime {
        return DateTime((serviceDay + scheduledDeparture) * 1000)
    }
}
