package io.cloudflight.ems.dto

import io.cloudflight.ems.api.dto.user.OutputUserWithRole

data class UserWithCredentials (
    val user: OutputUserWithRole,
    val password: String
)
