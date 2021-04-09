package io.cloudflight.jems.server.project.service.application.return_application_to_applicant

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanReturnApplicationToApplicant
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.application.ApplicationActionInfo
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.project.service.application.workflow.ApplicationStateFactory
import io.cloudflight.jems.server.project.service.projectStatusChanged
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ReturnApplicationToApplicant(
    private val projectPersistence: ProjectPersistence,
    private val applicationStateFactory: ApplicationStateFactory,
    private val auditPublisher: ApplicationEventPublisher
) : ReturnApplicationToApplicantInteractor {

    @CanReturnApplicationToApplicant
    @Transactional
    @ExceptionWrapper(ReturnApplicationToApplicantException::class)
    override fun returnToApplicant(projectId: Long): ApplicationStatus =
        projectPersistence.getProjectSummary(projectId).let { projectSummary ->
            applicationStateFactory.getInstance(projectSummary).returnToApplicant().also {
                auditPublisher.publishEvent(projectStatusChanged(projectSummary = projectSummary, newStatus = it))
            }
        }
}
