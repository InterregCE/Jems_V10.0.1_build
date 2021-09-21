package io.cloudflight.jems.server.user.service.model

data class UserRoleCreate (
    val name: String,
    val isDefault: Boolean,
    val permissions: Set<UserRolePermission>
)
