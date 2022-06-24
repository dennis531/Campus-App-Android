package de.tum.`in`.tumcampusapp.component.ui.news

import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.Group
import androidx.core.view.isVisible
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import de.tum.`in`.tumcampusapp.R
import de.tum.`in`.tumcampusapp.component.ui.news.model.NewsItem
import de.tum.`in`.tumcampusapp.component.ui.overview.CardInteractionListener
import de.tum.`in`.tumcampusapp.component.ui.overview.card.CardViewHolder
import de.tum.`in`.tumcampusapp.utils.Utils
import org.joda.time.format.DateTimeFormat
import java.util.regex.Pattern

class NewsViewHolder(
    itemView: View,
    interactionListener: CardInteractionListener?,
    private val showOptionsButton: Boolean = true
) : CardViewHolder(itemView, interactionListener) {

    private val optionsButtonGroup: Group by lazy { itemView.findViewById<Group>(R.id.cardMoreIconGroup) }
    private val imageView: ImageView? by lazy { itemView.findViewById<ImageView>(R.id.news_img) }
    private val titleTextView: TextView? by lazy { itemView.findViewById<TextView>(R.id.news_title) }
    private val dateTextView: TextView by lazy { itemView.findViewById<TextView>(R.id.news_date) }
    private val contentTextView: TextView by lazy { itemView.findViewById<TextView>(R.id.news_content) }

    fun bind(newsItem: NewsItem) = with(itemView) {
        val card = NewsCard(context = context, news = newsItem)
        currentCard = card

        val dateFormatter = DateTimeFormat.mediumDate()
        dateTextView.text = dateFormatter.print(newsItem.date)

        optionsButtonGroup.visibility = if (showOptionsButton) VISIBLE else GONE

        bindNews(newsItem)
    }

    private fun bindNews(newsItem: NewsItem) {
        val imageUrl = newsItem.imageUrl
        if (imageUrl.isNotBlank()) {
            loadNewsImage(imageUrl)
        } else {
            imageView?.visibility = GONE
        }

        titleTextView?.text = newsItem.title

        contentTextView.isVisible = newsItem.content.isNotBlank()
        if (newsItem.content.isNotBlank()) {
            contentTextView.text = Utils.fromHtml(newsItem.content)
        }
    }

    private fun loadNewsImage(url: String) {
        Picasso.get()
                .load(url)
                .into(imageView, object : Callback {
                    override fun onSuccess() = Unit

                    override fun onError(e: Exception?) {
                        imageView?.visibility = GONE
                    }
                })
    }

    companion object {
        private val COMPILE = Pattern.compile("^[0-9]+\\. [0-9]+\\. [0-9]+:[ ]*")
    }
}
