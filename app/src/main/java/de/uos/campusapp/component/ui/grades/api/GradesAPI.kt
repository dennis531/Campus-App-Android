package de.uos.campusapp.component.ui.grades.api

import de.uos.campusapp.api.generic.BaseAPI
import de.uos.campusapp.component.ui.grades.model.AbstractExam

interface GradesAPI: BaseAPI {
    fun getGrades(): List<AbstractExam>
}