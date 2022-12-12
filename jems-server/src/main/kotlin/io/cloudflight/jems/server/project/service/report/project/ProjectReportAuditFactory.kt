package io.cloudflight.jems.server.project.service.report.project

import io.cloudflight.jems.api.audit.dto.AuditAction
import io.cloudflight.jems.server.audit.model.AuditCandidateEvent
import io.cloudflight.jems.server.audit.service.AuditBuilder
import io.cloudflight.jems.server.project.service.model.ProjectFull
import io.cloudflight.jems.server.project.service.report.model.project.base.ProjectReportModel

fun projectReportCreated(
    context: Any,
    project: ProjectFull,
    report: ProjectReportModel,
): AuditCandidateEvent =
    AuditCandidateEvent(
        context = context,
        auditCandidate = AuditBuilder(AuditAction.PROJECT_REPORT_ADDED)
            .project(project)
            .entityRelatedId(entityRelatedId = report.id)
            .description("[${report.projectIdentifier}] Project report PR.${report.reportNumber} added")
            .build()
    )

fun projectReportDeleted(
    context: Any,
    report: ProjectReportModel,
): AuditCandidateEvent =
    AuditCandidateEvent(
        context = context,
        auditCandidate = AuditBuilder(AuditAction.PROJECT_REPORT_DELETED)
            .project(
                projectId = report.projectId,
                customIdentifier = report.projectIdentifier,
                acronym = report.projectAcronym,
            )
            .entityRelatedId(entityRelatedId = report.id)
            .description("[${report.projectIdentifier}] ${report.status} project report PR.${report.reportNumber} deleted")
            .build()
    )
