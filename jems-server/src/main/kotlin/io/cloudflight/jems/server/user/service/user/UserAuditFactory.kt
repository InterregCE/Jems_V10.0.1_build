package io.cloudflight.jems.server.user.service.user

import io.cloudflight.jems.api.audit.dto.AuditAction
import io.cloudflight.jems.server.audit.model.AuditUser
import io.cloudflight.jems.server.audit.service.AuditBuilder
import io.cloudflight.jems.server.common.event.JemsAuditEvent

fun userActivated(
    userId: Long,
    userEmail: String
): JemsAuditEvent =
    JemsAuditEvent(
        auditUser = AuditUser(userId, userEmail),
        auditCandidate = AuditBuilder(AuditAction.NEW_USER_CONFIRMED)
            .entityRelatedId(userId)
            .description(
                "User with email address ${userEmail} has been confirmed."
            )
            .build()
    )
