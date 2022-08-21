package de.uos.campusapp.component.ui.cafeteria.model

import de.uos.campusapp.component.ui.cafeteria.model.database.CafeteriaMenuItem
import org.joda.time.DateTime

/**
 * Represents a cafeteria menu
 *
 * @property id
 * @property cafeteriaId Related cafeteria id
 * @property name Menu name
 * @property date Menu date
 * @property type Menu type, e.g. "Bio"
 * @property prices List of prices
 */
abstract class AbstractCafeteriaMenu {
    abstract var id: String
    abstract var cafeteriaId: String
    abstract var name: String
    abstract var date: DateTime
    abstract var type: String
    abstract var prices: List<CafeteriaMenuPriceInterface>?

    fun toCafeteriaMenuItem(): CafeteriaMenuItem {
        return CafeteriaMenuItem(id, cafeteriaId, name, date, type, prices)
    }
}