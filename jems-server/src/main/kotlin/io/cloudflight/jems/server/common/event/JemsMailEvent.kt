package io.cloudflight.jems.server.common.event

import io.cloudflight.jems.server.notification.mail.service.model.MailNotificationInfo

data class JemsMailEvent(
    val emailTemplateFileName: String,
    val mailNotificationInfo: MailNotificationInfo
): JemsEvent
