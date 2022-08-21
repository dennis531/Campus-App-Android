package de.uos.campusapp.utils

import android.location.Location
import de.uos.campusapp.component.ui.cafeteria.model.database.CafeteriaItem

object LocationHelper {

    fun calculateDistanceToCafeteria(cafeteria: CafeteriaItem, location: Location): Float? {
        if (cafeteria.latitude == null || cafeteria.longitude == null) {
            return null
        }

        val results = FloatArray(1)
        Location.distanceBetween(cafeteria.latitude!!, cafeteria.longitude!!,
                location.latitude, location.longitude, results)
        return results[0]
    }
}
