package io.cloudflight.jems.server.project.service.application.start_second_step

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanReturnApplicationToApplicant
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.project.service.application.workflow.ApplicationStateFactory
import io.cloudflight.jems.server.project.service.projectStatusChanged
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class StartSecondStep(
    private val projectPersistence: ProjectPersistence,
    private val applicationStateFactory: ApplicationStateFactory,
    private val auditPublisher: ApplicationEventPublisher,
) : StartSecondStepInteractor {

    @CanReturnApplicationToApplicant
    @Transactional
    @ExceptionWrapper(StartSecondStepException::class)
    override fun startSecondStep(projectId: Long): ApplicationStatus =
        projectPersistence.getProjectSummary(projectId).let { projectSummary ->
            applicationStateFactory.getInstance(projectSummary).startSecondStep().also {
                auditPublisher.publishEvent(projectStatusChanged(this, projectSummary, newStatus = it))
            }
        }
}
