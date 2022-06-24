package de.tum.`in`.tumcampusapp.component.ui.news

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import de.tum.`in`.tumcampusapp.component.ui.news.model.NewsItem
import de.tum.`in`.tumcampusapp.component.ui.overview.CardInteractionListener
import de.tum.`in`.tumcampusapp.database.TcaDb

class NewsInflater(context: Context) {

    private val newsDao: NewsDao by lazy {
        TcaDb.getInstance(context).newsDao()
    }

    @JvmOverloads
    fun onCreateNewsView(
        parent: ViewGroup,
        layoutId: Int,
        showOptionsButton: Boolean = true,
        interactionListener: CardInteractionListener? = null
    ): NewsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(layoutId, parent, false)
        return NewsViewHolder(view, interactionListener, showOptionsButton)
    }

    fun onBindNewsView(viewHolder: NewsViewHolder, newsItem: NewsItem) {
        viewHolder.bind(newsItem)
    }
}
