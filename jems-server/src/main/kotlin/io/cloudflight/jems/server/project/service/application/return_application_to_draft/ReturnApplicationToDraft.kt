package io.cloudflight.jems.server.project.service.application.return_application_to_draft

import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.project.authorization.CanReturnApplicationToApplicant
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.ProjectVersionPersistence
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.project.service.application.workflow.ApplicationStateFactory
import io.cloudflight.jems.server.project.service.projectStatusChanged
import io.cloudflight.jems.server.project.service.projectVersionRecorded
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.ZoneOffset
import java.time.ZonedDateTime

@Service
class ReturnApplicationToDraft(
    private val projectPersistence: ProjectPersistence,
    private val projectVersionPersistence: ProjectVersionPersistence,
    private val applicationStateFactory: ApplicationStateFactory,
    private val securityService: SecurityService,
    private val auditPublisher: ApplicationEventPublisher,
) : ReturnApplicationToDraftInteractor {

    @CanReturnApplicationToApplicant
    @Transactional
    override fun returnToDraft(projectId: Long): ApplicationStatus =
        projectPersistence.getProjectSummary(projectId).let { projectSummary ->
            applicationStateFactory.getInstance(projectSummary).returnToDraft().also {

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
