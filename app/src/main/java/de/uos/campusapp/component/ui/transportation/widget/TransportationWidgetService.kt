package de.uos.campusapp.component.ui.transportation.widget

import android.annotation.SuppressLint
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import de.uos.campusapp.R
import de.uos.campusapp.component.ui.transportation.TransportController
import de.uos.campusapp.component.ui.transportation.model.Departure
import java.util.*

@SuppressLint("Registered")
class TransportationWidgetService : RemoteViewsService() {

    override fun onGetViewFactory(intent: Intent): RemoteViewsFactory {
        return TransportationRemoteViewFactory(this.applicationContext, intent)
    }

    private inner class TransportationRemoteViewFactory internal constructor(private val applicationContext: Context, intent: Intent) : RemoteViewsFactory {
        private var departures: List<Departure> = ArrayList()
        private val appWidgetID: Int = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, -1)
        private val forceLoadDepartures: Boolean = intent.getBooleanExtra(TransportationWidget.TRANSPORTATION_WIDGET_FORCE_RELOAD, true)

        override fun onCreate() {}

        override fun onDataSetChanged() {
            val widget = TransportController(applicationContext).getWidget(appWidgetID)
            departures = widget.getDepartures(applicationContext, this.forceLoadDepartures)
        }

        override fun onDestroy() {}

        override fun getCount() = departures.size

        override fun getViewAt(position: Int): RemoteViews? {
            // Get the departure for this view
            val currentItem = this.departures[position]
            val symbol = currentItem.symbol

            return RemoteViews(applicationContext.packageName, R.layout.departure_line_widget).apply {
                // Setup the line symbol
                setTextViewText(R.id.line_symbol, symbol.name)

                setTextColor(R.id.line_symbol, symbol.getTextColor(applicationContext))
                setInt(R.id.line_symbol_background, "setColorFilter", symbol.getBackgroundColor(applicationContext))

                // Setup the line name and the departure time
                setTextViewText(R.id.nameTextView, currentItem.direction)
                setTextViewText(R.id.departure_time, currentItem.calculatedCountDown.toString() + " min")
            }
        }

        override fun getLoadingView() = null

        override fun getViewTypeCount() = 1

        override fun getItemId(position: Int) = position.toLong()

        override fun hasStableIds() = true
    }
}