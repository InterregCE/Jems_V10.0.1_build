package io.cloudflight.ems.audit.service

import io.cloudflight.ems.api.user.dto.OutputCurrentUser
import io.cloudflight.ems.audit.entity.AuditUser
import io.cloudflight.ems.security.model.CurrentUser

fun CurrentUser.toEsUser() = AuditUser(
    id = user.id ?: 0,
    email = user.email
)

fun OutputCurrentUser.toEsUser() = AuditUser(
    id = id,
    email = name
)
