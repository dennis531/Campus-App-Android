package de.uos.campusapp.component.ui.transportation

import android.content.Context
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import de.uos.campusapp.R
import de.uos.campusapp.component.other.navigation.NavDestination
import de.uos.campusapp.component.ui.overview.CardInteractionListener
import de.uos.campusapp.component.ui.overview.CardManager.CARD_TRANSPORTATION
import de.uos.campusapp.component.ui.overview.card.Card
import de.uos.campusapp.component.ui.overview.card.CardViewHolder
import de.uos.campusapp.component.ui.transportation.model.AbstractDeparture
import de.uos.campusapp.component.ui.transportation.model.AbstractStation
import de.uos.campusapp.utils.Component

/**
 * Card that shows MVV departure times
 */
class TransportationCard(context: Context, val station: AbstractStation, val departures: List<AbstractDeparture>) : Card(CARD_TRANSPORTATION, context, Component.TRANSPORTATION, "card_transportation") {

    override val optionsMenuResId: Int
        get() = R.menu.card_popup_menu

    val title: String
        get() = station.name

    override fun updateViewHolder(viewHolder: RecyclerView.ViewHolder) {
        super.updateViewHolder(viewHolder)
        if (viewHolder is TransportationCardViewHolder) {
            viewHolder.bind(station, departures)
        }
    }

    override fun getNavigationDestination(): NavDestination? {
        val extras = station.getIntent(context).extras ?: return null
        return NavDestination.Activity(TransportationDetailsActivity::class.java, extras)
    }

    override fun discard(editor: Editor) {
        editor.putLong(TRANSPORTATION_TIME, System.currentTimeMillis())
    }

    override fun shouldShow(prefs: SharedPreferences): Boolean {
        // Card is only hidden for an hour when discarded
        val prevDate = prefs.getLong(TRANSPORTATION_TIME, 0)
        return prevDate + DateUtils.HOUR_IN_MILLIS < System.currentTimeMillis()
    }

    companion object {
        private const val TRANSPORTATION_TIME = "transportation_time"
        @JvmStatic
        fun inflateViewHolder(parent: ViewGroup, interactionListener: CardInteractionListener): CardViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.card_transportation, parent, false)
            return TransportationCardViewHolder(view, interactionListener)
        }
    }
}
