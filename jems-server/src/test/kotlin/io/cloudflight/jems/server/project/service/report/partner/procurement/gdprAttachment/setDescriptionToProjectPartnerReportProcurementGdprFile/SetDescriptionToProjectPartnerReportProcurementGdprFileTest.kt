package io.cloudflight.jems.server.project.service.report.partner.procurement.gdprAttachment.setDescriptionToProjectPartnerReportProcurementGdprFile

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.common.file.service.JemsFilePersistence
import io.cloudflight.jems.server.common.file.service.JemsProjectFileService
import io.cloudflight.jems.server.common.validator.AppInputValidationException
import io.cloudflight.jems.server.common.validator.GeneralValidatorService
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import io.cloudflight.jems.server.project.service.report.partner.SensitiveDataAuthorizationService
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class SetDescriptionToProjectPartnerReportProcurementGdprFileTest: UnitTest() {

    companion object {
        private const val expectedPath = "Project/000008/Report/Partner/000640/PartnerReport/000477/Procurement/000201/ProcurementGdprAttachment/"
    }

    @MockK
    lateinit var partnerPersistence: PartnerPersistence

    @MockK
    lateinit var filePersistence: JemsFilePersistence

    @MockK
    lateinit var fileService: JemsProjectFileService

    @MockK
    lateinit var generalValidator: GeneralValidatorService

    @MockK
    lateinit var sensitiveDataAuthorization: SensitiveDataAuthorizationService

    @InjectMockKs
    lateinit var service: SetDescriptionToProjectPartnerReportProcurementGdprFile

    @BeforeEach
    fun setup() {
        clearMocks(generalValidator, filePersistence, fileService, sensitiveDataAuthorization)
        every { generalValidator.throwIfAnyIsInvalid(*varargAny { it.isEmpty() }) } returns Unit
        every { generalValidator.throwIfAnyIsInvalid(*varargAny { it.isNotEmpty() }) } throws
            AppInputValidationException(emptyMap())
        every { generalValidator.maxLength(any<String>(), 250, "description") } returns emptyMap()
    }

    @Test
    fun setDescription() {
        val partnerId = 640L
        val projectId = 8L
        val fileId = 200L
        every { partnerPersistence.getProjectIdForPartnerId(partnerId) } returns projectId
        every { filePersistence.existsFile(partnerId, expectedPath, 200L) } returns true
        every { fileService.setDescription(fileId, "new desc") } answers { }
        every { sensitiveDataAuthorization.canEditPartnerSensitiveData(partnerId) } returns true

        service.setDescription(partnerId, reportId = 477L, fileId = 200L, procurementId = 201L, "new desc")
        verify(exactly = 1) { fileService.setDescription(200L, "new desc") }
    }

    @Test
    fun `setDescription - user does not have access to gdpr`() {
        val partnerId = 640L
        val projectId = 8L
        val fileId = 200L
        every { partnerPersistence.getProjectIdForPartnerId(partnerId) } returns projectId
        every { filePersistence.existsFile(partnerId, expectedPath, 200L) } returns true
        every { fileService.setDescription(fileId, "new desc") } answers { }
        every { sensitiveDataAuthorization.canEditPartnerSensitiveData(partnerId) } returns false

        assertThrows<SensitiveFileException> { service.setDescription(partnerId, 477L, fileId = fileId, 201L,"new desc") }
    }
    @Test
    fun `setDescription - not existing`() {
        val partnerId = 645L
        val projectId = 9L
        every { partnerPersistence.getProjectIdForPartnerId(partnerId) } returns projectId
        every { filePersistence
            .existsFile(partnerId, "Project/000009/Report/Partner/000645/PartnerReport/000477/Procurement/000201/ProcurementGdprAttachment/"
                , -1L)
        } returns false
        every { sensitiveDataAuthorization.canEditPartnerSensitiveData(partnerId) } returns true

        assertThrows<FileNotFound> { service.setDescription(partnerId, 477L, fileId = -1L, 201L,"") }
    }
}
