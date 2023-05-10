package io.cloudflight.jems.server.notification.inApp.service.project

import io.cloudflight.jems.server.call.service.model.notificationConfigurations.ProjectNotificationConfiguration
import io.cloudflight.jems.server.user.service.model.UserEmailNotification

interface ProjectNotificationRecipientServiceInteractor {

    fun getEmailsForProjectNotification(
        notificationConfig: ProjectNotificationConfiguration,
        projectId: Long,
    ): Map<String, UserEmailNotification>

    fun getEmailsForPartnerNotification(
        notificationConfig: ProjectNotificationConfiguration,
        partnerId: Long,
    ): Map<String, UserEmailNotification>

}
