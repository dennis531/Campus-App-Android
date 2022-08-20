package de.uos.campusapp.component.notifications

import de.uos.campusapp.component.notifications.model.AppNotification

/**
 * Defines that its implementor provides [AppNotification]s which will be displayed in the user's
 * notifications.
 */
interface ProvidesNotifications {

    /**
     * Returns whether the user has enabled notifications for a particular kind of notifications.
     *
     * @return Whether the user has enabled notifications for this kind
     */
    fun hasNotificationsEnabled(): Boolean

    /**
     * Returns the list of [AppNotification]s that should be displayed to the user.
     *
     * @return The list of [AppNotification]s
     */
    // fun getNotifications(): List<AppNotification>
}