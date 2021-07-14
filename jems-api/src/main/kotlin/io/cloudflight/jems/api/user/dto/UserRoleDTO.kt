package io.cloudflight.jems.api.user.dto

data class UserRoleDTO (
    val id: Long? = null,
    val name: String,
    val isDefault: Boolean? = null,
    val permissions: List<UserRolePermissionDTO>
)
