package de.uos.campusapp.component.ui.transportation.api.vbn.model

import com.google.gson.annotations.SerializedName

data class VbnPattern(
    @SerializedName("id")
    val id: String = "",
    @SerializedName("desc")
    val desc: String = ""
)
