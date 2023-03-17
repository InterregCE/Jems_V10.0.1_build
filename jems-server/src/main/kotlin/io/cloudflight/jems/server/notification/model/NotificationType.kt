package io.cloudflight.jems.server.notification.model

import io.cloudflight.jems.server.project.service.application.ApplicationStatus

enum class NotificationType {
    ProjectSubmittedStep1,
    ProjectSubmitted;

    companion object {

        val projectNotifications = sortedSetOf(
            ProjectSubmittedStep1,
            ProjectSubmitted,
        )

        fun ApplicationStatus.toNotificationType(): NotificationType? = when (this) {
            ApplicationStatus.SUBMITTED -> ProjectSubmitted
            ApplicationStatus.STEP1_SUBMITTED -> ProjectSubmittedStep1
            else -> null
        }
    }

    fun isProjectNotification() = this in projectNotifications
    fun isNotProjectNotification() = !isProjectNotification()

}
