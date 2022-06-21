package de.tum.`in`.tumcampusapp.component.tumui.calendar

import android.app.SearchManager
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding
import de.tum.`in`.tumcampusapp.R
import de.tum.`in`.tumcampusapp.api.general.exception.ForbiddenException
import de.tum.`in`.tumcampusapp.api.general.exception.NotFoundException
import de.tum.`in`.tumcampusapp.api.general.exception.UnauthorizedException
import de.tum.`in`.tumcampusapp.component.other.navigation.NavDestination
import de.tum.`in`.tumcampusapp.component.other.navigation.NavigationManager
import de.tum.`in`.tumcampusapp.component.tumui.calendar.api.CalendarAPI
import de.tum.`in`.tumcampusapp.component.tumui.calendar.model.CalendarItem
import de.tum.`in`.tumcampusapp.component.tumui.roomfinder.RoomFinderActivity
import de.tum.`in`.tumcampusapp.database.TcaDb
import de.tum.`in`.tumcampusapp.databinding.FragmentCalendarDetailsBinding
import de.tum.`in`.tumcampusapp.utils.*
import de.tum.`in`.tumcampusapp.utils.Const.CALENDAR_ID_PARAM
import de.tum.`in`.tumcampusapp.utils.Const.CALENDAR_SHOWN_IN_CALENDAR_ACTIVITY_PARAM
import de.tum.`in`.tumcampusapp.utils.ui.RoundedBottomSheetDialogFragment
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.net.UnknownHostException

class CalendarDetailsFragment : RoundedBottomSheetDialogFragment() {

    private var listener: OnEventInteractionListener? = null

    private val calendarItemId: String by lazy {
        arguments?.getString(CALENDAR_ID_PARAM) ?: throw IllegalStateException("No calendar item ID passed")
    }

    private val isShownInCalendarActivity: Boolean by lazy {
        arguments?.getBoolean(CALENDAR_SHOWN_IN_CALENDAR_ACTIVITY_PARAM)
                ?: throw IllegalStateException("Incomplete Bundle when opening calendar details fragment")
    }

    private val compositeDisposable = CompositeDisposable()

