package io.cloudflight.jems.server.user.service.model

data class UserSearchRequest(
    val userName: String? = null,
    val userSurname: String? = null,
    val userEmail: String? = null,
    val userRoles: List<UserRoleSummary>? = null
)
