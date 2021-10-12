package io.cloudflight.jems.server.notification.mail.service

import io.cloudflight.jems.server.notification.mail.config.MailConfigProperties
import io.cloudflight.jems.server.notification.mail.service.mail_sender_service.DefaultMailSender
import io.cloudflight.jems.server.notification.mail.service.mail_sender_service.MailNotSentEvent
import io.cloudflight.jems.server.notification.mail.service.mail_sender_service.MailSenderService
import io.cloudflight.jems.server.notification.mail.service.mail_sender_service.MailSentEvent
import io.cloudflight.jems.server.notification.mail.service.model.MailNotification
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.slot
import io.mockk.verify
import javax.mail.Session
import javax.mail.internet.MimeMessage
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.context.ApplicationEventPublisher
import org.springframework.mail.javamail.JavaMailSender


class MailSenderServiceTest {

    @MockK
    lateinit var mailSender: JavaMailSender

    @MockK
    lateinit var configs: MailConfigProperties

    @RelaxedMockK
    lateinit var auditPublisher: ApplicationEventPublisher


    lateinit var mailSenderService: MailSenderService

    @BeforeEach
    fun setup() {
        mailSenderService = DefaultMailSender(
            mailSender, auditPublisher, configs
        )
        every { configs.sender } returns "interact@interact.eu"
    }

    @Test
    fun `mail has not been sent`() {
        val mailNotification = MailNotification(
            id = 0L,
            subject = "[Jems] Please confirm your email address",
            body = "Test",
            recipients = setOf("applicant@interact.eu"),
            messageType = "Message has not been sent"
        )

        every { mailSender.createMimeMessage() } returnsArgument 0

        mailSenderService.send(mailNotification, null)

        val slotAudit = slot<MailNotSentEvent>()
        verify(exactly = 1) { auditPublisher.publishEvent(capture(slotAudit)) }

        assertThat(slotAudit.captured.notification).isNotNull
        assertThat(slotAudit.captured.notification.messageType).isEqualTo("Message has not been sent")
    }

    @Test
    fun `mail has been sent successfully`() {
        val mailNotification = MailNotification(
            id = 0L,
            subject = "[Jems] Please confirm your email address",
            body = "Test",
            recipients = setOf("applicant@interact.eu"),
            messageType = "Message has been sent"
        )
        val mimeMessage = MimeMessage(null as Session?)

        every { mailSender.createMimeMessage() } returns mimeMessage
        every { mailSender.send(mimeMessage) } returns Unit

        mailSenderService.send(mailNotification, null)

        val slotAudit = slot<MailSentEvent>()
        verify(exactly = 1) { auditPublisher.publishEvent(capture(slotAudit)) }

        assertThat(slotAudit.captured.notification).isNotNull
        assertThat(slotAudit.captured.notification.messageType).isEqualTo("Message has been sent")
    }


}