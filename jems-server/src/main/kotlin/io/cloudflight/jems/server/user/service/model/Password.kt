package io.cloudflight.jems.server.user.service.model

data class Password (
    val password: String,
    val oldPassword: String? = null
)
