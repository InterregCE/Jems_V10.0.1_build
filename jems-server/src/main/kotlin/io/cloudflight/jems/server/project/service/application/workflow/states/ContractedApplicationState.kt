package io.cloudflight.jems.server.project.service.application.workflow.states

import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.ProjectWorkflowPersistence
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.project.service.application.workflow.ApplicationState
import io.cloudflight.jems.server.project.service.model.ProjectSummary
import org.springframework.context.ApplicationEventPublisher

class ContractedApplicationState(
    override val projectSummary: ProjectSummary,
    override val projectWorkflowPersistence: ProjectWorkflowPersistence,
    override val auditPublisher: ApplicationEventPublisher,
    override val securityService: SecurityService,
    override val projectPersistence: ProjectPersistence
) : ApplicationState(projectSummary, projectWorkflowPersistence, auditPublisher, securityService, projectPersistence) {

    override fun startModification(): ApplicationStatus {
        return projectWorkflowPersistence.updateProjectCurrentStatus(
            projectId = projectSummary.id,
            userId = securityService.getUserIdOrThrow(),
            status = ApplicationStatus.IN_MODIFICATION
        )
    }

    override fun setToClosed(): ApplicationStatus {
        return projectWorkflowPersistence.updateProjectCurrentStatus(
            projectId = projectSummary.id,
            userId = securityService.getUserIdOrThrow(),
            status = ApplicationStatus.CLOSED
        )
    }
}
