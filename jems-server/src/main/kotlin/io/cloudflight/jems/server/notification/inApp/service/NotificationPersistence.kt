package io.cloudflight.jems.server.notification.inApp.service

import io.cloudflight.jems.server.notification.inApp.service.model.NotificationInApp
import io.cloudflight.jems.server.notification.inApp.service.model.UserNotification
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface NotificationPersistence {
    fun saveNotification(notification: NotificationInApp)
    fun getUserNotifications(userId: Long, pageable: Pageable): Page<UserNotification>
}
