package de.uos.campusapp.component.ui.transportation.model

import android.content.Context
import android.content.Intent
import com.google.gson.Gson
import de.uos.campusapp.component.other.general.model.Recent
import de.uos.campusapp.component.ui.transportation.TransportationDetailsActivity

/**
 * Represents a station
 *
 * @property id
 * @property name Station name
 * @property quality Accuracy of search result. Scale between 0 (low) and Integer.MAX_VALUE (high) (optional)
 */
abstract class AbstractStation {
    abstract val id: String
    abstract val name: String
    abstract var quality: Int

    override fun toString(): String = name

    fun getIntent(context: Context): Intent {
        return Intent(context, TransportationDetailsActivity::class.java).apply {
            putExtra(TransportationDetailsActivity.EXTRA_STATION_ID, id)
            putExtra(TransportationDetailsActivity.EXTRA_STATION, name)
        }
    }

    companion object {
        fun fromRecent(r: Recent): AbstractStation? {
            return Gson().fromJson(r.name, Station::class.java)
        }
    }
}