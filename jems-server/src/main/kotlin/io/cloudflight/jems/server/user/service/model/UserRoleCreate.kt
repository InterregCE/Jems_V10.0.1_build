package io.cloudflight.jems.server.user.service.model

data class UserRoleCreate (
    val name: String,
    val permissions: Set<UserRolePermission>,
)
