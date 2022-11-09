package io.cloudflight.jems.server.payments.service.attachment.deletePaymentAttachment

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.common.minio.GenericPaymentFileRepository
import io.cloudflight.jems.server.project.service.report.model.file.ProjectPartnerReportFileType
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class DeletePaymentAttachmentTest : UnitTest() {

    @MockK
    lateinit var genericFileRepository: GenericPaymentFileRepository

    @InjectMockKs
    lateinit var interactor: DeletePaymentAttachment

    @BeforeEach
    fun reset() {
        clearMocks(genericFileRepository)
    }

    @Test
    fun delete() {
        val fileId = 15L
        every { genericFileRepository.delete(any(), fileId) } answers { }

        interactor.delete(fileId)
        verify(exactly = 1) { genericFileRepository.delete(ProjectPartnerReportFileType.PaymentAttachment, fileId) }
    }

}
