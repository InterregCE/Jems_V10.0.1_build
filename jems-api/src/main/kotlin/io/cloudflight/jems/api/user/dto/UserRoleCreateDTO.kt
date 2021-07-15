package io.cloudflight.jems.api.user.dto

data class UserRoleCreateDTO (
    val name: String,
    val defaultForRegisteredUser: Boolean,
    val permissions: Set<UserRolePermissionDTO>
)
