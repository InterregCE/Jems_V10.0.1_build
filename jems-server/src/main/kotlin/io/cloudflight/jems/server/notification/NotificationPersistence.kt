package io.cloudflight.jems.server.notification

import io.cloudflight.jems.server.notification.model.Notification

interface NotificationPersistence {
    fun saveNotification(projectId: Long, notifications: List<Notification>)
}
