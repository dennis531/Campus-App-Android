package de.uos.campusapp.component.ui.grades.api

import de.uos.campusapp.api.generic.BaseAPI
import de.uos.campusapp.component.ui.grades.model.AbstractExam

/**
 * Api interface for the grades component
 */
interface GradesAPI : BaseAPI {

    /**
     * Get all grades of the user from external system
     *
     * @return List of grades
     */
    fun getGrades(): List<AbstractExam>
}