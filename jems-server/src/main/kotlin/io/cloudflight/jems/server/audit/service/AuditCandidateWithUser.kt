package io.cloudflight.jems.server.audit.service

import io.cloudflight.jems.api.audit.dto.AuditAction
import io.cloudflight.jems.server.audit.model.AuditProject
import io.cloudflight.jems.server.audit.model.AuditUser

@Deprecated("This class is about to be removed, use AuditCandidateEvent and publish with overrideCurrentUser")
data class AuditCandidateWithUser(
    val action: AuditAction,
    val project: AuditProject? = null,
    val description: String,
    val user: AuditUser
) {

    @Deprecated("Instantiate AuditCandidate and pass it through ApplicationEventPublisher", replaceWith = ReplaceWith("publishEvent"))
    fun logWith(auditService: AuditService) {
        auditService.logEvent(
            audit = AuditCandidate(
                action = action,
                project = project,
                description = description,
            ),
            optionalUser = user,
        )
    }
}
