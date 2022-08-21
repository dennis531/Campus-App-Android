package de.uos.campusapp.component.ui.cafeteria.model

import androidx.room.Embedded
import androidx.room.Relation
import de.uos.campusapp.component.ui.cafeteria.model.database.CafeteriaMenuItem
import de.uos.campusapp.component.ui.cafeteria.model.database.CafeteriaMenuPriceItem

data class CafeteriaMenuWithPrices(
    @Embedded val menu: CafeteriaMenuItem,
    @Relation(
        parentColumn = "id",
        entityColumn = "menuId"
    )
    val prices: List<CafeteriaMenuPriceItem>?
) {
    fun toCafeteriaMenuItem(): CafeteriaMenuItem {
        return menu.apply {
            prices = this@CafeteriaMenuWithPrices.prices?.map { CafeteriaMenuPrice(CafeteriaRole.fromId(it.role), it.amount) }
        }
    }
}
