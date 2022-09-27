package io.cloudflight.jems.server.project.service.report.partner.file.control.uploadFileToControlReport

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.project.service.file.model.ProjectFile
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import io.cloudflight.jems.server.project.service.report.ProjectReportPersistence
import io.cloudflight.jems.server.project.service.report.file.ProjectReportFilePersistence
import io.cloudflight.jems.server.project.service.report.model.ProjectPartnerReport
import io.cloudflight.jems.server.project.service.report.model.ReportStatus
import io.cloudflight.jems.server.project.service.report.model.file.*
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import java.io.InputStream

class UploadFileToControlReportTest : UnitTest() {

    companion object {
        private const val PROJECT_ID = 360L
        private const val PARTNER_ID = 434L
        private const val USER_ID = 9L

        private val content = mockk<InputStream>()
    }

    @MockK
    lateinit var reportFilePersistence: ProjectReportFilePersistence

    @MockK
    lateinit var reportPersistence: ProjectReportPersistence

    @MockK
    lateinit var partnerPersistence: PartnerPersistence

    @MockK
    lateinit var securityService: SecurityService

    @InjectMockKs
    lateinit var interactor: UploadFileToControlReport

    @BeforeEach
    fun reset() {
        clearMocks(partnerPersistence)
        clearMocks(reportFilePersistence)
        every { partnerPersistence.getProjectIdForPartnerId(PARTNER_ID) } returns PROJECT_ID
        every { securityService.getUserIdOrThrow() } returns USER_ID
    }

    @ParameterizedTest(name = "uploadToControlReport (status {0})")
    @EnumSource(value = ReportStatus::class, names = ["InControl"])
    fun uploadToControlReport(status: ReportStatus) {
        val reportId = 49L
        val report = mockk<ProjectPartnerReport>()
        every { report.status } returns status
        every { reportPersistence.getPartnerReportById(PARTNER_ID, reportId = reportId) } returns report
        every { reportFilePersistence
            .existsFile("Project/000360/Report/Partner/000434/PartnerControlReport/000049/ControlDocument/", "test.xlsx")
        } returns false

        val fileToAdd = slot<ProjectReportFileCreate>()
        val mockResult = mockk<ProjectReportFileMetadata>()
        every { reportFilePersistence.addAttachmentToPartnerReport(capture(fileToAdd)) } returns mockResult

        val file = ProjectFile(
            stream = content,
            name = "test.xlsx",
            size = 20L,
        )
        assertThat(interactor.uploadToControlReport(PARTNER_ID, reportId, file)).isEqualTo(mockResult)

        assertThat(fileToAdd.captured).isEqualTo(
            ProjectReportFileCreate(
                projectId = PROJECT_ID,
                partnerId = PARTNER_ID,
                name = "test.xlsx",
                path = "Project/000360/Report/Partner/000434/PartnerControlReport/000049/ControlDocument/",
                type = ProjectPartnerReportFileType.ControlDocument,
                size = 20L,
                content = content,
                userId = USER_ID,
            )
        )
    }

    @ParameterizedTest(name = "uploadToControlReport - wrong status (status {0})")
    @EnumSource(value = ReportStatus::class, names = ["InControl"], mode = EnumSource.Mode.EXCLUDE)
    fun `uploadToControlReport - wrong status`(status: ReportStatus) {
        val reportId = 56L
        val report = mockk<ProjectPartnerReport>()
        every { report.status } returns status
        every { reportPersistence.getPartnerReportById(PARTNER_ID, reportId = reportId) } returns report

        val file = ProjectFile(
            stream = content,
            name = "no-mikey-no.docx",
            size = 15L,
        )
        assertThrows<ReportNotInControl> { interactor.uploadToControlReport(PARTNER_ID, reportId, file) }
        verify(exactly = 0) { reportFilePersistence.addAttachmentToPartnerReport(any()) }
    }

    @ParameterizedTest(name = "uploadToControlReport - wrong file type (status {0})")
    @EnumSource(value = ReportStatus::class, names = ["InControl"])
    fun `uploadToControlReport - wrong file type`(status: ReportStatus) {
        val reportId = 10L
        val report = mockk<ProjectPartnerReport>()
        every { report.status } returns status
        every { reportPersistence.getPartnerReportById(PARTNER_ID, reportId = reportId) } returns report

        val file = ProjectFile(
            stream = content,
            name = "no-mikey-no.exe",
            size = 18L,
        )
        assertThrows<FileTypeNotSupported> { interactor.uploadToControlReport(PARTNER_ID, reportId, file) }
        verify(exactly = 0) { reportFilePersistence.addAttachmentToPartnerReport(any()) }
    }

    @ParameterizedTest(name = "uploadToControlReport - file duplicate (status {0})")
    @EnumSource(value = ReportStatus::class, names = ["InControl"])
    fun `uploadToControlReport - file duplicate`(status: ReportStatus) {
        val reportId = 28L
        val report = mockk<ProjectPartnerReport>()
        every { report.status } returns status
        every { reportPersistence.getPartnerReportById(PARTNER_ID, reportId = reportId) } returns report
        every { reportFilePersistence
            .existsFile("Project/000360/Report/Partner/000434/PartnerControlReport/000028/ControlDocument/", "duplicate.xlsx")
        } returns true

        val fileToAdd = slot<ProjectReportFileCreate>()
        val mockResult = mockk<ProjectReportFileMetadata>()
        every { reportFilePersistence.addAttachmentToPartnerReport(capture(fileToAdd)) } returns mockResult

        val file = ProjectFile(
            stream = content,
            name = "duplicate.xlsx",
            size = 20L,
        )
        assertThrows<FileAlreadyExists> { interactor.uploadToControlReport(PARTNER_ID, reportId, file) }
        verify(exactly = 0) { reportFilePersistence.addAttachmentToPartnerReport(any()) }
    }

}
