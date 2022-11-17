package io.cloudflight.jems.server.payments.service.advance.attachment.setDescriptionToPaymentAdvAttachment

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.common.minio.JemsProjectFileRepository
import io.cloudflight.jems.server.common.validator.GeneralValidatorService
import io.cloudflight.jems.server.project.service.report.ProjectReportFilePersistence
import io.cloudflight.jems.server.project.service.report.model.file.JemsFileType.PaymentAdvanceAttachment
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class SetDescriptionToPaymentAdvAttachmentTest : UnitTest() {

    @MockK
    lateinit var reportFilePersistence: ProjectReportFilePersistence

    @MockK
    lateinit var fileRepository: JemsProjectFileRepository

    @MockK
    lateinit var generalValidator: GeneralValidatorService

    @InjectMockKs
    lateinit var interactor: SetDescriptionToPaymentAdvAttachment

    @BeforeEach
    fun setup() {
        clearMocks(generalValidator, fileRepository)
        every { generalValidator.throwIfAnyIsInvalid(*varargAny { it.isEmpty() }) } returns Unit
        every { generalValidator.maxLength(any<String>(), 250, "description") } returns emptyMap()
    }

    @Test
    fun setDescription() {
        every { reportFilePersistence.existsFile(PaymentAdvanceAttachment, 91L) } returns true
        every { fileRepository.setDescription(91L, "new desc") } answers { }

        interactor.setDescription(fileId = 91L, "new desc")

        verify(exactly = 1) { fileRepository.setDescription(91L, "new desc") }
        verify(exactly = 1) { generalValidator.maxLength("new desc", 250, "description") }
        verify(exactly = 1) { generalValidator.throwIfAnyIsInvalid(*varargAny { it.isEmpty() }) }
    }

    @Test
    fun `setDescription - not existing`() {
        every { reportFilePersistence.existsFile(PaymentAdvanceAttachment, -1L) } returns false

        assertThrows<FileNotFound> { interactor.setDescription(fileId = -1L, "new desc") }

        verify(exactly = 0) { fileRepository.setDescription(-1L, "new desc") }
    }

}
