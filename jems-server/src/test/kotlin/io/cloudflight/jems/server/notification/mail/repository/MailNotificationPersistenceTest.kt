package io.cloudflight.jems.server.notification.mail.repository

import io.cloudflight.jems.server.notification.mail.entity.MailNotificationEntity
import io.cloudflight.jems.server.notification.mail.service.model.MailNotification
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class MailNotificationPersistenceTest {

    @MockK
    lateinit var repository: MailNotificationRepository

    @InjectMockKs
    lateinit var persistence: MailNotificationPersistenceProvider

    @Test
    fun `save successful`() {
        val mailNotification = MailNotification(
            id = 0L,
            subject = "[Jems] Please confirm your email address",
            body = "Test",
            recipients = setOf("applicant@interact.eu"),
            messageType = "Message has been sent"
        )

        every { repository.save(any()) } returns MailNotificationEntity(
            id = 0L,
            mailNotification.subject,
            mailNotification.body,
            mailNotification.recipients,
            mailNotification.messageType
        )

        val savedMailNotification = persistence.save(mailNotification)

        assertThat(savedMailNotification).isNotNull
        assertThat(savedMailNotification.body).isEqualTo("Test")
        assertThat(savedMailNotification.messageType).isEqualTo("Message has been sent")

    }

    @Test
    fun `delete if exists`() {
        val notificationId = 0L
        every { repository.deleteById(any()) } answers {}

        persistence.deleteIfExist(notificationId)

        verify(exactly = 1) { repository.deleteById(notificationId) }
    }
}