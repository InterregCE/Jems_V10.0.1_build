package io.cloudflight.jems.server.project.service.application.workflow.states

import io.cloudflight.jems.server.audit.service.AuditService
import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.ProjectWorkflowPersistence
import io.cloudflight.jems.server.project.service.application.ApplicationActionInfo
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.project.service.application.workflow.ApplicationState
import io.cloudflight.jems.server.project.service.model.ProjectSummary

class SubmittedApplicationState(
    override val projectSummary: ProjectSummary,
    override val projectWorkflowPersistence: ProjectWorkflowPersistence,
    override val auditService: AuditService,
    override val securityService: SecurityService,
    override val projectPersistence: ProjectPersistence
) : ApplicationState(projectSummary, projectWorkflowPersistence, auditService, securityService, projectPersistence) {

    override fun returnToApplicant(): ApplicationStatus =
        returnToApplicantDefaultImpl()

    override fun setAsIneligible(actionInfo: ApplicationActionInfo): ApplicationStatus =
        updateEligibilityDecision(ApplicationStatus.INELIGIBLE, actionInfo)

    override fun setAsEligible(actionInfo: ApplicationActionInfo): ApplicationStatus =
        updateEligibilityDecision(ApplicationStatus.ELIGIBLE, actionInfo)

}


