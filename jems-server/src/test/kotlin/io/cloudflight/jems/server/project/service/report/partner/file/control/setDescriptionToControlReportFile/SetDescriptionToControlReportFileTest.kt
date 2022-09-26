package io.cloudflight.jems.server.project.service.report.partner.file.control.setDescriptionToControlReportFile

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.common.validator.GeneralValidatorService
import io.cloudflight.jems.server.project.service.report.file.ProjectReportFilePersistence
import io.cloudflight.jems.server.project.service.report.partner.file.control.ControlReportFileAuthorizationService
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class SetDescriptionToControlReportFileTest : UnitTest() {

    companion object {
        private const val PARTNER_ID = 402L
    }

    @MockK
    lateinit var generalValidator: GeneralValidatorService
    @MockK
    lateinit var authorization: ControlReportFileAuthorizationService
    @MockK
    lateinit var reportFilePersistence: ProjectReportFilePersistence

    @InjectMockKs
    lateinit var interactor: SetDescriptionToControlReportFile

    @BeforeEach
    fun setup() {
        clearMocks(generalValidator, authorization, reportFilePersistence)
        every { generalValidator.throwIfAnyIsInvalid(*varargAny { it.isEmpty() }) } returns Unit
        every { generalValidator.maxLength(any<String>(), 250, "description") } returns emptyMap()
        every { authorization.validateChangeToFileAllowed(PARTNER_ID, any(), any()) } answers { }
    }

    @Test
    fun setDescription() {
        every { reportFilePersistence.setDescriptionToFile(261L, "new desc") } answers { }

        interactor.setDescription(PARTNER_ID, reportId = 477L, fileId = 261L, "new desc")

        verify(exactly = 1) { authorization.validateChangeToFileAllowed(PARTNER_ID, 477L, fileId = 261L) }
        verify(exactly = 1) { reportFilePersistence.setDescriptionToFile(261L, "new desc") }
        verify(exactly = 1) { generalValidator.maxLength("new desc", 250, "description") }
        verify(exactly = 1) { generalValidator.throwIfAnyIsInvalid(*varargAny { it.isEmpty() }) }
    }

}
