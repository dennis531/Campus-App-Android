package de.uos.campusapp.component.ui.cafeteria.repository

import de.uos.campusapp.component.ui.cafeteria.controller.CafeteriaManager
import de.uos.campusapp.component.ui.cafeteria.model.*
import de.uos.campusapp.component.ui.cafeteria.model.database.CafeteriaItem
import de.uos.campusapp.component.ui.cafeteria.model.database.CafeteriaMenuItem
import de.uos.campusapp.component.ui.cafeteria.model.database.CafeteriaMenuPriceItem
import de.uos.campusapp.database.CaDb
import de.uos.campusapp.utils.sync.model.Sync
import io.reactivex.Flowable
import org.joda.time.DateTime
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import javax.inject.Inject

class CafeteriaLocalRepository @Inject constructor(
    private val database: CaDb
) {

    private val executor: Executor = Executors.newSingleThreadExecutor()

    fun getCafeteriaWithMenus(cafeteriaId: String): CafeteriaWithMenus {
        return CafeteriaWithMenus(cafeteriaId).apply {
            name = getCafeteriaNameFromId(id)
            menuDates = getAllMenuDates()
            menus = getCafeteriaMenus(id, nextMenuDate)
        }
    }

    private fun getCafeteriaNameFromId(id: String): String? = getCafeteria(id)?.name

    // Menu methods //

    fun getCafeteriaMenus(id: String, date: DateTime): List<CafeteriaMenuItem> {
        return database.cafeteriaMenuDao().getMenusWithPrices(id, date).map { it.toCafeteriaMenuItem() }
    }

    fun getAllMenuDates(): List<DateTime> = database.cafeteriaMenuDao().allDates

    // Menu price methods //

    fun getMenuPrices(id: String): List<CafeteriaMenuPriceItem> = database.cafeteriaMenuPriceDao().getMenuPrices(id)

    fun getRoles(): List<Int> = database.cafeteriaMenuPriceDao().getPriceRoles()

    fun addMenuPrices(menuPriceItems: List<CafeteriaMenuPriceItem>) = executor.execute {
        database.cafeteriaMenuPriceDao().insert(menuPriceItems)
    }

    // Canteen methods //

    fun getAllCafeterias(): Flowable<List<CafeteriaItem>> = database.cafeteriaDao().all

    fun getCafeteria(id: String): CafeteriaItem? = database.cafeteriaDao().getById(id)

    fun addCafeterias(cafeterias: List<AbstractCafeteria>) = executor.execute {
        database.cafeteriaDao().insert(cafeterias.map { it.toCafeteriaItem() })
    }

    // Sync methods //

    fun getLastSync() = database.syncDao().getSyncSince(CafeteriaManager::class.java.name, TIME_TO_SYNC)

    fun updateLastSync() = database.syncDao().insert(Sync(CafeteriaManager::class.java.name, DateTime.now()))

    fun clear() = database.cafeteriaDao().removeCache()

    companion object {
        private const val TIME_TO_SYNC = 604800 // 1 week
    }
}
