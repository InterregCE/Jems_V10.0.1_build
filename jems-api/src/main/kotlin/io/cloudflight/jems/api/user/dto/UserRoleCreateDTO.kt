package io.cloudflight.jems.api.user.dto

data class UserRoleCreateDTO (
    val name: String,
    val permissions: Set<UserRolePermissionDTO>
)
