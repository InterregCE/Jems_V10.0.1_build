package io.cloudflight.jems.server.project.service.application.revert_application_decision

import io.cloudflight.jems.server.authentication.authorization.IsAdmin
import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.project.service.application.workflow.ApplicationStateFactory
import io.cloudflight.jems.server.project.service.projectStatusChanged
import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional


@Service
class RevertApplicationDecision(
    private val projectPersistence: ProjectPersistence,
    private val applicationStateFactory: ApplicationStateFactory,
    private val auditPublisher: ApplicationEventPublisher
) : RevertApplicationDecisionInteractor {
    companion object {
        private val log = LoggerFactory.getLogger(this::class.java)
    }

    @IsAdmin
    @Transactional
    @ExceptionWrapper(RevertApplicationDecisionException::class)
    override fun revert(projectId: Long): ApplicationStatus =
        projectPersistence.getProjectSummary(projectId).let { projectSummary ->
            applicationStateFactory.getInstance(projectSummary).revertDecision().also {
                auditPublisher.publishEvent(projectStatusChanged(this, projectSummary, newStatus = it))
                log.warn("Decision-reversion has been done for project(id=$projectId) status moved from ${projectSummary.status} to $it")
            }
        }
}
