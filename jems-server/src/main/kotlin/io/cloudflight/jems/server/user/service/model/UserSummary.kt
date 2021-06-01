package io.cloudflight.jems.server.user.service.model

data class UserSummary (
    val id: Long = 0,
    val email: String,
    val name: String,
    val surname: String,
    val userRole: UserRoleSummary,
)
