package de.uos.campusapp.component.tumui.lectures.api

import de.uos.campusapp.api.generic.BaseAPI
import de.uos.campusapp.component.tumui.lectures.model.FileInterface
import de.uos.campusapp.component.tumui.lectures.model.AbstractLecture
import de.uos.campusapp.component.tumui.lectures.model.LectureAppointmentInterface
import java.io.InputStream

interface LecturesAPI : BaseAPI {
    fun getPersonalLectures(): List<AbstractLecture>
    fun searchLectures(query: String): List<AbstractLecture>
    fun getLectureDetails(id: String): AbstractLecture
    fun getLectureAppointments(id: String): List<LectureAppointmentInterface>

    /**
     * Returns a list of files containing file details
     *
     * Needed if `LECTURES_SHOW_FILES` is true in configurations; otherwise return null or empty list
     */
    fun getLectureFiles(id: String): List<FileInterface>?

    /**
     * Provides an [InputStream] to the file content
     *
     * Needed if `LECTURES_SHOW_FILES` is true in configurations; otherwise return null
     */
    fun downloadLectureFile(file: FileInterface): InputStream?

    /**
     * Provides an url to the lecture recordings to the given lecture. In the lecture component, the user will be directed
     * to the url in the browser.
     *
     * Needed if `LECTURES_SHOW_RECORDS` is true in configuration; otherwise return null or empty string
     *
     * @param id lecture id
     * @return Url to the lecture recordings, e.g. "https://studip.de/plugins.php/opencast/course/index?cid={course-id}"
     */
    fun getLectureRecordingsUrl(id: String): String?
}
