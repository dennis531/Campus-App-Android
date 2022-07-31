package de.tum.`in`.tumcampusapp.component.ui.cafeteria.controller

import android.annotation.SuppressLint
import android.content.Context
import de.tum.`in`.tumcampusapp.api.tumonline.CacheControl
import de.tum.`in`.tumcampusapp.component.notifications.NotificationScheduler
import de.tum.`in`.tumcampusapp.component.notifications.persistence.NotificationType
import de.tum.`in`.tumcampusapp.component.ui.cafeteria.CafeteriaMenuDao
import de.tum.`in`.tumcampusapp.component.ui.cafeteria.CafeteriaMenuPriceDao
import de.tum.`in`.tumcampusapp.component.ui.cafeteria.CafeteriaNotificationSettings
import de.tum.`in`.tumcampusapp.component.ui.cafeteria.FavoriteDishDao
import de.tum.`in`.tumcampusapp.component.ui.cafeteria.model.CafeteriaMenu
import de.tum.`in`.tumcampusapp.database.TcaDb
import de.tum.`in`.tumcampusapp.utils.ConfigUtils
import de.tum.`in`.tumcampusapp.utils.DateTimeUtils
import de.tum.`in`.tumcampusapp.utils.Utils
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.joda.time.DateTime
import javax.inject.Inject

/**
 * Cafeteria Menu Manager, handles database stuff, external imports
 */
class CafeteriaMenuManager

@Inject
constructor(private val context: Context) {
    private val menuDao: CafeteriaMenuDao
    private val menuPriceDao: CafeteriaMenuPriceDao
    private val favoriteDishDao: FavoriteDishDao

    init {
        val db = TcaDb.getInstance(context)
        menuDao = db.cafeteriaMenuDao()
        menuPriceDao = db.cafeteriaMenuPriceDao()
        favoriteDishDao = db.favoriteDishDao()
    }

    /**
     * Download cafeteria menus from external interface (JSON)
     *
     * @param cacheControl BYPASS_CACHE to force download over normal sync period, else false
     */
    @SuppressLint("CheckResult")
    fun downloadMenus(cacheControl: CacheControl) {
        // Responses from the cafeteria API are cached for one day. If the download is forced,
        // we add a "no-cache" header to the request.
        Single.fromCallable {ConfigUtils.getCafeteriaClient(context).getMenus()}
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ response ->
                onDownloadSuccess(response)
            }, {
                Utils.log(it)
            })
    }

    private fun onDownloadSuccess(response: List<CafeteriaMenu>) {
        menuDao.removeCache()
        menuDao.insert(response)

        menuPriceDao.removeCache()
        response.forEach {
            menuPriceDao.insert(it.getCafeteriaMenuPriceItems())
        }

        scheduleNotificationAlarms()
    }

    fun scheduleNotificationAlarms() {
        val menuDates = menuDao.allDates
        val settings = CafeteriaNotificationSettings(context)

        val notificationTimes = menuDates.mapNotNull {
            settings.retrieveLocalTime(it)
        }.map {
            it.toDateTimeToday()
        }.distinct()

        val scheduler = NotificationScheduler(context)
        scheduler.scheduleAlarms(NotificationType.CAFETERIA, notificationTimes)
    }

    /**
     * Returns all the favorite dishes that a particular mensa serves on the specified date.
     *
     * @param queriedMensaId The Cafeteria for which to return the favorite dishes served
     * @param date The date for which to return the favorite dishes served
     * @return the favourite dishes at the given date
     */
    fun getFavoriteDishesServed(queriedMensaId: String, date: DateTime): List<CafeteriaMenu> {
        val dateString = DateTimeUtils.getDateString(date)

        val upcomingServings = favoriteDishDao.getFavouritedCafeteriaMenuOnDate(dateString)
        return upcomingServings.filter {
            it.cafeteriaId == queriedMensaId
        }
    }
}