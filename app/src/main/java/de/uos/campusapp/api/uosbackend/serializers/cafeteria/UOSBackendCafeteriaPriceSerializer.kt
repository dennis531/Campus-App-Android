package de.uos.campusapp.api.uosbackend.serializers.cafeteria

import com.google.gson.*
import de.uos.campusapp.api.uosbackend.model.cafeteria.UOSBackendMenuPrice
import de.uos.campusapp.component.ui.cafeteria.model.CafeteriaMenuPriceInterface
import java.lang.reflect.Type

/**
 * Deserializes [CafeteriaMenuPriceInterface] as [UOSBackendMenuPrice]
 */
class UOSBackendCafeteriaPriceSerializer : JsonDeserializer<CafeteriaMenuPriceInterface> {
    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): CafeteriaMenuPriceInterface {
        return Gson().fromJson(json, UOSBackendMenuPrice::class.java)
    }
}
