package de.uos.campusapp.component.ui.cafeteria.api.munich.model

import com.google.gson.annotations.SerializedName
import de.uos.campusapp.component.ui.cafeteria.model.CafeteriaMenu
import de.uos.campusapp.component.ui.cafeteria.model.CafeteriaMenuPrice
import org.joda.time.DateTime

class MunichCafeteriaMenu(
    @SerializedName("id")
    var id: String? = null,
    @SerializedName("mensa_id")
    var cafeteriaId: String,
    @SerializedName("date")
    var date: DateTime,
    @SerializedName("type_long")
    var type: String,
    @SerializedName("name")
    var name: String,
) {
    val prices: List<CafeteriaMenuPrice>
        get() = MunichCafeteriaPrices.getMenuPrices(type)

    fun toCafeteriaMenu(): CafeteriaMenu {
        // id is probably not set
        val id: String = id ?: "${cafeteriaId}_${date}_$name"

        return CafeteriaMenu(id, cafeteriaId, name, date, type, prices)
    }
}