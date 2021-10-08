package io.cloudflight.jems.server.notification.mail.service.mail_sender_service

import io.cloudflight.jems.api.audit.dto.AuditAction
import io.cloudflight.jems.server.audit.service.AuditBuilder
import io.cloudflight.jems.server.common.event.JemsAuditEvent
import io.cloudflight.jems.server.notification.mail.service.model.MailNotification
import org.springframework.context.ApplicationEventPublisher
import org.springframework.transaction.event.TransactionalEventListener

data class MailSentEvent(val notification: MailNotification)

data class MailSentEventListeners(private val eventPublisher: ApplicationEventPublisher) {


    @TransactionalEventListener(fallbackExecution = true)
    fun publishJemsAuditEvent(event: MailSentEvent) =
        eventPublisher.publishEvent(
            JemsAuditEvent(
                auditCandidate = AuditBuilder(AuditAction.MAIL_SENT)
                    .description("Message has been sent to `${event.notification.recipients}`. Message type is: '${event.notification.messageType}'.")
                    .build()
            )
        )
}


