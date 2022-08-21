package de.uos.campusapp.component.ui.cafeteria.model

import org.joda.time.DateTime

/**
 * Simple implementation of [AbstractCafeteriaMenu]
 */
data class CafeteriaMenu(
    override var id: String,
    override var cafeteriaId: String,
    override var name: String,
    override var date: DateTime,
    override var type: String,
    override var prices: List<CafeteriaMenuPriceInterface>?
) : AbstractCafeteriaMenu() {
}