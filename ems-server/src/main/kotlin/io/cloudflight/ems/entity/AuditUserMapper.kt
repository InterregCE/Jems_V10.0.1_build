package io.cloudflight.ems.entity

import io.cloudflight.ems.api.dto.user.OutputCurrentUser
import io.cloudflight.ems.security.model.CurrentUser

fun CurrentUser.toEsUser() = AuditUser (
    id = user.id ?: 0,
    email = user.email
)

fun OutputCurrentUser.toEsUser() = AuditUser (
    id = id,
    email = name
)
