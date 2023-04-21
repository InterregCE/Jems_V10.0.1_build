package io.cloudflight.jems.server.notification.inApp.service.model

import io.cloudflight.jems.server.project.service.application.ApplicationStatus

enum class NotificationType {
    ProjectSubmittedStep1,
    ProjectSubmitted,
    ProjectApprovedStep1,
    ProjectApprovedWithConditionsStep1,
    ProjectIneligibleStep1,
    ProjectNotApprovedStep1,
    ProjectApproved,
    ProjectApprovedWithConditions,
    ProjectIneligible,
    ProjectNotApproved,
    ProjectReturnedToApplicant,
    ProjectResubmitted,
    ProjectReturnedForConditions,
    ProjectConditionsSubmitted,
    ProjectContracted,
    ProjectInModification,
    ProjectModificationSubmitted,
    ProjectModificationApproved,
    ProjectModificationRejected;

    companion object {

        val projectNotifications = sortedSetOf(
            ProjectSubmittedStep1,
            ProjectSubmitted,
            ProjectApprovedStep1,
            ProjectApprovedWithConditionsStep1,
            ProjectIneligibleStep1,
            ProjectNotApprovedStep1,
            ProjectApproved,
            ProjectApprovedWithConditions,
            ProjectIneligible,
            ProjectNotApproved,
            ProjectReturnedToApplicant,
            ProjectResubmitted,
            ProjectReturnedForConditions,
            ProjectConditionsSubmitted,
            ProjectContracted,
            ProjectInModification,
            ProjectModificationSubmitted,
            ProjectModificationApproved,
            ProjectModificationRejected
        )

        fun ApplicationStatus.toNotificationType(prevStatus: ApplicationStatus): NotificationType? = when {
            this == ApplicationStatus.SUBMITTED && prevStatus == ApplicationStatus.DRAFT -> ProjectSubmitted
            this == ApplicationStatus.STEP1_SUBMITTED -> ProjectSubmittedStep1
            this == ApplicationStatus.STEP1_APPROVED -> ProjectApprovedStep1
            this == ApplicationStatus.STEP1_APPROVED_WITH_CONDITIONS -> ProjectApprovedWithConditionsStep1
            this == ApplicationStatus.STEP1_INELIGIBLE -> ProjectIneligibleStep1
            this == ApplicationStatus.STEP1_NOT_APPROVED -> ProjectNotApprovedStep1
            this == ApplicationStatus.APPROVED && prevStatus == ApplicationStatus.ELIGIBLE -> ProjectApproved
            this == ApplicationStatus.APPROVED_WITH_CONDITIONS -> ProjectApprovedWithConditions
            this == ApplicationStatus.INELIGIBLE -> ProjectIneligible
            this == ApplicationStatus.NOT_APPROVED -> ProjectNotApproved
            this == ApplicationStatus.RETURNED_TO_APPLICANT -> ProjectReturnedToApplicant
            this == ApplicationStatus.SUBMITTED -> ProjectResubmitted
            this == ApplicationStatus.RETURNED_TO_APPLICANT_FOR_CONDITIONS -> ProjectReturnedForConditions
            this == ApplicationStatus.CONDITIONS_SUBMITTED -> ProjectConditionsSubmitted
            this == ApplicationStatus.CONTRACTED && prevStatus == ApplicationStatus.APPROVED -> ProjectContracted
            this == ApplicationStatus.IN_MODIFICATION || this == ApplicationStatus.MODIFICATION_PRECONTRACTING -> ProjectInModification
            this == ApplicationStatus.MODIFICATION_SUBMITTED || this == ApplicationStatus.MODIFICATION_PRECONTRACTING_SUBMITTED -> ProjectModificationSubmitted
            this == ApplicationStatus.CONTRACTED && prevStatus == ApplicationStatus.MODIFICATION_SUBMITTED -> ProjectModificationApproved
            this == ApplicationStatus.APPROVED && prevStatus == ApplicationStatus.MODIFICATION_PRECONTRACTING_SUBMITTED -> ProjectModificationApproved
            this == ApplicationStatus.MODIFICATION_REJECTED -> ProjectModificationRejected
            else -> null
        }
    }

    fun isProjectNotification() = this in projectNotifications

    fun isNotProjectNotification() = !isProjectNotification()

}
