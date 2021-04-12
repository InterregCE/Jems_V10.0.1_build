package io.cloudflight.jems.server.project.service.application.approve_application

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanApproveApplication
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.application.ApplicationActionInfo
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.project.service.application.workflow.ApplicationStateFactory
import io.cloudflight.jems.server.project.service.projectStatusChanged
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ApproveApplication(
    private val projectPersistence: ProjectPersistence,
    private val applicationStateFactory: ApplicationStateFactory,
    private val auditPublisher: ApplicationEventPublisher
) : ApproveApplicationInteractor {

    @CanApproveApplication
    @Transactional
    @ExceptionWrapper(ApproveApplicationException::class)
    override fun approve(projectId: Long, actionInfo: ApplicationActionInfo): ApplicationStatus =
        projectPersistence.getProjectSummary(projectId).let { projectSummary ->
            applicationStateFactory.getInstance(projectSummary).approve(actionInfo).also {
                auditPublisher.publishEvent(projectStatusChanged(this, projectSummary = projectSummary, newStatus = it))
            }
        }
}
