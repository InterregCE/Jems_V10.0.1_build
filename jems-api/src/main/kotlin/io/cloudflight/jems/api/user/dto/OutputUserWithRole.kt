package io.cloudflight.jems.api.user.dto

data class OutputUserWithRole (
    val id: Long?,
    val email: String,
    val name: String,
    val surname: String,
    val userRole: OutputUserRole
)
