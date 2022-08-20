package de.uos.campusapp.component.ui.cafeteria.api.munich.model

import com.google.gson.annotations.SerializedName

data class MunichCafeteriaResponse(
    @SerializedName("mensa_menu")
    val menus: List<MunichCafeteriaMenu>,
    @SerializedName("mensa_beilagen")
    val sideDishes: List<MunichCafeteriaMenu>
)