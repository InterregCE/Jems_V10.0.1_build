package io.cloudflight.jems.server.project.service.application.approve_modification

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.common.validator.GeneralValidatorService
import io.cloudflight.jems.server.controllerInstitution.service.ControllerInstitutionPersistence
import io.cloudflight.jems.server.controllerInstitution.service.checkInstitutionPartnerAssignment.CheckInstitutionPartnerAssignments
import io.cloudflight.jems.server.notification.handler.ProjectStatusChangeEvent
import io.cloudflight.jems.server.project.authorization.CanApproveModification
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.ProjectVersionPersistence
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.project.service.application.ifIsValid
import io.cloudflight.jems.server.project.service.application.workflow.ApplicationStateFactory
import io.cloudflight.jems.server.project.service.auditAndControl.correction.AuditControlCorrectionPersistence
import io.cloudflight.jems.server.project.service.contracting.reporting.updateContractingReporting.UpdateContractingReportingInteractor
import io.cloudflight.jems.server.project.service.model.ProjectModificationCreate
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ApproveModification(
    private val projectPersistence: ProjectPersistence,
    private val projectVersionPersistence: ProjectVersionPersistence,
    private val generalValidatorService: GeneralValidatorService,
    private val applicationStateFactory: ApplicationStateFactory,
    private val auditPublisher: ApplicationEventPublisher,
    private val updateContractingReportingService: UpdateContractingReportingInteractor,
    private val checkInstitutionPartnerAssignments: CheckInstitutionPartnerAssignments,
    private val partnerPersistence: PartnerPersistence,
    private val controllerInstitutionPersistence: ControllerInstitutionPersistence,
    private val auditControlCorrectionPersistence: AuditControlCorrectionPersistence,
) : ApproveModificationInteractor {

    @CanApproveModification
    @Transactional
    @ExceptionWrapper(ApproveModificationException::class)
    override fun approveModification(projectId: Long, modification: ProjectModificationCreate): ApplicationStatus =
        modification.actionInfo.ifIsValid(generalValidatorService).let {
            validateCorrections(projectId, modification.correctionIds)
            projectPersistence.getProjectSummary(projectId).let { projectSummary ->
                applicationStateFactory.getInstance(projectSummary).approveModification(modification.actionInfo).also {
                    projectVersionPersistence.updateTimestampForApprovedModification(projectId)
                    // update versioned data inside institution-assignment table
                    controllerInstitutionPersistence.updatePartnerDataInAssignments(
                        partners = partnerPersistence.getCurrentPartnerAssignmentMetadata(projectId)
                    )
                    auditPublisher.publishEvent(ProjectStatusChangeEvent(this, projectSummary, it))
                    updateContractingReportingService.checkNoLongerAvailablePeriodsAndDatesToRemove(projectId)
                    checkInstitutionPartnerAssignments.checkInstitutionAssignmentsToRemoveForUpdatedPartners(projectId)
                    auditControlCorrectionPersistence.updateModificationByCorrectionIds(
                        projectId = projectId,
                        correctionIds = modification.correctionIds,
                        statuses = listOf(ApplicationStatus.APPROVED, ApplicationStatus.CONTRACTED)
                    )
                }
            }
        }

    private fun validateCorrections(projectId: Long, correctionIds: Set<Long>) {
        val availableCorrectionIds = auditControlCorrectionPersistence.getAvailableCorrectionsForModification(projectId)
            .mapTo(HashSet()) { it.id }
        val invalidCorrectionIds = correctionIds.minus(availableCorrectionIds)
        if (invalidCorrectionIds.isNotEmpty()) {
            throw CorrectionsNotValidException(invalidCorrectionIds)
        }
    }

}
