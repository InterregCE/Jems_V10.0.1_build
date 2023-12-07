package io.cloudflight.jems.server.payments.service.audit.attachment.setDescriptionToPaymentAttachment

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.common.file.service.JemsFilePersistence
import io.cloudflight.jems.server.common.file.service.JemsSystemFileService
import io.cloudflight.jems.server.common.file.service.model.JemsFileType.PaymentAuditAttachment
import io.cloudflight.jems.server.common.validator.GeneralValidatorService
import io.cloudflight.jems.server.payments.service.ecPayment.export.attachment.setDescriptionToPaymentAuditAttachment.FileNotFound
import io.cloudflight.jems.server.payments.service.ecPayment.export.attachment.setDescriptionToPaymentAuditAttachment.SetDescriptionToPaymentAuditAttachment
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class SetDescriptionToPaymentAuditAttachmentTest : UnitTest() {

    @MockK
    lateinit var filePersistence: JemsFilePersistence

    @MockK
    lateinit var fileService: JemsSystemFileService

    @MockK
    lateinit var generalValidator: GeneralValidatorService

    @InjectMockKs
    lateinit var interactor: SetDescriptionToPaymentAuditAttachment

    @BeforeEach
    fun setup() {
        clearMocks(generalValidator, fileService)
        every { generalValidator.throwIfAnyIsInvalid(*varargAny { it.isEmpty() }) } returns Unit
        every { generalValidator.maxLength(any<String>(), 250, "description") } returns emptyMap()
    }

    @Test
    fun setDescription() {
        every { filePersistence.existsFile(PaymentAuditAttachment, 261L) } returns true
        every { fileService.setDescription(261L, "new desc") } answers { }

        interactor.setDescription(fileId = 261L, "new desc")

        verify(exactly = 1) { fileService.setDescription(261L, "new desc") }
        verify(exactly = 1) { generalValidator.maxLength("new desc", 250, "description") }
        verify(exactly = 1) { generalValidator.throwIfAnyIsInvalid(*varargAny { it.isEmpty() }) }
    }

    @Test
    fun `setDescription - not existing`() {
        every { filePersistence.existsFile(PaymentAuditAttachment, -1L) } returns false

        assertThrows<FileNotFound> { interactor.setDescription(fileId = -1L, "new desc") }

        verify(exactly = 0) { fileService.setDescription(-1L, "new desc") }
    }

}
