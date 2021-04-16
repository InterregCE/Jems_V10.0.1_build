package io.cloudflight.jems.api.user.dto

data class UserChangeDTO (
    val id: Long? = null,
    val email: String,
    val name: String,
    val surname: String,
    val userRoleId: Long,
)
