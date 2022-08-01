package de.tum.`in`.tumcampusapp.component.ui.studyroom.api

import de.tum.`in`.tumcampusapp.api.generic.BaseAPI
import de.tum.`in`.tumcampusapp.component.ui.studyroom.model.StudyRoomGroup


interface StudyRoomAPI: BaseAPI {
    fun getStudyRoomGroups(): List<StudyRoomGroup>
}