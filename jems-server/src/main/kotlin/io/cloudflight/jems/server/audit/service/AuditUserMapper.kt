package io.cloudflight.jems.server.audit.service

import io.cloudflight.jems.api.authentication.dto.OutputCurrentUser
import io.cloudflight.jems.server.audit.model.AuditUser
import io.cloudflight.jems.server.authentication.model.CurrentUser

fun CurrentUser.toEsUser() = AuditUser(
    id = user.id ?: 0,
    email = user.email
)

fun OutputCurrentUser.toEsUser() = AuditUser(
    id = id,
    email = name
)
