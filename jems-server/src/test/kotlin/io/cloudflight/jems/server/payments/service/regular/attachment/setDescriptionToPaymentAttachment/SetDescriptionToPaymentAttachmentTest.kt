package io.cloudflight.jems.server.payments.service.regular.attachment.setDescriptionToPaymentAttachment

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.common.file.service.JemsFilePersistence
import io.cloudflight.jems.server.common.file.service.JemsProjectFileService
import io.cloudflight.jems.server.common.validator.GeneralValidatorService
import io.cloudflight.jems.server.project.service.report.model.file.JemsFileType.PaymentAttachment
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class SetDescriptionToPaymentAttachmentTest : UnitTest() {

    @MockK
    lateinit var filePersistence: JemsFilePersistence

    @MockK
    lateinit var fileRepository: JemsProjectFileService

    @MockK
    lateinit var generalValidator: GeneralValidatorService

    @InjectMockKs
    lateinit var interactor: SetDescriptionToPaymentAttachment

    @BeforeEach
    fun setup() {
        clearMocks(generalValidator, fileRepository)
        every { generalValidator.throwIfAnyIsInvalid(*varargAny { it.isEmpty() }) } returns Unit
        every { generalValidator.maxLength(any<String>(), 250, "description") } returns emptyMap()
    }

    @Test
    fun setDescription() {
        every { filePersistence.existsFile(PaymentAttachment, 261L) } returns true
        every { fileRepository.setDescription(261L, "new desc") } answers { }

        interactor.setDescription(fileId = 261L, "new desc")

        verify(exactly = 1) { fileRepository.setDescription(261L, "new desc") }
        verify(exactly = 1) { generalValidator.maxLength("new desc", 250, "description") }
        verify(exactly = 1) { generalValidator.throwIfAnyIsInvalid(*varargAny { it.isEmpty() }) }
    }

    @Test
    fun `setDescription - not existing`() {
        every { filePersistence.existsFile(PaymentAttachment, -1L) } returns false

        assertThrows<FileNotFound> { interactor.setDescription(fileId = -1L, "new desc") }

        verify(exactly = 0) { fileRepository.setDescription(-1L, "new desc") }
    }

}
