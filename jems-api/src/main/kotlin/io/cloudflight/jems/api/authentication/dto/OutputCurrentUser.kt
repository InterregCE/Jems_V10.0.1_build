package io.cloudflight.jems.api.authentication.dto

data class OutputCurrentUser(
    val id: Long,
    val name: String,
    val role: String
)
