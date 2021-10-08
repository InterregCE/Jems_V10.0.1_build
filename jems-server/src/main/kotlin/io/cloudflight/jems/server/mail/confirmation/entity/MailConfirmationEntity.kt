package io.cloudflight.jems.server.mail.confirmation.entity

import java.time.ZonedDateTime
import java.util.UUID
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.validation.constraints.NotNull

@Entity(name = "mail_confirmation")
class MailConfirmationEntity (
    @Id
    val token: UUID,

    @Column(name = "account_id")
    @field:NotNull
    val accountToBeActivatedId: Long,

    @field:NotNull
    val timestamp: ZonedDateTime = ZonedDateTime.now(),

    @field:NotNull
    val clicked: Boolean,
)
