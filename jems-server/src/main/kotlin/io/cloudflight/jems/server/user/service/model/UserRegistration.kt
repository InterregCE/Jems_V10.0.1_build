package io.cloudflight.jems.server.user.service.model

data class UserRegistration (
    val email: String,
    val name: String,
    val surname: String,
    val password: String,
)
