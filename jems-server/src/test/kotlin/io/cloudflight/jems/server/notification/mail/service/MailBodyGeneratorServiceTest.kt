package io.cloudflight.jems.server.notification.mail.service

import io.cloudflight.jems.server.common.event.JemsMailEvent
import io.cloudflight.jems.server.common.minio.MinioStorage
import io.cloudflight.jems.server.common.model.Variable
import io.cloudflight.jems.server.notification.mail.service.mail_body_generator_service.DefaultMailBodyGenerator
import io.cloudflight.jems.server.notification.mail.service.model.MailNotificationInfo
import io.minio.errors.ErrorResponseException
import io.minio.messages.ErrorResponse
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.thymeleaf.ITemplateEngine

class MailBodyGeneratorServiceTest {

    companion object {
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
    }

    @MockK
    lateinit var minioStorage: MinioStorage

    @MockK
    lateinit var templateEngine: ITemplateEngine

    @InjectMockKs
    private lateinit var generator: DefaultMailBodyGenerator

    @Test
    fun `generate body text`() {
        val fileByteArray = ByteArray(5)

        every { minioStorage.getFile(any(), any()) } returns fileByteArray
        every { templateEngine.process(String(fileByteArray), any()) } returns fileByteArray.toString()

        assertThat(generator.generateBodyText(mailEvent, variables)).isNotNull
    }

    @Test
    fun `template not found error`() {
        val errResponse = ErrorResponse("404", "The specified key does not exist.", "", "", "", "", "")
        every { minioStorage.getFile(any(), any()) } throws ErrorResponseException(errResponse, null, "")

        assertThrows<ErrorResponseException> {
            generator.generateBodyText(mailEvent, variables)
        }
    }

}
