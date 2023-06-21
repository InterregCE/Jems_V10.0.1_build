package io.cloudflight.jems.api.user.dto

data class UserSettingsChangeDTO (
    val id: Long? = null,
    val sendNotificationsToEmail: Boolean,
)
