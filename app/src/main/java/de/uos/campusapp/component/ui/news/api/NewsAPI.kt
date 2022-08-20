package de.uos.campusapp.component.ui.news.api

import de.uos.campusapp.api.generic.BaseAPI
import de.uos.campusapp.component.ui.news.model.AbstractNews

interface NewsAPI: BaseAPI {
    fun getNews(): List<AbstractNews>
}