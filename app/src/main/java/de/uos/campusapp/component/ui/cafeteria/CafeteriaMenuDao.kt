package de.uos.campusapp.component.ui.cafeteria

import androidx.room.*
import de.uos.campusapp.component.ui.cafeteria.model.database.CafeteriaMenuItem
import de.uos.campusapp.component.ui.cafeteria.model.CafeteriaMenuWithPrices
import io.reactivex.Flowable
import org.joda.time.DateTime

@Dao
interface CafeteriaMenuDao {

    @get:Query("SELECT DISTINCT date FROM cafeteriaMenu WHERE date >= date('now','localtime') ORDER BY date")
    val allDates: List<DateTime>

    @Query("DELETE FROM cafeteriaMenu")
    fun removeCache()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(cafeteriaMenus: List<CafeteriaMenuItem>)

    @Query("SELECT strftime('%d-%m-%Y', date) FROM cafeteriaMenu " +
            "WHERE date > date('now','localtime') AND cafeteriaId=:cafeteriaId AND name=:dishName " +
            "ORDER BY date ASC")
    fun getNextDatesForDish(cafeteriaId: String, dishName: String): Flowable<List<String>>

    @Query("SELECT * FROM cafeteriaMenu " +
            "WHERE cafeteriaId = :cafeteriaId AND date = :date ORDER BY type ASC")
    fun getCafeteriaMenus(cafeteriaId: String, date: DateTime): List<CafeteriaMenuItem>

    @Transaction
    @Query("SELECT * FROM cafeteriaMenu " +
            "WHERE cafeteriaId = :cafeteriaId AND date = :date ORDER BY type ASC")
    fun getMenusWithPrices(cafeteriaId: String, date: DateTime): List<CafeteriaMenuWithPrices>
}
