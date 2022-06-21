package de.tum.`in`.tumcampusapp.component.ui.news

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.Group
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import de.tum.`in`.tumcampusapp.R
import de.tum.`in`.tumcampusapp.component.ui.news.model.News
import de.tum.`in`.tumcampusapp.component.ui.news.model.NewsSources
import de.tum.`in`.tumcampusapp.component.ui.overview.CardInteractionListener
import de.tum.`in`.tumcampusapp.component.ui.overview.card.CardViewHolder
import de.tum.`in`.tumcampusapp.utils.addCompoundDrawablesWithIntrinsicBounds
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
    private val dateTextView: TextView by lazy { itemView.findViewById<TextView>(R.id.news_src_date) }
    private val sourceTextView: TextView by lazy { itemView.findViewById<TextView>(R.id.news_src_title) }
    private val ticketsIcon: ImageView? by lazy { itemView.findViewById<ImageView>(R.id.tickets_icon) }
    private val ticketsTextView: TextView? by lazy { itemView.findViewById<TextView>(R.id.tickets_available) }

    fun bind(newsItem: News, newsSource: NewsSources) = with(itemView) {
        val card = NewsCard(context = context, news = newsItem)
        currentCard = card

        val dateFormatter = DateTimeFormat.mediumDate()
        dateTextView.text = dateFormatter.print(newsItem.date)

        optionsButtonGroup.visibility = if (showOptionsButton) VISIBLE else GONE

        loadNewsSourceInformation(context, newsSource)

        bindNews(newsItem)
    }

    private fun loadNewsSourceInformation(context: Context, newsSource: NewsSources) {
        sourceTextView.text = newsSource.title

        val newsSourceIcon = newsSource.icon
        if (newsSourceIcon.isNotBlank() && newsSourceIcon != "null") {
            Picasso.get().load(newsSourceIcon).into(object : Target {
                override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                    val drawable = BitmapDrawable(context.resources, bitmap)
                    sourceTextView.addCompoundDrawablesWithIntrinsicBounds(start = drawable)
                }

                override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) = Unit

                override fun onPrepareLoad(placeHolderDrawable: Drawable?) = Unit
            })
        }
    }

    private fun bindNews(newsItem: News) {
        val imageUrl = newsItem.image
        if (imageUrl.isNotEmpty()) {
            loadNewsImage(imageUrl)
        } else {
            imageView?.visibility = GONE
        }

        val showTitle = newsItem.isNewspread.not()
        titleTextView?.visibility = if (showTitle) VISIBLE else GONE

        if (showTitle) {
            titleTextView?.text = newsItem.title
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
