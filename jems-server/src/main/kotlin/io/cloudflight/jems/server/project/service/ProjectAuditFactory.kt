package io.cloudflight.jems.server.project.service

import io.cloudflight.jems.api.audit.dto.AuditAction
import io.cloudflight.jems.api.project.dto.ProjectDetailDTO
import io.cloudflight.jems.server.audit.service.AuditBuilder
import io.cloudflight.jems.server.audit.service.AuditCandidate
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.project.service.model.ProjectSummary

fun projectApplicationCreated(
    projectId: Long,
    projectAcronym: String,
    newStatus: ApplicationStatus,
): AuditCandidate =
    AuditBuilder(AuditAction.APPLICATION_STATUS_CHANGED)
        .project(id = projectId, name = projectAcronym)
        .description("Project application created with status $newStatus")
        .build()

fun projectStatusChanged(projectSummary: ProjectSummary, newStatus: ApplicationStatus): AuditCandidate =
    AuditBuilder(AuditAction.APPLICATION_STATUS_CHANGED)
        .project(id = projectSummary.id, name = projectSummary.acronym)
        .description("Project application status changed from ${projectSummary.status} to $newStatus")
        .build()


fun callAlreadyEnded(callId: Long): AuditCandidate =
    AuditBuilder(AuditAction.CALL_ALREADY_ENDED)
        .description("Attempted unsuccessfully to submit or to apply for call '$callId' that has already ended").build()

fun qualityAssessmentConcluded(projectDetailDTO: ProjectDetailDTO): AuditCandidate =
    AuditBuilder(AuditAction.QUALITY_ASSESSMENT_CONCLUDED)
        .project(id = projectDetailDTO.id!!, name = projectDetailDTO.acronym)
        .description("Project application quality assessment concluded as ${projectDetailDTO.qualityAssessment?.result}")
        .build()

fun eligibilityAssessmentConcluded(projectDetailDTO: ProjectDetailDTO): AuditCandidate =
    AuditBuilder(AuditAction.ELIGIBILITY_ASSESSMENT_CONCLUDED)
        .project(id = projectDetailDTO.id!!, name = projectDetailDTO.acronym)
        .description("Project application eligibility assessment concluded as ${projectDetailDTO.eligibilityAssessment?.result}")
        .build()
