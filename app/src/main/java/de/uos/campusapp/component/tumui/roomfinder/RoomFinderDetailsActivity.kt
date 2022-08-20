package de.uos.campusapp.component.tumui.roomfinder

import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.fragment.app.Fragment
import de.uos.campusapp.R
import de.uos.campusapp.component.other.generic.activity.ProgressActivity
import de.uos.campusapp.component.tumui.roomfinder.api.RoomFinderAPI
import de.uos.campusapp.component.tumui.roomfinder.model.RoomFinderCoordinateInterface
import de.uos.campusapp.component.tumui.roomfinder.model.RoomFinderRoomInterface
import de.uos.campusapp.utils.Component
import de.uos.campusapp.utils.Utils
import javax.inject.Inject

/**
 * Displays the map regarding the searched room.
 */
class RoomFinderDetailsActivity : ProgressActivity<RoomFinderCoordinateInterface>(R.layout.activity_roomfinderdetails, Component.ROOMFINDER) {

    @Inject
    lateinit var apiClient: RoomFinderAPI

    private lateinit var detailsFragment: RoomFinderDetailsFragment
    private var weekViewFragment: Fragment? = null

    private val room: RoomFinderRoomInterface by lazy {
        intent.getSerializableExtra(EXTRA_ROOM_INFO) as RoomFinderRoomInterface
    }

    private var roomCoordinates: RoomFinderCoordinateInterface? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        injector.roomFinderComponent().inject(this)

        supportActionBar?.title = room.name

        detailsFragment = RoomFinderDetailsFragment.newInstance(room)
        supportFragmentManager.beginTransaction()
                .add(R.id.fragment_container, detailsFragment)
                .commit()

        loadGeo()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_roomfinder_detail, menu)

        val timetable = menu.findItem(R.id.action_room_timetable)
        timetable.setIcon(
                if (weekViewFragment == null) R.drawable.ic_outline_event_note_24px
                else R.drawable.ic_action_info
        )

        menu.findItem(R.id.action_directions).isVisible = weekViewFragment == null && roomCoordinates != null
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_room_timetable -> {
                toggleRoomTimetable()
                invalidateOptionsMenu()
                return true
            }
            R.id.action_directions -> {
                openDirections()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed() {
        // Remove weekViewFragment with room timetable if present and show details again
        if (weekViewFragment != null) {
            toggleRoomTimetable()
            invalidateOptionsMenu()
            return
        }

        super.onBackPressed()
    }

    private fun toggleRoomTimetable() {
        val ft = supportFragmentManager.beginTransaction()
        // Remove if weekViewFragment is already present
        if (weekViewFragment != null) {
            ft.replace(R.id.fragment_container, detailsFragment)
            ft.commit()
            weekViewFragment = null
            return
        }

        weekViewFragment = WeekViewFragment.newInstance(room)
        ft.replace(R.id.fragment_container, weekViewFragment!!)
        ft.commit()
    }

    private fun loadGeo() {
        fetch { apiClient.fetchRoomCoordinates(room) }
    }

    override fun onDownloadSuccessful(response: RoomFinderCoordinateInterface) {
        onGeoLoadFinished(response)
    }

    override fun onDownloadFailure(throwable: Throwable) {
        onLoadGeoFailed()
    }

    override fun onEmptyDownloadResponse() {
        onLoadGeoFailed()
    }

    private fun onLoadGeoFailed() {
        onGeoLoadFinished(null)
    }

    private fun onGeoLoadFinished(result: RoomFinderCoordinateInterface?) {
        showLoadingEnded()
        roomCoordinates = result
        invalidateOptionsMenu()
    }

    private fun openDirections() {
        val rCoordinates = roomCoordinates
        if (rCoordinates == null) {
            Utils.showToastOnUIThread(this@RoomFinderDetailsActivity, R.string.no_map_available)
            return
        }

        // Build get directions intent and see if some app can handle it
        val coordinates = "${rCoordinates.latitude},${rCoordinates.longitude}"
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("google.navigation:q=$coordinates"))
        val pkgAppsList = applicationContext.packageManager
                .queryIntentActivities(intent, PackageManager.GET_RESOLVED_FILTER)

        // If some app can handle this intent start it
        if (pkgAppsList.isNotEmpty()) {
            startActivity(intent)
            return
        }

        // If no app is capable of opening it link to google maps market entry
        try {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.google.android.apps.maps")))
        } catch (e: ActivityNotFoundException) {
            Utils.log(e)
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=com.google.android.apps.maps")))
        }
    }

    companion object {
        const val EXTRA_ROOM_INFO = "roomInfo"
    }
}
