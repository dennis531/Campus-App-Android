package de.uos.campusapp.component.ui.studyroom.api

import de.uos.campusapp.api.generic.BaseAPI
import de.uos.campusapp.component.ui.studyroom.model.AbstractStudyRoomGroup


interface StudyRoomAPI: BaseAPI {
    fun getStudyRoomGroups(): List<AbstractStudyRoomGroup>
}