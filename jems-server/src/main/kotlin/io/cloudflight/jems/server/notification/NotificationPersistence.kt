package io.cloudflight.jems.server.notification

import io.cloudflight.jems.server.notification.model.Notification

interface NotificationPersistence {
    fun saveNotifications(notifications: List<Notification>)
}
