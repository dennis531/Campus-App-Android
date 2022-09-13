package de.uos.campusapp.component.ui.cafeteria.model

import de.uos.campusapp.component.ui.cafeteria.model.database.CafeteriaItem

/**
 * Represents a Cafeteria
 *
 * @property id Cafeteria ID, e.g. 412
 * @property name Name, e.g. MensaX
 * @property address Address, e.g. "Arcisstr. 17, München" (optional)
 * @property latitude Coordinates of the cafeteria (optional)
 * @property longitude Coordinates of the cafeteria (optional)
 */
abstract class AbstractCafeteria {
    abstract val id: String
    abstract val name: String
    abstract val address: String?
    abstract val latitude: Double?
    abstract val longitude: Double?

    fun toCafeteriaItem(): CafeteriaItem {
        return CafeteriaItem(id, name, address, latitude, longitude)
    }
}