package de.uos.campusapp.component.ui.cafeteria

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import de.uos.campusapp.component.ui.cafeteria.model.database.CafeteriaItem
import io.reactivex.Flowable

@Dao
interface CafeteriaDao {
    @get:Query("SELECT * FROM cafeteria")
    val all: Flowable<List<CafeteriaItem>>

    @Query("SELECT * FROM cafeteria WHERE id = :id")
    fun getById(id: String): CafeteriaItem?

    @Query("DELETE FROM cafeteria")
    fun removeCache()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(cafeterias: List<CafeteriaItem>)

    @Query("SELECT name FROM cafeteria WHERE id = :id")
    fun getMensaNameFromId(id: String): String
}
