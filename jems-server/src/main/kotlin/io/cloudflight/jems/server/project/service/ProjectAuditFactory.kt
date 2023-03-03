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
import io.cloudflight.jems.server.project.entity.contracting.partner.ProjectContractingPartnerBeneficialOwnerEntity
import io.cloudflight.jems.server.project.entity.partner.ProjectPartnerEntity
import io.cloudflight.jems.server.project.repository.ProjectVersionUtils
import io.cloudflight.jems.server.project.repository.partner.toProjectPartnerDetail
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.project.service.contracting.model.ProjectContractingSection
import io.cloudflight.jems.server.project.service.contracting.partner.bankingDetails.ContractingPartnerBankingDetails
import io.cloudflight.jems.server.project.service.contracting.partner.beneficialOwner.ContractingPartnerBeneficialOwner
import io.cloudflight.jems.server.project.service.contracting.partner.documentsLocation.ContractingPartnerDocumentsLocation
import io.cloudflight.jems.server.project.service.file.model.ProjectFileCategory
import io.cloudflight.jems.server.project.service.file.model.ProjectFileCategoryType
import io.cloudflight.jems.server.project.service.file.model.ProjectFileMetadata
import io.cloudflight.jems.server.project.service.model.ProjectCallSettings
import io.cloudflight.jems.server.project.service.model.ProjectDetail
import io.cloudflight.jems.server.project.service.model.ProjectSummary
import io.cloudflight.jems.server.project.service.model.ProjectVersionSummary
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerDetail
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
    context: Any,
    projectSummary: ProjectSummary,
    userEmail: String,
    version: String = ProjectVersionUtils.DEFAULT_VERSION,
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

fun qualityAssessmentStep1Concluded(
    context: Any,
    project: ProjectSummary,
    result: ProjectAssessmentQualityResult
): AuditCandidateEvent =
    AuditCandidateEvent(
        context = context,
        auditCandidate = AuditBuilder(AuditAction.QUALITY_ASSESSMENT_CONCLUDED)
            .project(project)
            .description("Project application quality assessment (step 1) concluded as $result")
            .build()
    )

fun qualityAssessmentStep2Concluded(
    context: Any,
    project: ProjectSummary,
    result: ProjectAssessmentQualityResult
): AuditCandidateEvent =
    AuditCandidateEvent(
        context = context,
        auditCandidate = AuditBuilder(AuditAction.QUALITY_ASSESSMENT_CONCLUDED)
            .project(project)
            .description("Project application quality assessment concluded as $result")
            .build()
    )

fun eligibilityAssessmentStep1Concluded(
    context: Any,
    project: ProjectSummary,
    result: ProjectAssessmentEligibilityResult
): AuditCandidateEvent =
    AuditCandidateEvent(
        context = context,
        auditCandidate = AuditBuilder(AuditAction.ELIGIBILITY_ASSESSMENT_CONCLUDED)
            .project(project)
            .description("Project application eligibility assessment (step 1) concluded as $result")
            .build()
    )

fun eligibilityAssessmentStep2Concluded(
    context: Any,
    project: ProjectSummary,
    result: ProjectAssessmentEligibilityResult
): AuditCandidateEvent =
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
    context: Any, projectId: Long, fileId: Long, userId: Long
): AuditCandidateEvent =
    AuditCandidateEvent(
        context = context,
        auditCandidate = AuditCandidate(
            action = AuditAction.PROJECT_FILE_DOWNLOADED_FAILED,
            project = AuditProject(id = projectId.toString()),
            description = "document $fileId download failed from project application $projectId by $userId"

        )
    )

fun projectFileUploadFailed(
    context: Any,
    projectId: Long,
    fileName: String,
    projectFileCategory: ProjectFileCategory,
    userId: Long
): AuditCandidateEvent =
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

fun projectContractingMonitoringChanged(
    context: Any,
    project: ProjectSummary,
    changes: String
): AuditCandidateEvent {
    return AuditCandidateEvent(
        context = context,
        auditCandidate = AuditCandidate(
            action = AuditAction.PROJECT_CONTRACT_MONITORING_CHANGED,
            project = AuditProject(id = project.id.toString(), customIdentifier = project.customIdentifier, name = project.acronym),
            description = "Fields changed:\n$changes"
        )
    )
}

