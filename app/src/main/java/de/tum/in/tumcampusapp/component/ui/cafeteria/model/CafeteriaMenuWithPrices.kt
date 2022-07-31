package de.tum.`in`.tumcampusapp.component.ui.cafeteria.model

import androidx.room.Embedded
import androidx.room.Relation

data class CafeteriaMenuWithPrices(
    @Embedded val menu: CafeteriaMenu,
    @Relation(
        parentColumn = "id",
        entityColumn = "menuId"
    )
    val prices: List<CafeteriaMenuPriceItem>?
) {
    fun toCafeteriaMenu(): CafeteriaMenu {
        return menu.apply {
            prices = this@CafeteriaMenuWithPrices.prices?.map { CafeteriaMenuPrice(CafeteriaRole.fromId(it.role), it.amount) }
        }
    }
}
