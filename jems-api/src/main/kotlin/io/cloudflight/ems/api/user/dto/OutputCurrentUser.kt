package io.cloudflight.ems.api.user.dto

data class OutputCurrentUser(
    val id: Long,
    val name: String,
    val role: String
)
