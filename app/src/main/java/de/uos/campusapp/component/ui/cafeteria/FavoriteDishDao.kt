package de.uos.campusapp.component.ui.cafeteria

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

import de.uos.campusapp.component.ui.cafeteria.model.database.CafeteriaMenuItem
import de.uos.campusapp.component.ui.cafeteria.model.database.FavoriteDish

@Dao
interface FavoriteDishDao {

    @Query("SELECT * FROM favoriteDish WHERE tag = :tag")
    fun checkIfFavoriteDish(tag: String): List<FavoriteDish>

    @Insert
    fun insertFavouriteDish(favoriteDish: FavoriteDish)

    @Query("DELETE FROM favoriteDish WHERE cafeteriaId = :cafeteriaId AND dishName = :dishName")
    fun deleteFavoriteDish(cafeteriaId: String, dishName: String)

    @Query("SELECT cafeteriaMenu.* FROM favoriteDish " +
            "INNER JOIN cafeteriaMenu ON cafeteriaMenu.cafeteriaId = favoriteDish.cafeteriaId " +
            "AND favoriteDish.dishName = cafeteriaMenu.name WHERE cafeteriaMenu.date = :dayMonthYear")
    fun getFavouritedCafeteriaMenuOnDate(dayMonthYear: String): List<CafeteriaMenuItem>

    @Query("DELETE FROM favoriteDish")
    fun removeCache()
}
