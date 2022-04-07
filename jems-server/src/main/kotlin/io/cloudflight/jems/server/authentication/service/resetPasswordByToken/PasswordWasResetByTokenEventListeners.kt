package io.cloudflight.jems.server.authentication.service.resetPasswordByToken

import io.cloudflight.jems.server.common.event.JemsMailEvent
import io.cloudflight.jems.server.common.model.Variable
import io.cloudflight.jems.server.notification.mail.service.model.MailNotificationInfo
import io.cloudflight.jems.server.user.service.model.UserWithPassword
import io.cloudflight.jems.server.user.service.passwordResetByTokenAuditEvent
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Service
import org.springframework.transaction.event.TransactionalEventListener

data class PasswordWasResetByTokenEvent(val user: UserWithPassword)

@Service
data class PasswordWasResetByTokenEventListener(private val eventPublisher: ApplicationEventPublisher) {

    @TransactionalEventListener
    fun publishJemsAuditEvent(event: PasswordWasResetByTokenEvent) =
        eventPublisher.publishEvent(passwordResetByTokenAuditEvent(event.user))

    @EventListener
    fun publishJemsMailEvent(event: PasswordWasResetByTokenEvent) =
        eventPublisher.publishEvent(
            JemsMailEvent(
                emailTemplateFileName = "password-reset-success.html",
                mailNotificationInfo = MailNotificationInfo(
                    subject = "[Jems] Successfully changed password",
                    templateVariables =
                    setOf(
                        Variable("name", event.user.name),
                        Variable("surname", event.user.surname),
                    ),
                    recipients = setOf(event.user.email),
                    messageType = "User password changed confirmation"
                )
            )
        )
}
