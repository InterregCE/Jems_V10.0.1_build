package io.cloudflight.jems.api.authentication.dto

import io.cloudflight.jems.api.user.dto.UserRoleDTO

data class OutputCurrentUser(
    val id: Long,
    val name: String,
    val role: UserRoleDTO
)
