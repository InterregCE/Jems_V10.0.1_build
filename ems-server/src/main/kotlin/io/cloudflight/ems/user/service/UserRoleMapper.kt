package io.cloudflight.ems.user.service

import io.cloudflight.ems.api.user.dto.OutputUserRole
import io.cloudflight.ems.user.entity.UserRole

fun UserRole.toOutputUserRole() = OutputUserRole(
    id = this.id,
    name = this.name
)


