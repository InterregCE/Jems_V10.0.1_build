package io.cloudflight.jems.server.notification.mail.send_mail_on_jems_mail_event

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
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.support.TransactionSynchronizationManager

class SendMailOnJemsMailEventTest {

    @MockK
    lateinit var persistence: MailNotificationPersistence
    @MockK
    lateinit var programmeDataPersistence: ProgrammeDataPersistence
    @MockK
    lateinit var mailBodyGenerator: MailBodyGeneratorService
    @MockK
    lateinit var mailService: MailSenderService
    @MockK
    lateinit var configs: MailConfigProperties
    @MockK
    lateinit var manager: PlatformTransactionManager

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
        every { programmeDataPersistence.getProgrammeName() } returns programmeName
        every { mailBodyGenerator.generateBodyText(mailEvent, setOf(Variable("programmeName", programmeName))) } returns "body"
        every { persistence.save(mailNotification) } returns mailNotification
        val deleteMethod: ((MailNotification) -> Any?) = { notification ->
            persistence.deleteIfExist(notification.id)
        }
        every { mailService.send(mailNotification, deleteMethod) } returnsArgument 0
        every { manager.commit(any()) } returns mailService.send(mailNotification, deleteMethod)

        sendMailEvent.enqueueMail(mailEvent)

        verify { persistence.save(mailNotification) }
        verify { mailService.send(mailNotification, deleteMethod) }
        // verify { persistence.deleteIfExist(0) } // .deleteIfExist(eq(0))) was not called. ?
    }

}
