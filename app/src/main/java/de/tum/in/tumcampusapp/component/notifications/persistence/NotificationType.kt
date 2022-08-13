package de.tum.`in`.tumcampusapp.component.notifications.persistence

enum class NotificationType(val id: Int) {

    CAFETERIA(0),
    CALENDAR(1),
    CHAT(2),
    NEWS(3),
    TRANSPORT(4),
    TUITION_FEES(5),
    GRADES(6),
    MESSAGES(7);

    companion object {
        private val map = NotificationType.values().associateBy(NotificationType::id)
        fun fromId(id: Long) = map[id.toInt()]
    }
}