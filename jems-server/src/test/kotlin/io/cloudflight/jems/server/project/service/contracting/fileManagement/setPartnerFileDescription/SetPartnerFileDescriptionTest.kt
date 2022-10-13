package io.cloudflight.jems.server.project.service.contracting.fileManagement.setPartnerFileDescription

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.common.validator.AppInputValidationException
import io.cloudflight.jems.server.common.validator.GeneralValidatorService
import io.cloudflight.jems.server.project.service.report.file.ProjectReportFilePersistence
import io.cloudflight.jems.server.project.service.report.model.file.ProjectPartnerReportFileType
import io.cloudflight.jems.server.project.service.report.partner.file.setDescriptionToFile.FileNotFound
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class SetPartnerFileDescriptionTest: UnitTest() {

    companion object {
        private const val PARTNER_ID = 20L
        private const val FILE_ID = 30L
    }

    @MockK
    lateinit var reportFilePersistence: ProjectReportFilePersistence

    @MockK
    lateinit var generalValidator: GeneralValidatorService

    @InjectMockKs
    lateinit var setPartnerFileDescription: SetPartnerFileDescription


    @BeforeEach
    fun setup() {
        clearMocks(generalValidator, reportFilePersistence)
        every { generalValidator.throwIfAnyIsInvalid(*varargAny { it.isEmpty() }) } returns Unit
        every { generalValidator.throwIfAnyIsInvalid(*varargAny { it.isNotEmpty() }) } throws
            AppInputValidationException(emptyMap())
        every { generalValidator.maxLength(any<String>(), 250, "description") } returns emptyMap()
    }

    @Test
    fun setDescription() {
        every {
            reportFilePersistence.existsFileByPartnerIdAndFileIdAndFileTypeIn(
                PARTNER_ID,
                FILE_ID,
                setOf(ProjectPartnerReportFileType.ContractPartnerDoc)
            )
        } returns true
        every { reportFilePersistence.setDescriptionToFile(FILE_ID, "new desc") } answers { }

        setPartnerFileDescription.setPartnerFileDescription(PARTNER_ID, FILE_ID, "new desc")
        verify(exactly = 1) { reportFilePersistence.setDescriptionToFile(FILE_ID, "new desc") }
    }

    @Test
    fun `setDescription - not existing`() {
        every {
            reportFilePersistence.existsFileByPartnerIdAndFileIdAndFileTypeIn(
                PARTNER_ID,
                FILE_ID,
                setOf(ProjectPartnerReportFileType.ContractPartnerDoc)
            )
        } returns false
        assertThrows<FileNotFound> { setPartnerFileDescription.setPartnerFileDescription(PARTNER_ID, FILE_ID, "new desc") }
    }
}

