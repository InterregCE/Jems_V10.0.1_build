package io.cloudflight.jems.server.notification.handler

import io.cloudflight.jems.server.notification.inApp.service.model.NotificationType
import io.cloudflight.jems.server.notification.inApp.service.model.NotificationType.PartnerReportCertified
import io.cloudflight.jems.server.notification.inApp.service.model.NotificationType.PartnerReportControlOngoing
import io.cloudflight.jems.server.notification.inApp.service.model.NotificationType.PartnerReportReOpen
import io.cloudflight.jems.server.notification.inApp.service.model.NotificationType.PartnerReportReOpenCertified
import io.cloudflight.jems.server.notification.inApp.service.model.NotificationType.PartnerReportSubmitted
import io.cloudflight.jems.server.notification.inApp.service.model.NotificationType.ProjectApproved
import io.cloudflight.jems.server.notification.inApp.service.model.NotificationType.ProjectApprovedStep1
import io.cloudflight.jems.server.notification.inApp.service.model.NotificationType.ProjectApprovedWithConditions
import io.cloudflight.jems.server.notification.inApp.service.model.NotificationType.ProjectApprovedWithConditionsStep1
import io.cloudflight.jems.server.notification.inApp.service.model.NotificationType.ProjectConditionsSubmitted
import io.cloudflight.jems.server.notification.inApp.service.model.NotificationType.ProjectContracted
import io.cloudflight.jems.server.notification.inApp.service.model.NotificationType.ProjectInModification
import io.cloudflight.jems.server.notification.inApp.service.model.NotificationType.ProjectIneligible
import io.cloudflight.jems.server.notification.inApp.service.model.NotificationType.ProjectIneligibleStep1
import io.cloudflight.jems.server.notification.inApp.service.model.NotificationType.ProjectModificationApproved
import io.cloudflight.jems.server.notification.inApp.service.model.NotificationType.ProjectModificationRejected
import io.cloudflight.jems.server.notification.inApp.service.model.NotificationType.ProjectModificationSubmitted
import io.cloudflight.jems.server.notification.inApp.service.model.NotificationType.ProjectNotApproved
import io.cloudflight.jems.server.notification.inApp.service.model.NotificationType.ProjectNotApprovedStep1
import io.cloudflight.jems.server.notification.inApp.service.model.NotificationType.ProjectReportSubmitted
import io.cloudflight.jems.server.notification.inApp.service.model.NotificationType.ProjectResubmitted
import io.cloudflight.jems.server.notification.inApp.service.model.NotificationType.ProjectReturnedForConditions
import io.cloudflight.jems.server.notification.inApp.service.model.NotificationType.ProjectReturnedToApplicant
import io.cloudflight.jems.server.notification.inApp.service.model.NotificationType.ProjectSubmitted
import io.cloudflight.jems.server.notification.inApp.service.model.NotificationType.ProjectSubmittedStep1
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.project.service.report.model.partner.ReportStatus
import io.cloudflight.jems.server.project.service.report.model.partner.ReportStatus.Submitted
import io.cloudflight.jems.server.project.service.report.model.partner.ReportStatus.InControl
import io.cloudflight.jems.server.project.service.report.model.partner.ReportStatus.Certified
import io.cloudflight.jems.server.project.service.report.model.partner.ReportStatus.ReOpenCertified
import io.cloudflight.jems.server.project.service.report.model.partner.ReportStatus.ReOpenSubmittedLast
import io.cloudflight.jems.server.project.service.report.model.partner.ReportStatus.ReOpenInControlLast
import io.cloudflight.jems.server.project.service.report.model.partner.ReportStatus.ReOpenSubmittedLimited
import io.cloudflight.jems.server.project.service.report.model.partner.ReportStatus.ReOpenInControlLimited
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
    this == ApplicationStatus.MODIFICATION_REJECTED -> ProjectModificationRejected
    else -> null
}.enforceIsProjectNotification()

fun NotificationType?.enforceIsProjectNotification() = if (this != null && isProjectNotification()) this else null

fun ReportStatus.toNotificationType(): NotificationType? = when(this) {
    Submitted -> PartnerReportSubmitted
    InControl -> PartnerReportControlOngoing
    Certified -> PartnerReportCertified
    ReOpenCertified -> PartnerReportReOpenCertified
    ReOpenSubmittedLast, ReOpenSubmittedLimited, ReOpenInControlLast, ReOpenInControlLimited -> PartnerReportReOpen
    else -> null
}.enforceIsPartnerReportNotification()

fun NotificationType?.enforceIsPartnerReportNotification() = if (this != null && isPartnerReportNotification()) this else null

fun ProjectReportStatus.toNotificationType(): NotificationType? = when(this) {
    ProjectReportStatus.Submitted -> ProjectReportSubmitted
    else -> null
}.enforceIsProjectReportNotification()

fun NotificationType?.enforceIsProjectReportNotification() = if (this != null && isProjectReportNotification()) this else null
