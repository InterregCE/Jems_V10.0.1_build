package io.cloudflight.jems.api.notification.dto

enum class NotificationTypeDTO {
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
}
