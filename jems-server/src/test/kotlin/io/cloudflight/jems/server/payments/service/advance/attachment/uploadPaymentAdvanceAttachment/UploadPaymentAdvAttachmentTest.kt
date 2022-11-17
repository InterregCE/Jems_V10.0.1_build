package io.cloudflight.jems.server.payments.service.advance.attachment.uploadPaymentAdvanceAttachment

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.common.minio.JemsProjectFileRepository
import io.cloudflight.jems.server.payments.model.advance.AdvancePaymentDetail
import io.cloudflight.jems.server.payments.service.advance.PaymentAdvancePersistence
import io.cloudflight.jems.server.project.service.file.model.ProjectFile
import io.cloudflight.jems.server.project.service.report.ProjectReportFilePersistence
import io.cloudflight.jems.server.project.service.report.model.file.JemsFileCreate
import io.cloudflight.jems.server.project.service.report.model.file.JemsFileMetadata
import io.cloudflight.jems.server.project.service.report.model.file.JemsFileType
import io.mockk.*
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
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
    lateinit var reportFilePersistence: ProjectReportFilePersistence

    @MockK
    lateinit var fileRepository: JemsProjectFileRepository

    @MockK
    lateinit var securityService: SecurityService

    @InjectMockKs
    lateinit var interactor: UploadPaymentAdvanceAttachment

    @BeforeEach
    fun reset() {
        clearMocks(paymentPersistence)
        clearMocks(reportFilePersistence)
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
        every { reportFilePersistence.existsFile("Payment/Advance/000004/PaymentAdvanceAttachment/", "test.xlsx") } returns false

        val fileToAdd = slot<JemsFileCreate>()
        val mockResult = mockk<JemsFileMetadata>()
        every { fileRepository.persistProjectFile(capture(fileToAdd)) } returns mockResult

        val file = ProjectFile(stream = content, name = "test.xlsx", size = 20L)
        assertThat(interactor.upload(paymentId, file)).isEqualTo(mockResult)

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
        every { reportFilePersistence.existsFile("Payment/Advance/000011/PaymentAdvanceAttachment/", "test.xlsx") } returns true

        val file = ProjectFile(stream = content, name = "test.xlsx", size = 20L)
        assertThrows<FileAlreadyExists> { interactor.upload(paymentId, file) }

        verify(exactly = 0) { fileRepository.persistProjectFile(any()) }
    }

    @Test
    fun `upload - file type invalid`() {
        val paymentId = 15L
        val payment = mockk<AdvancePaymentDetail>()
        every { payment.id } returns paymentId
        every { paymentPersistence.getPaymentDetail(paymentId) } returns payment

        val file = ProjectFile(stream = content, name = "test.exe", size = 20L)
        assertThrows<FileTypeNotSupported> { interactor.upload(paymentId, file) }

        verify(exactly = 0) { fileRepository.persistProjectFile(any()) }
    }

}
