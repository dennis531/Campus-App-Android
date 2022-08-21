package de.uos.campusapp.component.ui.cafeteria.model

/**
 * Simple implementation of [CafeteriaMenuPriceInterface]
 */
data class CafeteriaMenuPrice(
    override var role: CafeteriaRole,
    override var amount: Double
) : CafeteriaMenuPriceInterface