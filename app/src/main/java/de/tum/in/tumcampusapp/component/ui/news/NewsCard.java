package de.tum.in.tumcampusapp.component.ui.news;

import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.NotificationCompat;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;

import org.joda.time.DateTime;

import java.io.IOException;

import de.tum.in.tumcampusapp.R;
import de.tum.in.tumcampusapp.component.ui.news.model.News;
import de.tum.in.tumcampusapp.component.ui.news.model.NewsSources;
import de.tum.in.tumcampusapp.component.ui.overview.CardManager;
import de.tum.in.tumcampusapp.component.ui.overview.card.CardViewHolder;
import de.tum.in.tumcampusapp.component.ui.overview.card.NotificationAwareCard;
import de.tum.in.tumcampusapp.database.TcaDb;
import de.tum.in.tumcampusapp.utils.Utils;

/**
 * Card that shows selected news
 */
public class NewsCard extends NotificationAwareCard {

    private News mNews;

    public NewsCard(Context context) {
        this(CardManager.CARD_NEWS, context);
    }

    public NewsCard(int type, Context context) {
        super(type, context, "card_news", false);
    }

    public static CardViewHolder inflateViewHolder(ViewGroup parent, int type) {
        return NewsAdapter.newNewsView(parent, type == CardManager.CARD_NEWS_FILM);
    }

    @Override
    public int getId() {
        return Integer.parseInt(mNews.getId());
    }

    @NonNull
    @Override
    public String getTitle() {
        return mNews.getTitle();
    }

    public String getSource() {
        return mNews.getSrc();
    }

    @Override
    protected boolean shouldShowNotification(@NonNull SharedPreferences prefs) {
        return (mNews.getDismissed() & 2) == 0;
    }

    @Override
    protected Notification fillNotification(@NonNull NotificationCompat.Builder notificationBuilder) {
        NewsSourcesDao newsSourcesDao = TcaDb.getInstance(getContext()).newsSourcesDao();
        NewsSources newsSource = newsSourcesDao.getNewsSource(Integer.parseInt(mNews.getSrc()));
        notificationBuilder.setContentTitle(getContext().getString(R.string.news));
        notificationBuilder.setContentText(mNews.getTitle());
        notificationBuilder.setContentInfo(newsSource.getTitle());
        notificationBuilder.setTicker(mNews.getTitle());
        notificationBuilder.setSmallIcon(R.drawable.ic_notification);
        try {
            if(!mNews.getImage().isEmpty()){
                Bitmap bgImg = Picasso.get().load(mNews.getImage()).get();
                notificationBuilder.extend(new NotificationCompat.WearableExtender().setBackground(bgImg));
            }
        } catch (IOException e) {
            // ignore it if download fails
        }
        return notificationBuilder.build();
    }

    /**
     * Sets the information needed to show news
     *
     * @param n News object
     */
    public void setNews(News n) {
        mNews = n;
    }

    @Override
    protected void discardNotification(@NonNull SharedPreferences.Editor editor) {
        NewsController newsController = new NewsController(getContext());
        newsController.setDismissed(mNews.getId(), mNews.getDismissed() | 2);
    }

    public DateTime getDate() {
        return mNews.getDate();
    }

    @Nullable
    @Override
    public Intent getIntent() {
        // Show regular news in browser
        String url = mNews.getLink();
        if (url.isEmpty()) {
            Utils.showToast(getContext(), R.string.no_link_existing);
            return null;
        }

        // Opens url in browser
        return new Intent(Intent.ACTION_VIEW, Uri.parse(url));
    }

    @Override
    public void updateViewHolder(@NonNull RecyclerView.ViewHolder viewHolder) {
        super.updateViewHolder(viewHolder);
        NewsAdapter.bindNewsView(viewHolder, mNews, getContext());
    }

    @Override
    protected boolean shouldShow(@NonNull SharedPreferences prefs) {
        return (mNews.getDismissed() & 1) == 0;
    }

    @Override
    protected void discard(@NonNull SharedPreferences.Editor editor) {
        NewsController newsController = new NewsController(getContext());
        newsController.setDismissed(mNews.getId(), mNews.getDismissed() | 1);
    }

}
