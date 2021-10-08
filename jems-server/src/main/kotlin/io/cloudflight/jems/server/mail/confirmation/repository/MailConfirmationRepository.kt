package io.cloudflight.jems.server.mail.confirmation.repository

import io.cloudflight.jems.server.mail.confirmation.entity.MailConfirmationEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface MailConfirmationRepository : JpaRepository<MailConfirmationEntity, Long> {
    fun findByToken(token: UUID): MailConfirmationEntity
}
