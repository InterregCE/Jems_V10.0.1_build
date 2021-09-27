package io.cloudflight.jems.server.notification.mail.service

import io.cloudflight.jems.server.notification.mail.service.model.MailNotification

interface MailNotificationPersistence {
    fun save(notification: MailNotification): MailNotification
    fun deleteIfExist(id: Long)
}
