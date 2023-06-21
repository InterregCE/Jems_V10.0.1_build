package io.cloudflight.jems.server.notification.inApp.service.project

import io.cloudflight.jems.server.notification.inApp.service.model.NotificationType
import io.cloudflight.jems.server.notification.inApp.service.model.NotificationVariable

interface GlobalProjectNotificationServiceInteractor {

    fun sendNotifications(type: NotificationType, variables: Map<NotificationVariable, Any>)
}
