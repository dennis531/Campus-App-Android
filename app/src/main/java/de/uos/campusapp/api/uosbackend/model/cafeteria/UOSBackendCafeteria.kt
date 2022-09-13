package de.uos.campusapp.api.uosbackend.model.cafeteria

import com.google.gson.annotations.SerializedName
import de.uos.campusapp.component.ui.cafeteria.model.AbstractCafeteria

data class UOSBackendCafeteria (
    @SerializedName("id")
    override val id: String,
    @SerializedName("name")
    override val name: String,
    @SerializedName("address")
    override val address: String? = null,
    @SerializedName("latitude")
    override val latitude: Double? = null,
    @SerializedName("longitude")
    override val longitude: Double? = null
) : AbstractCafeteria()