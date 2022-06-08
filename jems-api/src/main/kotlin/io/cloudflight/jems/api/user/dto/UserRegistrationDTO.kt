package io.cloudflight.jems.api.user.dto

data class UserRegistrationDTO(
    val email: String,
    val name: String,
    val surname: String,
    val password: String,
    val captcha: String
)
