package de.tum.`in`.tumcampusapp.component.tumui.grades.api

import de.tum.`in`.tumcampusapp.api.generic.BaseAPI
import de.tum.`in`.tumcampusapp.component.tumui.grades.model.AbstractExam

interface GradesAPI: BaseAPI {
    fun getGrades(): List<AbstractExam>
}