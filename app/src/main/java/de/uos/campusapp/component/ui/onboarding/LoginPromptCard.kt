package de.uos.campusapp.component.ui.onboarding

import android.content.Context
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import de.uos.campusapp.R
import de.uos.campusapp.component.ui.overview.CardInteractionListener
import de.uos.campusapp.component.ui.overview.CardManager
import de.uos.campusapp.component.ui.overview.card.Card
import de.uos.campusapp.component.ui.overview.card.CardViewHolder
import de.uos.campusapp.utils.Component
import de.uos.campusapp.utils.Const
import de.uos.campusapp.utils.Utils

/**
 * Card that prompts the user to login to TUMonline since we don't show the wizard after the first launch anymore.
 * It will be shown until it is swiped away for the first time.
 */
class LoginPromptCard(context: Context) : Card(CardManager.CARD_LOGIN, context, Component.ONBOARDING, "card_login") {

    public override fun discard(editor: SharedPreferences.Editor) {
        Utils.setSetting(context, CardManager.SHOW_LOGIN, false)
    }

    override fun shouldShow(prefs: SharedPreferences): Boolean {
        // show on top as long as user hasn't swiped it away and isn't connected to TUMonline
        return Utils.getSettingBool(context, CardManager.SHOW_LOGIN, true) &&
            Utils.getSetting(context, Const.USERNAME, "").isEmpty()
    }

    override fun getId(): Int {
        return 0
    }

    companion object {
        @JvmStatic
        fun inflateViewHolder(parent: ViewGroup, interactionListener: CardInteractionListener): CardViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.card_login_prompt, parent, false)
            view.findViewById<View>(R.id.loginButton).setOnClickListener {
                val loginIntent = OnboardingActivity.newIntent(view.context)
                view.context.startActivity(loginIntent)
            }
            return CardViewHolder(view, interactionListener)
        }
    }
}
