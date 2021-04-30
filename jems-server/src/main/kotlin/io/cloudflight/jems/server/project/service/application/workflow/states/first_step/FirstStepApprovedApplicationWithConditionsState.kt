package io.cloudflight.jems.server.project.service.application.workflow.states.first_step

import io.cloudflight.jems.server.audit.service.AuditService
import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.ProjectWorkflowPersistence
import io.cloudflight.jems.server.project.service.application.ApplicationActionInfo
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.project.service.application.workflow.ApplicationState
import io.cloudflight.jems.server.project.service.application.workflow.CallIsNotOpenException
import io.cloudflight.jems.server.project.service.callAlreadyEnded
import io.cloudflight.jems.server.project.service.model.ProjectSummary
import java.time.ZonedDateTime

class FirstStepApprovedApplicationWithConditionsState (
    override val projectSummary: ProjectSummary,
    override val projectWorkflowPersistence: ProjectWorkflowPersistence,
    override val auditService: AuditService,
    override val securityService: SecurityService
) : ApplicationState(projectSummary, projectWorkflowPersistence, auditService, securityService) {

    private val canBeRevertTo = setOf(ApplicationStatus.STEP1_ELIGIBLE)

    override fun approve(actionInfo: ApplicationActionInfo): ApplicationStatus =
        updateCurrentStatus(ApplicationStatus.STEP1_APPROVED, actionInfo)

    override fun refuse(actionInfo: ApplicationActionInfo): ApplicationStatus =
        updateCurrentStatus(ApplicationStatus.STEP1_NOT_APPROVED, actionInfo)

    override fun startSecondStep(): ApplicationStatus =
        startSecondStepDefaultImpl()

    override fun revertDecision(): ApplicationStatus =
        revertCurrentStatusToPreviousStatus(validRevertStatuses = canBeRevertTo).also {
            projectWorkflowPersistence.clearProjectFundingDecision(projectSummary.id)
        }

    override fun getPossibleStatusToRevertTo() =
        getPossibleStatusToRevertToDefaultImpl(canBeRevertTo)

    private fun updateCurrentStatus(targetStatus: ApplicationStatus, actionInfo: ApplicationActionInfo) =
        ifFundingDecisionDateIsValid(actionInfo.date).run {
            projectWorkflowPersistence.updateProjectCurrentStatus(
                projectSummary.id,
                securityService.getUserIdOrThrow(),
                targetStatus,
                actionInfo
            )
        }
}