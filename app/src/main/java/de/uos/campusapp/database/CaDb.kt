package de.uos.campusapp.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.work.WorkManager
import de.uos.campusapp.component.notifications.persistence.ActiveAlarm
import de.uos.campusapp.component.notifications.persistence.ActiveAlarmsDao
import de.uos.campusapp.component.notifications.persistence.ScheduledNotification
import de.uos.campusapp.component.notifications.persistence.ScheduledNotificationsDao
import de.uos.campusapp.component.other.general.RecentsDao
import de.uos.campusapp.component.other.general.model.Recent
import de.uos.campusapp.component.other.locations.BuildingToGpsDao
import de.uos.campusapp.component.other.locations.RoomLocationsDao
import de.uos.campusapp.component.other.locations.model.BuildingToGps
import de.uos.campusapp.component.ui.calendar.CalendarDao
import de.uos.campusapp.component.ui.calendar.WidgetsTimetableBlacklistDao
import de.uos.campusapp.component.ui.calendar.model.CalendarItem
import de.uos.campusapp.component.ui.calendar.model.EventSeriesMapping
import de.uos.campusapp.component.ui.calendar.model.WidgetsTimetableBlacklist
import de.uos.campusapp.component.other.locations.model.RoomLocations
import de.uos.campusapp.component.ui.cafeteria.CafeteriaDao
import de.uos.campusapp.component.ui.cafeteria.CafeteriaMenuDao
import de.uos.campusapp.component.ui.cafeteria.CafeteriaMenuPriceDao
import de.uos.campusapp.component.ui.cafeteria.FavoriteDishDao
import de.uos.campusapp.component.ui.cafeteria.model.database.CafeteriaItem
import de.uos.campusapp.component.ui.cafeteria.model.database.CafeteriaMenuItem
import de.uos.campusapp.component.ui.cafeteria.model.database.CafeteriaMenuPriceItem
import de.uos.campusapp.component.ui.cafeteria.model.database.FavoriteDish
import de.uos.campusapp.component.ui.chat.ChatMessageDao
import de.uos.campusapp.component.ui.chat.ChatRoomDao
import de.uos.campusapp.component.ui.chat.model.ChatMessageItem
import de.uos.campusapp.component.ui.chat.model.ChatRoomDbRow
import de.uos.campusapp.component.ui.messages.MessageDao
import de.uos.campusapp.component.ui.messages.model.MessageItem
import de.uos.campusapp.component.ui.news.NewsDao
import de.uos.campusapp.component.ui.news.model.NewsItem
import de.uos.campusapp.component.ui.openinghours.LocationDao
import de.uos.campusapp.component.ui.openinghours.model.LocationItem
import de.uos.campusapp.component.ui.studyroom.StudyRoomDao
import de.uos.campusapp.component.ui.studyroom.StudyRoomGroupDao
import de.uos.campusapp.component.ui.studyroom.model.StudyRoomItem
import de.uos.campusapp.component.ui.studyroom.model.StudyRoomGroupItem
import de.uos.campusapp.component.ui.transportation.TransportDao
import de.uos.campusapp.component.ui.transportation.model.TransportFavorites
import de.uos.campusapp.component.ui.transportation.model.WidgetsTransport
import de.uos.campusapp.utils.CacheManager
import de.uos.campusapp.utils.Const
import de.uos.campusapp.utils.sync.SyncDao
import de.uos.campusapp.utils.sync.model.Sync
import java.util.concurrent.ExecutionException

@Database(version = 1, entities = [
    CafeteriaItem::class,
    CafeteriaMenuItem::class,
    CafeteriaMenuPriceItem::class,
    FavoriteDish::class,
    Sync::class,
    BuildingToGps::class,
    ChatMessageItem::class,
    LocationItem::class,
    NewsItem::class,
    MessageItem::class,
    CalendarItem::class,
    EventSeriesMapping::class,
    RoomLocations::class,
    WidgetsTimetableBlacklist::class,
    Recent::class,
    StudyRoomGroupItem::class,
    StudyRoomItem::class,
    TransportFavorites::class,
    WidgetsTransport::class,
    ChatRoomDbRow::class,
    ScheduledNotification::class,
    ActiveAlarm::class])
@TypeConverters(Converters::class)
abstract class CaDb : RoomDatabase() {

    abstract fun cafeteriaDao(): CafeteriaDao

    abstract fun cafeteriaMenuDao(): CafeteriaMenuDao

    abstract fun cafeteriaMenuPriceDao(): CafeteriaMenuPriceDao

    abstract fun favoriteDishDao(): FavoriteDishDao

    abstract fun syncDao(): SyncDao

    abstract fun buildingToGpsDao(): BuildingToGpsDao

    abstract fun locationDao(): LocationDao

    abstract fun chatMessageDao(): ChatMessageDao

    abstract fun newsDao(): NewsDao

    abstract fun messageDao(): MessageDao

    abstract fun calendarDao(): CalendarDao

    abstract fun roomLocationsDao(): RoomLocationsDao

    abstract fun widgetsTimetableBlacklistDao(): WidgetsTimetableBlacklistDao

    abstract fun recentsDao(): RecentsDao

    abstract fun studyRoomGroupDao(): StudyRoomGroupDao

    abstract fun studyRoomDao(): StudyRoomDao

    abstract fun transportDao(): TransportDao

    abstract fun chatRoomDao(): ChatRoomDao

    abstract fun scheduledNotificationsDao(): ScheduledNotificationsDao

    abstract fun activeNotificationsDao(): ActiveAlarmsDao

    companion object {
//        private val migrations = arrayOf(
//                Migration1to2(),
//                Migration2to3(),
//                Migration3to4(),
//                Migration4to5(),
//                Migration5to6(),
//                Migration6to7()
//        )

        private var instance: CaDb? = null

        @Synchronized
        fun getInstance(context: Context): CaDb {
            var instance = this.instance
            if (instance == null) {
                instance = Room.databaseBuilder(context.applicationContext, CaDb::class.java, Const.DATABASE_NAME)
                        .allowMainThreadQueries()
//                        .addMigrations(*migrations)
                        .build()
                this.instance = instance
            }
            return instance
        }

        /**
         * Drop all tables, so we can do a complete clean start
         * Careful: After executing this method, almost all the managers are in an illegal state, and
         * can't do any SQL anymore. So take care to actually reinitialize all Managers
         *
         * @param c context
         */
        @Throws(ExecutionException::class, InterruptedException::class)
        fun resetDb(c: Context) {
            // Stop all work tasks in WorkManager, since they might access the DB
            WorkManager.getInstance().cancelAllWork().result.get()

            // Clear our cache table
            val cacheManager = CacheManager(c)
            cacheManager.clearCache()

            CaDb.getInstance(c).clearAllTables()
        }
    }
}
