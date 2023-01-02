package io.cloudflight.jems.server.project.service.report.partner

import io.cloudflight.jems.api.audit.dto.AuditAction
import io.cloudflight.jems.server.audit.model.AuditCandidateEvent
import io.cloudflight.jems.server.audit.service.AuditBuilder
import io.cloudflight.jems.server.project.service.model.ProjectFull
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerRole
import io.cloudflight.jems.server.project.service.report.model.partner.ProjectPartnerReport
import io.cloudflight.jems.server.project.service.report.model.partner.base.create.ProjectPartnerReportCreate
import io.cloudflight.jems.server.project.service.report.model.partner.ProjectPartnerReportSubmissionSummary

fun partnerReportCreated(
    context: Any,
    project: ProjectFull,
    report: ProjectPartnerReportCreate,
    reportId: Long,
): AuditCandidateEvent =
    AuditCandidateEvent(
        context = context,
        auditCandidate = AuditBuilder(AuditAction.PARTNER_REPORT_ADDED)
            .project(project)
            .entityRelatedId(entityRelatedId = reportId)
            .description("[" +
                report.identification.projectIdentifier +
                "] [" +
                (if (report.identification.partnerRole == ProjectPartnerRole.LEAD_PARTNER) "LP" else "PP") +
                "${report.identification.partnerNumber}" +
                "] Partner report R.${report.baseData.reportNumber} added")
            .build()
    )

fun partnerReportSubmitted(
    context: Any,
    projectId: Long,
    report: ProjectPartnerReportSubmissionSummary,
): AuditCandidateEvent =
    AuditCandidateEvent(
        context = context,
        auditCandidate = AuditBuilder(AuditAction.PARTNER_REPORT_SUBMITTED)
            .project(
                projectId = projectId,
                customIdentifier = report.projectIdentifier,
                acronym = report.projectAcronym,
            )
            .entityRelatedId(entityRelatedId = report.id)
            .description("[" +
                report.projectIdentifier +
                "] [" +
                (if (report.partnerRole == ProjectPartnerRole.LEAD_PARTNER) "LP" else "PP") +
                "${report.partnerNumber}" +
                "] Partner report R.${report.reportNumber} submitted")
            .build()
    )

fun partnerReportStartedControl(
    context: Any,
    projectId: Long,
    report: ProjectPartnerReportSubmissionSummary,
): AuditCandidateEvent =
    AuditCandidateEvent(
        context = context,
        auditCandidate = AuditBuilder(AuditAction.PARTNER_REPORT_CONTROL_ONGOING)
            .project(
                projectId = projectId,
                customIdentifier = report.projectIdentifier,
                acronym = report.projectAcronym,
            )
            .entityRelatedId(entityRelatedId = report.id)
            .description("[" +
                report.projectIdentifier +
                "] [" +
                (if (report.partnerRole == ProjectPartnerRole.LEAD_PARTNER) "LP" else "PP") +
                "${report.partnerNumber}" +
                "] Partner report R.${report.reportNumber} control ongoing")
            .build()
    )

fun partnerReportControlFinalized(
    context: Any,
    projectId: Long,
    report: ProjectPartnerReportSubmissionSummary,
): AuditCandidateEvent =
    AuditCandidateEvent(
        context = context,
        auditCandidate = AuditBuilder(AuditAction.PARTNER_REPORT_CONTROL_FINALIZED)
            .project(projectId = projectId, customIdentifier = report.projectIdentifier, acronym = report.projectAcronym)
            .entityRelatedId(entityRelatedId = report.id)
            .description("Control work is finalised for partner report R.${report.reportNumber} of partner " +
                (if (report.partnerRole == ProjectPartnerRole.LEAD_PARTNER) "LP" else "PP") + "${report.partnerNumber}")
            .build()
    )

fun partnerReportDeleted(
    context: Any,
    projectId: Long,
    partnerReport: ProjectPartnerReport,
): AuditCandidateEvent =
    AuditCandidateEvent(
        context = context,
        auditCandidate = AuditBuilder(AuditAction.PARTNER_REPORT_DELETED)
            .project(
                projectId = projectId,
                customIdentifier = partnerReport.identification.projectIdentifier,
                acronym = partnerReport.identification.projectAcronym,
            )
            .entityRelatedId(entityRelatedId = partnerReport.id)
            .description("[" +
                partnerReport.identification.projectIdentifier +
                "] [" +
                (if (partnerReport.identification.partnerRole == ProjectPartnerRole.LEAD_PARTNER) "LP" else "PP") +
                "${partnerReport.identification.partnerNumber}" +
                "] Draft partner report R.${partnerReport.reportNumber} deleted")
            .build()
    )

fun partnerReportExpenditureParked(
    context: Any,
    projectId: Long,
    partnerReport: ProjectPartnerReport,
    isParked: Boolean,
    expenditureIds: List<Int>
): AuditCandidateEvent =
    AuditCandidateEvent(
        context = context,
        auditCandidate = AuditBuilder(
            if (isParked) AuditAction.PARTNER_EXPENDITURE_PARKED else AuditAction.PARTNER_EXPENDITURE_UNPARKED)
            .project(
                projectId = projectId,
                customIdentifier = partnerReport.identification.projectIdentifier,
                acronym = partnerReport.identification.projectAcronym,
            )
            .entityRelatedId(entityRelatedId = partnerReport.id)
            .description("Controller " +
                (if (isParked) "parked the following expenditures: " else "unparked the following expenditures: ") +
                expenditureIds.joinToString(
                    separator = ", ",
                    prefix = "[", postfix = "]",
                    transform = {"R" + partnerReport.reportNumber + "." + it.toString()}) +
                " of partner " +
                (if (partnerReport.identification.partnerRole == ProjectPartnerRole.LEAD_PARTNER) "LP" else "PP") +
                " from report R.${partnerReport.reportNumber}"
                )
            .build()
    )
