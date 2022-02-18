package io.cloudflight.jems.server.project.service.report

import io.cloudflight.jems.api.audit.dto.AuditAction
import io.cloudflight.jems.server.audit.model.AuditCandidateEvent
import io.cloudflight.jems.server.audit.service.AuditBuilder
import io.cloudflight.jems.server.project.service.model.ProjectFull
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerRole
import io.cloudflight.jems.server.project.service.report.model.ProjectPartnerReportCreate
import io.cloudflight.jems.server.project.service.report.model.ProjectPartnerReportSubmissionSummary

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
                "] Partner report R.${report.reportNumber} added")
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
