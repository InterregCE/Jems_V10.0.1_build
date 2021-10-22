package io.cloudflight.jems.server.project.service.application.hand_back_to_applicant

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanReturnApplicationToApplicant
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.ProjectWorkflowPersistence
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.project.service.application.return_application_to_applicant.ReturnApplicationToApplicantException
import io.cloudflight.jems.server.project.service.application.workflow.ApplicationStateFactory
import io.cloudflight.jems.server.project.service.projectStatusChanged
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class HandBackToApplicant(
    private val projectPersistence: ProjectPersistence,
    private val projectWorkflowPersistence: ProjectWorkflowPersistence,
    private val applicationStateFactory: ApplicationStateFactory,
    private val auditPublisher: ApplicationEventPublisher
) : HandBackToApplicantInteractor {
    @CanReturnApplicationToApplicant
    @Transactional
    @ExceptionWrapper(ReturnApplicationToApplicantException::class)
    override fun handBackToApplicant(projectId: Long): ApplicationStatus =
        projectPersistence.getProjectSummary(projectId).let { projectSummary ->
            applicationStateFactory.getInstance(projectSummary).handBackToApplicant().also {
                auditPublisher.publishEvent(projectStatusChanged(this, projectSummary, newStatus = it))
            }
        }
}
