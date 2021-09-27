package io.cloudflight.jems.server.user.service.user.register_user

import io.cloudflight.jems.api.audit.dto.AuditAction
import io.cloudflight.jems.server.audit.model.AuditUser
import io.cloudflight.jems.server.audit.service.AuditBuilder
import io.cloudflight.jems.server.audit.service.AuditCandidate
import io.cloudflight.jems.server.common.audit.onlyNewChanges
import io.cloudflight.jems.server.common.event.JemsAuditEvent
import io.cloudflight.jems.server.common.event.JemsMailEvent
import io.cloudflight.jems.server.common.model.Variable
import io.cloudflight.jems.server.notification.mail.service.model.MailNotificationInfo
import io.cloudflight.jems.server.user.service.model.User
import io.cloudflight.jems.server.user.service.toAuditUser

data class UserRegisteredEvent(
    val user: User,
    override val emailTemplateFileName: String = "user-registration-confirmation.html",
    override val auditUser: AuditUser? = user.toAuditUser()
) : JemsMailEvent, JemsAuditEvent {

    override fun getAuditCandidate(): AuditCandidate =
        AuditBuilder(AuditAction.USER_REGISTERED)
            .entityRelatedId(user.id)
            .description("A new user ${user.email} registered:\n${user.getDiff().onlyNewChanges()}")
            .build()

    override fun getMailNotificationInfo() =
        MailNotificationInfo(
            subject = "[Jems] Please confirm your email address",
            templateVariables =
            setOf(
                Variable("name", user.name),
                Variable("surname", user.surname),
                Variable("accountValidationLink", "")
            ),
            recipients = setOf(user.email),
            messageType = "User registration confirmation"
        )

}
