package de.uos.campusapp.component.ui.cafeteria.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import de.uos.campusapp.R
import de.uos.campusapp.component.other.generic.activity.BaseActivity
import de.uos.campusapp.component.ui.cafeteria.CafeteriaNotificationSettings
import de.uos.campusapp.component.ui.cafeteria.CafeteriaNotificationSettingsAdapter
import de.uos.campusapp.component.ui.cafeteria.CafeteriaNotificationTime
import de.uos.campusapp.databinding.ActivityCafeteriaNotificationSettingsBinding
import de.uos.campusapp.utils.Component
import org.joda.time.DateTime
import org.joda.time.DateTimeConstants

/**
 * This activity enables the user to set a preferred notification time for a day of the week.
 * The actual local storage of the preferences is done in the CafeteriaNotificationSettings class.
 */
class CafeteriaNotificationSettingsActivity : BaseActivity(
    R.layout.activity_cafeteria_notification_settings,
    Component.CAFETERIA
) {

    private lateinit var binding: ActivityCafeteriaNotificationSettingsBinding

    override fun onCreateView(inflater: LayoutInflater, savedInstanceState: Bundle?): View? {
        binding = ActivityCafeteriaNotificationSettingsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        binding.notificationSettingsRecyclerView.layoutManager = layoutManager
        binding.notificationSettingsRecyclerView.setHasFixedSize(true)

        val notificationSettings = CafeteriaNotificationSettings.getInstance(this)
        val dailySchedule = buildDailySchedule(notificationSettings)

        val adapter = CafeteriaNotificationSettingsAdapter(this, dailySchedule)
        binding.notificationSettingsRecyclerView.adapter = adapter

        binding.notificationSettingsSaveButton.setOnClickListener {
            notificationSettings.saveEntireSchedule(dailySchedule)
            finish()
        }
    }

    /**
     * Reloads the settings into the dailySchedule list.
     */
    private fun buildDailySchedule(
        settings: CafeteriaNotificationSettings
    ): List<CafeteriaNotificationTime> {
        return (DateTimeConstants.MONDAY until DateTimeConstants.SATURDAY)
                .map {
                    val day = DateTime.now().withDayOfWeek(it)
                    val time = settings.retrieveLocalTime(day)
                    CafeteriaNotificationTime(day, time)
                }
                .toList()
    }
}
