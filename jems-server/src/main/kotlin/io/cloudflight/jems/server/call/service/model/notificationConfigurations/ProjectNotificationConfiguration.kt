package io.cloudflight.jems.server.call.service.model.notificationConfigurations

import io.cloudflight.jems.server.notification.inApp.service.model.NotificationType

data class ProjectNotificationConfiguration(
    val id: NotificationType,
    val active: Boolean,
    val sendToManager: Boolean,
    val sendToLeadPartner: Boolean,
    val sendToProjectPartners: Boolean,
    val sendToProjectAssigned: Boolean,
    val sendToControllers: Boolean,
    val emailSubject: String = "",
    val emailBody: String = ""
) {
    constructor(id: NotificationType) : this(id, false, false, false, false, false, false)
}
