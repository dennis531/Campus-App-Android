package de.tum.`in`.tumcampusapp.api.studip.model.lectures

import com.github.jasminb.jsonapi.annotations.Id
import com.github.jasminb.jsonapi.annotations.Type

@Type("semesters")
class StudipSemester {
    @Id
    val id: String = ""
    val title: String = ""
    val description: String = ""
    val start: String = ""
    val end: String = ""
}