package io.cloudflight.jems.server.project.service.application.approve_modification

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.common.validator.GeneralValidatorService
import io.cloudflight.jems.server.controllerInstitution.service.checkInstitutionPartnerAssignment.CheckInstitutionPartnerAssignments
import io.cloudflight.jems.server.payments.PaymentPersistence
import io.cloudflight.jems.server.project.authorization.CanApproveModification
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.ProjectVersionPersistence
import io.cloudflight.jems.server.project.service.application.ApplicationActionInfo
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.project.service.application.ifIsValid
import io.cloudflight.jems.server.project.service.application.workflow.ApplicationStateFactory
import io.cloudflight.jems.server.project.service.contracting.reporting.updateContractingReporting.UpdateContractingReportingInteractor
import io.cloudflight.jems.server.project.service.lumpsum.ProjectLumpSumPersistence
import io.cloudflight.jems.server.project.service.projectStatusChanged
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
    private val paymentPersistence: PaymentPersistence,
    private val projectLumpSumPersistence: ProjectLumpSumPersistence
) : ApproveModificationInteractor {

    @CanApproveModification
    @Transactional
    @ExceptionWrapper(ApproveModificationException::class)
    override fun approveModification(projectId: Long, actionInfo: ApplicationActionInfo): ApplicationStatus {
        val newStatus = actionInfo.ifIsValid(generalValidatorService).let {
            projectPersistence.getProjectSummary(projectId).let { projectSummary ->
                applicationStateFactory.getInstance(projectSummary).approveModification(actionInfo).also {
                    projectVersionPersistence.updateTimestampForApprovedModification(projectId)
                    auditPublisher.publishEvent(projectStatusChanged(this, projectSummary, newStatus = it))
                    updateContractingReportingService.checkNoLongerAvailablePeriodsAndDatesToRemove(projectId)
                    checkInstitutionPartnerAssignments.checkInstitutionAssignmentsToRemoveForUpdatedPartners(projectId)
                }
            }
        }
        this.updatePaymentsToProjects(projectId)

        return newStatus
    }

    private fun updatePaymentsToProjects(projectId: Long) {
        val projectLumpSumList = this.projectLumpSumPersistence.getLumpSums(projectId, this.projectVersionPersistence.getLatestApprovedOrCurrent(projectId))
        val calculatedAmountsToBeAdded = this.paymentPersistence.getAmountPerPartnerByProjectIdAndLumpSumOrderNrIn(
            projectId,
            projectLumpSumList.filter { it.readyForPayment }.mapIndexed { index, _ ->  index + 1 }.toMutableSet()
        )

        this.paymentPersistence.deleteAllByProjectId(projectId)
        if(calculatedAmountsToBeAdded.isNotEmpty())
            this.paymentPersistence.savePaymentToProjects(projectId, calculatedAmountsToBeAdded)
    }
}
