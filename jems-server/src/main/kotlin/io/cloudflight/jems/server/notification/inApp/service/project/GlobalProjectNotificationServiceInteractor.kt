package io.cloudflight.jems.server.notification.inApp.service.project

import io.cloudflight.jems.server.common.model.Variable
import io.cloudflight.jems.server.notification.inApp.service.model.NotificationProjectBase
import io.cloudflight.jems.server.notification.inApp.service.model.NotificationType

interface GlobalProjectNotificationServiceInteractor {

    fun sendNotifications(
        type: NotificationType,
        project: NotificationProjectBase,
        vararg extraVariables: Variable,
    )

}
