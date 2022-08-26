package de.uos.campusapp.component.ui.studyroom

import android.content.Context
import de.uos.campusapp.component.ui.studyroom.model.StudyRoomItem
import de.uos.campusapp.component.ui.studyroom.model.StudyRoomGroupItem
import de.uos.campusapp.database.TcaDb
import org.jetbrains.anko.doAsync

/**
 * Handles content for the study room feature, fetches external data.
 */
class StudyRoomGroupManager(context: Context) {

    private val roomsDao: StudyRoomDao
    private val groupsDao: StudyRoomGroupDao

    init {
        val db = TcaDb.getInstance(context)
        roomsDao = db.studyRoomDao()
        groupsDao = db.studyRoomGroupDao()
    }

    fun updateDatabase(groups: List<StudyRoomGroupItem>, callback: () -> Unit) {
        doAsync {
            groupsDao.removeCache()
            roomsDao.removeCache()

            groupsDao.insert(*groups.toTypedArray())

            groups.forEach { group ->
                group.rooms.forEach { room ->
                    // only insert rooms that have data
                    if (room.name.isNotEmpty() &&
                        room.id.isNotEmpty()
                    ) {
                        roomsDao.insert(room)
                    }
                }
            }

            callback()
        }
    }

    fun getAllStudyRoomsForGroup(groupId: String): List<StudyRoomItem> {
        return roomsDao.getAll(groupId).sorted()
    }
}
