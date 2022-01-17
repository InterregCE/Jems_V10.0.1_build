package io.cloudflight.jems.server.user.service.user.create_user

import io.cloudflight.jems.api.audit.dto.AuditAction
import io.cloudflight.jems.server.audit.service.AuditBuilder
import io.cloudflight.jems.server.common.audit.onlyNewChanges
import io.cloudflight.jems.server.common.event.JemsAuditEvent
import io.cloudflight.jems.server.common.event.JemsMailEvent
import io.cloudflight.jems.server.common.model.Variable
import io.cloudflight.jems.server.config.AppProperties
import io.cloudflight.jems.server.notification.mail.service.model.MailNotificationInfo
import io.cloudflight.jems.server.user.service.model.User
import io.cloudflight.jems.server.user.service.toAuditUser
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Service
import org.springframework.transaction.event.TransactionalEventListener

data class UserCreatedEvent(val user: User, val confirmationToken: String?)

@Service
data class UserCreatedEventListeners(
    private val eventPublisher: ApplicationEventPublisher, private val appProperties: AppProperties
) {

    @TransactionalEventListener
    fun publishJemsAuditEvent(event: UserCreatedEvent) =
        eventPublisher.publishEvent(
            JemsAuditEvent(
                auditUser = event.user.toAuditUser(),
                auditCandidate = AuditBuilder(AuditAction.USER_ADDED)
                    .entityRelatedId(event.user.id)
                    .description(
                        "A new user ${event.user.email} was created:\n${
                            event.user.getDiff().onlyNewChanges()
                        }"
                    )
                    .build()
            )
        )

    @EventListener
    fun publishJemsMailEvent(event: UserCreatedEvent) {
        if (event.confirmationToken == null) return
        eventPublisher.publishEvent(
            JemsMailEvent(
                emailTemplateFileName = "user-registration-confirmation.html",
                mailNotificationInfo = MailNotificationInfo(
                    subject = "[Jems] Please confirm your email address",
                    templateVariables =
                    setOf(
                        Variable("name", event.user.name),
                        Variable("surname", event.user.surname),
                        Variable(
                            "accountValidationLink",
                            "${appProperties.serverUrl}/registrationConfirmation?token=${event.confirmationToken}"
                        )
                    ),
                    recipients = setOf(event.user.email),
                    messageType = "User registration confirmation"
                )
            )
        )
    }

}
