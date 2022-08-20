package de.uos.campusapp.component.tumui.roomfinder

import android.app.SearchManager
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding
import de.uos.campusapp.R
import de.uos.campusapp.component.other.general.RecentsDao
import de.uos.campusapp.component.other.general.model.Recent
import de.uos.campusapp.component.other.generic.adapter.NoResultsAdapter
import de.uos.campusapp.component.other.generic.fragment.FragmentForSearchingInBackground
import de.uos.campusapp.component.tumui.roomfinder.api.RoomFinderAPI
import de.uos.campusapp.component.tumui.roomfinder.model.RoomFinderRoom
import de.uos.campusapp.component.tumui.roomfinder.model.RoomFinderRoomInterface
import de.uos.campusapp.database.TcaDb
import de.uos.campusapp.databinding.FragmentRoomfinderBinding
import de.uos.campusapp.di.injector
import de.uos.campusapp.utils.NetUtils
import de.uos.campusapp.utils.Utils
import javax.inject.Inject

class RoomFinderFragment : FragmentForSearchingInBackground<List<RoomFinderRoomInterface>>(
    R.layout.fragment_roomfinder,
    R.string.roomfinder,
    RoomFinderSuggestionProvider.AUTHORITY,
    minLen = 3
) {
    @Inject
    lateinit var apiClient: RoomFinderAPI

    private val recentsDao by lazy { TcaDb.getInstance(requireContext()).recentsDao() }
    private lateinit var adapter: RoomFinderListAdapter

    private val recents: List<RoomFinderRoomInterface>
        get() {
            return recentsDao.getAll(RecentsDao.ROOMS)?.mapNotNull {
                try {
                    RoomFinderRoom.fromRecent(it)
                } catch (ignore: IllegalArgumentException) {
                    null
                }
            }.orEmpty()
        }

    private val binding by viewBinding(FragmentRoomfinderBinding::bind)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        injector.roomFinderComponent().inject(this)
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = RoomFinderListAdapter(requireContext(), recents)

        binding.listView.setOnItemClickListener { _, _, position, _ ->
            val room = binding.listView.adapter.getItem(position) as RoomFinderRoomInterface
            openRoomDetails(room)
        }

        val intent = requireActivity().intent
        if (intent != null && intent.hasExtra(SearchManager.QUERY)) {
            val query = checkNotNull(intent.getStringExtra(SearchManager.QUERY))
            requestSearch(query)
            return
        }

        if (adapter.isEmpty) {
            openSearch()
        } else {
            binding.listView.adapter = adapter
        }
    }

    override fun onSearchInBackground(): List<RoomFinderRoomInterface> = recents

    override fun onSearchInBackground(query: String): List<RoomFinderRoomInterface>? {
        return try {
            apiClient.searchRooms(query)
        } catch (t: Throwable) {
            Utils.log(t)
            null
        }
    }

    override fun onSearchFinished(result: List<RoomFinderRoomInterface>?) {
        if (result == null) {
            if (NetUtils.isConnected(requireContext())) {
                showErrorLayout()
            } else {
                showNoInternetLayout()
            }
            return
        }

        if (result.isEmpty()) {
            binding.listView.adapter = NoResultsAdapter(requireContext())
        } else {
            adapter = RoomFinderListAdapter(requireContext(), result)
            binding.listView.adapter = adapter
        }
        showLoadingEnded()
    }

    /**
     * Opens a [RoomFinderDetailsActivity] that displays details (e.g. location on a map) for
     * a given room. Also adds this room to the recent queries.
     */
    private fun openRoomDetails(room: RoomFinderRoomInterface) {
        val values = "${room.id};${room.buildingId};${room.name};${room.address};${room.campus};${room.info};${room.imageUrl}"
        recentsDao.insert(Recent(values, RecentsDao.ROOMS))

        // Start detail activity
        val intent = Intent(requireContext(), RoomFinderDetailsActivity::class.java)
        intent.putExtra(RoomFinderDetailsActivity.EXTRA_ROOM_INFO, room)
        startActivity(intent)
    }

    companion object {
        fun newInstance() = RoomFinderFragment()
    }
}
