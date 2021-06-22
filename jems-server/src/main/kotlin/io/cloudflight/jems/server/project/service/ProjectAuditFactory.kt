package io.cloudflight.jems.server.project.service

import io.cloudflight.jems.api.audit.dto.AuditAction
import io.cloudflight.jems.api.project.dto.ProjectDetailDTO
import io.cloudflight.jems.api.project.dto.assessment.ProjectAssessmentEligibilityResult
import io.cloudflight.jems.api.project.dto.assessment.ProjectAssessmentQualityResult
import io.cloudflight.jems.server.audit.model.AuditCandidateEvent
import io.cloudflight.jems.server.audit.service.AuditBuilder
import io.cloudflight.jems.server.audit.service.AuditCandidate
import io.cloudflight.jems.server.call.service.model.CallDetail
import io.cloudflight.jems.server.project.entity.ProjectEntity
import io.cloudflight.jems.server.project.repository.ProjectVersionUtils
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.project.service.model.Project
import io.cloudflight.jems.server.project.service.model.ProjectCallSettings
import io.cloudflight.jems.server.project.service.model.ProjectSummary
import io.cloudflight.jems.server.project.service.model.ProjectVersion
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

fun projectApplicationCreated(
    context: Any,
    project: Project,
): AuditCandidateEvent =
    AuditCandidateEvent(
        context = context,
        auditCandidate = AuditBuilder(AuditAction.APPLICATION_STATUS_CHANGED)
            .project(id = project.id!!, name = project.acronym)
            .description("Project application created with status ${project.projectStatus.status}")
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

fun unsuccessfulProjectSubmission(
    context: Any, projectSummary: ProjectSummary
): AuditCandidateEvent =
    AuditCandidateEvent(
        context = context,
        auditCandidate = AuditBuilder(AuditAction.APPLICATION_STATUS_CHANGED)
            .project(id = projectSummary.id, name = projectSummary.acronym)
            .description("Unsuccessful attempt to submit an application from ${projectSummary.status}. Verification failed.")
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
                    "${projectVersion.createdAt.toLocalDateTime().withNano(0)
                        .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)}"
            ).build()
    )

fun projectVersionRecorded(
    context: Any, projectSummary: ProjectSummary, userEmail: String, version: String = ProjectVersionUtils.DEFAULT_VERSION,
): AuditCandidateEvent =
    AuditCandidateEvent(
        context = context,
        auditCandidate = AuditBuilder(AuditAction.APPLICATION_VERSION_RECORDED)
            .project(id = projectSummary.id, name = projectSummary.acronym)
            .description(
                "New project version \"V.$version\" is recorded by user: $userEmail on " +
                    "${ZonedDateTime.now(ZoneOffset.UTC).withNano(0).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)}"
            ).build()
    )


fun callAlreadyEnded(context: Any, call: CallDetail): AuditCandidateEvent =
    callAlreadyEnded(context, call.name, call.id)

fun callAlreadyEnded(context: Any, call: ProjectCallSettings): AuditCandidateEvent =
    callAlreadyEnded(context, call.callName, call.callId)

private fun callAlreadyEnded(context: Any, callName: String, callId: Long): AuditCandidateEvent =
    AuditCandidateEvent(
        context = context,
        auditCandidate = AuditBuilder(AuditAction.CALL_ALREADY_ENDED)
            .description("Attempted unsuccessfully to submit or to apply for call '$callName' (id=$callId) that is not open.")
            .entityRelatedId(callId)
            .build()
    )

fun qualityAssessmentStep1Concluded(context: Any, project: ProjectSummary, result: ProjectAssessmentQualityResult): AuditCandidateEvent =
    AuditCandidateEvent(
        context = context,
        auditCandidate = AuditBuilder(AuditAction.QUALITY_ASSESSMENT_CONCLUDED)
        .project(id = project.id, name = project.acronym)
        .description("Project application quality assessment (step 1) concluded as $result")
        .build()
    )

fun qualityAssessmentStep2Concluded(context: Any, project: ProjectSummary, result: ProjectAssessmentQualityResult): AuditCandidateEvent =
    AuditCandidateEvent(
        context = context,
        auditCandidate = AuditBuilder(AuditAction.QUALITY_ASSESSMENT_CONCLUDED)
            .project(id = project.id, name = project.acronym)
            .description("Project application quality assessment concluded as $result")
            .build()
    )

fun eligibilityAssessmentStep1Concluded(context: Any, project: ProjectSummary, result: ProjectAssessmentEligibilityResult): AuditCandidateEvent =
    AuditCandidateEvent(
        context = context,
        auditCandidate = AuditBuilder(AuditAction.ELIGIBILITY_ASSESSMENT_CONCLUDED)
            .project(id = project.id, name = project.acronym)
            .description("Project application eligibility assessment (step 1) concluded as $result")
            .build()
    )

fun eligibilityAssessmentStep2Concluded(context: Any, project: ProjectSummary, result: ProjectAssessmentEligibilityResult): AuditCandidateEvent =
    AuditCandidateEvent(
        context = context,
        auditCandidate = AuditBuilder(AuditAction.ELIGIBILITY_ASSESSMENT_CONCLUDED)
            .project(id = project.id, name = project.acronym)
            .description("Project application eligibility assessment concluded as $result")
            .build()
    )
