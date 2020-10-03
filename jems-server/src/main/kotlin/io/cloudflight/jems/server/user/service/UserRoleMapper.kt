package io.cloudflight.jems.server.user.service

import io.cloudflight.jems.api.user.dto.OutputUserRole
import io.cloudflight.jems.server.user.entity.UserRole

fun UserRole.toOutputUserRole() = OutputUserRole(
    id = this.id,
    name = this.name
)


