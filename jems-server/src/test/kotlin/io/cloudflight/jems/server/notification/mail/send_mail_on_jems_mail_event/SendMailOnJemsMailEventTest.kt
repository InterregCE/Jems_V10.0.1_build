package io.cloudflight.jems.server.notification.mail.send_mail_on_jems_mail_event

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.common.event.JemsMailEvent
import io.cloudflight.jems.server.common.model.Variable
import io.cloudflight.jems.server.notification.mail.config.MailConfigProperties
import io.cloudflight.jems.server.notification.mail.service.MailNotificationPersistence
import io.cloudflight.jems.server.notification.mail.service.mail_body_generator_service.MailBodyGeneratorService
import io.cloudflight.jems.server.notification.mail.service.mail_sender_service.MailSenderService
import io.cloudflight.jems.server.notification.mail.service.model.MailNotification
import io.cloudflight.jems.server.notification.mail.service.model.MailNotificationInfo
import io.cloudflight.jems.server.notification.mail.service.send_mail_on_jems_mail_event.SendMailOnJemsMailEvent
import io.cloudflight.jems.server.programme.service.userrole.ProgrammeDataPersistence
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.slot
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.actuate.info.InfoEndpoint
import org.springframework.transaction.support.TransactionSynchronizationManager

open class SendMailOnJemsMailEventTest : UnitTest() {

    @MockK
    lateinit var persistence: MailNotificationPersistence

    @MockK
    lateinit var programmeDataPersistence: ProgrammeDataPersistence

    @MockK
    lateinit var infoEndpoint: InfoEndpoint

    @MockK
    lateinit var mailBodyGenerator: MailBodyGeneratorService

    @MockK
    lateinit var mailService: MailSenderService

    @MockK
    lateinit var configs: MailConfigProperties


    @InjectMockKs
    lateinit var sendMailEvent: SendMailOnJemsMailEvent

    @BeforeEach
    fun setup() {
        every { configs.bccList } returns emptyList()
        TransactionSynchronizationManager.initSynchronization()
    }

    @Test
    fun `enqueue Mail`() {
        val info = MailNotificationInfo("subject", emptySet(), emptySet(), "messageType")
        val mailEvent = JemsMailEvent("emailTemplateFileName", info)
        val mailNotification = MailNotification(0, info.subject, "body", info.recipients, info.messageType)
        val programmeName = "programmeName"
        val helpdeskUrl = "https://helpdesk.com"
        val callback = slot<(MailNotification) -> Any?>()

        every { programmeDataPersistence.getProgrammeName() } returns programmeName
        every { infoEndpoint.info() } returns mapOf("helpdesk-url" to helpdeskUrl)
        every {
            mailBodyGenerator.generateBodyText(mailEvent, setOf(Variable("programmeName", programmeName), Variable("helpdeskLink", helpdeskUrl)))
        } returns "body"
        every { persistence.deleteIfExist(mailNotification.id) } returns Unit
        every { persistence.save(mailNotification) } returns mailNotification
        every { mailService.send(mailNotification, capture(callback)) } answers {
            callback.captured.invoke(mailNotification).let { }
        }

        sendMailEvent.enqueueMail(mailEvent)
        TransactionSynchronizationManager.getSynchronizations().first().afterCommit()

        verify { persistence.save(mailNotification) }
        verify { mailService.send(mailNotification, callback.captured) }
        verify { persistence.deleteIfExist(eq(0)) }
    }

}
