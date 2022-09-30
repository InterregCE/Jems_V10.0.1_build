package io.cloudflight.jems.server.payments.service.attachment.setDescriptionToPaymentAttachment

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.common.validator.GeneralValidatorService
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

class SetDescriptionToPaymentAttachmentTest : UnitTest() {

    @MockK
    lateinit var reportFilePersistence: ProjectReportFilePersistence

    @MockK
    lateinit var generalValidator: GeneralValidatorService

    @InjectMockKs
    lateinit var interactor: SetDescriptionToPaymentAttachment

    @BeforeEach
    fun setup() {
        clearMocks(generalValidator, reportFilePersistence)
        every { generalValidator.throwIfAnyIsInvalid(*varargAny { it.isEmpty() }) } returns Unit
        every { generalValidator.maxLength(any<String>(), 250, "description") } returns emptyMap()
    }

    @Test
    fun setDescription() {
        every { reportFilePersistence.existsFile(ProjectPartnerReportFileType.PaymentAttachment, 261L) } returns true
        every { reportFilePersistence.setDescriptionToFile(261L, "new desc") } answers { }

        interactor.setDescription(fileId = 261L, "new desc")

        verify(exactly = 1) { reportFilePersistence.setDescriptionToFile(261L, "new desc") }
        verify(exactly = 1) { generalValidator.maxLength("new desc", 250, "description") }
        verify(exactly = 1) { generalValidator.throwIfAnyIsInvalid(*varargAny { it.isEmpty() }) }
    }

    @Test
    fun `setDescription - not existing`() {
        every { reportFilePersistence.existsFile(ProjectPartnerReportFileType.PaymentAttachment, -1L) } returns false

        assertThrows<FileNotFound> { interactor.setDescription(fileId = -1L, "new desc") }

        verify(exactly = 0) { reportFilePersistence.setDescriptionToFile(any(), any()) }
    }

}
