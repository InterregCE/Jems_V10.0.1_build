package io.cloudflight.jems.server.mail.confirmation.repository

import io.cloudflight.jems.server.mail.confirmation.entity.MailConfirmationEntity
import io.cloudflight.jems.server.mail.confirmation.service.model.MailConfirmation

fun MailConfirmation.toEntity() = MailConfirmationEntity(
    token = token,
    accountToBeActivatedId = userId,
    timestamp = timestamp,
    clicked = clicked
)

fun MailConfirmationEntity.toModel() = MailConfirmation(
    token = token,
    userId = accountToBeActivatedId,
    timestamp = timestamp,
    clicked = clicked
)
