package de.tum.in.tumcampusapp.component.ui.news;

import java.util.List;

import androidx.annotation.Nullable;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import de.tum.in.tumcampusapp.component.ui.news.model.NewsItem;

@Dao
public interface NewsDao {

    @Query("DELETE FROM news")
    void cleanUp();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(NewsItem news);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(List<NewsItem> news);

    @Query("SELECT * FROM news ORDER BY date DESC")
    List<NewsItem> getAll();

    @Query("SELECT * FROM news WHERE date(date) > date()")
    List<NewsItem> getNewer();

    @Nullable
    @Query("SELECT * FROM news ORDER BY id DESC LIMIT 1")
    NewsItem getLast();

    @Query("UPDATE news SET dismissed=:d WHERE id=:id")
    void setDismissed(String d, String id);

    @Query("UPDATE news SET dismissed=0")
    void restoreAllNews();

    @Query("DELETE FROM news")
    void flush();
}
