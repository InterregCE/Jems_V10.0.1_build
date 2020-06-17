package io.cloudflight.ems.service

import io.cloudflight.ems.api.dto.OutputUserRole
import io.cloudflight.ems.entity.UserRole

fun UserRole.toOutputUserRole() = OutputUserRole(
    id = this.id,
    name = this.name
)


