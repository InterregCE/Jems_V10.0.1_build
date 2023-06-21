package io.cloudflight.jems.server.user.service.model

data class UserSettingsChange (
    val id: Long = 0,
    val sendNotificationsToEmail: Boolean = false,
)

