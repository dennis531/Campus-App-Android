package de.uos.campusapp.component.ui.calendar

import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import de.uos.campusapp.R
import de.uos.campusapp.api.general.exception.ForbiddenException
import de.uos.campusapp.api.general.exception.NotFoundException
import de.uos.campusapp.api.general.exception.UnauthorizedException
import de.uos.campusapp.component.other.generic.activity.ActivityForAccessingApi
import de.uos.campusapp.component.ui.calendar.api.CalendarAPI
import de.uos.campusapp.component.ui.calendar.model.*
import de.uos.campusapp.database.CaDb
import de.uos.campusapp.databinding.ActivityCreateEventBinding
import de.uos.campusapp.utils.Component
import de.uos.campusapp.utils.Const
import de.uos.campusapp.utils.Utils
import de.uos.campusapp.utils.plusAssign
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import org.jetbrains.anko.sdk27.coroutines.textChangedListener
import org.joda.time.DateTime
import org.joda.time.LocalDate
import org.joda.time.format.DateTimeFormat
import java.net.UnknownHostException
import java.util.*
import kotlin.collections.ArrayList

/**
 * Allows the user to create (and edit) a private event in TUMonline.
 */
class CreateEventActivity : ActivityForAccessingApi<String>(R.layout.activity_create_event, Component.CALENDAR) {

    private lateinit var start: DateTime
    private lateinit var end: DateTime

    private var isEditing: Boolean = false
    private var events: ArrayList<CalendarItem> = ArrayList()
    private var apiCallsFetched = 0
    private var apiCallsFailed = 0

    private val loadingDisposable = CompositeDisposable()

    private val repeatHelper = RepeatHelper()
    
    private lateinit var binding: ActivityCreateEventBinding

