package io.cloudflight.jems.api.user.dto

data class UserDTO (
    val id: Long? = null,
    val email: String,
    val name: String,
    val surname: String,
    val userRole: UserRoleDTO,
)
