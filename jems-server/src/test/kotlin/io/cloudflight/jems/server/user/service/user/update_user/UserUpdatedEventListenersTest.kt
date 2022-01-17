package io.cloudflight.jems.server.user.service.user.update_user

import io.cloudflight.jems.api.audit.dto.AuditAction
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.audit.service.AuditCandidate
import io.cloudflight.jems.server.common.event.JemsAuditEvent
import io.cloudflight.jems.server.common.event.JemsMailEvent
import io.cloudflight.jems.server.common.model.Variable
import io.cloudflight.jems.server.config.AppProperties
import io.cloudflight.jems.server.notification.mail.service.model.MailNotificationInfo
import io.cloudflight.jems.server.user.service.model.User
import io.cloudflight.jems.server.user.service.model.UserRole
import io.cloudflight.jems.server.user.service.model.UserStatus
import io.mockk.clearAllMocks
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.springframework.context.ApplicationEventPublisher

internal class UserUpdatedEventListenersTest : UnitTest() {

    companion object {
        val oldUser = User(
            id = 1,
            email = "applicant@interact.eu",
            name = "Noam",
            surname = "Chomsky",
            userRole = UserRole(
                id = 1,
                name = "applicant",
                permissions = emptySet()
            ),
            userStatus = UserStatus.ACTIVE
        )

        val user = User(
            id = 1,
            email = "applicant@interact.eu",
            name = "Michael",
            surname = "Schumacher",
            userRole = UserRole(
                id = 1,
                name = "applicant",
                permissions = emptySet()
            ),
            userStatus = UserStatus.UNCONFIRMED
        )
    }

    @RelaxedMockK
    lateinit var eventPublisher: ApplicationEventPublisher

    @RelaxedMockK
    lateinit var AppProperties: AppProperties

    @InjectMockKs
    lateinit var userUpdatedEventListeners: UserUpdatedEventListeners

    @AfterEach
    internal fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun `updating a user should trigger an audit log`() {
        val auditSlot = slot<JemsAuditEvent>()
        val userUpdatedEvent = UserUpdatedEvent(user, oldUser, "token")

        userUpdatedEventListeners.publishJemsAuditEvent(userUpdatedEvent)

        verify(exactly = 1) { eventPublisher.publishEvent(capture(auditSlot)) }
        assertThat(auditSlot.captured.auditCandidate).isEqualTo(
            AuditCandidate(
                action = AuditAction.USER_DATA_CHANGED,
                project = null,
                entityRelatedId = 1,
                description = "User data changed for user id=1:\n" +
                    "name changed from 'Noam' to 'Michael',\n" +
                    "surname changed from 'Chomsky' to 'Schumacher',\n" +
                    "userStatus changed from ACTIVE to UNCONFIRMED"
            )
        )
    }

    @Test
    fun `updating an unconfirmed user should trigger an mail event`() {
        val auditSlot = slot<JemsMailEvent>()
        val userUpdatedEvent = UserUpdatedEvent(user, oldUser, "token")

        userUpdatedEventListeners.publishJemsMailEvent(userUpdatedEvent)

        verify(exactly = 1) { eventPublisher.publishEvent(capture(auditSlot)) }
        assertThat(auditSlot.captured.mailNotificationInfo).isEqualTo(
            MailNotificationInfo(
                subject = "[Jems] Please confirm your email address",
                templateVariables = setOf(
                    Variable(name = "name", value = "Michael"),
                    Variable(name = "surname", value = "Schumacher"),
                    Variable(name = "accountValidationLink", value = "/registrationConfirmation?token=token")
                ),
                recipients = setOf("applicant@interact.eu"),
                messageType = "User registration confirmation"

            )
        )
    }
}
