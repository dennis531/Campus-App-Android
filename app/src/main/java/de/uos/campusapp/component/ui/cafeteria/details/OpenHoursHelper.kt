package de.uos.campusapp.component.ui.cafeteria.details

import android.content.Context
import de.uos.campusapp.R
import de.uos.campusapp.component.ui.cafeteria.CafeteriaDao
import de.uos.campusapp.component.ui.openinghours.LocationDao
import de.uos.campusapp.database.CaDb
import org.joda.time.DateTime

class OpenHoursHelper(private val context: Context) {
    private val dao: CafeteriaDao = CaDb.getInstance(context).cafeteriaDao()

    /**
     * Gets the opening hours of a cafeteria.
     *
     * @param id Cafeteria ID
     * @return Readable opening string
     */
    fun getHoursByIdAsString(id: String): String {
        val cafeteria = dao.getById(id) ?: return ""

        // Simple solution, as the format of the opening hours is not known.
        return context.getString(R.string.opening_hours) + ": " + cafeteria.openingHours
    }
}