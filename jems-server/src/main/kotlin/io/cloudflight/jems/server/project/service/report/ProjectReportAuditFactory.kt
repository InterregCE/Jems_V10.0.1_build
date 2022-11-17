package io.cloudflight.jems.server.project.service.report

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
        auditCandidate = AuditBuilder(AuditAction.CONTROL_ONGOING)
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
