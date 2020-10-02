package io.cloudflight.ems.project.service

import io.cloudflight.ems.api.project.dto.status.ProjectApplicationStatus
import io.cloudflight.ems.api.project.dto.status.ProjectEligibilityAssessmentResult
import io.cloudflight.ems.api.project.dto.status.ProjectQualityAssessmentResult
import io.cloudflight.ems.audit.entity.AuditAction
import io.cloudflight.ems.audit.service.AuditBuilder
import io.cloudflight.ems.audit.service.AuditCandidate

fun projectStatusChanged(
    projectId: Long,
    oldStatus: ProjectApplicationStatus? = null,
    newStatus: ProjectApplicationStatus
): AuditCandidate {
    val msg = if (oldStatus == null && newStatus == ProjectApplicationStatus.DRAFT)
        "Project application created with status $newStatus"
    else
        "Project application status changed from $oldStatus to $newStatus"
    return AuditBuilder(AuditAction.APPLICATION_STATUS_CHANGED)
        .projectId(projectId)
        .description(msg)
        .build()
}

fun callAlreadyEnded(callId: Long): AuditCandidate =
    AuditBuilder(AuditAction.CALL_ALREADY_ENDED)
        .description("Attempted unsuccessfully to submit or to apply for call '$callId' that has already ended").build()

fun qualityAssessmentConcluded(projectId: Long, result: ProjectQualityAssessmentResult): AuditCandidate =
    AuditBuilder(AuditAction.QUALITY_ASSESSMENT_CONCLUDED)
        .projectId(projectId)
        .description("Project application quality assessment concluded as $result")
        .build()

fun eligibilityAssessmentConcluded(projectId: Long, result: ProjectEligibilityAssessmentResult): AuditCandidate =
    AuditBuilder(AuditAction.ELIGIBILITY_ASSESSMENT_CONCLUDED)
        .projectId(projectId)
        .description("Project application eligibility assessment concluded as $result")
        .build()
