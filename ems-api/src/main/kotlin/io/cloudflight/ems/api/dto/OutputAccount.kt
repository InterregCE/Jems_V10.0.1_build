package io.cloudflight.ems.api.dto

data class OutputAccount (
    val id: Long?,
    val email: String,
    val name: String,
    val surname: String,
    val accountRole: OutputAccountRole
)
