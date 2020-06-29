package io.cloudflight.ems.dto

import io.cloudflight.ems.api.dto.user.OutputUser

data class UserWithCredentials (
    val user: OutputUser,
    val password: String
)
