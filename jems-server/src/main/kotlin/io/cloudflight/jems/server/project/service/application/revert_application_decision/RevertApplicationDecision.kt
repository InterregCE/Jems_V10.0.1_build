package io.cloudflight.jems.server.project.service.application.revert_application_decision

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanRevertDecision
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.project.service.application.workflow.ApplicationStateFactory
import io.cloudflight.jems.server.project.service.model.ProjectSummary
import io.cloudflight.jems.server.project.service.projectStatusChanged
import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

data class ApplicationDecisionRevertedEvent(val project: ProjectSummary)

@Service
class RevertApplicationDecision(
    private val projectPersistence: ProjectPersistence,
    private val applicationStateFactory: ApplicationStateFactory,
    private val eventPublisher: ApplicationEventPublisher,
    private val auditPublisher: ApplicationEventPublisher
) : RevertApplicationDecisionInteractor {
    companion object {
        private val log = LoggerFactory.getLogger(this::class.java)
    }

    @CanRevertDecision
    @Transactional
    @ExceptionWrapper(RevertApplicationDecisionException::class)
    override fun revert(projectId: Long): ApplicationStatus =
        projectPersistence.getProjectSummary(projectId).let { projectSummary ->
            applicationStateFactory.getInstance(projectSummary).revertDecision().also {
                auditPublisher.publishEvent(projectStatusChanged(this, projectSummary, newStatus = it))
                log.warn("Decision-reversion has been done for project(id=$projectId) status moved from ${projectSummary.status} to $it")
                eventPublisher.publishEvent(ApplicationDecisionRevertedEvent(projectSummary))
            }
        }
}
