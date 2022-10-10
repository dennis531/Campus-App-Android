package de.uos.campusapp.component.ui.lectures.api

import de.uos.campusapp.api.generic.BaseAPI
import de.uos.campusapp.component.ui.lectures.model.FileInterface
import de.uos.campusapp.component.ui.lectures.model.AbstractLecture
import de.uos.campusapp.component.ui.lectures.model.LectureAppointmentInterface
import de.uos.campusapp.utils.ConfigConst
import java.io.InputStream

/**
 * Api interface for the lectures component
 */
interface LecturesAPI : BaseAPI {

    /**
     * Gets all enrolled lectures of the user
     *
     * @return List of lectures
     */
    fun getPersonalLectures(): List<AbstractLecture>

    /**
     * Search for a lectures in the external system
     *
     * @param query Search string
     * @return List of found lectures
     */
    fun searchLectures(query: String): List<AbstractLecture>

    /**
     * Get details of a lecture
     *
     * @param id Lecture id
     * @return Lecture
     */
    fun getLectureDetails(id: String): AbstractLecture

    /**
     * Get appointments of a lecture
     *
     * @param id lecture id
     * @return List of lecture appointments
     */
    fun getLectureAppointments(id: String): List<LectureAppointmentInterface>

    /**
     * Gets files containing file details of a lecture from an external system
     *
     * Only required if config option [ConfigConst.LECTURES_SHOW_FILES] is set to [true] in config.
     * Otherwise simply return [null] or empty list.
     *
     * @param id Lecture id
     * @return List of files
     */
    fun getLectureFiles(id: String): List<FileInterface>?

    /**
     * Provides an [InputStream] to the file content
     *
     * Only required if config option [ConfigConst.LECTURES_SHOW_FILES] is set to [true] in config.
     * Otherwise simply return [null].
     *
     * @param file File to be downloaded
     * @return Input stream from file content
     */
    fun downloadLectureFile(file: FileInterface): InputStream?

    /**
     * Provides an url to the lecture recordings of the given lecture. In the lecture component, the user will be directed
     * to the url in the browser.
     *
     * Only required if config option [ConfigConst.LECTURES_SHOW_RECORDS] is set to [true] in config.
     * Otherwise simply return [null] or empty [String].
     *
     * @param id lecture id
     * @return Url to the lecture recordings, e.g. "https://studip.de/plugins.php/opencast/course/index?cid={course-id}"
     */
    fun getLectureRecordingsUrl(id: String): String?
}
