package io.cloudflight.ems.api.dto.user

data class OutputCurrentUser(
    val id: Long,
    val name: String,
    val role: String
)
