package io.cloudflight.jems.server.payments.service.account.attachment.uploadPaymentAccountAttachment

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.common.file.service.JemsFilePersistence
import io.cloudflight.jems.server.common.file.service.JemsSystemFileService
import io.cloudflight.jems.server.common.file.service.model.JemsFile
import io.cloudflight.jems.server.common.file.service.model.JemsFileCreate
import io.cloudflight.jems.server.common.file.service.model.JemsFileMetadata
import io.cloudflight.jems.server.common.file.service.model.JemsFileType
import io.cloudflight.jems.server.payments.model.ec.PaymentApplicationToEcDetail
import io.cloudflight.jems.server.project.service.file.model.ProjectFile
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.io.InputStream

class UploadPaymentAccountAttachmentTest : UnitTest() {

    companion object {
        private const val USER_ID = 9L

        private val content = mockk<InputStream>()
    }

    @MockK lateinit var filePersistence: JemsFilePersistence
    @MockK lateinit var fileRepository: JemsSystemFileService
    @MockK lateinit var securityService: SecurityService

    @InjectMockKs private lateinit var interactor: UploadPaymentAccountAttachment

    @BeforeEach
    fun reset() {
        clearMocks(filePersistence, fileRepository, securityService)
        every { securityService.getUserIdOrThrow() } returns USER_ID
    }

    @Test
    fun upload() {
        val paymentToEcId = 5L
        val paymentToEc = mockk<PaymentApplicationToEcDetail>()
        every { paymentToEc.id } returns paymentToEcId
        every { filePersistence.existsFile("Payment/Account/000005/PaymentAccountAttachment/", "test.txt") } returns false

        val fileToAdd = slot<JemsFileCreate>()
        val mockResult = mockk<JemsFile>()
        val mockResultSimple = mockk<JemsFileMetadata>()
        every { mockResult.toSimple() } returns mockResultSimple
        every { fileRepository.persistFile(capture(fileToAdd)) } returns mockResult

        val file = ProjectFile(stream = content, name = "test.txt", size = 20L)
        Assertions.assertThat(interactor.upload(paymentToEcId, file)).isEqualTo(mockResultSimple)

        Assertions.assertThat(fileToAdd.captured).isEqualTo(
            JemsFileCreate(
                projectId = null,
                partnerId = null,
                name = "test.txt",
                path = "Payment/Account/000005/PaymentAccountAttachment/",
                type = JemsFileType.PaymentAccountAttachment,
                size = 20L,
                content = content,
                userId = USER_ID,
            )
        )
    }

    @Test
    fun `upload - duplicate file`() {
        val paymentToEcId = 12L
        val paymentToEc = mockk<PaymentApplicationToEcDetail>()
        every {paymentToEc.id } returns paymentToEcId
        every { filePersistence.existsFile("Payment/Account/000012/PaymentAccountAttachment/", "test.txt") } returns true

        val file = ProjectFile(stream = content, name = "test.txt", size = 20L)
        assertThrows<FileAlreadyExists> { interactor.upload(paymentToEcId, file) }

        verify(exactly = 0) { fileRepository.persistFile(any()) }
    }

    @Test
    fun `upload - file type invalid`() {
        val paymentToEcId = 16L
        val paymentToEc = mockk<PaymentApplicationToEcDetail>()
        every {paymentToEc.id } returns paymentToEcId

        val file = ProjectFile(stream = content, name = "test.exe", size = 20L)
        assertThrows<FileTypeNotSupported> { interactor.upload(paymentToEcId, file) }

        verify(exactly = 0) { fileRepository.persistFile(any()) }
    }
}
