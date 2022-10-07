package io.cloudflight.jems.server.project.service

import io.cloudflight.jems.api.audit.dto.AuditAction
import io.cloudflight.jems.api.project.dto.assessment.ProjectAssessmentEligibilityResult
import io.cloudflight.jems.api.project.dto.assessment.ProjectAssessmentQualityResult
import io.cloudflight.jems.server.audit.model.AuditCandidateEvent
import io.cloudflight.jems.server.audit.model.AuditProject
import io.cloudflight.jems.server.audit.service.AuditBuilder
import io.cloudflight.jems.server.audit.service.AuditCandidate
import io.cloudflight.jems.server.call.service.model.CallDetail
import io.cloudflight.jems.server.common.audit.fromOldToNewChanges
import io.cloudflight.jems.server.project.entity.partner.ProjectPartnerEntity
import io.cloudflight.jems.server.project.repository.ProjectVersionUtils
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.project.service.contracting.model.ProjectContractingMonitoring
import io.cloudflight.jems.server.project.service.contracting.partner.bankingDetails.ContractingPartnerBankingDetails
import io.cloudflight.jems.server.project.service.file.model.ProjectFileCategory
import io.cloudflight.jems.server.project.service.file.model.ProjectFileCategoryType
import io.cloudflight.jems.server.project.service.file.model.ProjectFileMetadata
import io.cloudflight.jems.server.project.service.model.ProjectCallSettings
import io.cloudflight.jems.server.project.service.model.ProjectDetail
import io.cloudflight.jems.server.project.service.model.ProjectSummary
import io.cloudflight.jems.server.project.service.model.ProjectVersionSummary
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

fun projectApplicationCreated(
    context: Any,
    project: ProjectDetail,
): AuditCandidateEvent =
    AuditCandidateEvent(
        context = context,
        auditCandidate = AuditBuilder(AuditAction.APPLICATION_STATUS_CHANGED)
            .project(project)
            .description("Project application created with status ${project.projectStatus.status}")
            .build()
    )

fun projectStatusChanged(
    context: Any, projectSummary: ProjectSummary, newStatus: ApplicationStatus
): AuditCandidateEvent =
    AuditCandidateEvent(
        context = context,
        auditCandidate = AuditBuilder(AuditAction.APPLICATION_STATUS_CHANGED)
            .project(projectSummary)
            .description("Project application status changed from ${projectSummary.status} to $newStatus")
            .build()
    )

fun unsuccessfulProjectSubmission(
    context: Any, projectSummary: ProjectSummary
): AuditCandidateEvent =
    AuditCandidateEvent(
        context = context,
        auditCandidate = AuditBuilder(AuditAction.APPLICATION_STATUS_CHANGED)
            .project(projectSummary)
            .description("Unsuccessful attempt to submit an application from ${projectSummary.status}. Verification failed.")
            .build()
    )


fun projectVersionSnapshotCreated(
    context: Any, projectSummary: ProjectSummary, projectVersion: ProjectVersionSummary
): AuditCandidateEvent =
    AuditCandidateEvent(
        context = context,
        auditCandidate = AuditBuilder(AuditAction.APPLICATION_VERSION_SNAPSHOT_CREATED)
            .project(projectSummary)
            .description(
                "New project version \"V.${projectVersion.version}\" is created by user: ${projectVersion.user.email} on " +
                    projectVersion.createdAt.toLocalDateTime().withNano(0)
                        .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
            ).build()
    )

