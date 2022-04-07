package io.cloudflight.jems.server.authentication.service.emailPasswordResetLink

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.authentication.model.PasswordResetToken
import io.cloudflight.jems.server.authentication.service.SecurityPersistence
import io.cloudflight.jems.server.common.event.JemsMailEvent
import io.cloudflight.jems.server.common.model.Variable
import io.cloudflight.jems.server.config.AppProperties
import io.cloudflight.jems.server.notification.mail.service.model.MailNotificationInfo
import io.cloudflight.jems.server.user.service.UserPersistence
import io.cloudflight.jems.server.user.service.model.UserRole
import io.cloudflight.jems.server.user.service.model.UserStatus
import io.cloudflight.jems.server.user.service.model.UserWithPassword
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.slot
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.springframework.context.ApplicationEventPublisher

internal class SendPasswordResetLinkToEmailTest : UnitTest() {

    private val email = "email"
    private val userWithPassword = UserWithPassword(
        id = 1L,
        email = email,
        name = "name",
        surname = "surname",
        userRole = UserRole(2L, "name", emptySet(), false),
        encodedPassword = "hash_pass",
        userStatus = UserStatus.ACTIVE
    )

    @MockK
    lateinit var userPersistence: UserPersistence

    @MockK
    lateinit var securityPersistence: SecurityPersistence

    @MockK
    lateinit var eventPublisher: ApplicationEventPublisher

    @MockK
    lateinit var appProperties: AppProperties

    @InjectMockKs
    lateinit var sendPasswordResetLinkToEmail: SendPasswordResetLinkToEmail

    @Test
    fun `should do nothing if email address is not valid`() {
        every { userPersistence.getByEmail(email) } returns null

        assertDoesNotThrow {
            sendPasswordResetLinkToEmail.send(email)
        }
    }

    @Test
    fun `should generate token and publish JemsMailEvent if email address is valid`() {
        val tokenSlot = slot<PasswordResetToken>()
        val emailEventSlot = slot<JemsMailEvent>()
        every { userPersistence.getByEmail(email) } returns userWithPassword
        every { securityPersistence.savePasswordResetToken(capture(tokenSlot)) } returns Unit
        every { appProperties.serverUrl } returns "base-server-url"
        every { eventPublisher.publishEvent(capture(emailEventSlot)) } returns Unit

        sendPasswordResetLinkToEmail.send(email)

        assertThat(emailEventSlot.captured).isEqualTo(
            JemsMailEvent(
                emailTemplateFileName = "password-reset-link.html",
                mailNotificationInfo = MailNotificationInfo(
                    subject = "[Jems] Your password reset instructions",
                    templateVariables =
                    setOf(
                        Variable("name", userWithPassword.name),
                        Variable("surname", userWithPassword.surname),
                        Variable("validityPeriodInHour", RESET_PASSWORD_TOKEN_VALIDITY_PERIOD_IN_HOUR),
                        Variable(
                            "passwordResetLink",
                            "${appProperties.serverUrl}/no-auth/resetPassword?token=${tokenSlot.captured.token}&email=${userWithPassword.email}"
                        )
                    ),
                    recipients = setOf(email),
                    messageType = "Password reset link"
                )
            )
        )


    }
}
