package io.cloudflight.jems.api.user.dto

data class PasswordDTO(
    val password: String,
    val oldPassword: String? = null
)
