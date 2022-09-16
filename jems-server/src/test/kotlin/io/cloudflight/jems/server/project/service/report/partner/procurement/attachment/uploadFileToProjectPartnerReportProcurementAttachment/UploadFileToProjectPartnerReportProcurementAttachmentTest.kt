package io.cloudflight.jems.server.project.service.report.partner.procurement.attachment.uploadFileToProjectPartnerReportProcurementAttachment

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.project.service.file.model.ProjectFile
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import io.cloudflight.jems.server.project.service.report.ProjectReportPersistence
import io.cloudflight.jems.server.project.service.report.file.ProjectReportFilePersistence
import io.cloudflight.jems.server.project.service.report.model.file.ProjectPartnerReportFileType
import io.cloudflight.jems.server.project.service.report.model.file.ProjectReportFileCreate
import io.cloudflight.jems.server.project.service.report.model.file.ProjectReportFileMetadata
import io.cloudflight.jems.server.project.service.report.model.procurement.ProjectPartnerReportProcurement
import io.cloudflight.jems.server.project.service.report.partner.procurement.ProjectReportProcurementPersistence
import io.cloudflight.jems.server.project.service.report.partner.procurement.attachment.ProjectReportProcurementAttachmentPersistence
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

internal class UploadFileToProjectPartnerReportProcurementAttachmentTest : UnitTest() {

    companion object {
        private const val PROJECT_ID = 4877L
        private const val PARTNER_ID = 5920L
        private const val USER_ID = 11L

        private val stream = ByteArray(5).inputStream()
        private const val expectedPath = "Project/004877/Report/Partner/005920/PartnerReport/000299/Procurement/000484/ProcurementAttachment/"
    }

    @MockK
    lateinit var reportPersistence: ProjectReportPersistence
    @MockK
    lateinit var reportProcurementPersistence: ProjectReportProcurementPersistence
    @MockK
    lateinit var partnerPersistence: PartnerPersistence
    @MockK
    lateinit var reportFilePersistence: ProjectReportFilePersistence
    @MockK
    lateinit var reportProcurementAttachmentPersistence: ProjectReportProcurementAttachmentPersistence
    @MockK
    lateinit var securityService: SecurityService

    @InjectMockKs
    lateinit var interactor: UploadFileToProjectPartnerReportProcurementAttachment

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
        every { reportProcurementAttachmentPersistence.countAttachmentsCreatedUpUntilNow(procurementId, reportId) } returns 4L

        val file = ProjectFile(stream = stream, name = "filename.docx", size = 5L)

        every { reportFilePersistence.existsFile(exactPath = expectedPath, fileName = "filename.docx") } returns false
        val slotFile = slot<ProjectReportFileCreate>()
        val mockResult = mockk<ProjectReportFileMetadata>()
        every { reportFilePersistence
            .addPartnerReportProcurementAttachment(reportId = reportId, procurementId, capture(slotFile))
        } returns mockResult

        assertThat(interactor.uploadToProcurement(PARTNER_ID, reportId, procurementId, file)).isEqualTo(mockResult)

        assertThat(slotFile.captured.projectId).isEqualTo(PROJECT_ID)
        assertThat(slotFile.captured.partnerId).isEqualTo(PARTNER_ID)
        assertThat(slotFile.captured.name).isEqualTo("filename.docx")
        assertThat(slotFile.captured.path).isEqualTo(expectedPath)
        assertThat(slotFile.captured.type).isEqualTo(ProjectPartnerReportFileType.ProcurementAttachment)
        assertThat(slotFile.captured.size).isEqualTo(5L)
        assertThat(slotFile.captured.userId).isEqualTo(USER_ID)
    }

    @Test
    fun `uploadToProcurement - report not found`() {
        val procurementId = 488L
        mockProcurement(procurementId)

        val reportId = -1L
        every { reportPersistence.exists(partnerId = PARTNER_ID, reportId = reportId) } returns false

        assertThrows<ReportNotFound> { interactor.uploadToProcurement(PARTNER_ID, reportId, procurementId, mockk()) }
        verify(exactly = 0) { reportFilePersistence.addPartnerReportProcurementAttachment(any(), any(), any()) }
    }

    @Test
    fun `uploadToProcurement - file type not supported`() {
        val procurementId = 447L
        mockProcurement(procurementId)

        val reportId = 237L
        every { reportPersistence.exists(partnerId = PARTNER_ID, reportId = reportId) } returns true
        every { reportProcurementAttachmentPersistence.countAttachmentsCreatedUpUntilNow(procurementId, reportId) } returns 7L

        val file = ProjectFile(stream = stream, name = "filename.wrongext", size = 5L)

        every { reportFilePersistence.existsFile(exactPath = expectedPath, fileName = "filename.docx") } returns false
        assertThrows<FileTypeNotSupported> { interactor.uploadToProcurement(PARTNER_ID, reportId, procurementId, file) }
        verify(exactly = 0) { reportFilePersistence.addPartnerReportProcurementAttachment(any(), any(), any()) }
    }

    @Test
    fun `uploadToProcurement - max amount of attachments`() {
        val procurementId = 441L
        mockProcurement(procurementId)

        val reportId = 306L
        every { reportPersistence.exists(partnerId = PARTNER_ID, reportId = reportId) } returns true
        every { reportProcurementAttachmentPersistence.countAttachmentsCreatedUpUntilNow(procurementId, reportId) } returns 30L

        val file = ProjectFile(stream = stream, name = "filename.pdf", size = 5L)

        assertThrows<MaxAmountOfAttachmentReachedException> { interactor.uploadToProcurement(PARTNER_ID, reportId, procurementId, file) }
        verify(exactly = 0) { reportFilePersistence.addPartnerReportProcurementAttachment(any(), any(), any()) }
    }

    private fun mockProcurement(id: Long) {
        val procurement = mockk<ProjectPartnerReportProcurement>()
        every { procurement.id } returns id
        every { reportProcurementPersistence.getById(PARTNER_ID, procurementId = id) } returns procurement
    }

}
