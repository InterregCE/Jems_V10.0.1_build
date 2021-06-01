package io.cloudflight.jems.api.user.dto

data class UserSummaryDTO (
    val id: Long?,
    val email: String,
    val name: String,
    val surname: String,
    val userRole: UserRoleSummaryDTO,
)
