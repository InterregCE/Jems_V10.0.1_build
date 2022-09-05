package io.cloudflight.jems.server.project.service.application.approve_modification

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.common.validator.GeneralValidatorService
import io.cloudflight.jems.server.controllerInstitution.service.checkInstitutionPartnerAssignment.CheckInstitutionPartnerAssignments
import io.cloudflight.jems.server.project.authorization.CanApproveModification
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.ProjectVersionPersistence
import io.cloudflight.jems.server.project.service.application.ApplicationActionInfo
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.project.service.application.ifIsValid
import io.cloudflight.jems.server.project.service.application.workflow.ApplicationStateFactory
import io.cloudflight.jems.server.project.service.contracting.reporting.updateContractingReporting.UpdateContractingReportingInteractor
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
    private val checkInstitutionPartnerAssignments: CheckInstitutionPartnerAssignments
) : ApproveModificationInteractor {

    @CanApproveModification
    @Transactional
    @ExceptionWrapper(ApproveModificationException::class)
    override fun approveModification(projectId: Long, actionInfo: ApplicationActionInfo): ApplicationStatus =
        actionInfo.ifIsValid(generalValidatorService).let {
            projectPersistence.getProjectSummary(projectId).let { projectSummary ->
                val lastVersion = projectVersionPersistence.getLatestApprovedOrCurrent(projectId)
                val lastDuration = projectPersistence.getProject(projectId, lastVersion).duration
                applicationStateFactory.getInstance(projectSummary).approveModification(actionInfo).also {
                    projectVersionPersistence.updateTimestampForApprovedModification(projectId)
                    auditPublisher.publishEvent(projectStatusChanged(this, projectSummary, newStatus = it))
                    val newDuration = projectPersistence.getProject(projectId).duration
                    if (newDuration!! < lastDuration!!) {
                        updateContractingReportingService.clearNoLongerAvailablePeriodsAndDates(projectId, newDuration)
                    }
                    checkInstitutionPartnerAssignments.checkInstitutionAssignmentsToRemoveForUpdatedPartners(projectId)
                }
            }
        }
}
