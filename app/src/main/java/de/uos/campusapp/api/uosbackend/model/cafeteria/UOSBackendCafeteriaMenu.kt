package de.uos.campusapp.api.uosbackend.model.cafeteria

import com.google.gson.annotations.SerializedName
import de.uos.campusapp.component.ui.cafeteria.model.AbstractCafeteriaMenu
import de.uos.campusapp.component.ui.cafeteria.model.CafeteriaMenuPriceInterface
import org.joda.time.DateTime

class UOSBackendCafeteriaMenu(
    @SerializedName("id")
    override var id: String,
    @SerializedName("cafeteria")
    override var cafeteriaId: String,
    @SerializedName("name")
    override var name: String,
    @SerializedName("date")
    override var date: DateTime,
    @SerializedName("type")
    override var type: String,
    @SerializedName("prices")
    override var prices: List<CafeteriaMenuPriceInterface>? = null
) : AbstractCafeteriaMenu()
