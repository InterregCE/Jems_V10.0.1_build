package io.cloudflight.jems.server.project.service.report.partner.control.file

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.common.file.service.JemsFilePersistence
import io.cloudflight.jems.server.common.file.service.JemsProjectFileService
import io.cloudflight.jems.server.common.validator.AppInputValidationException
import io.cloudflight.jems.server.common.validator.GeneralValidatorService
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import io.cloudflight.jems.server.project.service.report.partner.control.file.setDescriptionToCertificate.SetDescriptionToCertificate
import io.cloudflight.jems.server.project.service.report.partner.file.setDescriptionToFile.FileNotFound
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

open class SetDescriptionToCertificateTest: UnitTest() {

    @MockK
    lateinit var generalValidator: GeneralValidatorService

    @MockK
    lateinit var partnerPersistence: PartnerPersistence

    @MockK
    lateinit var filePersistence: JemsFilePersistence

    @MockK
    lateinit var fileService: JemsProjectFileService

    @InjectMockKs
    lateinit var setDescriptionToCertificate: SetDescriptionToCertificate

    companion object {
        private const val expectedPath = "Project/000061/Report/Partner/000008/PartnerControlReport/000067/ControlCertificate/"
    }


    @BeforeEach
    fun setup() {
        clearMocks(generalValidator, filePersistence, fileService)
        every { generalValidator.throwIfAnyIsInvalid(*varargAny { it.isEmpty() }) } returns Unit
        every { generalValidator.throwIfAnyIsInvalid(*varargAny { it.isNotEmpty() }) } throws
                AppInputValidationException(emptyMap())
        every { generalValidator.maxLength(any<String>(), 250, "description") } returns emptyMap()
    }

    @Test
    fun setDescription() {
        val partnerId = 8L
        val projectId = 61L
        every { partnerPersistence.getProjectIdForPartnerId(partnerId) } returns projectId
        every { filePersistence.existsFile(partnerId, expectedPath, 315L) } returns true
        every { fileService.setDescription(315L, "new desc") } answers { }

        setDescriptionToCertificate.setDescription(partnerId, reportId = 67L, fileId = 315L, "new desc")
        verify(exactly = 1) { fileService.setDescription(315L, "new desc") }
    }

    @Test
    fun `setDescription - not existing file`() {
        val partnerId = 8L
        val projectId = 61L
        every { partnerPersistence.getProjectIdForPartnerId(partnerId) } returns projectId
        every { filePersistence
            .existsFile(partnerId, expectedPath, -1L)
        } returns false

        assertThrows<FileNotFound> { setDescriptionToCertificate.setDescription(partnerId, reportId = 67L, fileId = -1L, "new desc") }
    }

}