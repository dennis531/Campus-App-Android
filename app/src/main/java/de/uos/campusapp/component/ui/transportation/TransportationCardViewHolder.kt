package de.uos.campusapp.component.ui.transportation

import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import de.uos.campusapp.R
import de.uos.campusapp.component.ui.overview.CardInteractionListener
import de.uos.campusapp.component.ui.overview.card.CardViewHolder
import de.uos.campusapp.component.ui.transportation.model.AbstractDeparture
import de.uos.campusapp.component.ui.transportation.model.AbstractStation
import kotlin.math.min

class TransportationCardViewHolder(
    itemView: View,
    interactionListener: CardInteractionListener
) : CardViewHolder(itemView, interactionListener) {

    fun bind(station: AbstractStation, departures: List<AbstractDeparture>) {
        with(itemView) {
            val controller = TransportController(context)
            val items = min(departures.size, 5)
            val stationNameTextView = itemView.findViewById<TextView>(R.id.stationNameTextView)
            val contentContainerLayout = itemView.findViewById<LinearLayout>(R.id.contentContainerLayout)

            if (stationNameTextView.text != station.name) {
                stationNameTextView.text = station.name
                contentContainerLayout.removeAllViews()
            }
            if (contentContainerLayout.childCount == 0) {
                departures.asSequence()
                        .take(items)
                        .map { departure ->
                            DepartureView(context, true).apply {
                                val isFavorite = controller.isFavorite(departure.symbol.name)
                                setSymbol(departure.symbol, isFavorite)
                                setLine(departure.direction)
                                setTime(departure.departureTime)
                            }
                        }
                        .toList()
                        .forEach { departureView ->
                            contentContainerLayout.addView(departureView)
                        }
            }
        }
    }
}