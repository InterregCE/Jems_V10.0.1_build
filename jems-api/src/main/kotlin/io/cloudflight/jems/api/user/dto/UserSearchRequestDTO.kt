package io.cloudflight.jems.api.user.dto

data class UserSearchRequestDTO(
    val name: String? = null,
    val surname: String? = null,
    val email: String? = null,
    val roles: Set<Long> = emptySet(),
    val userStatuses: Set<UserStatusDTO> = emptySet()
)
