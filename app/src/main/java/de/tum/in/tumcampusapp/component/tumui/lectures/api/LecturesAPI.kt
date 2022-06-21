package de.tum.`in`.tumcampusapp.component.tumui.lectures.api

import de.tum.`in`.tumcampusapp.component.tumui.lectures.model.AbstractLecture
import de.tum.`in`.tumcampusapp.component.tumui.lectures.model.LectureAppointmentInterface

interface LecturesAPI {
    fun getPersonalLectures(): List<AbstractLecture>
    fun getLectureDetails(id: String): AbstractLecture
    fun getLectureAppointments(id: String): List<LectureAppointmentInterface>
    fun searchLectures(query: String): List<AbstractLecture>
}