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

    // Partner Report
    PartnerReportSubmitted,
    PartnerReportReOpen,
    PartnerReportControlOngoing,
    PartnerReportCertified,
    PartnerReportReOpenCertified,

    // Project Report
    ProjectReportSubmitted,
    ProjectReportVerificationOngoing,
    ProjectReportVerificationFinalized,

    // Project File
    SharedFolderFileUpload,
    SharedFolderFileDelete,
    ControlCommunicationFileUpload,
    ControlCommunicationFileDelete;


    companion object {

        val projectNotifications = setOf(
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

        val partnerReportNotifications  = setOf(
            PartnerReportSubmitted,
            PartnerReportReOpen,
            PartnerReportControlOngoing,
            PartnerReportCertified,
            PartnerReportReOpenCertified,
        )

        val projectReportNotifications = setOf(
            ProjectReportSubmitted,
            ProjectReportVerificationOngoing,
            ProjectReportVerificationFinalized
        )

        val projectFileSharedFolderNotifications = setOf(SharedFolderFileUpload, SharedFolderFileDelete)
        val projectFileControlCommunicationNotifications = setOf(ControlCommunicationFileUpload, ControlCommunicationFileDelete)

    }

    fun isProjectNotification() = this in projectNotifications

    fun isPartnerReportNotification() = this in partnerReportNotifications

    fun isProjectReportNotification() = this in projectReportNotifications


    fun isProjectFileNotification() = this in projectFileSharedFolderNotifications

    fun isPartnerReportFileNotification() = this in projectFileControlCommunicationNotifications

}
