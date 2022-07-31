package de.tum.`in`.tumcampusapp.component.ui.cafeteria.model

/**
 * CafeteriaMenuPrice
 *
 * @param role price category, e.g. Studierende
 * @param amount price, e.g. 2.7
 */
open class CafeteriaMenuPrice(
    open var role: CafeteriaRole,
    open var amount: Double
)