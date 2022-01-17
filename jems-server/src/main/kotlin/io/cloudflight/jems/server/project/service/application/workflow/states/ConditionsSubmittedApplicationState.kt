package io.cloudflight.jems.server.project.service.application.workflow.states

import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.project.authorization.ProjectAuthorization
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.ProjectWorkflowPersistence
import io.cloudflight.jems.server.project.service.application.ApplicationActionInfo
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.project.service.application.hand_back_to_applicant.HandBackToApplicantAccessDeniedException
import io.cloudflight.jems.server.project.service.application.workflow.ApplicationState
import io.cloudflight.jems.server.project.service.model.ProjectSummary
import io.cloudflight.jems.server.user.service.model.UserRolePermission
import org.springframework.context.ApplicationEventPublisher


class ConditionsSubmittedApplicationState(
    override val projectSummary: ProjectSummary,
    override val projectWorkflowPersistence: ProjectWorkflowPersistence,
    override val auditPublisher: ApplicationEventPublisher,
    override val securityService: SecurityService,
    override val projectPersistence: ProjectPersistence,
    private val projectAuthorization: ProjectAuthorization
) : ApplicationState(projectSummary, projectWorkflowPersistence, auditPublisher, securityService, projectPersistence) {

    override fun approve(actionInfo: ApplicationActionInfo): ApplicationStatus =
        updateFundingDecision(ApplicationStatus.APPROVED, actionInfo)

    override fun refuse(actionInfo: ApplicationActionInfo): ApplicationStatus =
        updateFundingDecision(ApplicationStatus.NOT_APPROVED, actionInfo)

    override fun handBackToApplicant(): ApplicationStatus =
        if (projectAuthorization.hasPermission(UserRolePermission.ProjectStatusReturnToApplicant, projectSummary.id))
            returnToApplicantDefaultImpl(ApplicationStatus.RETURNED_TO_APPLICANT_FOR_CONDITIONS)
        else
            throw HandBackToApplicantAccessDeniedException()
}


