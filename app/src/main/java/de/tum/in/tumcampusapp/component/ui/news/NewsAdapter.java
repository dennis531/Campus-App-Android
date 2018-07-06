package de.tum.in.tumcampusapp.component.ui.news;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.List;
import java.util.regex.Pattern;

import de.tum.in.tumcampusapp.R;
import de.tum.in.tumcampusapp.component.ui.news.model.News;
import de.tum.in.tumcampusapp.component.ui.news.model.NewsSources;
import de.tum.in.tumcampusapp.component.ui.overview.card.CardViewHolder;
import de.tum.in.tumcampusapp.component.ui.tufilm.FilmCard;
import de.tum.in.tumcampusapp.database.TcaDb;

public class NewsAdapter extends RecyclerView.Adapter<CardViewHolder> {
    private static final Pattern COMPILE = Pattern.compile("^[0-9]+\\. [0-9]+\\. [0-9]+:[ ]*");
    private final List<News> news;
    private final Context mContext;

    NewsAdapter(Context context, List<News> news) {
        this.mContext = context;
        this.news = news;
    }

    public static NewsViewHolder newNewsView(ViewGroup parent, boolean isFilm) {
        View card;
        if (isFilm) {
            card = LayoutInflater.from(parent.getContext())
                                 .inflate(R.layout.card_news_film_item, parent, false);
        } else {
            card = LayoutInflater.from(parent.getContext())
                                 .inflate(R.layout.card_news_item, parent, false);
        }
        NewsViewHolder holder = new NewsViewHolder(card);
        holder.imageView = card.findViewById(R.id.news_img);
        holder.titleTextView = card.findViewById(R.id.news_title);
        holder.dateTextView = card.findViewById(R.id.news_src_date);
        holder.sourceTextView = card.findViewById(R.id.news_src_title);
        card.setTag(holder);
        return holder;
    }

    public static void bindNewsView(RecyclerView.ViewHolder newsViewHolder, News news, Context context) {
        NewsViewHolder holder = (NewsViewHolder) newsViewHolder;
        NewsSourcesDao newsSourcesDao = TcaDb.getInstance(context).newsSourcesDao();
        NewsSources newsSource = newsSourcesDao.getNewsSource(Integer.parseInt(news.getSrc()));

        // Hide the image view if the news item doesn't contain an image.
        String imageUrl = news.getImage();
        holder.imageView.setVisibility(imageUrl.isEmpty() ? View.GONE : View.VISIBLE);
        if (!imageUrl.isEmpty()) {
            Picasso.get()
                    .load(imageUrl)
                    .placeholder(R.drawable.chat_background)
                    .into(holder.imageView);
        }

        // The newspread image already contains the news title. Thus, we hide the dedicated title
        // text view.
        boolean showTitle = !newsSource.isNewspread();
        holder.titleTextView.setVisibility(showTitle ? View.VISIBLE : View.GONE);
        if (showTitle) {
            String title = news.getTitle();
            if (news.isFilm()) {
                title = COMPILE.matcher(title)
                               .replaceAll("");
            }
            holder.titleTextView.setText(title);
        }

        // Adds date
        DateTime date = news.getDate();
        DateTimeFormatter sdf = DateTimeFormat.mediumDate();
        holder.dateTextView.setText(sdf.print(date));

        holder.sourceTextView.setText(newsSource.getTitle());

        String icon = newsSource.getIcon();
        if (icon.isEmpty() || "null".equals(icon)) {
            Drawable drawable = ContextCompat.getDrawable(context, R.drawable.ic_comment);
            holder.sourceTextView.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
        } else {
            Picasso.get()
                   .load(icon)
                   .into(new Target() {
                       @Override
                       public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                           Drawable drawable = new BitmapDrawable(context.getResources(), bitmap);
                           holder.sourceTextView.setCompoundDrawablesWithIntrinsicBounds(
                                   drawable, null, null, null);
                       }

                       @Override
                       public void onBitmapFailed(Exception e, Drawable errorDrawable) { }

                       @Override
                       public void onPrepareLoad(Drawable placeHolderDrawable) { }
                   });
        }
    }

    @Override
    public CardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return newNewsView(parent, viewType == 0);
    }

    @Override
    public void onBindViewHolder(CardViewHolder holder, int position) {
        NewsViewHolder nHolder = (NewsViewHolder) holder;
        NewsCard card;
        if (news.get(position).isFilm()) {
            card = new FilmCard(mContext);
        } else {
            card = new NewsCard(mContext);
        }
        card.setNews(news.get(position));
        nHolder.setCurrentCard(card);

        bindNewsView(holder, news.get(position), mContext);
    }

    @Override
    public int getItemViewType(int position) {
        return news.get(position).isFilm() ? 0 : 1;
    }

    @Override
    public int getItemCount() {
        return news.size();
    }

    private static class NewsViewHolder extends CardViewHolder {
        ImageView imageView;
        TextView titleTextView;
        TextView dateTextView;
        TextView sourceTextView;

        NewsViewHolder(View itemView) {
            super(itemView);
        }
    }
}
