package io.cloudflight.jems.server.user.service

import io.cloudflight.jems.api.audit.dto.AuditAction
import io.cloudflight.jems.server.audit.model.AuditUser
import io.cloudflight.jems.server.audit.service.AuditBuilder
import io.cloudflight.jems.server.common.event.JemsAuditEvent
import io.cloudflight.jems.server.user.service.model.User
import io.cloudflight.jems.server.user.service.model.UserWithPassword

fun passwordChanged(changedUser: UserWithPassword, initiator: User? = null): JemsAuditEvent =
    JemsAuditEvent(
        auditCandidate = AuditBuilder(AuditAction.PASSWORD_CHANGED)
            .entityRelatedId(changedUser.id)
            .description(
                "Password of user ${
                    changedUser.getUser().auditString()
                } has been ${if (initiator == null) "changed by himself/herself" else "reset"}"
            ).build(),
        auditUser = initiator?.toAuditUser()
    )

fun passwordResetByTokenAuditEvent(changedUser: UserWithPassword): JemsAuditEvent =
    JemsAuditEvent(
        auditCandidate = AuditBuilder(AuditAction.PASSWORD_CHANGED)
            .entityRelatedId(changedUser.id)
            .description("Password of user ${changedUser.getUser().auditString()} has been changed by reset link")
            .build(),
        auditUser = changedUser.toAuditUser()
    )

fun User.auditString() = "'$name $surname' ($email)"

fun User.toAuditUser() = AuditUser(
    id = id,
    email = email,
)

fun UserWithPassword.toAuditUser() = AuditUser(
    id = id,
    email = email
)
