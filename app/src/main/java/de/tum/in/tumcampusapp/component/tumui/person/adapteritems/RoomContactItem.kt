package de.tum.`in`.tumcampusapp.component.tumui.person.adapteritems

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import de.tum.`in`.tumcampusapp.R
import de.tum.`in`.tumcampusapp.component.tumui.roomfinder.RoomFinderActivity
import de.tum.`in`.tumcampusapp.utils.Component
import de.tum.`in`.tumcampusapp.utils.ConfigUtils

class RoomContactItem(
    text: String,
    private val roomQuery: String
) : AbstractContactItem(R.string.room, text, R.drawable.ic_outline_business_24px) {

    override fun getIntent(context: Context) : Intent? {
        if (roomQuery.isBlank() || !ConfigUtils.isComponentEnabled(context, Component.ROOMFINDER)) {
            return null
        }

        return Intent(context, RoomFinderActivity::class.java).apply {
            putExtra(SearchManager.QUERY, roomQuery)
        }
    }
}