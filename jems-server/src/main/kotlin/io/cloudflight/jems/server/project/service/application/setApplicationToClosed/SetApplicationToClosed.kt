package io.cloudflight.jems.server.project.service.application.setApplicationToClosed

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanSetProjectToContracted
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.notification.handler.ProjectStatusChangeEvent
import io.cloudflight.jems.server.project.service.application.workflow.ApplicationStateFactory
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class SetApplicationToClosed(
    private val projectPersistence: ProjectPersistence,
    private val applicationStateFactory: ApplicationStateFactory,
    private val auditPublisher: ApplicationEventPublisher
) : SetApplicationToClosedInteractor {

    @CanSetProjectToContracted
    @Transactional
    @ExceptionWrapper(SetApplicationToClosedException::class)
    override fun setApplicationToClosed(projectId: Long): ApplicationStatus {
        val project = projectPersistence.getProjectSummary(projectId)
        val statusInstance = applicationStateFactory.getInstance(project)
        return statusInstance.setToClosed()
            .also {
                auditPublisher.publishEvent(ProjectStatusChangeEvent(this, project, it))
            }
    }

}
