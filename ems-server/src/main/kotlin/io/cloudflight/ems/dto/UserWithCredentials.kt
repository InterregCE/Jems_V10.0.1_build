package io.cloudflight.ems.dto

import io.cloudflight.ems.api.user.dto.OutputUserWithRole

data class UserWithCredentials (
    val user: OutputUserWithRole,
    val password: String
)
