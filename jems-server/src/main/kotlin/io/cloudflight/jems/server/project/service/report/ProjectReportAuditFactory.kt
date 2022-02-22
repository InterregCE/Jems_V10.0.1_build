package io.cloudflight.jems.server.project.service.report

import io.cloudflight.jems.api.audit.dto.AuditAction
import io.cloudflight.jems.server.audit.model.AuditCandidateEvent
import io.cloudflight.jems.server.audit.service.AuditBuilder
import io.cloudflight.jems.server.project.service.model.ProjectFull
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerRole
import io.cloudflight.jems.server.project.service.report.model.ProjectPartnerReportCreate

fun partnerReportCreated(
    context: Any,
    project: ProjectFull,
    report: ProjectPartnerReportCreate,
): AuditCandidateEvent =
    AuditCandidateEvent(
        context = context,
        auditCandidate = AuditBuilder(AuditAction.PARTNER_REPORT_ADDED)
            .project(project)
            .description("[" +
                project.customIdentifier +
                "] [" +
                (if (report.identification.partnerRole == ProjectPartnerRole.LEAD_PARTNER) "LP" else "PP") +
                "${report.identification.partnerNumber}" +
                "] Partner report R.${report.reportNumber} added")
            .build()
    )
