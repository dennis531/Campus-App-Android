package de.tum.`in`.tumcampusapp.api.studip.model.news

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import com.github.jasminb.jsonapi.annotations.Id
import com.github.jasminb.jsonapi.annotations.Type
import de.tum.`in`.tumcampusapp.component.ui.news.model.AbstractNews
import org.joda.time.DateTime

@Type("news")
class StudipNews: AbstractNews() {
    @Id
    override val id: String = ""

    @JsonProperty("title")
    override val title: String = ""

    @JsonIgnore
    override val link: String? = null

    @JsonIgnore
    override val imageUrl: String? = null

    @JsonProperty("content")
    override val content: String? = null

    @JsonProperty("publication-start")
    private val startDate: String = ""

    @JsonIgnore
    override var date: DateTime = DateTime()
        get() = DateTime(startDate)
}