package de.uos.campusapp.component.ui.cafeteria.model

/**
 * Represents a cafeteria menu price
 *
 * @param role price category, e.g. CafeteriaRole.STUDENT
 * @param amount price, e.g. 2.7
 */
interface CafeteriaMenuPriceInterface {
    var role: CafeteriaRole
    var amount: Double
}
