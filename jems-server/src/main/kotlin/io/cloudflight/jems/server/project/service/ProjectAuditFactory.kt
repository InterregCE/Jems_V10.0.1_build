package io.cloudflight.jems.server.project.service

import io.cloudflight.jems.api.audit.dto.AuditAction
import io.cloudflight.jems.api.project.dto.ProjectDetailDTO
import io.cloudflight.jems.server.audit.model.AuditCandidateEvent
import io.cloudflight.jems.server.audit.service.AuditBuilder
import io.cloudflight.jems.server.audit.service.AuditCandidate
import io.cloudflight.jems.server.project.entity.ProjectEntity
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.project.service.model.ProjectSummary
import io.cloudflight.jems.server.project.service.model.ProjectVersion
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

fun projectApplicationCreated(
    context: Any,
    project: ProjectEntity,
): AuditCandidateEvent =
    AuditCandidateEvent(
        context = context,
        auditCandidate = AuditBuilder(AuditAction.APPLICATION_STATUS_CHANGED)
            .project(id = project.id, name = project.acronym)
            .description("Project application created with status ${project.currentStatus.status}")
            .build()
    )

fun projectStatusChanged(
    context: Any, projectSummary: ProjectSummary, newStatus: ApplicationStatus
): AuditCandidateEvent =
    AuditCandidateEvent(
        context = context,
        auditCandidate = AuditBuilder(AuditAction.APPLICATION_STATUS_CHANGED)
            .project(id = projectSummary.id, name = projectSummary.acronym)
            .description("Project application status changed from ${projectSummary.status} to $newStatus")
            .build()
    )


fun projectVersionSnapshotCreated(
    context: Any, projectSummary: ProjectSummary, projectVersion: ProjectVersion
): AuditCandidateEvent =
    AuditCandidateEvent(
        context = context,
        auditCandidate = AuditBuilder(AuditAction.APPLICATION_VERSION_SNAPSHOT_CREATED)
            .project(id = projectSummary.id, name = projectSummary.acronym)
            .description(
                "New project version \"V.${projectVersion.version}\" is created by user: ${projectVersion.user.email} on " +
                    "${projectVersion.createdAt.toLocalDateTime().withNano(0).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)}"
            ).build()
    )

fun projectVersionRecorded(
    context: Any, projectSummary: ProjectSummary, userEmail: String, version: String, createdAt: ZonedDateTime
): AuditCandidateEvent =
    AuditCandidateEvent(
        context = context,
        auditCandidate = AuditBuilder(AuditAction.APPLICATION_VERSION_RECORDED)
            .project(id = projectSummary.id, name = projectSummary.acronym)
            .description(
                "New project version \"V.$version\" is recorded by user: $userEmail on " +
                    "${createdAt.withNano(0).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)}"
            ).build()
    )


fun callAlreadyEnded(callId: Long): AuditCandidate =
    AuditBuilder(AuditAction.CALL_ALREADY_ENDED)
        .description("Attempted unsuccessfully to submit or to apply for call '$callId' that has already ended").build()

fun qualityAssessmentConcluded(projectDetailDTO: ProjectDetailDTO): AuditCandidate =
    AuditBuilder(AuditAction.QUALITY_ASSESSMENT_CONCLUDED)
        .project(id = projectDetailDTO.id!!, name = projectDetailDTO.acronym)
        .description("Project application quality assessment concluded as ${projectDetailDTO.firstStepDecision?.qualityAssessment?.result}")
        .build()

fun eligibilityAssessmentConcluded(projectDetailDTO: ProjectDetailDTO): AuditCandidate =
    AuditBuilder(AuditAction.ELIGIBILITY_ASSESSMENT_CONCLUDED)
        .project(id = projectDetailDTO.id!!, name = projectDetailDTO.acronym)
        .description("Project application eligibility assessment concluded as ${projectDetailDTO.firstStepDecision?.eligibilityAssessment?.result}")
        .build()
