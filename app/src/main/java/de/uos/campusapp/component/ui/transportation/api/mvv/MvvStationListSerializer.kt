package de.uos.campusapp.component.ui.transportation.api.mvv

import com.google.gson.*
import de.uos.campusapp.component.ui.transportation.api.mvv.model.MvvStationList
import de.uos.campusapp.component.ui.transportation.model.AbstractStation
import de.uos.campusapp.component.ui.transportation.model.Station
import java.lang.reflect.Type

/**
 * Parses the weird MVV XML_STOPFINDER_REQUEST response
 */
class MvvStationListSerializer : JsonDeserializer<MvvStationList> {
    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): MvvStationList {
        if (json !is JsonObject) {
            throw JsonParseException("Invalid MvvStationList: $json")
        }

        val points = json.getAsJsonObject("stopFinder").get("points")

        // This is where the fun starts: points can either be an Object, an Array or null
        // empty result
        if (points is JsonNull) {
            return MvvStationList(emptyList())
        }

        // singleton result, i.e. exact match
        if (points is JsonObject) {
            return MvvStationList(listOf(getStation(points.get("point") as JsonObject)))
        }

        if (points is JsonArray) {
            val resultList = points.map {
                getStation(it as JsonObject)
            }
            return MvvStationList(resultList)
        }

        throw JsonParseException("Unknown MvvStationList: $json")
    }

    private fun getStation(json: JsonObject): AbstractStation {
        return Station(
            json.getAsJsonObject("ref").get("id").asString,
            json.get("name").asString,
            json.get("quality")?.asInt ?: 0
        )
    }
}