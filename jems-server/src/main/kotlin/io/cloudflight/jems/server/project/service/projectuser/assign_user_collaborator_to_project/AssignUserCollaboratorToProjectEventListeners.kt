package io.cloudflight.jems.server.project.service.projectuser.assign_user_collaborator_to_project

import io.cloudflight.jems.api.audit.dto.AuditAction
import io.cloudflight.jems.server.audit.service.AuditBuilder
import io.cloudflight.jems.server.common.event.JemsAuditEvent
import io.cloudflight.jems.server.project.service.model.ProjectSummary
import io.cloudflight.jems.server.user.service.model.assignment.CollaboratorAssignedToProject
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.event.TransactionalEventListener

data class AssignUserCollaboratorEvent(val project: ProjectSummary, val collaborators: List<CollaboratorAssignedToProject>)

@Service
data class AssignUserCollaboratorToProjectEventListeners(
    private val eventPublisher: ApplicationEventPublisher,
) {

    @TransactionalEventListener
    fun publishJemsAuditEvent(event: AssignUserCollaboratorEvent) =
        eventPublisher.publishEvent(
            JemsAuditEvent(
                auditCandidate = AuditBuilder(AuditAction.PROJECT_LEAD_APPLICANT_ASSIGNMENT)
                    .project(event.project)
                    .description("List of Lead applicants: ${collaboratorsWithLevels(event.collaborators)}")
                    .build()
            )
        )

    private fun collaboratorsWithLevels(collaborators: List<CollaboratorAssignedToProject>): String =
        collaborators.joinToString(", ") { "${it.userEmail}: ${it.level}" }
}
