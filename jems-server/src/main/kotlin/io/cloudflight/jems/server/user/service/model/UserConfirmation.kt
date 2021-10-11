package io.cloudflight.jems.server.user.service.model

import java.time.ZonedDateTime
import java.util.UUID

data class UserConfirmation(
    val token: UUID,
    val userId: Long,
    val timestamp: ZonedDateTime,
    val confirmed: Boolean = false,
)
