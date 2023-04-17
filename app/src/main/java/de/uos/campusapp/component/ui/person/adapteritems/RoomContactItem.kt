package de.uos.campusapp.component.ui.person.adapteritems

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import de.uos.campusapp.R
import de.uos.campusapp.component.ui.roomfinder.RoomFinderActivity
import de.uos.campusapp.utils.Component
import de.uos.campusapp.utils.ConfigUtils

class RoomContactItem(
    text: String,
    private val roomQuery: String
) : AbstractContactItem(R.string.room, text, R.drawable.ic_outline_business_24px) {

    override fun getIntent(context: Context): Intent? {
        if (roomQuery.isBlank() || !ConfigUtils.isComponentEnabled(context, Component.ROOMFINDER)) {
            return null
        }

        return Intent(context, RoomFinderActivity::class.java).apply {
            putExtra(SearchManager.QUERY, roomQuery)
        }
    }
}