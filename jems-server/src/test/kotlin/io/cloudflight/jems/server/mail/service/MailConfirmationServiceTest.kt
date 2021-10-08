package io.cloudflight.jems.server.mail.service

import io.cloudflight.jems.api.audit.dto.AuditAction
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.audit.service.AuditCandidate
import io.cloudflight.jems.server.common.event.JemsAuditEvent
import io.cloudflight.jems.server.common.event.JemsEvent
import io.cloudflight.jems.server.common.model.Variable
import io.cloudflight.jems.server.mail.confirmation.service.MailConfirmationPersistence
import io.cloudflight.jems.server.mail.confirmation.service.MailConfirmationService
import io.cloudflight.jems.server.mail.confirmation.service.model.MailConfirmation
import io.cloudflight.jems.server.notification.mail.config.MailConfigProperties
import io.cloudflight.jems.server.notification.mail.service.model.MailNotificationInfo
import io.cloudflight.jems.server.user.service.model.User
import io.cloudflight.jems.server.user.service.model.UserRole
import io.cloudflight.jems.server.user.service.model.UserStatus
import io.cloudflight.jems.server.user.service.user.ConfirmUserEmailEvent
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.verify
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.context.ApplicationEventPublisher
import java.time.ZonedDateTime
import java.util.UUID

internal class MailConfirmationServiceTest : UnitTest() {

    companion object {
        private const val USER_ID = 11L
        private const val ROLE_ID = 8L
        private val TOKEN = UUID.fromString("9d9938b9-a479-4892-89a8-86f1aaa5ef7c")
        private val TIMESTAMP = ZonedDateTime.now()
    }

    @MockK
    lateinit var confirmationMailPersistence: MailConfirmationPersistence

    @RelaxedMockK
    lateinit var auditPublisher: ApplicationEventPublisher

    @RelaxedMockK
    lateinit var configs: MailConfigProperties

    @InjectMockKs
    lateinit var mailConfirmationService: MailConfirmationService

    @Test
    fun publishUserRelatedEvents() {
        val user = User(
            id = MailConfirmationServiceTest.USER_ID,
            email = "maintainer@interact.eu",
            name = "Michael",
            surname = "Schumacher",
            userRole = UserRole(
                id = MailConfirmationServiceTest.ROLE_ID,
                name = "maintainer",
                permissions = emptySet()
            ),
            userStatus = UserStatus.UNCONFIRMED
        )

        val mailConfirmation = MailConfirmation(
            token = MailConfirmationServiceTest.TOKEN,
            userId = MailConfirmationServiceTest.USER_ID,
            timestamp = MailConfirmationServiceTest.TIMESTAMP,
            clicked = false
        )

        every { confirmationMailPersistence.save(any()) } returns mailConfirmation

        mailConfirmationService.createConfirmationEmail(user)

        val events = mutableListOf<JemsEvent>()
        verify(exactly = 1) { auditPublisher.publishEvent(capture(events)) }
        Assertions.assertThat((events[0] as ConfirmUserEmailEvent).getMailNotificationInfo()).isEqualTo(
            MailNotificationInfo(
                subject = "[Jems] Please confirm your email address",
                templateVariables = setOf(
                    Variable(name = "name", value = "Michael"),
                    Variable(name = "surname", value = "Schumacher"),
                    Variable(name = "accountValidationLink", value = "/registrationConfirmation?token=9d9938b9-a479-4892-89a8-86f1aaa5ef7c")
                ),
                recipients = setOf("maintainer@interact.eu"),
                messageType = "User registration confirmation"
            )
        )
    }
}
