package de.uos.campusapp.component.tumui.lectures.api

import de.uos.campusapp.api.generic.BaseAPI
import de.uos.campusapp.component.tumui.lectures.model.AbstractLecture
import de.uos.campusapp.component.tumui.lectures.model.LectureAppointmentInterface

interface LecturesAPI: BaseAPI {
    fun getPersonalLectures(): List<AbstractLecture>
    fun getLectureDetails(id: String): AbstractLecture
    fun getLectureAppointments(id: String): List<LectureAppointmentInterface>
    fun searchLectures(query: String): List<AbstractLecture>
}