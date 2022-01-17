package io.cloudflight.jems.server.notification.mail.service

import io.cloudflight.jems.server.common.event.JemsMailEvent
import io.cloudflight.jems.server.common.minio.MinioStorage
import io.cloudflight.jems.server.common.model.Variable
import io.cloudflight.jems.server.notification.mail.service.mail_body_generator_service.DefaultMailBodyGenerator
import io.cloudflight.jems.server.notification.mail.service.model.MailNotificationInfo
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.thymeleaf.ITemplateEngine

class MailBodyGeneratorServiceTest {

    @MockK
    lateinit var minioStorage: MinioStorage

    @MockK
    lateinit var templateEngine: ITemplateEngine

    @InjectMockKs
    private lateinit var generator: DefaultMailBodyGenerator

    @Test
    fun `generate body text`() {
        val variables = setOf(
            Variable("name", "name"),
            Variable("surname", "surname"),
            Variable(
                "accountValidationLink",
                "/registrationConfirmation?token=xx"
            )
        )
        val notificationInfo = MailNotificationInfo(
            subject = "subject",
            templateVariables = variables,
            recipients = setOf("notification@interact.eu"),
            messageType = "notif"
        )
        val mailEvent = JemsMailEvent(
            "template",
            notificationInfo
        )
        val fileByteArray = ByteArray(5)

        every { minioStorage.getFile(any(), any()) } returns fileByteArray
        every { templateEngine.process(String(fileByteArray), any()) } returns fileByteArray.toString()

        assertThat(generator.generateBodyText(mailEvent, variables)).isNotNull
    }

}