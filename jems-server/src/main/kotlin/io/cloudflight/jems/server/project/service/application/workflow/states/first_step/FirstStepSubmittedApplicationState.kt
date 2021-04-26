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

class FirstStepSubmittedApplicationState (
    override val projectSummary: ProjectSummary,
    override val projectWorkflowPersistence: ProjectWorkflowPersistence,
    override val auditService: AuditService,
    override val securityService: SecurityService
) : ApplicationState(projectSummary, projectWorkflowPersistence, auditService, securityService) {


    override fun returnToApplicant(): ApplicationStatus =
        returnToApplicantDefaultImpl()

    override fun setAsIneligible(actionInfo: ApplicationActionInfo): ApplicationStatus =
        updateEligibilityDecision(ApplicationStatus.STEP1_INELIGIBLE, actionInfo)

    override fun setAsEligible(actionInfo: ApplicationActionInfo): ApplicationStatus =
        updateEligibilityDecision(ApplicationStatus.STEP1_ELIGIBLE, actionInfo)


    private fun updateEligibilityDecision(targetStatus: ApplicationStatus, actionInfo: ApplicationActionInfo) =
        projectWorkflowPersistence.updateProjectEligibilityDecision(
            projectId = projectSummary.id,
            userId = securityService.getUserIdOrThrow(),
            status = targetStatus,
            actionInfo = actionInfo
        )
}