package io.cloudflight.jems.server.project.service.application.return_application_to_applicant

import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanReturnApplicationToApplicant
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.project.service.application.workflow.ApplicationStateFactory
import io.cloudflight.jems.server.project.service.projectStatusChanged
import io.cloudflight.jems.server.project.service.projectVersionRecorded
import io.cloudflight.jems.server.project.service.save_project_version.CreateNewProjectVersionInteractor
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ReturnApplicationToApplicant(
    private val projectPersistence: ProjectPersistence,
    private val applicationStateFactory: ApplicationStateFactory,
    private val createNewProjectVersion: CreateNewProjectVersionInteractor,
    private val securityService: SecurityService,
    private val auditPublisher: ApplicationEventPublisher,
) : ReturnApplicationToApplicantInteractor {

    @CanReturnApplicationToApplicant
    @Transactional
    @ExceptionWrapper(ReturnApplicationToApplicantException::class)
    override fun returnToApplicant(projectId: Long): ApplicationStatus =
        createNewProjectVersion.create(projectId, ApplicationStatus.RETURNED_TO_APPLICANT).let { newProjectVersion ->
            projectPersistence.getProjectSummary(projectId).let { projectSummary ->
                applicationStateFactory.getInstance(projectSummary).returnToApplicant().also {
                    auditPublisher.publishEvent(projectStatusChanged(this, projectSummary, newStatus = it))
                    auditPublisher.publishEvent(
                        projectVersionRecorded(
                            this, projectSummary,
                            userEmail = securityService.currentUser?.user?.email!!,
                            version = newProjectVersion.version
                        )
                    )
                }
            }
        }
}
