package io.cloudflight.jems.server.mail.confirmation.repository

import io.cloudflight.jems.server.mail.confirmation.service.MailConfirmationPersistence
import io.cloudflight.jems.server.mail.confirmation.service.model.MailConfirmation
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class MailConfirmationPersistenceProvider(
    private val repository: MailConfirmationRepository
) : MailConfirmationPersistence {

    @Transactional
    override fun save(confirmation: MailConfirmation): MailConfirmation =
        repository.save(confirmation.toEntity()).toModel()

    @Transactional
    override fun getByToken(token: UUID): MailConfirmation =
        repository.findByToken(token).toModel()
}
