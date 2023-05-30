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
    PartnerReportReOpenFromSubmitted,
    PartnerReportControlOngoing,
    PartnerReportReOpenFromControlOngoing,
    PartnerReportCertified,

    // Project Report
    ProjectReportSubmitted;


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
            PartnerReportReOpenFromSubmitted,
            PartnerReportControlOngoing,
            PartnerReportReOpenFromControlOngoing,
            PartnerReportCertified,
        )

        val projectReportNotifications = sortedSetOf(
            ProjectReportSubmitted,
        )
    }

    fun isProjectNotification() = this in projectNotifications

    fun isNotProjectNotification() = !isProjectNotification()

    fun isPartnerReportNotification() = this in partnerReportNotifications

    fun isNotPartnerReportNotification() = !isPartnerReportNotification()

    fun isProjectReportNotification() = this in projectReportNotifications

    fun isNotProjectReportNotification() = !isProjectReportNotification()

}
