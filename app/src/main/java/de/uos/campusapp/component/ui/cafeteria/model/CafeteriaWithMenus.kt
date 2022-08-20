package de.uos.campusapp.component.ui.cafeteria.model

import android.content.Context
import android.content.Intent
import de.uos.campusapp.component.ui.cafeteria.activity.CafeteriaActivity
import de.uos.campusapp.utils.Const
import org.joda.time.DateTime
import org.joda.time.LocalDate
import java.util.*

data class CafeteriaWithMenus(val id: String) {

    var name: String? = null
    var menus: List<CafeteriaMenu> = ArrayList()
    var menuDates: List<DateTime> = ArrayList()

    val nextMenuDate: DateTime
        get() {
            val now = DateTime.now()
            var nextDate = menuDates
                    .getOrElse(0) {
                        DateTime.now()
                    }

            if (nextDate.isToday() && now.hourOfDay >= 15 && menuDates.size > 1) {
                nextDate = menuDates[1]
            }

            return nextDate
        }

    fun getIntent(context: Context): Intent =
            Intent(context, CafeteriaActivity::class.java).apply {
                putExtra(Const.CAFETERIA_ID, id)
            }

    // We notify the user when the cafeteria typically opens
    val notificationTime: DateTime
        get() = nextMenuDate
                .withHourOfDay(11)
                .withMinuteOfHour(0)
                .withSecondOfMinute(0)
}

fun DateTime.isToday() = LocalDate.now() == LocalDate(this)
