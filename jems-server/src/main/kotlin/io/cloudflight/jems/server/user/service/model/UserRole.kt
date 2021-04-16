package io.cloudflight.jems.server.user.service.model

data class UserRole (
    val id: Long = 0L,
    val name: String,
    val permissions: Set<UserRolePermission>,
)
