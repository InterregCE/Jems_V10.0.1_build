package io.cloudflight.jems.server.user.service

import io.cloudflight.jems.api.audit.dto.AuditAction
import io.cloudflight.jems.server.audit.model.AuditCandidateEvent
import io.cloudflight.jems.server.audit.model.AuditUser
import io.cloudflight.jems.server.audit.service.AuditBuilder
import io.cloudflight.jems.server.user.service.model.User
import io.cloudflight.jems.server.user.service.model.UserWithPassword

fun passwordChanged(ctx: Any, changedUser: UserWithPassword, initiator: User? = null): AuditCandidateEvent =
    if (initiator == null)
        AuditCandidateEvent(
            context = ctx,
            auditCandidate = AuditBuilder(AuditAction.PASSWORD_CHANGED)
                .entityRelatedId(changedUser.id)
                .description(
                    "Password of user ${
                        changedUser.getUser().auditString()
                    } has been changed by himself/herself"
                )
                .build()
        )
    else
        AuditCandidateEvent(
            context = ctx,
            overrideCurrentUser = initiator.toAuditUser(),
            auditCandidate = AuditBuilder(AuditAction.PASSWORD_CHANGED)
                .entityRelatedId(changedUser.id)
                .description(
                    "Password of user ${
                        changedUser.getUser().auditString()
                    } has been changed by user ${initiator.auditString()}"
                )
                .build()
        )

fun User.auditString() = "'$name $surname' ($email)"

fun User.toAuditUser() = AuditUser(
    id = id,
    email = email,
)
