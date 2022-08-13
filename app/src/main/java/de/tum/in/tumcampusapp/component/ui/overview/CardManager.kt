package de.tum.`in`.tumcampusapp.component.ui.overview

import android.content.Context
import android.preference.PreferenceManager
import de.tum.`in`.tumcampusapp.R
import de.tum.`in`.tumcampusapp.database.TcaDb
import de.tum.`in`.tumcampusapp.utils.Const.CARD_POSITION_PREFERENCE_SUFFIX
import de.tum.`in`.tumcampusapp.utils.Const.DISCARD_SETTINGS_START

/**
 * Card manager, manages inserting, dismissing, updating and displaying of cards
 */
object CardManager {

    const val SHOW_SUPPORT = "show_support"
    const val SHOW_LOGIN = "show_login"

    /**
     * Card typ constants
     */
    const val CARD_CAFETERIA = R.layout.card_cafeteria_menu
    const val CARD_TUITION_FEE = R.layout.card_tuition_fees
    const val CARD_NEXT_LECTURE = R.layout.card_next_lecture_item
    const val CARD_RESTORE = R.layout.card_restore
    const val CARD_NO_INTERNET = R.layout.card_no_internet
    const val CARD_TRANSPORTATION = R.layout.card_transportation
    const val CARD_MESSAGES = R.layout.card_messages
    const val CARD_NEWS = R.layout.card_news_item
    const val CARD_EDUROAM = R.layout.card_eduroam
    const val CARD_CHAT = R.layout.card_chat_messages
    const val CARD_SUPPORT = R.layout.card_support
    const val CARD_LOGIN = R.layout.card_login_prompt
    const val CARD_EDUROAM_FIX = R.layout.card_eduroam_fix
    const val CARD_UPDATE_NOTE = R.layout.card_update_note

    /**
     * Resets dismiss settings for all cards
     */
    fun restoreCards(context: Context) {
        context.getSharedPreferences(DISCARD_SETTINGS_START, 0)
                .edit()
                .clear()
                .apply()

        TcaDb.getInstance(context)
                .newsDao()
                .restoreAllNews()

        restoreCardPositions(context)
    }

    private fun restoreCardPositions(context: Context) {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = preferences.edit()

        for (s in preferences.all.keys) {
            if (s.endsWith(CARD_POSITION_PREFERENCE_SUFFIX)) {
                editor.remove(s)
            }
        }
        editor.apply()
    }
}
