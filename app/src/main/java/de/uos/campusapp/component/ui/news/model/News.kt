package de.uos.campusapp.component.ui.news.model

import org.joda.time.DateTime

/**
 * Simple implementation of [AbstractNews]
 */
data class News(
    override val id: String,
    override val title: String,
    override val link: String?,
    override val imageUrl: String?,
    override val content: String?,
    override val date: DateTime
) : AbstractNews()
