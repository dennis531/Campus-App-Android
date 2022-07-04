package de.tum.`in`.tumcampusapp.component.tumui.calendar

import com.alamkanak.weekview.WeekViewDisplayable
import com.alamkanak.weekview.WeekViewEvent
import de.tum.`in`.tumcampusapp.component.tumui.calendar.model.CalendarItem
import de.tum.`in`.tumcampusapp.component.tumui.roomfinder.model.RoomFinderScheduleInterface
import org.joda.time.DateTime

/**
 * A class to represent events for the integrated WeekView calendar
 */
data class WidgetCalendarItem(
    val id: String,
    val title: String,
    val startTime: DateTime,
    val endTime: DateTime,
    val location: String
) : WeekViewDisplayable<WidgetCalendarItem> {

    var color: Int = 0

    var isFirstOnDay: Boolean = false

    override fun toWeekViewEvent(): WeekViewEvent<WidgetCalendarItem> {
        val style = WeekViewEvent.Style.Builder()
                .setBackgroundColor(color)
                .build()

        return WeekViewEvent.Builder<WidgetCalendarItem>(this)
                .setId(id.hashCode().toLong())
                .setTitle(title)
                .setStartTime(startTime.toGregorianCalendar())
                .setEndTime(endTime.toGregorianCalendar())
                .setLocation(location)
                .setAllDay(false)
                .setStyle(style)
                .build()
    }

    companion object {

        @JvmStatic
        fun create(calendarItem: CalendarItem): WidgetCalendarItem {
            return WidgetCalendarItem(
                    calendarItem.id,
                    calendarItem.title,
                    calendarItem.dtstart,
                    calendarItem.dtend,
                    calendarItem.location
            )
        }

        @JvmStatic
        fun create(schedule: RoomFinderScheduleInterface): WidgetCalendarItem {
            return WidgetCalendarItem(schedule.id, schedule.title, schedule.start, schedule.end, "")
        }
    }
}
