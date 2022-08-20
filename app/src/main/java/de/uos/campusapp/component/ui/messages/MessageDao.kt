package de.uos.campusapp.component.ui.messages

import androidx.room.*
import de.uos.campusapp.component.ui.messages.model.MessageItem

@Dao
interface MessageDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(message: MessageItem)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(messages: List<MessageItem>)

    @Query("SELECT * FROM message ORDER BY date DESC")
    fun getAll(): List<MessageItem>

    @Query("SELECT * FROM message WHERE type_id=:messageTypeId ORDER BY date DESC")
    fun getAllByType(messageTypeId: Int): List<MessageItem>

    @Query("SELECT * FROM message ORDER BY date DESC LIMIT 1")
    fun getLast(): MessageItem?

    @Query("DELETE FROM message WHERE id=:messageId")
    fun delete(messageId: String)

    @Query("DELETE FROM message WHERE type_id NOT IN (:excludedTypes)")
    fun removeCache(excludedTypes: List<Int> = emptyList())
}