fun projectVersionRecorded(
    context: Any, projectSummary: ProjectSummary, userEmail: String, version: String = ProjectVersionUtils.DEFAULT_VERSION,
): AuditCandidateEvent =
    AuditCandidateEvent(
        context = context,
        auditCandidate = AuditBuilder(AuditAction.APPLICATION_VERSION_RECORDED)
            .project(projectSummary)
            .description(
                "New project version \"V.$version\" is recorded by user: $userEmail on " +
                    ZonedDateTime.now(ZoneOffset.UTC).withNano(0).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
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
        .project(project)
        .description("Project application quality assessment (step 1) concluded as $result")
        .build()
    )

fun qualityAssessmentStep2Concluded(context: Any, project: ProjectSummary, result: ProjectAssessmentQualityResult): AuditCandidateEvent =
    AuditCandidateEvent(
        context = context,
        auditCandidate = AuditBuilder(AuditAction.QUALITY_ASSESSMENT_CONCLUDED)
            .project(project)
            .description("Project application quality assessment concluded as $result")
            .build()
    )

fun eligibilityAssessmentStep1Concluded(context: Any, project: ProjectSummary, result: ProjectAssessmentEligibilityResult): AuditCandidateEvent =
    AuditCandidateEvent(
        context = context,
        auditCandidate = AuditBuilder(AuditAction.ELIGIBILITY_ASSESSMENT_CONCLUDED)
            .project(project)
            .description("Project application eligibility assessment (step 1) concluded as $result")
            .build()
    )

fun eligibilityAssessmentStep2Concluded(context: Any, project: ProjectSummary, result: ProjectAssessmentEligibilityResult): AuditCandidateEvent =
    AuditCandidateEvent(
        context = context,
        auditCandidate = AuditBuilder(AuditAction.ELIGIBILITY_ASSESSMENT_CONCLUDED)
            .project(project)
            .description("Project application eligibility assessment concluded as $result")
            .build()
    )


fun projectFileDownloadSucceed(
    context: Any, projectFileMetadata: ProjectFileMetadata
): AuditCandidateEvent =
    AuditCandidateEvent(
        context = context,
        auditCandidate = AuditCandidate(
            action = AuditAction.PROJECT_FILE_DOWNLOADED_SUCCESSFULLY,
            project = AuditProject(id = projectFileMetadata.projectId.toString()),
            description = "document ${projectFileMetadata.name} downloaded from project application ${projectFileMetadata.projectId} by ${projectFileMetadata.uploadedBy.id}"

        )
    )

fun projectFileDownloadFailed(
    context: Any, projectId: Long, fileId:Long, userId:Long
): AuditCandidateEvent =
    AuditCandidateEvent(
        context = context,
        auditCandidate = AuditCandidate(
            action = AuditAction.PROJECT_FILE_DOWNLOADED_FAILED,
            project = AuditProject(id = projectId.toString()),
            description = "document $fileId download failed from project application $projectId by $userId"

        )
    )

fun projectFileUploadSucceed(
    context: Any, projectFileMetaData: ProjectFileMetadata, projectFileCategory: ProjectFileCategory
): AuditCandidateEvent =
    AuditCandidateEvent(
        context = context,
        auditCandidate = AuditCandidate(
            action = AuditAction.PROJECT_FILE_UPLOADED_SUCCESSFULLY,
            project = AuditProject(id = projectFileMetaData.projectId.toString()),
            description = when (projectFileCategory.type) {
                ProjectFileCategoryType.PARTNER -> "document ${projectFileMetaData.name} uploaded to project application ${projectFileMetaData.projectId} for Partner ${projectFileCategory.id} by ${projectFileMetaData.uploadedBy.id}"
                ProjectFileCategoryType.INVESTMENT -> "document ${projectFileMetaData.name} uploaded to project application ${projectFileMetaData.projectId} for Investment ${projectFileCategory.id} by ${projectFileMetaData.uploadedBy.id}"
                else -> "document ${projectFileMetaData.name} uploaded to project application ${projectFileMetaData.projectId} by ${projectFileMetaData.uploadedBy.id}"
            }
        )
    )

fun projectFileUploadFailed(context: Any, projectId: Long, fileName: String, projectFileCategory: ProjectFileCategory, userId: Long): AuditCandidateEvent =
    AuditCandidateEvent(
        context = context,
        auditCandidate = AuditCandidate(
            action = AuditAction.PROJECT_FILE_UPLOAD_FAILED,
            project = AuditProject(id = projectId.toString()),
            description = when (projectFileCategory.type) {
                ProjectFileCategoryType.PARTNER -> "FAILED upload of document $fileName to project application $projectId for Partner ${projectFileCategory.id} by $userId"
                ProjectFileCategoryType.INVESTMENT -> "FAILED upload of document $fileName to project application $projectId for Investment ${projectFileCategory.id} by $userId"
                else -> "FAILED upload of document $fileName to project application $projectId by $userId"
            }
        )
    )

fun projectFileDeleteSucceed(
    context: Any, projectFileMetadata: ProjectFileMetadata
): AuditCandidateEvent =
    AuditCandidateEvent(
        context = context,
        auditCandidate = AuditCandidate(
            action = AuditAction.PROJECT_FILE_DELETED,
            project = AuditProject(id = projectFileMetadata.projectId.toString()),
            description = "document ${projectFileMetadata.name} deleted from project application ${projectFileMetadata.projectId} by ${projectFileMetadata.uploadedBy.id}"

        )
    )

fun projectFileDescriptionChanged(
    context: Any, projectFileMetadata: ProjectFileMetadata, oldValue: String
): AuditCandidateEvent =
    AuditCandidateEvent(
        context = context,
        auditCandidate = AuditCandidate(
            action = AuditAction.PROJECT_FILE_DESCRIPTION_CHANGED,
            project = AuditProject(id = projectFileMetadata.projectId.toString()),
            description = "description of document ${projectFileMetadata.name} in project application ${projectFileMetadata.projectId} has changed from `$oldValue` to `${projectFileMetadata.description}` by ${projectFileMetadata.uploadedBy.id}"
        )
    )

fun projectContractingMonitoringChanged(
    context: Any,
    project: ProjectSummary,
    oldMonitoring: ProjectContractingMonitoring,
    newMonitoring: ProjectContractingMonitoring
): AuditCandidateEvent {
    val changes = newMonitoring.getDiff(old = oldMonitoring).fromOldToNewChanges()

    return AuditCandidateEvent(
        context = context,
        auditCandidate = AuditCandidate(
            action = AuditAction.PROJECT_CONTRACT_MONITORING_CHANGED,
            project = AuditProject(id = project.id.toString()),
            description = "Fields changed:\n$changes"
        )
    )
}

fun projectContractInfoChanged(
    context: Any,
    project: ProjectSummary,
    oldPartnershipAgreementDate:  LocalDate?,
    newPartnershipAgreementDate:  LocalDate?
): AuditCandidateEvent {
    val oldValue = oldPartnershipAgreementDate ?: "Not defined"
    return AuditCandidateEvent(
        context = context,
        auditCandidate = AuditCandidate(
            action = AuditAction.PROJECT_CONTRACT_INFO_CHANGED,
            project = AuditProject(id = project.id.toString()),
            description = "Partnership agreement date changed from:\n $oldValue to $newPartnershipAgreementDate"
        )
    )
}

fun projectContractingPartnerInfoChanged(
    context: Any,
    partner: ProjectPartnerEntity,
    oldBankingDetails: ContractingPartnerBankingDetails?,
    newBankingDetails: ContractingPartnerBankingDetails
): AuditCandidateEvent {
    val changes = newBankingDetails.getDiff(old = oldBankingDetails).fromOldToNewChanges()

    return AuditCandidateEvent(
        context = context,
        auditCandidate = AuditCandidate(
            action = AuditAction.PROJECT_CONTRACT_PARTNER_INFO_CHANGE,
            project = AuditProject(id = partner.project.id.toString()),
            description = "Fields changed for partner info of ${getPartnerName(partner)}:\n$changes"
        )
    )
}

private fun getPartnerName(partner: ProjectPartnerEntity): String =
    partner.role.isLead.let {
        if (it) "LP${partner.sortNumber}" else "PP${partner.sortNumber}"
    }