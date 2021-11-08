package io.cloudflight.jems.server.user.service.userproject.assign_user_to_project

import io.cloudflight.jems.api.audit.dto.AuditAction
import io.cloudflight.jems.server.audit.service.AuditBuilder
import io.cloudflight.jems.server.common.event.JemsAuditEvent
import io.cloudflight.jems.server.config.AppProperties
import io.cloudflight.jems.server.project.service.model.ProjectSummary
import io.cloudflight.jems.server.user.service.model.UserSummary
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.event.TransactionalEventListener

data class AssignUserEvent(val project: ProjectSummary, val users: List<UserSummary>)

@Service
data class AssignUserEventListeners(
    private val eventPublisher: ApplicationEventPublisher, private val appProperties: AppProperties
) {

    @TransactionalEventListener
    fun publishJemsAuditEvent(event: AssignUserEvent) =
        eventPublisher.publishEvent(
            JemsAuditEvent(
                auditCandidate = AuditBuilder(AuditAction.PROJECT_USER_ASSIGNMENT)
                    .project(event.project)
                    .description("Users can access: ${emailAddresses(event.users)}")
                    .build()
            )
        )

    private fun emailAddresses(users: List<UserSummary>): String =
        users.map { it.email }.joinToString(", ")
}
