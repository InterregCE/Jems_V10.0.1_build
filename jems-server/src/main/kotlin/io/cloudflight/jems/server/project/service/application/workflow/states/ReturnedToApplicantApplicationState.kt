package io.cloudflight.jems.server.project.service.application.workflow.states

import io.cloudflight.jems.server.audit.service.AuditService
import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.project.service.application.workflow.ApplicationState
import io.cloudflight.jems.server.project.service.application.workflow.ApplicationStateFactory
import io.cloudflight.jems.server.project.service.application.workflow.ReturnToApplicantIsNotPossibleException
import io.cloudflight.jems.server.project.service.model.ProjectSummary

class ReturnedToApplicantApplicationState(
    override val projectSummary: ProjectSummary,
    override val projectPersistence: ProjectPersistence,
    override val auditService: AuditService,
    override val securityService: SecurityService
) : ApplicationState(projectSummary, projectPersistence, auditService, securityService) {

    override fun submit() =
        ifPreviousStateIsValid().also { previousStatus ->
            projectPersistence.updateProjectLastResubmission(
                projectId = projectSummary.id,
                userId = securityService.getUserIdOrThrow(),
                status = previousStatus
            )
        }


    private fun ifPreviousStateIsValid(): ApplicationStatus =
        projectPersistence.getApplicationPreviousStatus(projectSummary.id).also { previousStatus ->
            when (previousStatus) {
                ApplicationStatus.SUBMITTED, ApplicationStatus.ELIGIBLE, ApplicationStatus.APPROVED, ApplicationStatus.APPROVED_WITH_CONDITIONS -> Unit
                else -> throw ReturnToApplicantIsNotPossibleException(
                    fromStatus = projectSummary.status,
                    toStatus = previousStatus
                )
            }
        }

}
