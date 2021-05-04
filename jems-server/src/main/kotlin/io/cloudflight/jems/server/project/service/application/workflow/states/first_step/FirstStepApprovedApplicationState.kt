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

class FirstStepApprovedApplicationState (
    override val projectSummary: ProjectSummary,
    override val projectWorkflowPersistence: ProjectWorkflowPersistence,
    override val auditService: AuditService,
    override val securityService: SecurityService
) : ApplicationState(projectSummary, projectWorkflowPersistence, auditService, securityService) {

    private val canBeRevertTo = setOf(ApplicationStatus.STEP1_ELIGIBLE, ApplicationStatus.STEP1_APPROVED_WITH_CONDITIONS)

    override fun startSecondStep(): ApplicationStatus =
        startSecondStepDefaultImpl()

    override fun revertDecision(): ApplicationStatus =
        revertCurrentStatusToPreviousStatus(validRevertStatuses = canBeRevertTo).also { reestablishedStatus ->
            when (reestablishedStatus) {
                ApplicationStatus.STEP1_APPROVED -> projectWorkflowPersistence.clearProjectFundingDecision(projectSummary.id)
                ApplicationStatus.STEP1_ELIGIBLE -> projectWorkflowPersistence.clearProjectFundingDecision(projectSummary.id)
                ApplicationStatus.STEP1_APPROVED_WITH_CONDITIONS -> projectWorkflowPersistence.resetProjectFundingDecisionToCurrentStatus(
                    projectSummary.id
                )
                else -> Unit
            }
        }

    override fun getPossibleStatusToRevertTo() =
        getPossibleStatusToRevertToDefaultImpl(canBeRevertTo)

}