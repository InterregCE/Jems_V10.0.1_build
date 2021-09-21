package io.cloudflight.jems.server.user.service.model

data class UserRoleSummary (
    val id: Long = 0,
    val name: String,
    val isDefault: Boolean = false
)
