package io.cloudflight.jems.server.project.service.report.partner.control.file

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.common.file.service.JemsProjectFileService
import io.cloudflight.jems.server.common.validator.AppInputValidationException
import io.cloudflight.jems.server.common.validator.GeneralValidatorService
import io.cloudflight.jems.server.project.service.report.model.partner.control.file.PartnerReportControlFile
import io.cloudflight.jems.server.project.service.report.partner.control.file.setDescriptionToFile.SetDescriptionToFile
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

open class SetDescriptionToFileTest : UnitTest() {

    @MockK
    lateinit var generalValidator: GeneralValidatorService

    @MockK
    lateinit var projectPartnerReportControlFilePersistence: ProjectPartnerReportControlFilePersistence

    @MockK
    lateinit var fileService: JemsProjectFileService

    @InjectMockKs
    lateinit var setDescriptionToCertificate: SetDescriptionToFile

    @BeforeEach
    fun setup() {
        clearMocks(generalValidator, projectPartnerReportControlFilePersistence, fileService)
        every { generalValidator.throwIfAnyIsInvalid(*varargAny { it.isEmpty() }) } returns Unit
        every { generalValidator.throwIfAnyIsInvalid(*varargAny { it.isNotEmpty() }) } throws
            AppInputValidationException(emptyMap())
        every { generalValidator.maxLength(any<String>(), 250, "description") } returns emptyMap()
    }

    @Test
    fun setDescription() {
        val partnerId = 8L
        val reportId = 67L
        val fileId = 315L
        val controlFile = mockk<PartnerReportControlFile>()

        every { projectPartnerReportControlFilePersistence.getByReportIdAndId(reportId = reportId, 267L) } returns controlFile
        every { controlFile.generatedFile.id } returns fileId
        every { fileService.setDescription(fileId, "new desc") } answers { }

        setDescriptionToCertificate.setDescription(partnerId, reportId = reportId, fileId = 267L, "new desc")
        verify(exactly = 1) { fileService.setDescription(fileId, "new desc") }
    }


}
