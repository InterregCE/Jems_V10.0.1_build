package io.cloudflight.jems.server.user.service.user.register_user

import io.cloudflight.jems.api.audit.dto.AuditAction
import io.cloudflight.jems.server.audit.model.AuditUser
import io.cloudflight.jems.server.audit.service.AuditBuilder
import io.cloudflight.jems.server.audit.service.AuditCandidate
import io.cloudflight.jems.server.common.audit.onlyNewChanges
import io.cloudflight.jems.server.common.event.JemsAuditEvent
import io.cloudflight.jems.server.user.service.model.User
import io.cloudflight.jems.server.user.service.toAuditUser
import io.cloudflight.jems.server.user.service.user.ConfirmUserEmailEvent

data class UserRegisteredEvent(
    val user: User,
    override val auditUser: AuditUser? = user.toAuditUser()
) : JemsAuditEvent {

    override fun getAuditCandidate(): AuditCandidate =
        AuditBuilder(AuditAction.USER_REGISTERED)
            .entityRelatedId(user.id)
            .description("A new user ${user.email} registered:\n${user.getDiff().onlyNewChanges()}")
            .build()
}
