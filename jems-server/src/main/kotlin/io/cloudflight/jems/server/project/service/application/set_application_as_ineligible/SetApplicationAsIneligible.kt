package io.cloudflight.jems.server.project.service.application.set_application_as_ineligible

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanSetApplicationAsIneligible
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.application.ApplicationActionInfo
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.project.service.application.workflow.ApplicationStateFactory
import io.cloudflight.jems.server.project.service.projectStatusChanged
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class SetApplicationAsIneligible(
    private val projectPersistence: ProjectPersistence,
    private val applicationStateFactory: ApplicationStateFactory,
    private val auditPublisher: ApplicationEventPublisher
) : SetApplicationAsIneligibleInteractor {

    @CanSetApplicationAsIneligible
    @Transactional
    @ExceptionWrapper(SetApplicationAsIneligibleException::class)
    override fun setAsIneligible(projectId: Long, actionInfo: ApplicationActionInfo): ApplicationStatus =
        projectPersistence.getProjectSummary(projectId).let { projectSummary ->
            applicationStateFactory.getInstance(projectSummary).setAsIneligible(actionInfo).also {
                auditPublisher.publishEvent(projectStatusChanged(projectSummary = projectSummary, newStatus = it))
            }
        }
}
