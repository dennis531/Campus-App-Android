package de.uos.campusapp.component.ui.grades

import android.content.Context
import de.uos.campusapp.component.notifications.NotificationScheduler
import de.uos.campusapp.component.ui.grades.api.GradesAPI
import de.uos.campusapp.utils.Component
import de.uos.campusapp.utils.ConfigUtils
import de.uos.campusapp.utils.Utils
import org.jetbrains.anko.doAsync
import javax.inject.Inject

class GradesBackgroundUpdater @Inject constructor(
    private val context: Context,
    private val notificationScheduler: NotificationScheduler,
    private val gradesStore: GradesStore
) {

    private val apiClient: GradesAPI by lazy {
        ConfigUtils.getApiClient(context, Component.GRADES) as GradesAPI
    }

    fun fetchGradesAndNotifyIfNecessary() {
        if (ConfigUtils.getAuthManager(context, Component.GRADES).hasAccess()) {
            doAsync {
                fetchGrades()
            }
        }
    }

    private fun fetchGrades() {
        try {
            val exams = apiClient.getGrades()
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
