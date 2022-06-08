package io.cloudflight.jems.api.authentication.dto

data class ResetPasswordByTokenRequestDTO(
    val token: String,
    val password: String
)
