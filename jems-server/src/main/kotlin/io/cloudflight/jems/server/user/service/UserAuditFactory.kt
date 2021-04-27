package io.cloudflight.jems.server.user.service

import io.cloudflight.jems.api.audit.dto.AuditAction
import io.cloudflight.jems.server.audit.model.AuditCandidateEvent
import io.cloudflight.jems.server.audit.model.AuditUser
import io.cloudflight.jems.server.audit.service.AuditBuilder
import io.cloudflight.jems.server.common.audit.fromOldToNewChanges
import io.cloudflight.jems.server.common.audit.onlyNewChanges
import io.cloudflight.jems.server.user.service.model.User
import io.cloudflight.jems.server.user.service.model.UserWithPassword

fun userCreated(context: Any, createdUser: User): AuditCandidateEvent =
    AuditCandidateEvent(
        context = context,
        auditCandidate = AuditBuilder(AuditAction.USER_ADDED)
            .entityRelatedId(createdUser.id)
            .description("A new user ${createdUser.email} was created:\n${createdUser.getDiff().onlyNewChanges()}")
            .build()
    )

fun userRegistered(context: Any, createdUser: User): AuditCandidateEvent =
    AuditCandidateEvent(
        context = context,
        overrideCurrentUser = createdUser.toAuditUser(),
        auditCandidate = AuditBuilder(AuditAction.USER_REGISTERED)
            .entityRelatedId(createdUser.id)
            .description("A new user ${createdUser.email} registered:\n${createdUser.getDiff().onlyNewChanges()}")
            .build()
    )

fun userUpdated(context: Any, oldUser: User, newUser: User): AuditCandidateEvent =
    AuditCandidateEvent(
        context = context,
        auditCandidate = AuditBuilder(AuditAction.USER_DATA_CHANGED)
            .entityRelatedId(oldUser.id)
            .description("User data changed for user id=${oldUser.id}:\n${newUser.getDiff(oldUser).fromOldToNewChanges()}")
            .build()
    )

fun passwordChanged(ctx: Any, changedUser: UserWithPassword, initiator: User? = null): AuditCandidateEvent =
    if (initiator == null)
        AuditCandidateEvent(
            context = ctx,
            auditCandidate = AuditBuilder(AuditAction.PASSWORD_CHANGED)
                .entityRelatedId(changedUser.id)
                .description("Password of user ${changedUser.getUser().auditString()} has been changed by himself/herself")
                .build()
        )
    else
        AuditCandidateEvent(
            context = ctx,
            overrideCurrentUser = initiator.toAuditUser(),
            auditCandidate = AuditBuilder(AuditAction.PASSWORD_CHANGED)
                .entityRelatedId(changedUser.id)
                .description("Password of user ${changedUser.getUser().auditString()} has been changed by user ${initiator.auditString()}")
                .build()
        )

fun User.auditString() = "'$name $surname' ($email)"

fun User.toAuditUser() = AuditUser(
    id = id,
    email = email,
)
