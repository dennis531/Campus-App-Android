package de.uos.campusapp.component.ui.news.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.RoomWarnings
import android.content.Context
import android.content.Intent
import android.net.Uri
import org.joda.time.DateTime

/**
 * New News
 *
 * @param id News Facebook-ID
 * @param title Title
 * @param link Url, e.g. http://www.in.tum.de
 * @param imageUrl Image url e.g. http://www.tu-film.de/img/film/poster/Fack%20ju%20Ghte.jpg
 * @param date Date
 * @param created Creation date
 */
@Entity(tableName = "news")
@SuppressWarnings(RoomWarnings.DEFAULT_CONSTRUCTOR)
data class NewsItem(
    @PrimaryKey
    var id: String = "",
    var title: String = "",
    var link: String = "",
    var imageUrl: String = "",
    val content: String = "",
    var date: DateTime = DateTime(),
    var dismissed: Int = 0
) {

    fun getIntent(context: Context): Intent? {
        return if (link.isBlank()) null else Intent(Intent.ACTION_VIEW, Uri.parse(link))
    }
}