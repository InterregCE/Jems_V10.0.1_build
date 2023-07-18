package io.cloudflight.jems.server.notification.inApp.service.model

enum class NotificationType {
    // Project
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
    ProjectModificationRejected,
    SharedFolderFileUpload,
    SharedFolderFileDelete,

    // Partner Report
    PartnerReportSubmitted,
    PartnerReportReOpen,
    PartnerReportControlOngoing,
    PartnerReportCertified,
    PartnerReportReOpenCertified,
    ControlCommunicationFileUpload,
    ControlCommunicationFileDelete,

    // Project Report
    ProjectReportSubmitted,
    ProjectReportVerificationOngoing,
    ProjectReportVerificationFinalized;


    companion object {

        val projectStatusNotifications = sortedSetOf(
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
            ProjectModificationRejected,
        )

        val projectFileActionNotifications = sortedSetOf(
            SharedFolderFileUpload,
            SharedFolderFileDelete
        )

        val projectNotifications = projectStatusNotifications union projectFileActionNotifications

        val partnerReportStatusNotifications  = sortedSetOf(
            PartnerReportSubmitted,
            PartnerReportReOpen,
            PartnerReportControlOngoing,
            PartnerReportCertified,
            PartnerReportReOpenCertified,
        )

        val partnerReportFileActionNotifications = sortedSetOf(
            ControlCommunicationFileUpload,
            ControlCommunicationFileDelete
        )

        val partnerReportNotifications = partnerReportStatusNotifications union partnerReportFileActionNotifications

        val projectReportNotifications = sortedSetOf(
            ProjectReportSubmitted,
            ProjectReportVerificationOngoing,
            ProjectReportVerificationFinalized
        )

    }

    fun isProjectNotification() = this in projectNotifications

    fun isNotProjectNotification() = !isProjectNotification()

    fun isPartnerReportNotification() = this in partnerReportNotifications

    fun isNotPartnerReportNotification() = !isPartnerReportNotification()

    fun isProjectReportNotification() = this in projectReportNotifications

    fun isNotProjectReportNotification() = !isProjectReportNotification()

    fun isProjectStatusNotification() = this in projectStatusNotifications

    fun isProjectFileActionNotification() = this in projectFileActionNotifications

    fun isPartnerReportStatusNotification() = this in partnerReportStatusNotifications

    fun isPartnerReportFileActionNotification() = this in partnerReportFileActionNotifications

}
