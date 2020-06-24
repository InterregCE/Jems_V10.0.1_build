package io.cloudflight.ems.service

import io.cloudflight.ems.api.dto.OutputAccountRole
import io.cloudflight.ems.entity.AccountRole

fun AccountRole.toOutputAccountRole() = OutputAccountRole(
    id = this.id,
    name = this.name
)


