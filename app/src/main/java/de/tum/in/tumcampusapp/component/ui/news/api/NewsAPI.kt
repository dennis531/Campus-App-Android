package de.tum.`in`.tumcampusapp.component.ui.news.api

import de.tum.`in`.tumcampusapp.api.generic.BaseAPI
import de.tum.`in`.tumcampusapp.component.ui.news.model.AbstractNews

interface NewsAPI: BaseAPI {
    fun getNews(): List<AbstractNews>
}