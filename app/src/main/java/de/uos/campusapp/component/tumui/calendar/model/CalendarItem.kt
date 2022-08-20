package de.uos.campusapp.component.tumui.calendar.model

import android.content.ContentValues
import android.graphics.Color
import android.provider.CalendarContract
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.alamkanak.weekview.WeekViewDisplayable
import com.alamkanak.weekview.WeekViewEvent
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import java.util.*

enum class CalendarItemType(val typeName: String) {
    CANCELED(CalendarItem.CANCELED),
    LECTURE(CalendarItem.LECTURE),
    EXERCISE(CalendarItem.EXERCISE),
    OTHER(CalendarItem.OTHER)
}

/**
 * Entity for storing information about lecture events
 */
@Entity(tableName = "calendar")
data class CalendarItem(
    @PrimaryKey
    var id: String = "",
    var title: String = "",
    var typeName: String = LECTURE,
    var description: String = "",
    var dtstart: DateTime = DateTime(),
    var dtend: DateTime = DateTime(),
    var location: String = "",
    var isEditable: Boolean = false,
    @Ignore
    var blacklisted: Boolean = false
) : WeekViewDisplayable<CalendarItem> {

    @Ignore
    var color: Int? = null

    val type: CalendarItemType
        get() {
            return when (typeName) {
                CANCELED -> CalendarItemType.CANCELED
                LECTURE -> CalendarItemType.LECTURE
                EXERCISE -> CalendarItemType.EXERCISE
                else -> CalendarItemType.OTHER
            }
        }

    val isCanceled: Boolean
        get() = type == CalendarItemType.CANCELED

    fun getEventDateString(): String {
        val timeFormat = DateTimeFormat.forPattern("HH:mm").withLocale(Locale.US)
        val dateFormat = DateTimeFormat.forPattern("EEE, dd.MM.yyyy").withLocale(Locale.US)
        return String.format("%s %s - %s", dateFormat.print(dtstart), timeFormat.print(dtstart), timeFormat.print(dtend))
    }

    /**
     * Prepares ContentValues object with related values plugged
     */
    fun toContentValues(): ContentValues {
        val values = ContentValues()

        // Put the received values into a contentResolver to
        // transmit the to Google Calendar
        values.put(CalendarContract.Events.DTSTART, dtstart.millis)
        values.put(CalendarContract.Events.DTEND, dtend.millis)
        values.put(CalendarContract.Events.TITLE, title)
        values.put(CalendarContract.Events.DESCRIPTION, description)
        values.put(CalendarContract.Events.EVENT_LOCATION, location)
        return values
    }

    fun isSameEventButForLocation(other: CalendarItem): Boolean {
        return title == other.title &&
                dtstart == other.dtstart &&
                dtend == other.dtend
    }

    override fun toWeekViewEvent(): WeekViewEvent<CalendarItem> {
        val color = checkNotNull(color) { "No color provided for CalendarItem" }

        val backgroundColor = if (isCanceled) Color.WHITE else color
        val textColor = if (isCanceled) color else Color.WHITE
        val borderWidth = if (isCanceled) 2 else 0

        val style = WeekViewEvent.Style.Builder()
                .setBackgroundColor(backgroundColor)
                .setTextColor(textColor)
                .setTextStrikeThrough(isCanceled)
                .setBorderWidth(borderWidth)
                .setBorderColor(color)
                .build()

        return WeekViewEvent.Builder<CalendarItem>(this)
                .setId(id.hashCode().toLong())
                .setTitle(title)
                .setStartTime(dtstart.toGregorianCalendar())
                .setEndTime(dtend.toGregorianCalendar())
                .setLocation(location)
                .setStyle(style)
                .setAllDay(false)
                .build()
    }

    companion object {
        const val CANCELED = "canceled"
        const val LECTURE = "lecture"
        const val EXERCISE = "exercise"
        const val OTHER = "other"
    }
}