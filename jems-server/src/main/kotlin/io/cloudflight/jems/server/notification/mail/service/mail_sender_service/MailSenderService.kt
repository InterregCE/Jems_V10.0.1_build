package io.cloudflight.jems.server.notification.mail.service.mail_sender_service

import io.cloudflight.jems.server.notification.mail.service.model.MailNotification


interface MailSenderService {
    fun send(notification: MailNotification, afterSendHook: ((MailNotification) -> Any?)?)
}
