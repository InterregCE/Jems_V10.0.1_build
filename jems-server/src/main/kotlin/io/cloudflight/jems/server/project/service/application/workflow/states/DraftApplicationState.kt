package io.cloudflight.jems.server.project.service.application.workflow.states

import io.cloudflight.jems.server.audit.service.AuditService
import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.ProjectWorkflowPersistence
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.project.service.application.workflow.ApplicationState
import io.cloudflight.jems.server.project.service.application.workflow.CallIsNotOpenException
import io.cloudflight.jems.server.project.service.callAlreadyEnded
import io.cloudflight.jems.server.project.service.model.ProjectSummary
import java.time.ZonedDateTime

class DraftApplicationState(
    override val projectSummary: ProjectSummary,
    override val projectWorkflowPersistence: ProjectWorkflowPersistence,
    override val auditService: AuditService,
    override val securityService: SecurityService,
    private val projectPersistence: ProjectPersistence
) : ApplicationState(projectSummary, projectWorkflowPersistence, auditService, securityService) {

    override fun submit(): ApplicationStatus =
        ifCallIsOpen().run {
            projectWorkflowPersistence.updateApplicationFirstSubmission(
                projectId = projectSummary.id, userId = securityService.getUserIdOrThrow(), status = ApplicationStatus.SUBMITTED
            )
        }

    private fun ifCallIsOpen() {
        projectPersistence.getProjectCallSettings(projectSummary.id).also { projectCallSettings ->
            if (ZonedDateTime.now().isBefore(projectCallSettings.startDate) ||
                ZonedDateTime.now().isAfter(projectCallSettings.endDate)
            ) {
                auditService.logEvent(callAlreadyEnded(projectCallSettings.callId))

                throw CallIsNotOpenException()
            }
        }
    }
}
