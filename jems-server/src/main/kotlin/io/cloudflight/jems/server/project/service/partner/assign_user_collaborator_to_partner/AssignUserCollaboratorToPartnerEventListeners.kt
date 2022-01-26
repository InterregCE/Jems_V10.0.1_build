package io.cloudflight.jems.server.project.service.partner.assign_user_collaborator_to_partner

import io.cloudflight.jems.api.audit.dto.AuditAction
import io.cloudflight.jems.server.audit.service.AuditBuilder
import io.cloudflight.jems.server.common.event.JemsAuditEvent
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.project.service.application.revert_application_decision.ApplicationDecisionRevertedEvent
import io.cloudflight.jems.server.project.service.model.ProjectSummary
import io.cloudflight.jems.server.project.service.partner.UserPartnerCollaboratorPersistence
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerSummary
import io.cloudflight.jems.server.user.service.model.assignment.PartnerCollaborator
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener

data class AssignCollaboratorToPartnerEvent(val project: ProjectSummary,
                                            val partner: ProjectPartnerSummary,
                                            val collaborators: Set<PartnerCollaborator>)

@Service
data class AssignUserCollaboratorToPartnerEventListeners(
    private val eventPublisher: ApplicationEventPublisher,
    private val partnerCollaboratorPersistence: UserPartnerCollaboratorPersistence
) {

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun applicationDecisionReverted(event: ApplicationDecisionRevertedEvent) {
        if (event.project.status == ApplicationStatus.APPROVED) {
            partnerCollaboratorPersistence.deleteByProjectId(event.project.id)
        }
    }

    @TransactionalEventListener
    fun publishJemsAuditEvent(event: AssignCollaboratorToPartnerEvent) =
        eventPublisher.publishEvent(
            JemsAuditEvent(
                auditCandidate = AuditBuilder(AuditAction.PROJECT_USER_ASSIGNMENT_APPLICANTS)
                    .project(event.project)
                    .description("[${partner(event.partner)}] List of users: ${collaboratorsWithLevels(event.collaborators)}")
                    .build()
            )
        )

    private fun partner(partner: ProjectPartnerSummary): String =
        partner.role.isLead.let {
            if (it) "LP${partner.sortNumber} ${partner.abbreviation}"
            else "PP${partner.sortNumber} ${partner.abbreviation}"
        }

    private fun collaboratorsWithLevels(collaborators: Set<PartnerCollaborator>): String =
        collaborators.joinToString(", ") { "${it.userEmail}: ${it.level}" }
}
