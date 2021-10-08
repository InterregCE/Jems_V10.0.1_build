package io.cloudflight.jems.server.mail.confirmation.service.model

import java.time.ZonedDateTime
import java.util.UUID

data class MailConfirmation(
    val token: UUID,
    val userId: Long,
    val timestamp: ZonedDateTime,
    val clicked: Boolean = false,
)
