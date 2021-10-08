package io.cloudflight.jems.server.mail.confirmation.service

import io.cloudflight.jems.server.mail.confirmation.service.model.MailConfirmation
import java.util.UUID

interface MailConfirmationPersistence {
    fun save(confirmation: MailConfirmation): MailConfirmation

    fun getByToken(token: UUID): MailConfirmation
}
