package io.cloudflight.jems.server.user.service.model

data class UserSearchRequest(
    val name: String? = null,
    val surname: String? = null,
    val email: String? = null,
    val roles: Set<Long> = emptySet(),
    val userStatuses: Set<UserStatus> = emptySet()
)
