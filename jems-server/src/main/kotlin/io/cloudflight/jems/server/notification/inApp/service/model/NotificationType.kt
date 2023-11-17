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
    ProjectReportReOpen,
    ProjectReportVerificationOngoing,
    ProjectReportVerificationDoneNotificationSent,
    ProjectReportVerificationFinalized,
    ProjectReportVerificationReOpen,

    // Project File
    SharedFolderFileUpload,
    SharedFolderFileDelete,
    ControlCommunicationFileUpload,
    ControlCommunicationFileDelete,
    ProjectReportVerificationFileUpload,
    ProjectReportVerificationFileDelete,

    // System Message
    SystemMessage;

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
            ProjectReportReOpen,
            ProjectReportVerificationOngoing,
            ProjectReportVerificationDoneNotificationSent,
            ProjectReportVerificationFinalized,
            ProjectReportVerificationReOpen,
        )

        val projectFileSharedFolderNotifications = setOf(SharedFolderFileUpload, SharedFolderFileDelete)
        val partnerReportFileControlCommunicationNotifications = setOf(ControlCommunicationFileUpload, ControlCommunicationFileDelete)

        val projectFileVerificationCommunicationNotifications = setOf(ProjectReportVerificationFileUpload, ProjectReportVerificationFileDelete)
    }

    fun isProjectNotification() = this in projectNotifications

    fun isPartnerReportNotification() = this in partnerReportNotifications

    fun isProjectReportNotification() = this in projectReportNotifications


    fun isProjectFileNotification() = this in projectFileSharedFolderNotifications

    fun isPartnerReportFileNotification() = this in partnerReportFileControlCommunicationNotifications

    fun isProjectReportFileNotification() = this in projectFileVerificationCommunicationNotifications


    fun isSystemMessage() = this == SystemMessage

}
