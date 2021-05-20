package io.cloudflight.jems.server.project.service.application.workflow.states.first_step

import io.cloudflight.jems.server.audit.service.AuditService
import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.ProjectWorkflowPersistence
import io.cloudflight.jems.server.project.service.application.ApplicationActionInfo
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.project.service.application.workflow.ApplicationState
import io.cloudflight.jems.server.project.service.model.ProjectSummary

class FirstStepEligibleApplicationState (
    override val projectSummary: ProjectSummary,
    override val projectWorkflowPersistence: ProjectWorkflowPersistence,
    override val auditService: AuditService,
    override val securityService: SecurityService,
    override val projectPersistence: ProjectPersistence
) : ApplicationState(projectSummary, projectWorkflowPersistence, auditService, securityService, projectPersistence) {

    private val canBeRevertTo = setOf(ApplicationStatus.STEP1_SUBMITTED)

    override fun revertDecision(): ApplicationStatus =
        revertCurrentStatusToPreviousStatus(canBeRevertTo).also {
            projectWorkflowPersistence.clearProjectEligibilityDecision(projectSummary.id)
        }

    override fun getPossibleStatusToRevertTo() =
        getPossibleStatusToRevertToDefaultImpl(canBeRevertTo)

    override fun approve(actionInfo: ApplicationActionInfo): ApplicationStatus =
        updateFundingDecision(ApplicationStatus.STEP1_APPROVED, actionInfo)

    override fun approveWithConditions(actionInfo: ApplicationActionInfo): ApplicationStatus =
        updateFundingDecision(ApplicationStatus.STEP1_APPROVED_WITH_CONDITIONS, actionInfo)

    override fun refuse(actionInfo: ApplicationActionInfo): ApplicationStatus =
        updateFundingDecision(ApplicationStatus.STEP1_NOT_APPROVED, actionInfo)

}