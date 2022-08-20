package de.uos.campusapp.component.tumui.tuitionfees.model

import android.content.Context
import android.content.Intent
import de.uos.campusapp.R
import de.uos.campusapp.component.tumui.tuitionfees.TuitionFeesActivity
import de.uos.campusapp.utils.ConfigUtils
import de.uos.campusapp.utils.Const
import de.uos.campusapp.utils.Utils
import org.joda.time.DateTime
import java.text.ParseException
import java.util.*

/**
 * Abstract class holding tuition information.
 */
abstract class AbstractTuition {
    abstract val start: DateTime
    abstract val deadline: DateTime
    abstract val semester: String
    abstract val amount: Double

    open val hasStarted: Boolean
        get() = start.isBeforeNow

    open fun isPaid(context: Context): Boolean {
        // TUITION_PAID setting returns true if tuition is marked as paid by the user
        return amount == 0.0 || Utils.getSettingBool(context, Const.TUITION_PAID, false)
    }

    /**
     * If true the user can manually mark the tuition fee as paid
     */
    open fun canMarkedAsPaid(context: Context): Boolean = !ConfigUtils.shouldTuitionLoadedFromApi()

    open fun getAmountText(context: Context): String {
        // Set zero amount if tuition fee is marked as paid by the user
        val amount = if (isPaid(context)) 0.0 else amount

        return try {
            val amountText = String.format(Locale.getDefault(), "%.2f", amount)
            return "$amountText €"
        } catch (e: ParseException) {
            context.getString(R.string.not_available)
        }
    }

    fun getIntent(context: Context): Intent = Intent(context, TuitionFeesActivity::class.java)
}
