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
    ProjectReportVerificationFinalized;


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

        val partnerReportNotifications = sortedSetOf(
            PartnerReportSubmitted,
            PartnerReportReOpen,
            PartnerReportControlOngoing,
            PartnerReportCertified,
            PartnerReportReOpenCertified
        )

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

}
