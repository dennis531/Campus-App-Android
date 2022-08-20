package de.uos.campusapp.component.ui.cafeteria

import org.joda.time.DateTime
import org.joda.time.LocalTime

data class CafeteriaNotificationTime(val weekday: DateTime, var time: LocalTime?)