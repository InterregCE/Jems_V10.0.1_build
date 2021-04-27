package io.cloudflight.jems.server.user.service.model

data class UserChange (
    val id: Long = 0,
    val email: String,
    val name: String,
    val surname: String,
    val userRoleId: Long,
)
