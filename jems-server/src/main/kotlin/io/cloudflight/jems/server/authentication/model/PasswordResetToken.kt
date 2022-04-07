package io.cloudflight.jems.server.authentication.model

import io.cloudflight.jems.server.user.service.model.UserSummary
import java.time.Instant
import java.util.UUID

data class PasswordResetToken(
    val user: UserSummary,
    val token: UUID,
    val generatedAt: Instant
)
