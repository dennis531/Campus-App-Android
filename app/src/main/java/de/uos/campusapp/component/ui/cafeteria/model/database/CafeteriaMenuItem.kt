package de.uos.campusapp.component.ui.cafeteria.model.database

import android.content.Context
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import androidx.room.RoomWarnings
import de.uos.campusapp.component.ui.cafeteria.model.CafeteriaMenuPriceInterface
import de.uos.campusapp.component.ui.cafeteria.model.CafeteriaRole
import de.uos.campusapp.utils.Const
import de.uos.campusapp.utils.Utils
import org.joda.time.DateTime

/**
 * CafeteriaMenu
 *
 * @param id CafeteriaMenu Id (empty for addendum)
 * @param cafeteriaId Cafeteria ID
 * @param date Menu date
 * @param type Menu type, e.g. Tagesgericht 1, Vegetarisch, Bio
 * @param name Menu name
 * @param prices Menu prices (optional)
 */
@Entity(tableName = "cafeteriaMenu")
@SuppressWarnings(RoomWarnings.DEFAULT_CONSTRUCTOR)
class CafeteriaMenuItem(
    @PrimaryKey
    var id: String,
    var cafeteriaId: String,
    var name: String,
    var date: DateTime,
    var type: String,
    @Ignore
    var prices: List<CafeteriaMenuPriceInterface>? = null
) {
    constructor(
        id: String,
        cafeteriaId: String,
        name: String,
        date: DateTime,
        type: String
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
