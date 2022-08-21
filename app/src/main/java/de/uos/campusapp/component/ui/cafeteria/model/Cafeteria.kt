package de.uos.campusapp.component.ui.cafeteria.model

/**
 * Simple implementation of [AbstractCafeteria]
 */
data class Cafeteria(
    override val id: String,
    override val name: String,
    override val address: String? = null,
    override val latitude: Double? = null,
    override val longitude: Double? = null
) : AbstractCafeteria()