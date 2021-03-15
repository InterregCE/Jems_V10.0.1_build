package io.cloudflight.jems.api.user.dto

data class InputPassword(
    val password: String,
    val oldPassword: String? = null
)
