package io.cloudflight.jems.server.user.entity

import java.time.ZonedDateTime
import java.util.UUID
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.validation.constraints.NotNull

@Entity(name = "account_confirmation")
class UserConfirmationEntity(
    @Id
    val token: UUID,

    @field:NotNull
    val userId: Long,

    @field:NotNull
    val timestamp: ZonedDateTime = ZonedDateTime.now(),

    @field:NotNull
    val confirmed: Boolean,
)
