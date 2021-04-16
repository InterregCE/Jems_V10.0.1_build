package io.cloudflight.jems.api.user.dto

data class UserRoleDTO (
    val id: Long? = null,
    val name: String,
    val permissions: List<UserRolePermissionDTO>
)
