package io.cloudflight.jems.server.project.service.application.start_second_step

import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.project.authorization.CanStartSecondStep
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.ProjectVersionPersistence
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.project.service.application.workflow.ApplicationStateFactory
import io.cloudflight.jems.server.project.service.projectStatusChanged
import io.cloudflight.jems.server.project.service.projectVersionRecorded
import java.time.ZoneOffset
import java.time.ZonedDateTime
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class StartSecondStep(
    private val projectPersistence: ProjectPersistence,
    private val projectVersionPersistence: ProjectVersionPersistence,
    private val applicationStateFactory: ApplicationStateFactory,
    private val securityService: SecurityService,
    private val auditPublisher: ApplicationEventPublisher,
) : StartSecondStepInteractor {

    @CanStartSecondStep
    @Transactional
    override fun startSecondStep(projectId: Long): ApplicationStatus =
        projectPersistence.getProjectSummary(projectId).let { projectSummary ->
            applicationStateFactory.getInstance(projectSummary).startSecondStep().also {

                auditPublisher.publishEvent(projectStatusChanged(this, projectSummary, newStatus = it))

                auditPublisher.publishEvent(
                    projectVersionRecorded(
                        this,
                        projectSummary,
                        userEmail = securityService.currentUser?.user?.email!!,
                        version = projectVersionPersistence.getLatestVersionOrNull(projectId)
                            ?.let { projectVersion -> projectVersion.version + 1 } ?: 1,
                        createdAt = ZonedDateTime.now(ZoneOffset.UTC)
                    )
                )

            }
        }
}
