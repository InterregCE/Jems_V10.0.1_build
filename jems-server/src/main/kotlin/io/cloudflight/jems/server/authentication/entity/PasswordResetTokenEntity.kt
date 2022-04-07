package io.cloudflight.jems.server.authentication.entity

import java.time.Instant
import java.util.UUID
import javax.persistence.Entity
import javax.persistence.Id
import javax.validation.constraints.NotNull

@Entity(name = "password_reset_token")
class PasswordResetTokenEntity(
    @Id
    var id: PasswordResetTokenId,

    @field:NotNull
    val token: UUID,

    @field:NotNull
    val generatedAt: Instant
)
