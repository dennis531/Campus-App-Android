package de.tum.`in`.tumcampusapp.component.tumui.grades

import android.content.Context
import de.tum.`in`.tumcampusapp.api.generic.LMSClient
import de.tum.`in`.tumcampusapp.component.notifications.NotificationScheduler
import de.tum.`in`.tumcampusapp.component.tumui.grades.api.GradesAPI
import de.tum.`in`.tumcampusapp.utils.ConfigUtils
import de.tum.`in`.tumcampusapp.utils.Utils
import org.jetbrains.anko.doAsync
import javax.inject.Inject

class GradesBackgroundUpdater @Inject constructor(
    private val context: Context,
    private val apiClient: LMSClient,
    private val notificationScheduler: NotificationScheduler,
    private val gradesStore: GradesStore
) {

    fun fetchGradesAndNotifyIfNecessary() {
        if (ConfigUtils.getAuthManager(context).hasAccess()) {
            doAsync {
                fetchGrades()
            }
        }
    }

    private fun fetchGrades() {
        try {
            val exams = (apiClient as GradesAPI).getGrades()
            val newCourses = exams.map { it.course }
            val existingCourses = gradesStore.gradedCourses
            val diff = newCourses - existingCourses

            if (diff.size > NOTIFICATION_THRESHOLD) {
                // We assume that this is the first time the user's grades are fetched and stored. Since
                // this likely includes old grades, we don't display a notification.
                gradesStore.store(newCourses)
                return
            }

            if (diff.isNotEmpty()) {
                showGradesNotification(diff)
            }
        } catch (t: Throwable) {
            Utils.log(t)
        }
    }

    private fun showGradesNotification(newGrades: List<String>) {
        val provider = GradesNotificationProvider(context, newGrades)
        val notification = provider.buildNotification() ?: return
        notificationScheduler.schedule(notification)
    }

    companion object {
        private const val NOTIFICATION_THRESHOLD = 2
    }
}
