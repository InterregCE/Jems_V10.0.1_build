package io.cloudflight.ems.dto

import io.cloudflight.ems.api.dto.OutputAccount

data class UserWithCredentials (
    val user: OutputAccount,
    val password: String
)
