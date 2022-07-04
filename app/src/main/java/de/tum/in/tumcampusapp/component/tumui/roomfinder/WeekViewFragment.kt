package de.tum.`in`.tumcampusapp.component.tumui.roomfinder

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.alamkanak.weekview.WeekView
import com.alamkanak.weekview.WeekViewDisplayable
import de.tum.`in`.tumcampusapp.R
import de.tum.`in`.tumcampusapp.component.tumui.calendar.WidgetCalendarItem
import de.tum.`in`.tumcampusapp.component.tumui.roomfinder.api.RoomFinderAPI
import de.tum.`in`.tumcampusapp.component.tumui.roomfinder.model.RoomFinderRoomInterface
import de.tum.`in`.tumcampusapp.utils.ConfigUtils
import de.tum.`in`.tumcampusapp.utils.Const
import de.tum.`in`.tumcampusapp.utils.Utils
import org.jetbrains.anko.support.v4.runOnUiThread
import java.util.*

class WeekViewFragment : Fragment() {

    private val room: RoomFinderRoomInterface by lazy {
        arguments?.getSerializable(Const.ROOM_ID) as RoomFinderRoomInterface
    }

    private lateinit var weekView: WeekView<WidgetCalendarItem>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? =
            inflater.inflate(R.layout.fragment_day_view, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        weekView = view.findViewById(R.id.weekView)
        weekView.goToHour(8)

        loadEventsInBackground()
    }

    private fun loadEventsInBackground() {
        // Populate the week view with the events of the month to display
        Thread {
            // Convert to the proper type
            val events = fetchEventList(room)

            requireActivity().runOnUiThread {
                weekView.submit(events)
                weekView.notifyDataSetChanged()
            }
        }.start()
    }

    private fun fetchEventList(room: RoomFinderRoomInterface): List<WeekViewDisplayable<WidgetCalendarItem>> {
        try {
            val apiClient = ConfigUtils.getLMSClient(requireContext())
            val schedules = (apiClient as RoomFinderAPI).fetchRoomSchedule(room)

            if (schedules == null) {
                runOnUiThread {
                    Utils.showToast(requireContext(), R.string.no_schedules_available)
                }
                return emptyList()
            }

            // Convert to the proper type
            val events = ArrayList<WeekViewDisplayable<WidgetCalendarItem>>()
            for (schedule in schedules) {
                val calendarItem = WidgetCalendarItem.create(schedule)
                calendarItem.color = ContextCompat.getColor(requireContext(), R.color.event_lecture)
                events.add(calendarItem)
            }

            return events
        } catch (e: Exception) {
            Utils.log(e)
        }

        return emptyList()
    }

    companion object {

        fun newInstance(room: RoomFinderRoomInterface): WeekViewFragment {
            val fragment = WeekViewFragment()
            fragment.arguments = Bundle().apply { putSerializable(Const.ROOM_ID, room) }
            return fragment
        }
    }
}