package io.cloudflight.jems.server.payments.service.attachment.uploadPaymentAttachment

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.payments.PaymentPersistence
import io.cloudflight.jems.server.project.service.file.model.ProjectFile
import io.cloudflight.jems.server.project.service.report.file.ProjectReportFilePersistence
import io.cloudflight.jems.server.project.service.report.model.file.ProjectPartnerReportFileType
import io.cloudflight.jems.server.project.service.report.model.file.ProjectReportFileCreate
import io.cloudflight.jems.server.project.service.report.model.file.ProjectReportFileMetadata
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

class UploadPaymentAttachmentTest : UnitTest() {

    companion object {
        private const val USER_ID = 9L

        private val content = mockk<InputStream>()
    }

    @MockK
    lateinit var paymentPersistence: PaymentPersistence

    @MockK
    lateinit var reportFilePersistence: ProjectReportFilePersistence

    @MockK
    lateinit var securityService: SecurityService

    @InjectMockKs
    lateinit var interactor: UploadPaymentAttachment

    @BeforeEach
    fun reset() {
        clearMocks(paymentPersistence)
        clearMocks(reportFilePersistence)
        every { securityService.getUserIdOrThrow() } returns USER_ID
    }

    @Test
    fun upload() {
        val paymentId = 4L
        every { paymentPersistence.existsById(paymentId) } returns true
        every { reportFilePersistence.existsFile("Payment/000004/PaymentAttachment/", "test.xlsx") } returns false

        val fileToAdd = slot<ProjectReportFileCreate>()
        val mockResult = mockk<ProjectReportFileMetadata>()
        every { reportFilePersistence.addAttachmentToPartnerReport(capture(fileToAdd)) } returns mockResult

        val file = ProjectFile(stream = content, name = "test.xlsx", size = 20L)
        assertThat(interactor.upload(paymentId, file)).isEqualTo(mockResult)

        assertThat(fileToAdd.captured).isEqualTo(
            ProjectReportFileCreate(
                projectId = null,
                partnerId = null,
                name = "test.xlsx",
                path = "Payment/000004/PaymentAttachment/",
                type = ProjectPartnerReportFileType.PaymentAttachment,
                size = 20L,
                content = content,
                userId = USER_ID,
            )
        )
    }

    @Test
    fun `upload - duplicate`() {
        val paymentId = 11L
        every { paymentPersistence.existsById(paymentId) } returns true
        every { reportFilePersistence.existsFile("Payment/000011/PaymentAttachment/", "test.xlsx") } returns true

        val file = ProjectFile(stream = content, name = "test.xlsx", size = 20L)
        assertThrows<FileAlreadyExists> { interactor.upload(paymentId, file) }

        verify(exactly = 0) { reportFilePersistence.addAttachmentToPartnerReport(any()) }
    }

    @Test
    fun `upload - file type invalid`() {
        val paymentId = 15L
        every { paymentPersistence.existsById(paymentId) } returns true

        val file = ProjectFile(stream = content, name = "test.exe", size = 20L)
        assertThrows<FileTypeNotSupported> { interactor.upload(paymentId, file) }

        verify(exactly = 0) { reportFilePersistence.addAttachmentToPartnerReport(any()) }
    }

    @Test
    fun `upload - payment not exist`() {
        val paymentId = -1L
        every { paymentPersistence.existsById(paymentId) } returns false

        val file = ProjectFile(stream = content, name = "test.docx", size = 20L)
        assertThrows<PaymentNotFound> { interactor.upload(paymentId, file) }

        verify(exactly = 0) { reportFilePersistence.addAttachmentToPartnerReport(any()) }
    }

}
