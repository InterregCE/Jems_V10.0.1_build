package io.cloudflight.jems.api.user.dto

data class UserSearchRequestDTO(
    val userName: String? = null,
    val userSurname: String? = null,
    val userEmail: String? = null,
    val userRoles: Set<String?> = emptySet()
)
