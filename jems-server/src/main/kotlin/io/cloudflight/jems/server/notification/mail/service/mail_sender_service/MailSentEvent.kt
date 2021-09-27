package io.cloudflight.jems.server.notification.mail.service.mail_sender_service

import io.cloudflight.jems.api.audit.dto.AuditAction
import io.cloudflight.jems.server.audit.model.AuditUser
import io.cloudflight.jems.server.audit.service.AuditBuilder
import io.cloudflight.jems.server.audit.service.AuditCandidate
import io.cloudflight.jems.server.common.event.JemsAuditEvent
import io.cloudflight.jems.server.notification.mail.service.model.MailNotification

data class MailSentEvent(
    val notification: MailNotification, override val auditUser: AuditUser? = null
) : JemsAuditEvent {

    override fun getAuditCandidate(): AuditCandidate =
        AuditBuilder(AuditAction.MAIL_SENT)
            .description("Message has been sent to `${notification.recipients}`. Message type is: '${notification.messageType}'.")
            .build()
}
