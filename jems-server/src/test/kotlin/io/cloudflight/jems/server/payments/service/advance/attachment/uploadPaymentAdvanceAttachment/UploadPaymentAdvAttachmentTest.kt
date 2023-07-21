package io.cloudflight.jems.server.payments.service.advance.attachment.uploadPaymentAdvanceAttachment

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.common.file.service.JemsFilePersistence
import io.cloudflight.jems.server.common.file.service.JemsProjectFileService
import io.cloudflight.jems.server.common.file.service.model.JemsFile
import io.cloudflight.jems.server.common.file.service.model.JemsFileCreate
import io.cloudflight.jems.server.common.file.service.model.JemsFileMetadata
import io.cloudflight.jems.server.common.file.service.model.JemsFileType
import io.cloudflight.jems.server.payments.model.advance.AdvancePaymentDetail
import io.cloudflight.jems.server.payments.service.advance.PaymentAdvancePersistence
import io.cloudflight.jems.server.project.service.file.model.ProjectFile
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.io.InputStream

class UploadPaymentAdvAttachmentTest : UnitTest() {

    companion object {
        private const val USER_ID = 9L

        private val content = mockk<InputStream>()
    }

    @MockK
    lateinit var paymentPersistence: PaymentAdvancePersistence

    @MockK
    lateinit var filePersistence: JemsFilePersistence

    @MockK
    lateinit var fileRepository: JemsProjectFileService

    @MockK
    lateinit var securityService: SecurityService

    @InjectMockKs
    lateinit var interactor: UploadPaymentAdvanceAttachment

    @BeforeEach
    fun reset() {
        clearMocks(paymentPersistence)
        clearMocks(filePersistence)
        clearMocks(fileRepository)
        every { securityService.getUserIdOrThrow() } returns USER_ID
    }

    @Test
    fun upload() {
        val paymentId = 4L
        val payment = mockk<AdvancePaymentDetail>()
        every { payment.id } returns paymentId
        every { payment.projectId } returns 540L
        every { paymentPersistence.getPaymentDetail(paymentId) } returns payment
        every { filePersistence.existsFile("Payment/Advance/000004/PaymentAdvanceAttachment/", "test.xlsx") } returns false

        val fileToAdd = slot<JemsFileCreate>()
        val mockResult = mockk<JemsFile>()
        val mockResultSimple = mockk<JemsFileMetadata>()
        every { mockResult.toSimple() } returns mockResultSimple
        every { fileRepository.persistFile(capture(fileToAdd)) } returns mockResult

        val file = ProjectFile(stream = content, name = "test.xlsx", size = 20L)
        assertThat(interactor.upload(paymentId, file)).isEqualTo(mockResultSimple)

        assertThat(fileToAdd.captured).isEqualTo(
            JemsFileCreate(
                projectId = 540L,
                partnerId = null,
                name = "test.xlsx",
                path = "Payment/Advance/000004/PaymentAdvanceAttachment/",
                type = JemsFileType.PaymentAdvanceAttachment,
                size = 20L,
                content = content,
                userId = USER_ID,
            )
        )
    }

    @Test
    fun `upload - duplicate`() {
        val paymentId = 11L
        val payment = mockk<AdvancePaymentDetail>()
        every { payment.id } returns paymentId
        every { paymentPersistence.getPaymentDetail(paymentId) } returns payment
        every { filePersistence.existsFile("Payment/Advance/000011/PaymentAdvanceAttachment/", "test.xlsx") } returns true

        val file = ProjectFile(stream = content, name = "test.xlsx", size = 20L)
        assertThrows<FileAlreadyExists> { interactor.upload(paymentId, file) }

        verify(exactly = 0) { fileRepository.persistFile(any()) }
    }

    @Test
    fun `upload - file type invalid`() {
        val paymentId = 15L
        val payment = mockk<AdvancePaymentDetail>()
        every { payment.id } returns paymentId
        every { paymentPersistence.getPaymentDetail(paymentId) } returns payment

        val file = ProjectFile(stream = content, name = "test.exe", size = 20L)
        assertThrows<FileTypeNotSupported> { interactor.upload(paymentId, file) }

        verify(exactly = 0) { fileRepository.persistFile(any()) }
    }
}
