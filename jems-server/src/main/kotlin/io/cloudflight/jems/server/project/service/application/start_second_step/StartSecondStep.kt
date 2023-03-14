package io.cloudflight.jems.server.project.service.application.start_second_step

import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanStartSecondStep
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
class StartSecondStep(
    private val projectPersistence: ProjectPersistence,
    private val applicationStateFactory: ApplicationStateFactory,
    private val createNewProjectVersion: CreateNewProjectVersionInteractor,
    private val securityService: SecurityService,
    private val auditPublisher: ApplicationEventPublisher,
) : StartSecondStepInteractor {

    @CanStartSecondStep
    @Transactional
    @ExceptionWrapper(StartSecondStepException::class)
    override fun startSecondStep(projectId: Long): ApplicationStatus =
        createNewProjectVersion.create(projectId).let { newProjectVersion ->
            projectPersistence.getProjectSummary(projectId).let { projectSummary ->
                applicationStateFactory.getInstance(projectSummary).startSecondStep().also {
                    auditPublisher.publishEvent(projectStatusChanged(projectSummary, newStatus = it))
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
