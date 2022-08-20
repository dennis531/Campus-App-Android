package de.uos.campusapp.component.ui.cafeteria.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.RoomWarnings

@Entity(tableName = "cafeteriaMenuPrice")
@SuppressWarnings(RoomWarnings.DEFAULT_CONSTRUCTOR)
data class CafeteriaMenuPriceItem(
    var menuId: String,
    var role: Int,
    var amount: Double
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}