package io.cloudflight.jems.server.project.service.application.start_modification

import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanOpenModification
import io.cloudflight.jems.server.project.repository.ProjectVersionUtils
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.ProjectVersionPersistence
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.project.service.application.workflow.ApplicationStateFactory
import io.cloudflight.jems.server.project.service.projectStatusChanged
import io.cloudflight.jems.server.project.service.projectVersionRecorded
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class StartModification(
    private val projectPersistence: ProjectPersistence,
    private val projectVersionPersistence: ProjectVersionPersistence,
    private val applicationStateFactory: ApplicationStateFactory,
    private val securityService: SecurityService,
    private val auditPublisher: ApplicationEventPublisher,
) : StartModificationInteractor {

    @CanOpenModification
    @Transactional
    @ExceptionWrapper(StartModificationExceptions::class)
    override fun startModification(projectId: Long): ApplicationStatus =
        projectPersistence.getProjectSummary(projectId).let { projectSummary ->
            applicationStateFactory.getInstance(projectSummary).startModification().also {

                auditPublisher.publishEvent(projectStatusChanged(this, projectSummary, newStatus = it))

                auditPublisher.publishEvent(
                    projectVersionRecorded(
                        this,
                        projectSummary,
                        userEmail = securityService.currentUser?.user?.email!!,
                        version = ProjectVersionUtils.increaseMajor(
                            projectVersionPersistence.getLatestVersionOrNull(projectId)?.version
                        ),
                    )
                )

            }
        }
}


