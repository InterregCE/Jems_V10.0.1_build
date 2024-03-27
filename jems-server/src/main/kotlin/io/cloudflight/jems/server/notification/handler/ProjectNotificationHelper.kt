package io.cloudflight.jems.server.notification.handler

import io.cloudflight.jems.server.notification.inApp.service.model.NotificationType
import io.cloudflight.jems.server.notification.inApp.service.model.NotificationType.*
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.project.service.report.model.partner.ReportStatus
import io.cloudflight.jems.server.project.service.report.model.partner.ReportStatus.*
import io.cloudflight.jems.server.project.service.report.model.project.ProjectReportStatus

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
    this == ApplicationStatus.APPROVED && prevStatus == ApplicationStatus.CONDITIONS_SUBMITTED -> ProjectApproved
    this == ApplicationStatus.MODIFICATION_REJECTED -> ProjectModificationRejected
    this == ApplicationStatus.CLOSED -> ProjectClosed
    else -> null
}.enforceIsProjectNotification()

fun NotificationType?.enforceIsProjectNotification() = if (this != null && isProjectNotification()) this else null

fun ReportStatus.toNotificationType(previousReportStatus: ReportStatus): NotificationType? = when {
    this == Submitted -> PartnerReportSubmitted
    this == ReOpenSubmittedLast -> PartnerReportReOpen
    this == ReOpenSubmittedLimited -> PartnerReportReOpen
    this == ReOpenInControlLast -> PartnerReportReOpen
    this == ReOpenInControlLimited -> PartnerReportReOpen
    this == InControl && previousReportStatus == ReOpenInControlLast -> PartnerReportSubmitted
    this == InControl && previousReportStatus == ReOpenInControlLimited -> PartnerReportSubmitted
    this == InControl && previousReportStatus == Submitted -> PartnerReportControlOngoing
    this == Certified -> PartnerReportCertified
    this == ReOpenCertified && previousReportStatus == ReOpenInControlLast -> PartnerReportSubmitted
    this == ReOpenCertified && previousReportStatus == ReOpenInControlLimited -> PartnerReportSubmitted
    this == ReOpenCertified -> PartnerReportReOpenCertified
    else -> null
}.enforceIsPartnerReportNotification()

fun NotificationType?.enforceIsPartnerReportNotification() = if (this != null && isPartnerReportNotification()) this else null

fun ProjectReportStatus.toNotificationType(previousReportStatus: ProjectReportStatus): NotificationType? = when {
    this == ProjectReportStatus.Submitted -> ProjectReportSubmitted
    this == ProjectReportStatus.ReOpenSubmittedLast -> ProjectReportReOpen
    this == ProjectReportStatus.ReOpenSubmittedLimited -> ProjectReportReOpen
    this == ProjectReportStatus.VerificationReOpenedLast -> ProjectReportReOpen
    this == ProjectReportStatus.VerificationReOpenedLimited -> ProjectReportReOpen
    this == ProjectReportStatus.InVerification && previousReportStatus == ProjectReportStatus.VerificationReOpenedLast -> ProjectReportSubmitted
    this == ProjectReportStatus.InVerification && previousReportStatus == ProjectReportStatus.VerificationReOpenedLimited -> ProjectReportSubmitted
    this == ProjectReportStatus.InVerification && previousReportStatus == ProjectReportStatus.Submitted -> ProjectReportVerificationOngoing
    this == ProjectReportStatus.Finalized -> ProjectReportVerificationFinalized
    this == ProjectReportStatus.ReOpenFinalized && previousReportStatus == ProjectReportStatus.VerificationReOpenedLast -> ProjectReportSubmitted
    this == ProjectReportStatus.ReOpenFinalized && previousReportStatus == ProjectReportStatus.VerificationReOpenedLimited -> ProjectReportSubmitted
    this == ProjectReportStatus.ReOpenFinalized -> ProjectReportVerificationReOpen
    else -> null
}.enforceIsProjectReportNotification()

fun NotificationType?.enforceIsProjectReportNotification() = if (this != null && isProjectReportNotification()) this else null
