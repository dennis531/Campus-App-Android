package de.uos.campusapp.component.ui.news.model

import org.joda.time.DateTime

/**
 * Represents a news
 *
 * @property id
 * @property title Message title
 * @property link Message URL link. Directs the user to this URL when clicking on the news.
 *                Only supported when `content` is empty or null because `content` is collapsable. (optional)
 * @property imageUrl (optional)
 * @property content Message text with HTML markup. Long contents are expendable. (optional)
 * @property date Date of news
 */
abstract class AbstractNews {
    abstract val id: String
    abstract val title: String
    abstract val link: String?
    abstract val imageUrl: String?
    abstract val content: String?
    abstract val date: DateTime

    open fun toNewsItem(): NewsItem {
        return NewsItem(id, title, link ?: "", imageUrl ?: "", content ?: "", date)
    }
}