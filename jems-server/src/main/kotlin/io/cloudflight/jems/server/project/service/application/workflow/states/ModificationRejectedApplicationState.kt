package io.cloudflight.jems.server.project.service.application.workflow.states

import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.ProjectWorkflowPersistence
import io.cloudflight.jems.server.project.service.application.workflow.ApplicationState
import io.cloudflight.jems.server.project.service.model.ProjectSummary
import org.springframework.context.ApplicationEventPublisher

class ModificationRejectedApplicationState(
    override val projectSummary: ProjectSummary,
    override val projectWorkflowPersistence: ProjectWorkflowPersistence,
    override val auditPublisher: ApplicationEventPublisher,
    override val securityService: SecurityService,
    override val projectPersistence: ProjectPersistence
) : ApplicationState(projectSummary, projectWorkflowPersistence, auditPublisher, securityService, projectPersistence)
