package io.cloudflight.jems.server.notification.handler

import io.cloudflight.jems.server.notification.inApp.service.model.NotificationType
import io.cloudflight.jems.server.notification.inApp.service.model.NotificationType.*
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.project.service.report.model.partner.ReportStatus

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
}.enforceIsProjectNotification()

fun NotificationType?.enforceIsProjectNotification() = if (this != null && isProjectNotification()) this else null

fun ReportStatus.toNotificationType(): NotificationType? = when(this) {
    ReportStatus.Submitted -> PartnerReportSubmitted
    ReportStatus.InControl -> PartnerReportControlOngoing
    ReportStatus.Certified -> PartnerReportCertified
    ReportStatus.ReOpenSubmittedLast, ReportStatus.ReOpenSubmittedLimited -> PartnerReportReOpenFromSubmitted
    ReportStatus.ReOpenInControlLast, ReportStatus.ReOpenInControlLimited -> PartnerReportReOpenFromControlOngoing
    else -> null
}.enforceIsPartnerReportNotification()

fun NotificationType?.enforceIsPartnerReportNotification() = if (this != null && isPartnerReportNotification()) this else null


