package io.cloudflight.jems.server.user.service.user.update_user

import io.cloudflight.jems.api.audit.dto.AuditAction
import io.cloudflight.jems.server.audit.service.AuditBuilder
import io.cloudflight.jems.server.common.audit.fromOldToNewChanges
import io.cloudflight.jems.server.common.event.JemsAuditEvent
import io.cloudflight.jems.server.common.event.JemsMailEvent
import io.cloudflight.jems.server.common.model.Variable
import io.cloudflight.jems.server.config.AppProperties
import io.cloudflight.jems.server.notification.mail.service.model.MailNotificationInfo
import io.cloudflight.jems.server.user.service.model.User
import io.cloudflight.jems.server.user.service.toAuditUser
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.event.TransactionalEventListener

data class UserUpdatedEvent(val updatedUser: User, val oldUser: User)

@Service
data class UserUpdatedEventListeners(
    private val eventPublisher: ApplicationEventPublisher, private val appProperties: AppProperties
) {

    @TransactionalEventListener
    fun publishJemsAuditEvent(event: UserUpdatedEvent) =
        eventPublisher.publishEvent(
            JemsAuditEvent(
                auditUser = event.updatedUser.toAuditUser(),
                auditCandidate = AuditBuilder(AuditAction.USER_DATA_CHANGED)
                    .entityRelatedId(event.updatedUser.id)
                    .description(
                        "User data changed for user id=${event.oldUser.id}:\n${
                            event.updatedUser.getDiff(event.oldUser).fromOldToNewChanges()
                        }"
                    )
                    .build()
            )
        )

    @TransactionalEventListener
    fun publishJemsMailEvent(event: UserUpdatedEvent) {
        if (event.updatedUser.confirmationToken == null) return
        eventPublisher.publishEvent(
            JemsMailEvent(
                emailTemplateFileName = "user-registration-confirmation.html",
                mailNotificationInfo = MailNotificationInfo(
                    subject = "[Jems] Please confirm your email address",
                    templateVariables =
                    setOf(
                        Variable("name", event.updatedUser.name),
                        Variable("surname", event.updatedUser.surname),
                        Variable(
                            "accountValidationLink",
                            "${appProperties.serverUrl}/registrationConfirmation?token=${event.updatedUser.confirmationToken}"
                        )
                    ),
                    recipients = setOf(event.updatedUser.email),
                    messageType = "User registration confirmation"
                )
            )
        )
    }

}
