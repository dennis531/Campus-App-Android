package de.tum.`in`.tumcampusapp.models.cafeteria

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity
data class FavoriteDish(@PrimaryKey(autoGenerate = true)
                        var id: Int = 0,
                        var cafeteriaId: Int = -1,
                        var dishName: String = "",
                        var date: String = "",
                        var tag: String = "") {
    companion object {
        fun create(cafeteriaId: Int, dishName: String, date: String, tag: String) = FavoriteDish(
                cafeteriaId = cafeteriaId,
                dishName = dishName,
                date = date,
                tag = tag)
    }
}