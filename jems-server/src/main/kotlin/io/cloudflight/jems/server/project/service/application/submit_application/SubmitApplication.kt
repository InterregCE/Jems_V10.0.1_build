package io.cloudflight.jems.server.project.service.application.submit_application

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanSubmitApplication
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.ProjectWorkflowPersistence
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.project.service.application.workflow.ApplicationStateFactory
import io.cloudflight.jems.server.project.service.create_new_project_version.CreateNewProjectVersionInteractor
import io.cloudflight.jems.server.project.service.projectStatusChanged
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class SubmitApplication(
    private val projectPersistence: ProjectPersistence,
    private val projectWorkflowPersistence: ProjectWorkflowPersistence,
    private val applicationStateFactory: ApplicationStateFactory,
    private val createNewProjectVersion: CreateNewProjectVersionInteractor,
    private val auditPublisher: ApplicationEventPublisher
) : SubmitApplicationInteractor {

    @CanSubmitApplication
    @Transactional
    @ExceptionWrapper(SubmitApplicationException::class)
    override fun submit(projectId: Long): ApplicationStatus =
        projectPersistence.getProjectSummary(projectId).let { projectSummary ->
            applicationStateFactory.getInstance(projectSummary).submit().also {
                auditPublisher.publishEvent(projectStatusChanged(this, projectSummary = projectSummary, newStatus = it))
                createNewProjectVersion.create(
                    projectId = projectSummary.id,
                    status = projectWorkflowPersistence.getLatestApplicationStatusNotEqualTo(
                        projectSummary.id,
                        ApplicationStatus.RETURNED_TO_APPLICANT
                    )
                )
            }
        }
}
