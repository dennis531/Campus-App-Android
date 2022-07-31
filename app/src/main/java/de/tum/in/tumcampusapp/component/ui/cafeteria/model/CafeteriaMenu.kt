package de.tum.`in`.tumcampusapp.component.ui.cafeteria.model

import android.content.Context
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import androidx.room.RoomWarnings
import de.tum.`in`.tumcampusapp.utils.Const
import de.tum.`in`.tumcampusapp.utils.Utils
import org.joda.time.DateTime

/**
 * CafeteriaMenu
 *
 * @param id CafeteriaMenu Id (empty for addendum)
 * @param cafeteriaId Cafeteria ID
 * @param date Menu date
 * @param type Menu type, e.g. Tagesgericht 1, Vegetarisch, Bio
 * @param typeNr Type ID
 * @param name Menu name
 * @param prices Menu prices (optional)
 */
@Entity
@SuppressWarnings(RoomWarnings.DEFAULT_CONSTRUCTOR)
open class CafeteriaMenu(
    @PrimaryKey
    open var id: String,
    open var cafeteriaId: String,
    open var name: String,
    open var date: DateTime,
    open var type: String,
    @Ignore
    open var prices: List<CafeteriaMenuPrice>? = null
) {
    constructor(
        id: String,
        cafeteriaId: String,
        name: String,
        date: DateTime,
        type: String,
    ) : this(id, cafeteriaId, name, date, type, null)

    val tag: String
        get() = "${name}__$cafeteriaId"

    val notificationTitle: String
        get() = type

    fun getPriceText(context: Context): String {
        val selectedRoleId: Int = Utils.getSettingInt(context, Const.ROLE, CafeteriaRole.STUDENT.id)
        val price = prices?.find { it.role.id == selectedRoleId } ?: return ""

        return price.amount.run { "%.2f €".format(this) }
    }

    fun getCafeteriaMenuPriceItems(): List<CafeteriaMenuPriceItem> {
        return prices?.map { CafeteriaMenuPriceItem(id, it.role.id, it.amount) } ?: emptyList()
    }
}
