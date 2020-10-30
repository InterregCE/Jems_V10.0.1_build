package io.cloudflight.jems.server.user.model

import io.cloudflight.jems.api.user.dto.OutputUserWithRole

data class UserWithCredentials (
    val user: OutputUserWithRole,
    val password: String
)
