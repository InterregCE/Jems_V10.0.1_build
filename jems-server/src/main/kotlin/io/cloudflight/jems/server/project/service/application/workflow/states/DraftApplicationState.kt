package io.cloudflight.jems.server.project.service.application.workflow.states

import io.cloudflight.jems.server.audit.service.AuditService
import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.project.service.application.workflow.ApplicationState
import io.cloudflight.jems.server.project.service.application.workflow.ApplicationStateFactory
import io.cloudflight.jems.server.project.service.application.workflow.CallIsNotOpenException
import io.cloudflight.jems.server.project.service.callAlreadyEnded
import io.cloudflight.jems.server.project.service.model.ProjectSummary
import java.time.ZonedDateTime

class DraftApplicationState(
    override val projectSummary: ProjectSummary,
    override val projectPersistence: ProjectPersistence,
    override val auditService: AuditService,
    override val securityService: SecurityService,
) : ApplicationState(projectSummary, projectPersistence, auditService, securityService) {

    override fun submit(): ApplicationStatus =
        ifCallIsOpen().run {
            projectPersistence.updateApplicationFirstSubmission(
                projectId = projectSummary.id, userId = securityService.getUserIdOrThrow()
            )
        }

    private fun ifCallIsOpen() {
        projectPersistence.getProjectCallSettings(projectSummary.id).also { projectCallSettings ->
            if (ZonedDateTime.now().isBefore(projectCallSettings.startDate) ||
                ZonedDateTime.now().isAfter(projectCallSettings.endDate)
            ) {
                // todo do we need this audit log
                auditService.logEvent(callAlreadyEnded(projectCallSettings.callId))

                throw CallIsNotOpenException()
            }
        }
    }
}
