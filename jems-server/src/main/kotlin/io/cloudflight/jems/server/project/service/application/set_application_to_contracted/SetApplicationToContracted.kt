package io.cloudflight.jems.server.project.service.application.set_application_to_contracted

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanSetProjectToContracted
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.project.service.application.workflow.ApplicationStateFactory
import io.cloudflight.jems.server.project.service.projectStatusChanged
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class SetApplicationToContracted(
    private val projectPersistence: ProjectPersistence,
    private val applicationStateFactory: ApplicationStateFactory,
    private val auditPublisher: ApplicationEventPublisher
) : SetApplicationToContractedInteractor {

    @CanSetProjectToContracted
    @Transactional
    @ExceptionWrapper(SetApplicationToContractedException::class)
    override fun setApplicationToContracted(projectId: Long): ApplicationStatus =
        projectPersistence.getProjectSummary(projectId).let { projectSummary ->
            applicationStateFactory.getInstance(projectSummary).setToContracted().also {
                auditPublisher.publishEvent(projectStatusChanged(this, projectSummary, newStatus = it))
            }
        }
}
