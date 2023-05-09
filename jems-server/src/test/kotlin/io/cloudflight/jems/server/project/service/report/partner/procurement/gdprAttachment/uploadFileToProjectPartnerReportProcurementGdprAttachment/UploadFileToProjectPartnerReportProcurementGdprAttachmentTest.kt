package io.cloudflight.jems.server.project.service.report.partner.procurement.gdprAttachment.uploadFileToProjectPartnerReportProcurementGdprAttachment

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.common.file.service.JemsFilePersistence
import io.cloudflight.jems.server.common.file.service.model.JemsFileCreate
import io.cloudflight.jems.server.common.file.service.model.JemsFileMetadata
import io.cloudflight.jems.server.common.file.service.model.JemsFileType
import io.cloudflight.jems.server.project.service.file.model.ProjectFile
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import io.cloudflight.jems.server.project.service.report.model.partner.procurement.ProjectPartnerReportProcurement
import io.cloudflight.jems.server.project.service.report.partner.ProjectPartnerReportPersistence
import io.cloudflight.jems.server.project.service.report.partner.SensitiveDataAuthorizationService
import io.cloudflight.jems.server.project.service.report.partner.file.ProjectPartnerReportFilePersistence
import io.cloudflight.jems.server.project.service.report.partner.procurement.ProjectPartnerReportProcurementPersistence
import io.cloudflight.jems.server.project.service.report.partner.procurement.gdprAttachment.ProjectPartnerReportProcurementGdprAttachmentPersistence
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class UploadFileToProjectPartnerReportProcurementGdprAttachmentTest : UnitTest() {

    companion object {
        private const val PROJECT_ID = 4877L
        private const val PARTNER_ID = 5920L
        private const val USER_ID = 11L

        private val stream = ByteArray(5).inputStream()
        private const val expectedPath = "Project/004877/Report/Partner/005920/PartnerReport/000299/Procurement/000484/ProcurementGdprAttachment/"
    }

    @MockK
    lateinit var reportPersistence: ProjectPartnerReportPersistence
    @MockK
    lateinit var reportProcurementPersistence: ProjectPartnerReportProcurementPersistence
    @MockK
    lateinit var partnerPersistence: PartnerPersistence
    @MockK
    lateinit var reportFilePersistence: ProjectPartnerReportFilePersistence
    @MockK
    lateinit var filePersistence: JemsFilePersistence
    @MockK
    lateinit var reportProcurementAttachmentPersistence: ProjectPartnerReportProcurementGdprAttachmentPersistence
    @MockK
    lateinit var securityService: SecurityService
    @MockK
    lateinit var sensitiveDataAuthorization: SensitiveDataAuthorizationService

    @InjectMockKs
    lateinit var interactor: UploadFileToProjectPartnerReportProcurementGdprAttachment

    @BeforeEach
    fun setup() {
        clearMocks(reportPersistence)
        clearMocks(reportProcurementPersistence)
        clearMocks(partnerPersistence)
        clearMocks(reportFilePersistence)
        clearMocks(reportProcurementAttachmentPersistence)
        clearMocks(securityService)
        every { partnerPersistence.getProjectIdForPartnerId(PARTNER_ID) } returns PROJECT_ID
        every { securityService.getUserIdOrThrow() } returns USER_ID
    }

    @Test
    fun uploadToProcurement() {
        val procurementId = 484L
        mockProcurement(procurementId)

        val reportId = 299L
        every { reportPersistence.exists(partnerId = PARTNER_ID, reportId = reportId) } returns true
        every { reportProcurementAttachmentPersistence.countGdprAttachmentsCreatedUpUntilNow(procurementId, reportId) } returns 4L
        every { sensitiveDataAuthorization.canEditPartnerSensitiveData(PARTNER_ID) } returns true


        val file = ProjectFile(stream = stream, name = "filename.docx", size = 5L)

        every { filePersistence.existsFile(exactPath = expectedPath, fileName = "filename.docx") } returns false
        val slotFile = slot<JemsFileCreate>()
        val mockResult = mockk<JemsFileMetadata>()
        every { reportFilePersistence
            .addPartnerReportProcurementGdprAttachment(reportId = reportId, procurementId, capture(slotFile))
        } returns mockResult

        assertThat(interactor.uploadToGdprProcurement(PARTNER_ID, reportId, procurementId, file)).isEqualTo(mockResult)

        assertThat(slotFile.captured.projectId).isEqualTo(PROJECT_ID)
        assertThat(slotFile.captured.partnerId).isEqualTo(PARTNER_ID)
        assertThat(slotFile.captured.name).isEqualTo("filename.docx")
        assertThat(slotFile.captured.path).isEqualTo(expectedPath)
        assertThat(slotFile.captured.type).isEqualTo(JemsFileType.ProcurementGdprAttachment)
        assertThat(slotFile.captured.size).isEqualTo(5L)
        assertThat(slotFile.captured.userId).isEqualTo(USER_ID)
    }

    @Test
    fun `uploadToProcurement - report not found`() {
        val procurementId = 488L
        mockProcurement(procurementId)

        val reportId = -1L
        every { reportPersistence.exists(partnerId = PARTNER_ID, reportId = reportId) } returns false
        every { sensitiveDataAuthorization.canEditPartnerSensitiveData(PARTNER_ID) } returns true

        assertThrows<ReportNotFound> { interactor.uploadToGdprProcurement(PARTNER_ID, reportId, procurementId, mockk()) }
        verify(exactly = 0) { reportFilePersistence.addPartnerReportProcurementGdprAttachment(any(), any(), any()) }
    }

    @Test
    fun `uploadToProcurement - file type not supported`() {
        val procurementId = 447L
        mockProcurement(procurementId)

        val reportId = 237L
        every { reportPersistence.exists(partnerId = PARTNER_ID, reportId = reportId) } returns true
        every { reportProcurementAttachmentPersistence.countGdprAttachmentsCreatedUpUntilNow(procurementId, reportId) } returns 7L
        every { sensitiveDataAuthorization.canEditPartnerSensitiveData(PARTNER_ID) } returns true

        val file = ProjectFile(stream = stream, name = "filename.wrongext", size = 5L)

        every { filePersistence.existsFile(exactPath = expectedPath, fileName = "filename.docx") } returns false
        assertThrows<FileTypeNotSupported> { interactor.uploadToGdprProcurement(PARTNER_ID, reportId, procurementId, file) }
        verify(exactly = 0) { reportFilePersistence.addPartnerReportProcurementAttachment(any(), any(), any()) }
    }

    @Test
    fun `uploadToProcurement - file already exists`() {
        val procurementId = 449L
        mockProcurement(procurementId)

        val reportId = 239L
        every { reportPersistence.exists(partnerId = PARTNER_ID, reportId = reportId) } returns true
        every { reportProcurementAttachmentPersistence.countGdprAttachmentsCreatedUpUntilNow(procurementId, reportId) } returns 0L
        every { sensitiveDataAuthorization.canEditPartnerSensitiveData(PARTNER_ID) } returns true

        val file = ProjectFile(stream = stream, name = "existing.pptx", size = 5L)

        every { filePersistence.existsFile(
            exactPath = "Project/004877/Report/Partner/005920/PartnerReport/000239/Procurement/000449/ProcurementGdprAttachment/",
            fileName = "existing.pptx",
        ) } returns true
        assertThrows<FileAlreadyExists> { interactor.uploadToGdprProcurement(PARTNER_ID, reportId, procurementId, file) }
        verify(exactly = 0) { reportFilePersistence.addPartnerReportProcurementGdprAttachment(any(), any(), any()) }
    }

    @Test
    fun `uploadToProcurement - max amount of attachments`() {
        val procurementId = 441L
        mockProcurement(procurementId)

        val reportId = 306L
        every { reportPersistence.exists(partnerId = PARTNER_ID, reportId = reportId) } returns true
        every { reportProcurementAttachmentPersistence.countGdprAttachmentsCreatedUpUntilNow(procurementId, reportId) } returns 30L
        every { sensitiveDataAuthorization.canEditPartnerSensitiveData(PARTNER_ID) } returns true

        val file = ProjectFile(stream = stream, name = "filename.pdf", size = 5L)

        assertThrows<MaxAmountOfAttachmentReachedException> { interactor.uploadToGdprProcurement(PARTNER_ID, reportId, procurementId, file) }
        verify(exactly = 0) { reportFilePersistence.addPartnerReportProcurementAttachment(any(), any(), any()) }
    }

    @Test
    fun `uploadToProcurement - user does not have gdpr permission`() {
        val procurementId = 441L
        mockProcurement(procurementId)

        val reportId = 306L
        every { reportPersistence.exists(partnerId = PARTNER_ID, reportId = reportId) } returns true
        every { reportProcurementAttachmentPersistence.countGdprAttachmentsCreatedUpUntilNow(procurementId, reportId) } returns 30L
        every { sensitiveDataAuthorization.canEditPartnerSensitiveData(PARTNER_ID) } returns false

        val file = ProjectFile(stream = stream, name = "filename.pdf", size = 5L)

        assertThrows<SensitiveFileException> { interactor.uploadToGdprProcurement(PARTNER_ID, reportId, procurementId, file) }
    }

    private fun mockProcurement(id: Long) {
        val procurement = mockk<ProjectPartnerReportProcurement>()
        every { procurement.id } returns id
        every { reportProcurementPersistence.getById(PARTNER_ID, procurementId = id) } returns procurement
    }

}
