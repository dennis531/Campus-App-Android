package de.tum.`in`.tumcampusapp.component.ui.transportation

import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import de.tum.`in`.tumcampusapp.R
import de.tum.`in`.tumcampusapp.component.ui.overview.CardInteractionListener
import de.tum.`in`.tumcampusapp.component.ui.overview.card.CardViewHolder
import de.tum.`in`.tumcampusapp.component.ui.transportation.model.Departure
import de.tum.`in`.tumcampusapp.component.ui.transportation.model.Station
import kotlin.math.min

class TransportationCardViewHolder(
    itemView: View,
    interactionListener: CardInteractionListener
) : CardViewHolder(itemView, interactionListener) {

    fun bind(station: Station, departures: List<Departure>) {
        with(itemView) {
            val controller = TransportController(context)
            val items = min(departures.size, 5)
            val stationNameTextView = itemView.findViewById<TextView>(R.id.stationNameTextView)
            val contentContainerLayout = itemView.findViewById<LinearLayout>(R.id.contentContainerLayout)

            if(stationNameTextView.text != station.name){
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