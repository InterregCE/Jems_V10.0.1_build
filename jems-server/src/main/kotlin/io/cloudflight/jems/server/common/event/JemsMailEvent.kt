package io.cloudflight.jems.server.common.event

import io.cloudflight.jems.server.notification.mail.service.model.MailNotificationInfo

interface JemsMailEvent : JemsEvent {
    val emailTemplateFileName: String
    fun getMailNotificationInfo(): MailNotificationInfo
}
