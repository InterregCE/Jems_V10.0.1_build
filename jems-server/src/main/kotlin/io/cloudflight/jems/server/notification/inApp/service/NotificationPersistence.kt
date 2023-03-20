package io.cloudflight.jems.server.notification.inApp.service

import io.cloudflight.jems.server.notification.inApp.service.model.Notification
import io.cloudflight.jems.server.notification.inApp.service.model.UserNotification
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface NotificationPersistence {
    fun saveNotifications(notification: Notification, recipients: Set<String>)
    fun getUserNotifications(userId: Long, pageable: Pageable): Page<UserNotification>
}
