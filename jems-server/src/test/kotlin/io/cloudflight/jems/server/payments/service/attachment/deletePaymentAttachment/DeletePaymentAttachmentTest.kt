package io.cloudflight.jems.server.payments.service.attachment.deletePaymentAttachment

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.service.report.file.ProjectReportFilePersistence
import io.cloudflight.jems.server.project.service.report.model.file.ProjectPartnerReportFileType
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class DeletePaymentAttachmentTest : UnitTest() {

    @MockK
    lateinit var reportFilePersistence: ProjectReportFilePersistence

    @InjectMockKs
    lateinit var interactor: DeletePaymentAttachment

    @BeforeEach
    fun reset() {
        clearMocks(reportFilePersistence)
    }

    @Test
    fun delete() {
        val fileId = 15L
        every { reportFilePersistence.existsFile(ProjectPartnerReportFileType.PaymentAttachment, fileId) } returns true
        every { reportFilePersistence.deleteFile(ProjectPartnerReportFileType.PaymentAttachment, fileId) } answers { }

        interactor.delete(fileId)
        verify(exactly = 1) { reportFilePersistence.deleteFile(ProjectPartnerReportFileType.PaymentAttachment, fileId) }
    }

    @Test
    fun `delete - not existing`() {
        every { reportFilePersistence.existsFile(ProjectPartnerReportFileType.PaymentAttachment, -1L) } returns false
        assertThrows<FileNotFound> { interactor.delete(-1L) }

        verify(exactly = 0) { reportFilePersistence.deleteFile(any<ProjectPartnerReportFileType>(), any()) }
        verify(exactly = 0) { reportFilePersistence.deleteFile(any<Long>(), any()) }
    }

}
