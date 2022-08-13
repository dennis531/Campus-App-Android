package de.tum.`in`.tumcampusapp.component.ui.messages

import android.content.Context
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import de.tum.`in`.tumcampusapp.R
import de.tum.`in`.tumcampusapp.component.other.navigation.NavDestination
import de.tum.`in`.tumcampusapp.component.ui.messages.fragment.MessagesFragment
import de.tum.`in`.tumcampusapp.component.ui.messages.model.AbstractMessage
import de.tum.`in`.tumcampusapp.component.ui.overview.CardInteractionListener
import de.tum.`in`.tumcampusapp.component.ui.overview.CardManager.CARD_MESSAGES
import de.tum.`in`.tumcampusapp.component.ui.overview.card.Card
import de.tum.`in`.tumcampusapp.component.ui.overview.card.CardViewHolder
import de.tum.`in`.tumcampusapp.utils.Component

class MessagesCard(context: Context, val messages: List<AbstractMessage>) : Card(CARD_MESSAGES, context, Component.MESSAGES, "card_messages") {

    override val optionsMenuResId: Int
        get() = R.menu.card_popup_menu

    override fun updateViewHolder(viewHolder: RecyclerView.ViewHolder) {
        super.updateViewHolder(viewHolder)

        if (viewHolder is MessagesCardViewHolder) {
            viewHolder.bind(messages)
        }
    }

    override fun getNavigationDestination(): NavDestination {
        return NavDestination.Fragment(MessagesFragment::class.java)
    }

    override fun discard(editor: SharedPreferences.Editor) {
        val last = messages.firstOrNull() ?: return
        editor.putLong(LAST_MESSAGE_DATE, last.date.millis)
    }

    override fun shouldShow(prefs: SharedPreferences): Boolean {
        // Card reappears when new messages are available
        val latest = messages.firstOrNull() ?: return false
        val prevTime = prefs.getLong(LAST_MESSAGE_DATE, 0)
        return latest.date.isAfter(prevTime)
    }

    companion object {
        private const val LAST_MESSAGE_DATE = "last_massage_time"

        @JvmStatic
        fun inflateViewHolder(parent: ViewGroup, interactionListener: CardInteractionListener): CardViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.card_messages, parent, false)
            return MessagesCardViewHolder(view, interactionListener)
        }
    }
}
