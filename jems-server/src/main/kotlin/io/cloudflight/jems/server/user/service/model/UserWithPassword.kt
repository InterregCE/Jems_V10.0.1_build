package io.cloudflight.jems.server.user.service.model

data class UserWithPassword(
    val id: Long = 0,
    val email: String,
    val name: String,
    val surname: String,
    val userRole: UserRole,
    val encodedPassword: String,
    val userStatus: UserStatus
) {
    fun getUser(): User = User(
        id = id,
        email = email,
        name = name,
        surname = surname,
        userRole = userRole,
        userStatus = userStatus
    )
}