    override fun onCreateView(inflater: LayoutInflater, savedInstanceState: Bundle?): View? {
        binding = ActivityCreateEventBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val closeIcon = ContextCompat.getDrawable(this, R.drawable.ic_clear)
        val color = ContextCompat.getColor(this, R.color.color_primary)
        if (closeIcon != null) {
            closeIcon.setTint(color)
            supportActionBar?.setHomeAsUpIndicator(closeIcon)
        }

        // We only use the SwipeRefreshLayout to indicate progress, not to allow
        // the user to pull to refresh.
        swipeRefreshLayout?.isEnabled = false

        with(binding) {
            eventTitleView.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                    val isEmpty = s.toString().isEmpty()
                    val alpha = if (isEmpty) 0.5f else 1.0f
                    createEventButton.isEnabled = !isEmpty
                    createEventButton.alpha = alpha
                }

                override fun afterTextChanged(s: Editable) {}
            })
        }


        val extras = intent.extras
        with(binding) {
            if (extras != null) {
                // an event with extras can either be editing an existing event
                // or adding a new event from Tickets & Events
                isEditing = extras.getBoolean(Const.EVENT_EDIT)
                if (isEditing) {
                    createEventButton.setText(R.string.event_save_edit_button)
                }
                eventTitleView.setText(extras.getString(Const.EVENT_TITLE))
                eventDescriptionView.setText(extras.getString(Const.EVENT_COMMENT))
            } else {
                eventTitleView.requestFocus()
                showKeyboard()
            }
        }

        initStartEndDates(extras)
        setDateAndTimeListeners()
        initRepeatingSettingsListeners()


        binding.createEventButton.setOnClickListener {
            if (end.isBefore(start)) {
                showErrorDialog(getString(R.string.create_event_time_error))
                return@setOnClickListener
            }
            if (repeatHelper.isTooShort(start)) {
                showErrorDialog(getString(R.string.create_event_too_little_error))
                return@setOnClickListener
            }
            if (repeatHelper.isTooLong(start)) {
                showErrorDialog(getString(R.string.create_event_too_many_error))
                return@setOnClickListener
            }
            if (isEditing) {
                editEvent()
            } else {
                createEvent()
            }
        }

        // edited events cannot repeat
        if (isEditing) {
            binding.repeatingSwitch.visibility = View.GONE
        }
    }

    private fun initRepeatingSettingsListeners() {
            with(binding) {
                repeatingSwitch.setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) {
                        repeatHelper.setRepeatingNTimes()
                        endAfterRadioBtn.isChecked = true
                        repeatingSettings.layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT
                        repeatingSettings.requestLayout()
                    } else {
                        repeatHelper.setNotRepeating()
                        repeatingSettings.layoutParams.height = 0
                        repeatingSettings.requestLayout()
                    }
                }
            }

            binding.endOnRadioBtn.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    repeatHelper.setRepeatingUntil()
                }
            }

            binding.endAfterRadioBtn.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    repeatHelper.setRepeatingNTimes()
                }
            }

            binding.eventRepeatsTimes.textChangedListener {
                afterTextChanged {
                    if (it.toString() != "") {
                        repeatHelper.times = it.toString().toInt()
                    } else {
                        repeatHelper.times = 0
                    }
                }
            }

            binding.eventLastDateView.setOnClickListener {
                hideKeyboard()
                DatePickerDialog(this, { _, year, month, dayOfMonth ->
                    repeatHelper.end = repeatHelper.end?.withDate(year, month + 1, dayOfMonth)
                    updateDateViews()
                }, repeatHelper.end?.year!!, repeatHelper.end?.monthOfYear!! - 1, repeatHelper.end?.dayOfMonth!!).show()
            }
    }

    private fun initStartEndDates(extras: Bundle?) {
        val initialDate = extras?.getSerializable(Const.DATE) as LocalDate?
        val startTime = extras?.getSerializable(Const.EVENT_START) as DateTime?
        val endTime = extras?.getSerializable(Const.EVENT_END) as DateTime?

        if (startTime == null || endTime == null) {
            if (initialDate == null) {
                throw IllegalStateException("No date provided for CreateEventActivity")
            }

            // We’re creating a new event, so we set the start and end time to the next full hour
            start = initialDate.toDateTimeAtCurrentTime()
                    .plusHours(1)
                    .withMinuteOfHour(0)
                    .withSecondOfMinute(0)
                    .withMillisOfSecond(0)
            end = start.plusHours(1)
            repeatHelper.end = end.plusWeeks(1)
        } else {
            start = startTime
            end = endTime
            repeatHelper.end = end.plusWeeks(1)
        }

        updateDateViews()
        updateTimeViews()
    }

    private fun setDateAndTimeListeners() {
        // DATE

        // Month +/- 1 is needed because the date picker uses zero-based month values while DateTime
        // starts counting months at 1.
        binding.eventStartDateView.setOnClickListener {
            hideKeyboard()
            DatePickerDialog(this, { _, year, month, dayOfMonth ->
                start = start.withDate(year, month + 1, dayOfMonth)
                if (end.isBefore(start)) {
                    end = end.withDate(year, month + 1, dayOfMonth)
                }
                updateDateViews()
            }, start.year, start.monthOfYear - 1, start.dayOfMonth).show()
        }
        binding.eventEndDateView.setOnClickListener {
            hideKeyboard()
            DatePickerDialog(this, { _, year, month, dayOfMonth ->
                end = end.withDate(year, month + 1, dayOfMonth)
                updateDateViews()
            }, start.year, start.monthOfYear - 1, start.dayOfMonth).show()
        }

        // TIME
        binding.eventStartTimeView.setOnClickListener { view ->
            hideKeyboard()
            TimePickerDialog(this, { timePicker, hour, minute ->
                val eventLength = end.millis - start.millis
                start = start.withHourOfDay(hour)
                        .withMinuteOfHour(minute)
                end = end.withMillis(start.millis + eventLength)
                updateTimeViews()
            }, start.hourOfDay, start.minuteOfHour, true).show()
        }

        binding.eventEndTimeView.setOnClickListener { view ->
            hideKeyboard()
            TimePickerDialog(this, { timePicker, hour, minute ->
                end = end.withHourOfDay(hour)
                        .withMinuteOfHour(minute)
                updateTimeViews()
            }, end.hourOfDay, end.minuteOfHour, true).show()
        }
    }

    private fun updateTimeViews() {
        val format = DateTimeFormat.forPattern("HH:mm")
                .withLocale(Locale.getDefault())
        with(binding) {
            eventStartTimeView.text = format.print(start)
            eventEndTimeView.text = format.print(end)
        }
    }

    private fun updateDateViews() {
        val format = DateTimeFormat.forPattern("EEE, dd.MM.yyyy")
                .withLocale(Locale.getDefault())
        with(binding) {
            eventStartDateView.text = format.print(start)
            eventEndDateView.text = format.print(end)
            eventLastDateView.text = format.print(repeatHelper.end)
        }
    }

    private fun editEvent() {
        val eventId = intent.getStringExtra(Const.EVENT_NR) ?: return
        val seriesId = CaDb.getInstance(this).calendarDao().getSeriesIdForEvent(eventId)
        repeatHelper.seriesId = seriesId
        // Because we don't show a loading screen for the delete request (only for the create
        // request), we use a short Toast to let the user know that something is happening.
        Toast.makeText(this, R.string.updating_event, Toast.LENGTH_SHORT).show()

        loadingDisposable += Single.fromCallable { (apiClient as CalendarAPI).deleteCalenderEvent(eventId) }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                Utils.log("Event successfully deleted (now creating the edited version)")
                CaDb.getInstance(this@CreateEventActivity).calendarDao().delete(eventId)
                createEvent()
            }, {
                Utils.log(it)
                displayErrorMessage(it)
            })
    }

    private fun displayErrorMessage(throwable: Throwable) {
        val messageResId: Int = when (throwable) {
            is UnknownHostException -> R.string.error_no_internet_connection
            is UnauthorizedException -> R.string.error_unauthorized
            is ForbiddenException -> R.string.error_no_rights_to_access_function
            is NotFoundException -> R.string.error_resource_not_found
            else -> R.string.error_unknown
        }
        Utils.showToast(this, messageResId)
    }

    private fun createEvent() {
        val event = CalendarItem()
        event.dtstart = start
        event.dtend = end
        event.typeName = CalendarItem.OTHER

        var title = binding.eventTitleView.text.toString()
        if (title.length > 255) {
            title = title.substring(0, 255)
        }
        event.title = title

        var description = binding.eventDescriptionView.text.toString()
        if (description.length > 4000) {
            description = description.substring(0, 4000)
        }
        event.description = description
        this.events.add(event)
        generateAdditionalEvents()

        for (curEvent in events) {
            fetch { (apiClient as CalendarAPI).createCalendarEvent(curEvent.toEvent()) }
        }
    }

    /**
     * adds events to the events list if more than one needs to be created
     * depending on the repeat-setting
     */
    private fun generateAdditionalEvents() {
        if (repeatHelper.isNotRepeating()) {
            return
        }
        val baseEvent = events[0]

        // event ends after n times
        if (repeatHelper.isRepeatingNTimes()) {
            for (i in 1 until repeatHelper.times) {
                events.add(CalendarItem("", baseEvent.title, CalendarItem.OTHER, baseEvent.description, baseEvent.dtstart.plusWeeks(i), baseEvent.dtend.plusWeeks(i), "", true, false))
            }
            // event ends after "last" date
        } else {
            var curDateStart = baseEvent.dtstart
            var curDateEnd = baseEvent.dtend
            repeatHelper.end!!.plusDays(1)
            while (curDateStart.isBefore(repeatHelper.end!!)) {
                curDateStart = curDateStart.plusWeeks(1)
                curDateEnd = curDateEnd.plusWeeks(1)
                events.add(CalendarItem("", baseEvent.title, CalendarItem.OTHER, baseEvent.description, curDateStart, curDateEnd, "", true, false))

            }
        }
    }

    @Synchronized
    override fun onDownloadSuccessful(response: String) {
        events[apiCallsFetched++].let {
            it.id = response
            CaDb.getInstance(this).calendarDao().insert(it)
            if (!repeatHelper.isNotRepeating() || isEditing) {
                if (repeatHelper.seriesId != null) {
                    CaDb.getInstance(this).calendarDao().insert(EventSeriesMapping(repeatHelper.seriesId!!, response))
                }
            }
        }
        // finish when all events have been created
        if (apiCallsFetched == events.size) {
            setResult(Activity.RESULT_OK)
            finish()
            return
        }
        if (apiCallsFetched + apiCallsFailed == events.size) {
            finishWithError()
            return
        }
    }

    @Synchronized
    override fun onDownloadFailure(throwable: Throwable) {
        Utils.log(throwable)

        apiCallsFailed++
        if (apiCallsFetched + apiCallsFailed == events.size) {
            finishWithError()
        }
    }

    private fun finishWithError() {
        val i = Intent()
        i.putExtra("failed", apiCallsFailed.toString())
        i.putExtra("sum", (apiCallsFetched + apiCallsFailed).toString())
        setResult(CalendarFragment.RESULT_ERROR, i)
        finish()
    }

    override fun onBackPressed() {
        hideKeyboard()

        val handled = handleOnBackPressed()
        if (handled) {
            finish()
        } else {
            displayCloseDialog()
        }
    }

    private fun showKeyboard() {
        val inputManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
    }

    private fun hideKeyboard() {
        val inputManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(binding.eventTitleView.windowToken, 0)
    }

    private fun handleOnBackPressed(): Boolean {
        with(binding) {
            val title = eventTitleView.text.toString()
            val description = eventDescriptionView.text.toString()

            // TODO: If the user is in edit mode, check whether any data was changed.
            return title.isEmpty() && description.isEmpty()
        }
    }

    private fun displayCloseDialog() {
        val dialog = AlertDialog.Builder(this)
                .setMessage(R.string.discard_changes_question)
                .setNegativeButton(R.string.discard) { dialogInterface, which -> finish() }
                .setPositiveButton(R.string.keep_editing, null)
                .create()
        dialog.window?.setBackgroundDrawableResource(R.drawable.rounded_corners_background)
        dialog.show()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showErrorDialog(message: String) {
        val dialog = AlertDialog.Builder(this)
                .setTitle(R.string.error)
                .setMessage(message)
                .setIcon(R.drawable.ic_error_outline)
                .setPositiveButton(R.string.ok, null)
                .create()
        dialog.window?.setBackgroundDrawableResource(R.drawable.rounded_corners_background)
        dialog.show()
    }

    override fun onDestroy() {
        loadingDisposable.dispose()
        super.onDestroy()
    }
}
