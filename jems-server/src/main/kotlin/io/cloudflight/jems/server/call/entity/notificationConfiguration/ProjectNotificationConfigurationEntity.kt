package io.cloudflight.jems.server.call.entity.notificationConfiguration

import javax.persistence.EmbeddedId
import javax.persistence.Entity
import javax.validation.constraints.NotNull

@Entity(name = "project_call_project_notification_configuration")
class ProjectNotificationConfigurationEntity (
    @EmbeddedId
    val id: ProjectNotificationConfigurationId,

    @field:NotNull
    var active: Boolean,

    @field:NotNull
    var sendToManager: Boolean,

    @field:NotNull
    var sendToLeadPartner: Boolean,

    @field:NotNull
    var sendToProjectPartners: Boolean,

    @field:NotNull
    var sendToProjectAssigned: Boolean,

    @field:NotNull
    var sendToControllers: Boolean,

    @field:NotNull
    var emailSubject: String,

    @field:NotNull
    var emailBody: String,
)
