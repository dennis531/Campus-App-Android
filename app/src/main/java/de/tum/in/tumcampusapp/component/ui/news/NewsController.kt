package de.tum.`in`.tumcampusapp.component.ui.news

import android.content.Context
import de.tum.`in`.tumcampusapp.api.generic.LMSClient
import de.tum.`in`.tumcampusapp.api.tumonline.CacheControl
import de.tum.`in`.tumcampusapp.component.notifications.NotificationScheduler
import de.tum.`in`.tumcampusapp.component.notifications.ProvidesNotifications
import de.tum.`in`.tumcampusapp.component.ui.news.api.NewsAPI
import de.tum.`in`.tumcampusapp.component.ui.news.model.NewsItem
import de.tum.`in`.tumcampusapp.component.ui.overview.card.Card
import de.tum.`in`.tumcampusapp.component.ui.overview.card.ProvidesCard
import de.tum.`in`.tumcampusapp.database.TcaDb
import de.tum.`in`.tumcampusapp.utils.Utils
import de.tum.`in`.tumcampusapp.utils.sync.SyncManager
import org.joda.time.DateTime
import java.io.IOException
import javax.inject.Inject

private const val TIME_TO_SYNC = 86400

class NewsController @Inject constructor(
    private val context: Context
) : ProvidesCard, ProvidesNotifications {

    @Inject
    lateinit var apiClient: LMSClient

    private val newsDao = TcaDb.getInstance(context).newsDao()

    /**
     * Get the index of the newest item that is older than 'now'
     *
     * @return index of the newest item that is older than 'now' - 1
     */
    val todayIndex: Int
        get() {
            val news = newsDao.getNewer()
            return if (news.isEmpty()) 0 else news.size - 1
        }

    /**
     * Download news from external interface (JSON)
     *
     * @param force True to force download over normal sync period, else false
     */
    fun downloadFromExternal(force: CacheControl) {
        val sync = SyncManager(context)
        if (force === CacheControl.USE_CACHE && !sync.needSync(this, TIME_TO_SYNC)) {
            return
        }

        val latestNews = newsDao.last
        val latestNewsDate = latestNews?.date ?: DateTime.now()

        // Delete all items
        newsDao.cleanUp()

        // Load all news since the last sync
        try {
            val news = (apiClient as NewsAPI).getNews()
                .map { it.toNewsItem() }
            newsDao.insert(news)
            showNewsNotification(news, latestNewsDate)
        } catch (e: IOException) {
            Utils.log(e)
            return
        }

        // Finish sync
        sync.replaceIntoDb(this)
    }

    private fun showNewsNotification(news: List<NewsItem>, latestNewsDate: DateTime) {
        if (!hasNotificationsEnabled()) {
            return
        }

        val newNews = news.filter { it.date.isAfter(latestNewsDate) }

        if (newNews.isEmpty()) {
            return
        }

        val provider = NewsNotificationProvider(context, newNews)
        val notification = provider.buildNotification()

        if (notification != null) {
            val scheduler = NotificationScheduler(context)
            scheduler.schedule(notification)
        }
    }

    /**
     * Get all news from the database
     *
     * @return List of News
     */
    fun getAllFromDb(context: Context): List<NewsItem> {
        return newsDao.getAll()
    }

    private fun getLastId(): String {
        return newsDao.last?.id ?: ""
    }

    fun setDismissed(id: String, d: Int) {
        newsDao.setDismissed(d.toString(), id)
    }

    override fun getCards(cacheControl: CacheControl): List<Card> {
        val news = if (Utils.getSettingBool(context, "card_news_latest_only", true)) {
            listOf(newsDao.getLast())
        } else {
            newsDao.getAll()
        }

        return news
            .filterNotNull()
            .map { item ->  NewsCard(context, item) }
            .mapNotNull { it.getIfShowOnStart() }
    }

    override fun hasNotificationsEnabled(): Boolean {
        return Utils.getSettingBool(context, "card_news_phone", false)
    }
}
