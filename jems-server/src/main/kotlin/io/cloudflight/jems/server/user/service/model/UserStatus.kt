package io.cloudflight.jems.server.user.service.model

enum class UserStatus {
    ACTIVE,
    INACTIVE,
    UNCONFIRMED;

    fun isActive() = this == ACTIVE
}
