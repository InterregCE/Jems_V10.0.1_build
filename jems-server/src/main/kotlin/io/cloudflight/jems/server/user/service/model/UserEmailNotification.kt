package io.cloudflight.jems.server.user.service.model

data class UserEmailNotification(
    val isEmailEnabled: Boolean,
    val userStatus: UserStatus
) {
    fun isActive() = userStatus.isActive()
}
