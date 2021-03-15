package io.cloudflight.jems.server.project.service

import io.cloudflight.jems.api.project.dto.status.ProjectApplicationStatus
import io.cloudflight.jems.api.audit.dto.AuditAction
import io.cloudflight.jems.api.project.dto.OutputProject
import io.cloudflight.jems.server.audit.service.AuditBuilder
import io.cloudflight.jems.server.audit.service.AuditCandidate
import io.cloudflight.jems.server.project.entity.ProjectEntity

fun projectStatusChanged(
    project: ProjectEntity,
    oldStatus: ProjectApplicationStatus? = null,
): AuditCandidate {
    val newStatus = project.projectStatus.status
    val msg = if (oldStatus == null && newStatus == ProjectApplicationStatus.DRAFT)
        "Project application created with status $newStatus"
    else
        "Project application status changed from $oldStatus to $newStatus"
    return AuditBuilder(AuditAction.APPLICATION_STATUS_CHANGED)
        .project(id = project.id, name = project.acronym)
        .description(msg)
        .build()
}

fun callAlreadyEnded(callId: Long): AuditCandidate =
    AuditBuilder(AuditAction.CALL_ALREADY_ENDED)
        .description("Attempted unsuccessfully to submit or to apply for call '$callId' that has already ended").build()

fun qualityAssessmentConcluded(project: OutputProject): AuditCandidate =
    AuditBuilder(AuditAction.QUALITY_ASSESSMENT_CONCLUDED)
        .project(id = project.id!!, name = project.acronym)
        .description("Project application quality assessment concluded as ${project.qualityAssessment?.result}")
        .build()

fun eligibilityAssessmentConcluded(project: OutputProject): AuditCandidate =
    AuditBuilder(AuditAction.ELIGIBILITY_ASSESSMENT_CONCLUDED)
        .project(id = project.id!!, name = project.acronym)
        .description("Project application eligibility assessment concluded as ${project.eligibilityAssessment?.result}")
        .build()
