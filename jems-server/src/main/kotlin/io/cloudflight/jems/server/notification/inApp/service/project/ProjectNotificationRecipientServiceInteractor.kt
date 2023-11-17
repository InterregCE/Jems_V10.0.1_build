package io.cloudflight.jems.server.notification.inApp.service.project

import io.cloudflight.jems.server.call.service.model.notificationConfigurations.ProjectNotificationConfiguration
import io.cloudflight.jems.server.user.service.model.UserEmailNotification

interface ProjectNotificationRecipientServiceInteractor {

    fun getEmailsForProjectManagersAndAssignedUsers(
        notificationConfig: ProjectNotificationConfiguration,
        projectId: Long,
    ): Map<String, UserEmailNotification>

    fun getEmailsForPartners(
        notificationConfig: ProjectNotificationConfiguration,
        projectId: Long,
    ): Map<String, UserEmailNotification>

    fun getEmailsForSpecificPartner(
        notificationConfig: ProjectNotificationConfiguration,
        projectId: Long,
        partnerId: Long,
    ): Map<String, UserEmailNotification>

    fun getEmailsForPartnerControllers(
        notificationConfig: ProjectNotificationConfiguration,
        partnerId: Long,
    ): Map<String, UserEmailNotification>

    fun getSystemAdminEmails(): Map<String, UserEmailNotification>

}
