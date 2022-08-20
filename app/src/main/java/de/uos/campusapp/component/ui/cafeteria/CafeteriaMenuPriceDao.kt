package de.uos.campusapp.component.ui.cafeteria

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import de.uos.campusapp.component.ui.cafeteria.model.CafeteriaMenuPriceItem

@Dao
interface CafeteriaMenuPriceDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(cafeteriaMenuPriceItem: List<CafeteriaMenuPriceItem>)

    @Query("SELECT * FROM cafeteriaMenuPrice WHERE menuId=:menuId")
    fun getMenuPrices(menuId: String): List<CafeteriaMenuPriceItem>

    @Query("SELECT DISTINCT role FROM cafeteriaMenuPrice GROUP BY role ORDER BY role")
    fun getPriceRoles(): List<Int>

    @Query("DELETE FROM cafeteriaMenuPrice")
    fun removeCache()
}