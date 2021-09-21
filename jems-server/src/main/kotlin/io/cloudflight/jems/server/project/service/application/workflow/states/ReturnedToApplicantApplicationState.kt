package io.cloudflight.jems.server.project.service.application.workflow.states

import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.ProjectWorkflowPersistence
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.project.service.application.workflow.ApplicationState
import io.cloudflight.jems.server.project.service.application.workflow.ReturnToApplicantIsNotPossibleException
import io.cloudflight.jems.server.project.service.model.ProjectStatus
import io.cloudflight.jems.server.project.service.model.ProjectSummary
import org.springframework.context.ApplicationEventPublisher

class ReturnedToApplicantApplicationState(
    override val projectSummary: ProjectSummary,
    override val projectWorkflowPersistence: ProjectWorkflowPersistence,
    override val auditPublisher: ApplicationEventPublisher,
    override val securityService: SecurityService,
    override val projectPersistence: ProjectPersistence
) : ApplicationState(projectSummary, projectWorkflowPersistence, auditPublisher, securityService, projectPersistence) {

    override fun submit() =
        ifPreviousStateIsValid().also { previousStatus ->
            projectWorkflowPersistence.updateProjectLastResubmission(
                projectId = projectSummary.id,
                userId = securityService.getUserIdOrThrow(),
                status = previousStatus
            )
        }.status


    private fun ifPreviousStateIsValid(): ProjectStatus =
        projectWorkflowPersistence.getApplicationPreviousStatus(projectSummary.id).also { previousStatus ->
            when (previousStatus.status) {
                ApplicationStatus.SUBMITTED, ApplicationStatus.ELIGIBLE, ApplicationStatus.APPROVED, ApplicationStatus.APPROVED_WITH_CONDITIONS -> Unit
                else -> throw ReturnToApplicantIsNotPossibleException(
                    fromStatus = projectSummary.status,
                    toStatus = previousStatus.status
                )
            }
        }

}
