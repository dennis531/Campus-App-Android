package de.uos.campusapp.component.ui.overview

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.NotificationCompat
import de.uos.campusapp.R
import de.uos.campusapp.component.notifications.NotificationScheduler
import de.uos.campusapp.component.notifications.model.FutureNotification
import de.uos.campusapp.component.notifications.persistence.NotificationType
import de.uos.campusapp.component.ui.overview.card.Card
import de.uos.campusapp.component.ui.overview.card.CardViewHolder
import de.uos.campusapp.utils.Component
import de.uos.campusapp.utils.Const
import de.uos.campusapp.utils.Utils
import org.joda.time.DateTime

class SurveyCard(context: Context) : Card(CardManager.CARD_SURVEY, context, Component.OVERVIEW, "card_survey") {

    override fun discard(editor: SharedPreferences.Editor) {
        editor.putBoolean(CardManager.SHOW_SURVEY, false)
    }

    private fun buildNotification(): FutureNotification {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(context.getString(R.string.survey_link))

        val pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        val notification =
            NotificationCompat.Builder(context, Const.NOTIFICATION_CHANNEL_DEFAULT)
                .setSmallIcon(R.drawable.ic_outline_feedback_24px)
                .setColor(context.getColor(R.color.color_primary))
                .setContentTitle(context.getString(R.string.survey_title))
                .setContentText(context.getString(R.string.survey_info))
                .setContentIntent(pendingIntent)
                .build()

        return FutureNotification(NotificationType.SURVEY, 0, notification, DateTime.now().plusDays(FEEDBACK_DELAY_DAYS))
    }

    override fun shouldShow(prefs: SharedPreferences): Boolean {
        if (Utils.getSettingLong(context, FEEDBACK_FIRST_DATE, 0L) == 0L) {
            Utils.setSetting(context, FEEDBACK_FIRST_DATE, DateTime.now().millis)

            // Schedule notification to show after feedback delay
            val notification = buildNotification()
            val scheduler = NotificationScheduler(context)
            scheduler.schedule(notification)
        }

        return prefs.getBoolean(CardManager.SHOW_SURVEY, true) &&
                Utils.getSettingLong(context, FEEDBACK_FIRST_DATE, DateTime.now().millis) < DateTime.now().minusDays(FEEDBACK_DELAY_DAYS).millis
    }

    companion object {
        private const val FEEDBACK_FIRST_DATE = "feedback_first_date"
        private const val FEEDBACK_DELAY_DAYS = 7

        @JvmStatic
        fun inflateViewHolder(parent: ViewGroup, interactionListener: CardInteractionListener): CardViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.card_survey, parent, false)
            view.findViewById<View>(R.id.survey_button).setOnClickListener { v ->
                val browserIntent = Intent(Intent.ACTION_VIEW)
                browserIntent.data = Uri.parse(view.context
                    .getString(R.string.survey_link))
                v.context.startActivity(browserIntent)
            }
            return CardViewHolder(view, interactionListener)
        }
    }
}