package de.uos.campusapp.api.uosbackend.model.transportation

import com.google.gson.annotations.SerializedName

data class VbnPattern(
    @SerializedName("id")
    val id: String = "",
    @SerializedName("desc")
    val desc: String = ""
)
