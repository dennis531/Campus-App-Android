package de.uos.campusapp.api.studip.model.news

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import com.github.jasminb.jsonapi.annotations.Id
import com.github.jasminb.jsonapi.annotations.Type
import de.uos.campusapp.component.ui.news.model.AbstractNews
import org.joda.time.DateTime

@Type("news")
class StudipNews : AbstractNews() {
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
        get() {
            if (field != null && field.startsWith("<!--HTML-->")) {
                // Remove paragraph in listings, as a line break is automatically created after the list mark
                return field.replace(REMOVE_LIST_PARAGRAPH_REGEX.toRegex(RegexOption.DOT_MATCHES_ALL), "$1$2$3")
            }
            return field
        }

    @JsonProperty("publication-start")
    private val startDate: String = ""

    @JsonIgnore
    override var date: DateTime = DateTime()
        get() = DateTime(startDate)

    companion object {
        private const val REMOVE_LIST_PARAGRAPH_REGEX = "(<li>.*?)<p>(.*?)</p>(.*?</li>)"
    }
}