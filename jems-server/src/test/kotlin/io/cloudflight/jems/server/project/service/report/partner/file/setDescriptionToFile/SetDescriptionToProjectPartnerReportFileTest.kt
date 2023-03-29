package io.cloudflight.jems.server.project.service.report.partner.file.setDescriptionToFile

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.common.file.service.JemsFilePersistence
import io.cloudflight.jems.server.common.file.service.JemsProjectFileService
import io.cloudflight.jems.server.common.validator.AppInputValidationException
import io.cloudflight.jems.server.common.validator.GeneralValidatorService
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import io.cloudflight.jems.server.project.service.report.partner.SensitiveDataAuthorizationService
import io.cloudflight.jems.server.project.service.report.partner.expenditure.ProjectPartnerReportExpenditurePersistence
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class SetDescriptionToProjectPartnerReportFileTest : UnitTest() {

    companion object {
        private const val expectedPath = "Project/000008/Report/Partner/000640/PartnerReport/000477/"
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

    @MockK
    lateinit var reportExpenditurePersistence: ProjectPartnerReportExpenditurePersistence

    @InjectMockKs
    lateinit var interactor: SetDescriptionToProjectPartnerReportFile

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
        val partnerId = 640L
        val projectId = 8L
        val fileId = 200L
        every { partnerPersistence.getProjectIdForPartnerId(partnerId) } returns projectId
        every { filePersistence.existsFile(partnerId, expectedPath, 200L) } returns true
        every { fileService.setDescription(fileId, "new desc") } answers { }

        every { sensitiveDataAuthorization.canEditPartnerSensitiveData(partnerId) } returns true
        every { reportExpenditurePersistence.existsByPartnerIdAndAttachmentIdAndGdprTrue(
            partnerId, fileId = fileId) } returns false

        interactor.setDescription(partnerId, reportId = 477L, fileId = 200L, "new desc")
        verify(exactly = 1) { fileService.setDescription(200L, "new desc") }
    }

    @Test
    fun `setDescription - sensitive`() {
        val partnerId = 640L
        val projectId = 8L
        val fileId = 200L
        every { partnerPersistence.getProjectIdForPartnerId(partnerId) } returns projectId
        every { filePersistence.existsFile(partnerId, expectedPath, 200L) } returns true
        every { fileService.setDescription(fileId, "new desc") } answers { }

        every { sensitiveDataAuthorization.canEditPartnerSensitiveData(partnerId) } returns true
        every { reportExpenditurePersistence.existsByPartnerIdAndAttachmentIdAndGdprTrue(
            partnerId, fileId = fileId) } returns true

        interactor.setDescription(partnerId, reportId = 477L, fileId = 200L, "new desc")
        verify(exactly = 1) { fileService.setDescription(200L, "new desc") }
    }

    @Test
    fun `setDescription - not existing`() {
        val partnerId = 645L
        val projectId = 9L
        every { partnerPersistence.getProjectIdForPartnerId(partnerId) } returns projectId
        every { reportExpenditurePersistence.existsByPartnerIdAndAttachmentIdAndGdprTrue(
            partnerId, fileId = -1L) } returns false
        every { filePersistence
            .existsFile(partnerId, "Project/000009/Report/Partner/000645/PartnerReport/000000/", -1L)
        } returns false

        assertThrows<FileNotFound> { interactor.setDescription(partnerId, 0L, fileId = -1L, "") }
    }

    @Test
    fun `setDescription to sensitive file throws for non gdpr user`() {
        val partnerId = 329L
        val fileId = 1197L

        every { sensitiveDataAuthorization.canEditPartnerSensitiveData(partnerId) } returns false
        every { reportExpenditurePersistence.existsByPartnerIdAndAttachmentIdAndGdprTrue(
            partnerId, fileId = fileId) } returns true

        assertThrows<SensitiveFileException> {
            interactor.setDescription(partnerId, 871L, fileId = fileId, "") }
    }

}
