package io.cloudflight.jems.server.notification.mail.repository

import io.cloudflight.jems.server.notification.mail.service.model.MailNotification
import io.cloudflight.jems.server.notification.mail.service.MailNotificationPersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class MailNotificationPersistenceProvider(
    private val repository: MailNotificationRepository
) : MailNotificationPersistence {

    @Transactional
    override fun save(notification: MailNotification): MailNotification =
        repository.save(notification.toEntity()).toModel()

    @Transactional
    override fun deleteIfExist(id: Long) =
        repository.deleteById(id)
}
