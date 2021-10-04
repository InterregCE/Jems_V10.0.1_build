package io.cloudflight.jems.server.user.service.user.update_user

import io.cloudflight.jems.api.audit.dto.AuditAction
import io.cloudflight.jems.server.audit.model.AuditUser
import io.cloudflight.jems.server.audit.service.AuditBuilder
import io.cloudflight.jems.server.audit.service.AuditCandidate
import io.cloudflight.jems.server.common.audit.fromOldToNewChanges
import io.cloudflight.jems.server.common.event.JemsAuditEvent
import io.cloudflight.jems.server.user.service.model.User
import io.cloudflight.jems.server.user.service.toAuditUser

data class UserUpdatedEvent(
    val newUser: User,
    val oldUser: User,
    override val auditUser: AuditUser? = newUser.toAuditUser()
) : JemsAuditEvent {

    override fun getAuditCandidate(): AuditCandidate =
        AuditBuilder(AuditAction.USER_DATA_CHANGED)
            .entityRelatedId(newUser.id)
            .description(
                "User data changed for user id=${oldUser.id}:\n${
                    newUser.getDiff(oldUser).fromOldToNewChanges()
                }"
            )
            .build()
}
