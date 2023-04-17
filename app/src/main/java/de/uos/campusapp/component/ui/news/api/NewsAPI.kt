package de.uos.campusapp.component.ui.news.api

import de.uos.campusapp.api.generic.BaseAPI
import de.uos.campusapp.component.ui.news.model.AbstractNews

/**
 * Api interface for the news component
 */
interface NewsAPI : BaseAPI {

    /**
     * Gets latest news from external system
     *
     * @return List of news
     */
    fun getNews(): List<AbstractNews>
}