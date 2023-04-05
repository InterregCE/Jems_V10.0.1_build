package io.cloudflight.jems.server.project.service.report.partner.file.control.setDescriptionToControlReportFile

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.common.file.service.JemsProjectFileService
import io.cloudflight.jems.server.common.validator.GeneralValidatorService
import io.cloudflight.jems.server.project.authorization.AuthorizationUtil
import io.cloudflight.jems.server.project.service.report.partner.SensitiveDataAuthorizationService
import io.cloudflight.jems.server.project.service.report.partner.expenditure.ProjectPartnerReportExpenditurePersistence
import io.cloudflight.jems.server.project.service.report.partner.file.control.ControlReportFileAuthorizationService
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class SetDescriptionToControlReportFileTest : UnitTest() {

    companion object {
        private const val PARTNER_ID = 402L
    }

    @MockK
    lateinit var generalValidator: GeneralValidatorService
    @MockK
    lateinit var authorization: ControlReportFileAuthorizationService
    @MockK
    lateinit var fileService: JemsProjectFileService

    @MockK
    lateinit var securityService: SecurityService

    @MockK
    lateinit var sensitiveDataAuthorization: SensitiveDataAuthorizationService
    @MockK
    lateinit var reportExpenditurePersistence: ProjectPartnerReportExpenditurePersistence

    @InjectMockKs
    lateinit var interactor: SetDescriptionToControlReportFile

    @BeforeEach
    fun setup() {
        clearMocks(generalValidator, authorization, fileService)
        every { generalValidator.throwIfAnyIsInvalid(*varargAny { it.isEmpty() }) } returns Unit
        every { generalValidator.maxLength(any<String>(), 250, "description") } returns emptyMap()
        every { authorization.validateChangeToFileAllowed(PARTNER_ID, any(), any(), any()) } answers { }
    }

    @Test
    fun setDescription() {
        every { fileService.setDescription(261L, "new desc") } answers { }

        every { securityService.getUserIdOrThrow() }  returns AuthorizationUtil.applicantUser.user.id
        every { sensitiveDataAuthorization.canEditPartnerSensitiveData(PARTNER_ID) } returns true
        every { reportExpenditurePersistence.existsByPartnerIdAndAttachmentIdAndGdprTrue(PARTNER_ID, fileId = 261L) } returns false

        interactor.setDescription(PARTNER_ID, reportId = 477L, fileId = 261L, "new desc")

        verify(exactly = 1) { authorization.validateChangeToFileAllowed(PARTNER_ID, 477L, fileId = 261L, false) }
        verify(exactly = 1) { fileService.setDescription(261L, "new desc") }
        verify(exactly = 1) { generalValidator.maxLength("new desc", 250, "description") }
        verify(exactly = 1) { generalValidator.throwIfAnyIsInvalid(*varargAny { it.isEmpty() }) }
    }

    @Test
    fun `can set description for sensitive file`() {
        every { fileService.setDescription(261L, "new desc") } answers { }

        every { securityService.getUserIdOrThrow() }  returns AuthorizationUtil.applicantUser.user.id
        every { sensitiveDataAuthorization.canEditPartnerSensitiveData(PARTNER_ID) } returns true
        every { reportExpenditurePersistence.existsByPartnerIdAndAttachmentIdAndGdprTrue(PARTNER_ID, fileId = 261L) } returns true

        interactor.setDescription(PARTNER_ID, reportId = 477L, fileId = 261L, "new desc")

        verify(exactly = 1) { fileService.setDescription(261L, "new desc") }
    }


    @Test
    fun `setDescription sensitive throws for non gdpr user`() {
        every { reportExpenditurePersistence.existsByPartnerIdAndAttachmentIdAndGdprTrue(PARTNER_ID, fileId = 261L) } returns true
        every { sensitiveDataAuthorization.canEditPartnerSensitiveData(PARTNER_ID) } returns false

        assertThrows<SensitiveFileException> { interactor.setDescription(PARTNER_ID, reportId = 477L, fileId = 261L, "new desc") }

    }

}
