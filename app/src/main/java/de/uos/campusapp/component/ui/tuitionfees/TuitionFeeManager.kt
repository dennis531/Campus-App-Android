package de.uos.campusapp.component.ui.tuitionfees

import android.content.Context
import de.uos.campusapp.api.tumonline.CacheControl
import de.uos.campusapp.component.notifications.NotificationScheduler
import de.uos.campusapp.component.notifications.ProvidesNotifications
import de.uos.campusapp.component.notifications.persistence.NotificationType
import de.uos.campusapp.component.ui.tuitionfees.api.TuitionFeesAPI
import de.uos.campusapp.component.ui.tuitionfees.model.AbstractTuition
import de.uos.campusapp.component.ui.overview.card.Card
import de.uos.campusapp.component.ui.overview.card.ProvidesCard
import de.uos.campusapp.utils.Component
import de.uos.campusapp.utils.ConfigConst
import de.uos.campusapp.utils.ConfigUtils
import de.uos.campusapp.utils.Utils
import java.util.*

/**
 * Tuition manager, handles tuition card
 */
class TuitionFeeManager(private val context: Context) : ProvidesCard, ProvidesNotifications {

    override fun getCards(cacheControl: CacheControl): List<Card> {
        val results = ArrayList<Card>()
        val tuition = loadTuition(cacheControl) ?: return results

        val card = TuitionFeesCard(context, tuition)
        card.getIfShowOnStart()?.let { results.add(it) }
        return results
    }

    override fun hasNotificationsEnabled(): Boolean {
        return Utils.getSettingBool(context, "card_tuition_fee_phone", true)
    }

    fun loadTuition(cacheControl: CacheControl): AbstractTuition? {
        try {
            // Indicates if tuition is loaded from api
            val tuition = if (ConfigUtils.shouldTuitionLoadedFromApi()) {
                (ConfigUtils.getApiClient(context, Component.TUITIONFEES) as TuitionFeesAPI).getTuitionFeesStatus()
            } else {
                ConfigUtils.getConfig(ConfigConst.TUITIONFEES_TUITION, null)
            }

            if (tuition == null || tuition.deadline.isBeforeNow) {
                return null
            }

            if (!tuition.isPaid(context) && hasNotificationsEnabled()) {
                scheduleNotificationAlarm(tuition)
            }

            return tuition
        } catch (t: Throwable) {
            Utils.log(t)
            return null
        }
    }

    private fun scheduleNotificationAlarm(tuition: AbstractTuition) {
        val notificationTime = TuitionNotificationScheduler.getNextNotificationTime(tuition)
        val scheduler = NotificationScheduler(context)
        scheduler.scheduleAlarm(NotificationType.TUITION_FEES, notificationTime)
    }
}
