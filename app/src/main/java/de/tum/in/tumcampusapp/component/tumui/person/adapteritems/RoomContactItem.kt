package de.tum.`in`.tumcampusapp.component.tumui.person.adapteritems

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import de.tum.`in`.tumcampusapp.R
import de.tum.`in`.tumcampusapp.component.tumui.roomfinder.RoomFinderActivity

class RoomContactItem(text: String,
                      private val roomNumber: String) : AbstractContactItem("Room", text, R.drawable.ic_business_black_24dp) {

    override fun getFormattedValue() = value

    override fun getIntent(context: Context) = Intent(context, RoomFinderActivity::class.java).apply {
        putExtra(SearchManager.QUERY, roomNumber)
    }

}