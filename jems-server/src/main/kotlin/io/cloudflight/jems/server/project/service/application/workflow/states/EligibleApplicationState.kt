package io.cloudflight.jems.server.project.service.application.workflow.states

import io.cloudflight.jems.server.audit.service.AuditService
import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.project.service.ProjectWorkflowPersistence
import io.cloudflight.jems.server.project.service.application.ApplicationActionInfo
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.project.service.application.workflow.ApplicationState
import io.cloudflight.jems.server.project.service.model.ProjectSummary

class EligibleApplicationState(
    override val projectSummary: ProjectSummary,
    override val projectWorkflowPersistence: ProjectWorkflowPersistence,
    override val auditService: AuditService,
    override val securityService: SecurityService
) : ApplicationState(projectSummary, projectWorkflowPersistence, auditService, securityService) {

    private val canBeRevertTo = setOf(ApplicationStatus.SUBMITTED)

    override fun returnToApplicant(): ApplicationStatus =
        returnToApplicantDefaultImpl()

    override fun revertDecision(): ApplicationStatus =
        revertCurrentStatusToPreviousStatus(canBeRevertTo).also {
            projectWorkflowPersistence.clearProjectEligibilityDecision(projectSummary.id)
        }

    override fun getPossibleStatusToRevertTo() =
        getPossibleStatusToRevertToDefaultImpl(canBeRevertTo)

    override fun approve(actionInfo: ApplicationActionInfo): ApplicationStatus =
        updateFundingDecision(ApplicationStatus.APPROVED, actionInfo)

    override fun approveWithConditions(actionInfo: ApplicationActionInfo): ApplicationStatus =
        updateFundingDecision(ApplicationStatus.APPROVED_WITH_CONDITIONS, actionInfo)

    override fun refuse(actionInfo: ApplicationActionInfo): ApplicationStatus =
        updateFundingDecision(ApplicationStatus.NOT_APPROVED, actionInfo)

}
