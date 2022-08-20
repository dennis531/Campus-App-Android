package de.uos.campusapp.component.ui.news.model

import org.joda.time.DateTime

abstract class AbstractNews {
    abstract val id: String
    abstract val title: String
    abstract val link: String?
    abstract val imageUrl: String?
    abstract val content: String? // HTML Text allowed
    abstract val date: DateTime

    open fun toNewsItem(): NewsItem {
        return NewsItem(id, title, link ?: "", imageUrl ?: "", content ?: "", date)
    }
}