    private val binding by viewBinding(FragmentCalendarDetailsBinding::bind)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_calendar_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        context?.let {
            val dao = TcaDb.getInstance(it).calendarDao()
            val calendarItem = dao.getCalendarItemsById(calendarItemId)
            updateView(calendarItem)
        }
    }

    private fun updateView(calendarItemList: List<CalendarItem>) {
        val calendarItem = calendarItemList[0]

        with(binding) {
            if (calendarItemList.all { it.isCanceled }) {
                cancelButtonsContainer.visibility = View.VISIBLE
                descriptionTextView.setTextColor(Color.RED)
            }

            titleTextView.text = calendarItem.title
            dateTextView.text = calendarItem.getEventDateString()

            val locationList = calendarItemList.map { it.location }
            if (locationList.all { it.isBlank() }) {
                locationIcon.visibility = View.GONE
            } else {
                locationIcon.visibility = View.VISIBLE
                for (item in calendarItemList) {
                    if (item.location.isBlank()) {
                        continue
                    }
                    val locationText: TextView = layoutInflater
                            .inflate(R.layout.calendar_location_text, locationLinearLayout, false) as TextView
                    if (item.isCanceled) {
                        locationText.setTextColor(ContextCompat.getColor(requireContext(), R.color.event_canceled))
                        val textForCancelledEvent = "${item.location} (${R.string.event_canceled})"
                        locationText.text = textForCancelledEvent
                    } else {
                        locationText.text = item.location
                    }
                    if (ConfigUtils.isComponentEnabled(requireContext(), Component.ROOMFINDER)) {
                        locationText.setOnClickListener { onLocationClicked(item.location) }
                    }
                    locationLinearLayout.addView(locationText)
                }
            }

            if (calendarItem.description.isEmpty()) {
                descriptionTextView.visibility = View.GONE
            } else {
                descriptionTextView.text = calendarItem.description
            }

            if (!isShownInCalendarActivity) {
                showInCalendarButtonContainer.visibility = View.VISIBLE
                showInCalendarButton.setOnClickListener {
                    openEventInCalendarActivity(calendarItem)
                    dismiss()
                }
            }

            if (ConfigUtils.isCalendarEditable() && calendarItem.isEditable && isShownInCalendarActivity) {
                // We only provide edit and delete functionality if the user is in CalendarActivity,
                // but not if the user opens the fragment from MainActivity.
                buttonsContainer.visibility = View.VISIBLE
                deleteButton.setOnClickListener { displayDeleteDialog(calendarItem.id) }
                editButton.setOnClickListener { listener?.onEditEvent(calendarItem) }
            } else {
                buttonsContainer.visibility = View.GONE
            }
        }

    }

    private fun openEventInCalendarActivity(calendarItem: CalendarItem) {
        val args = Bundle().apply {
            putLong(Const.EVENT_TIME, calendarItem.dtstart.millis)
        }
        val destination = NavDestination.Fragment(CalendarFragment::class.java, args)
        NavigationManager.open(requireContext(), destination)
    }

    private fun displayDeleteDialog(eventId: String) {
        val s = TcaDb.getInstance(requireContext()).calendarDao().getSeriesIdForEvent(eventId)
        val alertDialog = AlertDialog.Builder(requireContext())
                .setTitle(R.string.event_delete_title)
                .setMessage(R.string.delete_event_info)
                .setPositiveButton(R.string.delete) { _, _ -> deleteEvent(eventId) }
                .setNeutralButton(R.string.cancel, null)
        if (s != null) { // a event series
            alertDialog.setNegativeButton(R.string.delete_series) { _, _ -> deleteEventSeries(s) }
        }
        alertDialog.show()
    }

    private fun deleteEventSeries(seriesId: String) {
        val calendarItems = TcaDb.getInstance(requireContext()).calendarDao().getCalendarItemsInSeries(seriesId)
        calendarItems.forEach {
            deleteEvent(it.id)
        }
        TcaDb.getInstance(requireContext()).calendarDao().removeSeriesIdMappings(seriesId)
    }

    private fun deleteEvent(eventId: String) {
        val c = requireContext()
        compositeDisposable += Single.fromCallable { (ConfigUtils.getLMSClient(c) as CalendarAPI).deleteCalenderEvent(eventId) }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ response ->
                dismiss()
                listener?.onEventDeleted(eventId)
            }, {
                // if (call.isCanceled) {
                //     return
                //}
                handleDeleteEventError(it)
            })
    }

    private fun handleDeleteEventError(t: Throwable) {
        val c = requireContext()
        val messageResId = when (t) {
            is UnknownHostException -> R.string.error_no_internet_connection
            is UnauthorizedException -> R.string.error_unauthorized
            is ForbiddenException -> R.string.error_no_rights_to_access_function
            is NotFoundException -> R.string.error_resource_not_found
            else -> R.string.error_unknown
        }
        Utils.showToast(c, messageResId)
    }

    private fun onLocationClicked(location: String) {
        val findStudyRoomIntent = Intent()
        findStudyRoomIntent.putExtra(SearchManager.QUERY, location)
        findStudyRoomIntent.setClass(requireContext(), RoomFinderActivity::class.java)
        startActivity(findStudyRoomIntent)
    }

    companion object {

        @JvmStatic
        fun newInstance(
            calendarItemId: String,
            isShownInCalendarActivity: Boolean = true,
            listener: OnEventInteractionListener? = null
        ): CalendarDetailsFragment {
            return CalendarDetailsFragment().apply {
                this.arguments = Bundle().apply {
                    putString(CALENDAR_ID_PARAM, calendarItemId)
                    putBoolean(CALENDAR_SHOWN_IN_CALENDAR_ACTIVITY_PARAM, isShownInCalendarActivity)
                }
                this.listener = listener
            }
        }
    }

    override fun onDetach() {
        super.onDetach()
        compositeDisposable.dispose()
    }

    interface OnEventInteractionListener {
        fun onEventDeleted(eventId: String)
        fun onEditEvent(calendarItem: CalendarItem)
    }
}