fun projectContractInfoChanged(
    context: Any,
    project: ProjectSummary,
    oldPartnershipAgreementDate: LocalDate?,
    newPartnershipAgreementDate: LocalDate?
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

fun projectContractingSectionLocked(
    context: Any,
    contractingSection: ProjectContractingSection,
    projectId: Long
): AuditCandidateEvent {
    return AuditCandidateEvent(
        context = context,
        auditCandidate = AuditCandidate(
            action = AuditAction.PROJECT_CONTRACTING_SECTION_LOCKED,
            project = AuditProject(id = projectId.toString()),
            description = "Project contracting section ${contractingSection.name} was set to Locked"
        )
    )
}

fun projectContractingSectionUnlocked(
    context: Any,
    contractingSection: ProjectContractingSection,
    projectId: Long
): AuditCandidateEvent {
    return AuditCandidateEvent(
        context = context,
        auditCandidate = AuditCandidate(
            action = AuditAction.PROJECT_CONTRACTING_SECTION_UNLOCKED,
            project = AuditProject(id = projectId.toString()),
            description = "Project contracting section ${contractingSection.name} was set to Unlocked"
        )
    )
}

fun projectContractingPartnerLocked(context: Any, partner: ProjectPartnerDetail, projectId: Long): AuditCandidateEvent {
    return AuditCandidateEvent(
        context = context,
        auditCandidate = AuditCandidate(
            action = AuditAction.PROJECT_CONTRACTING_SECTION_LOCKED,
            project = AuditProject(id = projectId.toString()),
            description = "Project contracting partner ${getPartnerName(partner)} was set to Locked"
        )
    )
}

fun projectContractingPartnerUnlocked(
    context: Any,
    partner: ProjectPartnerDetail,
    projectId: Long
): AuditCandidateEvent {
    return AuditCandidateEvent(
        context = context,
        auditCandidate = AuditCandidate(
            action = AuditAction.PROJECT_CONTRACTING_SECTION_UNLOCKED,
            project = AuditProject(id = projectId.toString()),
            description = "Project contracting partner ${getPartnerName(partner)} was set to Unlocked"
        )
    )
}

fun projectContractingPartnerBeneficialOwnerCreated(
    context: Any,
    projectSummary: ProjectSummary,
    partner: ProjectPartnerEntity,
    beneficialOwner: ContractingPartnerBeneficialOwner
): AuditCandidateEvent {

    return AuditCandidateEvent(
        context = context,
        auditCandidate = AuditBuilder(AuditAction.PROJECT_BENEFICIAL_OWNER_ADDED)
            .project(projectSummary)
            .description(
                "Project beneficial owner added to partner ${getPartnerName(partner)}: firstName '${beneficialOwner.firstName}'," +
                        " lastName '${beneficialOwner.lastName}', birth '${beneficialOwner.birth}', vatNumber '${beneficialOwner.vatNumber}';\n"
            )
            .build()
    )
}

fun projectContractingPartnerBeneficialOwnersDeleted(
    context: Any,
    projectSummary: ProjectSummary,
    partner: ProjectPartnerEntity,
    removedBeneficialOwners: Collection<ProjectContractingPartnerBeneficialOwnerEntity>
): AuditCandidateEvent {
    val listOfRemovals = StringBuilder()
    removedBeneficialOwners.forEach { owner ->
        listOfRemovals.append(
            "Project beneficial owner removed from partner ${getPartnerName(partner)}: firstName '${owner.firstName}'," +
                    " lastName '${owner.lastName}', birth '${owner.birth}', vatNumber '${owner.vatNumber}';\n"
        )
    }

    return AuditCandidateEvent(
        context = context,
        auditCandidate = AuditBuilder(AuditAction.PROJECT_BENEFICIAL_OWNER_REMOVED)
            .project(projectSummary)
            .description("$listOfRemovals")
            .build()
    )
}

fun projectContractingPartnerBeneficialOwnerChanged(
    context: Any,
    projectSummary: ProjectSummary,
    partner: ProjectPartnerEntity,
    oldBeneficialOwner: ContractingPartnerBeneficialOwner,
    newBeneficialOwner: ContractingPartnerBeneficialOwner
): AuditCandidateEvent {
    val changes = newBeneficialOwner.getDiff(old = oldBeneficialOwner).fromOldToNewChanges()

    return AuditCandidateEvent(
        context = context,
        auditCandidate = AuditBuilder(AuditAction.PROJECT_BENEFICIAL_OWNER_CHANGED)
            .project(projectSummary)
            .description("Project beneficial owner changed for partner ${getPartnerName(partner)}:\n$changes")
            .build()
    )
}

fun projectContractingPartnerBankingDetailsChanged(
    context: Any,
    projectSummary: ProjectSummary,
    partner: ProjectPartnerEntity,
    oldBankingDetails: ContractingPartnerBankingDetails?,
    newBankingDetails: ContractingPartnerBankingDetails
): AuditCandidateEvent {
    val changes = newBankingDetails.getDiff(old = oldBankingDetails).fromOldToNewChanges()

    return AuditCandidateEvent(
        context = context,
        auditCandidate = AuditBuilder(AuditAction.PROJECT_CONTRACT_PARTNER_INFO_CHANGE)
            .project(projectSummary)
            .description("Banking Details fields changed for partner ${getPartnerName(partner)}:\n$changes")
            .build()
    )
}

fun projectContractingPartnerDocumentsLocationChanged(
    context: Any,
    projectSummary: ProjectSummary,
    partner: ProjectPartnerEntity,
    oldDocumentsLocation: ContractingPartnerDocumentsLocation?,
    newDocumentsLocation: ContractingPartnerDocumentsLocation
): AuditCandidateEvent {
    val changes = newDocumentsLocation.getDiff(old = oldDocumentsLocation).fromOldToNewChanges()

    return AuditCandidateEvent(
        context = context,
        auditCandidate = AuditBuilder(AuditAction.PROJECT_CONTRACT_PARTNER_INFO_CHANGE)
            .project(projectSummary)
            .description("Documents Location fields changed for partner ${getPartnerName(partner)}:\n$changes")
            .build()
    )
}

private fun getPartnerName(partner: ProjectPartnerDetail): String =
    partner.role.isLead.let {
        if (it) "LP${partner.sortNumber}" else "PP${partner.sortNumber}"
    }
private fun getPartnerName(partnerEntity: ProjectPartnerEntity): String =
    getPartnerName(partnerEntity.toProjectPartnerDetail())
