package io.cloudflight.jems.server.user.service

import io.cloudflight.jems.api.user.dto.OutputUser
import io.cloudflight.jems.api.user.dto.OutputUserWithRole
import io.cloudflight.jems.server.audit.entity.AuditAction
import io.cloudflight.jems.server.audit.entity.AuditUser
import io.cloudflight.jems.server.audit.service.AuditBuilder
import io.cloudflight.jems.server.audit.service.AuditCandidate
import io.cloudflight.jems.server.audit.service.AuditCandidateWithUser
import io.cloudflight.jems.server.security.model.CurrentUser
import java.util.stream.Collectors

fun userCreated(currentUser: CurrentUser?, createdUser: OutputUserWithRole): AuditCandidate {
    val author = currentUser?.user?.email
    return AuditBuilder(AuditAction.USER_CREATED)
        .description("new user ${createdUser.email} with role ${createdUser.userRole.name} has been created by $author")
        .build()
}

fun userRoleChanged(currentUser: CurrentUser?, user: OutputUserWithRole): AuditCandidate {
    val author = currentUser?.user?.email
    return AuditBuilder(AuditAction.USER_ROLE_CHANGED)
        .description("user role '${user.userRole.name}' has been assigned to ${user.name} ${user.surname} by $author")
        .build()
}

fun userDataChanged(userId: Long, changes: Map<String, Pair<String, String>>): AuditCandidate {
    val changedString = changes.entries.stream()
        .map { "${it.key} changed from ${it.value.first} to ${it.value.second}" }
        .collect(Collectors.joining(",\n"))

    return AuditBuilder(AuditAction.USER_DATA_CHANGED)
        .description("User data changed for user $userId:\n$changedString")
        .build()
}

/**
 * In this specific case we are logging user, which is not currently-logged-in user.
 */
fun applicantRegistered(createdUser: OutputUserWithRole): AuditCandidateWithUser {
    return AuditCandidateWithUser(
        action = AuditAction.USER_REGISTERED,
        user = AuditUser(createdUser.id!!, createdUser.email),
        description = "new user '${createdUser.name} ${createdUser.surname}' with role '${createdUser.userRole.name}' registered"
    )
}

fun passwordChanged(initiator: CurrentUser?, changedUser: OutputUser): AuditCandidate =
    AuditBuilder(AuditAction.PASSWORD_CHANGED)
        .description(
            if (initiator?.user?.id == changedUser.id)
                "Password of user '${changedUser.name} ${changedUser.surname}' (${changedUser.email}) has been changed by himself/herself"
            else
                "Password of user '${changedUser.name} ${changedUser.surname}' (${changedUser.email}) has been changed by user ${initiator?.user?.email}")
        .build()
