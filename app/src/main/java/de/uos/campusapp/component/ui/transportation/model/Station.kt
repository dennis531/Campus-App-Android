package de.uos.campusapp.component.ui.transportation.model

import android.content.Context
import android.content.Intent
import com.google.gson.Gson
import de.uos.campusapp.component.other.general.model.Recent
import de.uos.campusapp.component.ui.transportation.TransportationDetailsActivity

open class Station(
    open val id: String,
    open val name: String,
    open var quality: Int = 0 // Accuracy of search result (0: low); optional
) {

    override fun toString(): String = name

    fun getIntent(context: Context): Intent {
        return Intent(context, TransportationDetailsActivity::class.java).apply {
            putExtra(TransportationDetailsActivity.EXTRA_STATION_ID, id)
            putExtra(TransportationDetailsActivity.EXTRA_STATION, name)
        }
    }

    companion object {
        fun fromRecent(r: Recent): Station? {
            return Gson().fromJson(r.name, Station::class.java)
        }
    }
}