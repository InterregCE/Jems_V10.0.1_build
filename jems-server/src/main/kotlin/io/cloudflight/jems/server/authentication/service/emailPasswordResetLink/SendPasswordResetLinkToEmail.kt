package io.cloudflight.jems.server.authentication.service.emailPasswordResetLink

import io.cloudflight.jems.server.authentication.model.PasswordResetToken
import io.cloudflight.jems.server.authentication.service.SecurityPersistence
import io.cloudflight.jems.server.common.event.JemsMailEvent
import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.common.model.Variable
import io.cloudflight.jems.server.config.AppProperties
import io.cloudflight.jems.server.notification.mail.service.model.MailNotificationInfo
import io.cloudflight.jems.server.user.repository.user.toUserSummary
import io.cloudflight.jems.server.user.service.UserPersistence
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.util.UUID

const val RESET_PASSWORD_TOKEN_VALIDITY_PERIOD_IN_HOUR = 24L

@Service
class SendPasswordResetLinkToEmail(
    private val userPersistence: UserPersistence,
    private val securityPersistence: SecurityPersistence,
    private val eventPublisher: ApplicationEventPublisher,
    private val appProperties: AppProperties
) : SendPasswordResetLinkToEmailInteractor {

    @Transactional
    @ExceptionWrapper(EmailPasswordResetLinkException::class)
    override fun send(email: String) {
        userPersistence.getByEmail(email)?.let { user ->
            val resetToken = PasswordResetToken(user.toUserSummary(), UUID.randomUUID(), Instant.now())
            securityPersistence.savePasswordResetToken(resetToken)
            eventPublisher.publishEvent(
                JemsMailEvent(
                    emailTemplateFileName = "password-reset-link.html",
                    mailNotificationInfo = MailNotificationInfo(
                        subject = "[Jems] Your password reset instructions",
                        templateVariables =
                        setOf(
                            Variable("name", user.name),
                            Variable("surname", user.surname),
                            Variable("validityPeriodInHour", RESET_PASSWORD_TOKEN_VALIDITY_PERIOD_IN_HOUR),
                            Variable(
                                "passwordResetLink",
                                "${appProperties.serverUrl}/no-auth/resetPassword?token=${resetToken.token}&email=${user.email}"
                            )
                        ),
                        recipients = setOf(email),
                        messageType = "Password reset link"
                    )
                )
            )
        }

    }
}
