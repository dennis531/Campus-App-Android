package de.uos.campusapp.component.ui.studyroom.api

import de.uos.campusapp.api.generic.BaseAPI
import de.uos.campusapp.component.ui.studyroom.model.AbstractStudyRoomGroup

/**
 * Api interface for the study room component
 */
interface StudyRoomAPI: BaseAPI {

    /**
     * Get room groups, e.g. library, containing study rooms
     *
     * @return List of study room groups
     */
    fun getStudyRoomGroups(): List<AbstractStudyRoomGroup>
}