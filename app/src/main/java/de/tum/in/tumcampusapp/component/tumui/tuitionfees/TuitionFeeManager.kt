package de.tum.`in`.tumcampusapp.component.tumui.tuitionfees

import android.content.Context
import de.tum.`in`.tumcampusapp.api.tumonline.CacheControl
import de.tum.`in`.tumcampusapp.component.notifications.NotificationScheduler
import de.tum.`in`.tumcampusapp.component.notifications.ProvidesNotifications
import de.tum.`in`.tumcampusapp.component.notifications.persistence.NotificationType
import de.tum.`in`.tumcampusapp.component.tumui.tuitionfees.api.TuitionFeesAPI
import de.tum.`in`.tumcampusapp.component.tumui.tuitionfees.model.AbstractTuition
import de.tum.`in`.tumcampusapp.component.ui.overview.card.Card
import de.tum.`in`.tumcampusapp.component.ui.overview.card.ProvidesCard
import de.tum.`in`.tumcampusapp.utils.ConfigConst
import de.tum.`in`.tumcampusapp.utils.ConfigUtils
import de.tum.`in`.tumcampusapp.utils.Utils
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
            val tuition = if (ConfigUtils.shouldTuitionLoadedFromApi(context)) {
                (ConfigUtils.getLMSClient(context) as TuitionFeesAPI).getTuitionFeesStatus()
            } else {
                ConfigUtils.getConfig(ConfigConst.TUITIONFEES_TUITION, null)
            }

            if (tuition == null) {
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
