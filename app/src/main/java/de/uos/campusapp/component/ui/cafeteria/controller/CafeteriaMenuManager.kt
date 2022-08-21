package de.uos.campusapp.component.ui.cafeteria.controller

import android.annotation.SuppressLint
import android.content.Context
import de.uos.campusapp.api.tumonline.CacheControl
import de.uos.campusapp.component.notifications.NotificationScheduler
import de.uos.campusapp.component.notifications.persistence.NotificationType
import de.uos.campusapp.component.ui.cafeteria.CafeteriaMenuDao
import de.uos.campusapp.component.ui.cafeteria.CafeteriaMenuPriceDao
import de.uos.campusapp.component.ui.cafeteria.CafeteriaNotificationSettings
import de.uos.campusapp.component.ui.cafeteria.FavoriteDishDao
import de.uos.campusapp.component.ui.cafeteria.model.AbstractCafeteriaMenu
import de.uos.campusapp.component.ui.cafeteria.model.database.CafeteriaMenuItem
import de.uos.campusapp.database.TcaDb
import de.uos.campusapp.utils.ConfigUtils
import de.uos.campusapp.utils.DateTimeUtils
import de.uos.campusapp.utils.Utils
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

    private fun onDownloadSuccess(response: List<AbstractCafeteriaMenu>) {
        val cafeteriaMenuItems = response.map { it.toCafeteriaMenuItem() }

        menuDao.removeCache()
        menuDao.insert(cafeteriaMenuItems)

        menuPriceDao.removeCache()
        cafeteriaMenuItems.forEach {
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
    fun getFavoriteDishesServed(queriedMensaId: String, date: DateTime): List<CafeteriaMenuItem> {
        val dateString = DateTimeUtils.getDateString(date)

        val upcomingServings = favoriteDishDao.getFavouritedCafeteriaMenuOnDate(dateString)
        return upcomingServings.filter {
            it.cafeteriaId == queriedMensaId
        }
    }
}