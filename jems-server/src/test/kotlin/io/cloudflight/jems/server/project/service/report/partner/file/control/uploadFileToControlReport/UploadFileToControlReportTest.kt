package io.cloudflight.jems.server.project.service.report.partner.file.control.uploadFileToControlReport

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.authentication.model.CurrentUser
import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.common.file.service.JemsFilePersistence
import io.cloudflight.jems.server.common.file.service.model.JemsFile
import io.cloudflight.jems.server.common.file.service.model.JemsFileCreate
import io.cloudflight.jems.server.common.file.service.model.JemsFileMetadata
import io.cloudflight.jems.server.common.file.service.model.JemsFileType
import io.cloudflight.jems.server.notification.handler.FileChangeAction
import io.cloudflight.jems.server.notification.handler.ProjectFileChangeEvent
import io.cloudflight.jems.server.payments.controller.PaymentAdvanceAttachmentControllerTest
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.project.service.file.model.ProjectFile
import io.cloudflight.jems.server.project.service.model.ProjectSummary
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerRole
import io.cloudflight.jems.server.project.service.report.model.partner.ProjectPartnerReport
import io.cloudflight.jems.server.project.service.report.model.partner.ProjectPartnerReportSubmissionSummary
import io.cloudflight.jems.server.project.service.report.model.partner.ReportStatus
import io.cloudflight.jems.server.project.service.report.partner.ProjectPartnerReportPersistence
import io.cloudflight.jems.server.project.service.report.partner.file.ProjectPartnerReportFilePersistence
import io.cloudflight.jems.server.project.service.sharedFolder.delete.DeleteFileFromSharedFolderTest
import io.cloudflight.jems.server.utils.FILE_NAME
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import java.io.InputStream
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import org.springframework.context.ApplicationEventPublisher
import java.time.ZonedDateTime

class UploadFileToControlReportTest : UnitTest() {

    companion object {
        private const val PROJECT_ID = 360L
        private const val PARTNER_ID = 434L
        private const val USER_ID = 9L

        private fun partnerReportSubmissionSummary(status: ReportStatus): ProjectPartnerReportSubmissionSummary {
            return ProjectPartnerReportSubmissionSummary(
                id = 49L,
                reportNumber = 1,
                status = status,
                version = "v1.0",
                firstSubmission = ZonedDateTime.now().minusDays(1),
                controlEnd = mockk(),
                createdAt = ZonedDateTime.now().minusDays(2),
                projectIdentifier = "test",
                partnerAbbreviation = "pa",
                partnerId = PARTNER_ID,
                partnerNumber = 1,
                partnerRole = ProjectPartnerRole.PARTNER,
                projectAcronym = "project"
            )
        }

        private val projectSummary = ProjectSummary(
            id = PROJECT_ID,
            customIdentifier = "test",
            callId = 1L,
            callName = "",
            acronym = "project",
            status = ApplicationStatus.CONTRACTED
        )

        private val jemsFile = JemsFile(
            id = 904L,
            name = "test.xlsx",
            type = JemsFileType.ControlReport,
            uploaded = ZonedDateTime.now(),
            author = mockk(),
            size = 4L,
            description = "desc",
            indexedPath = "indexed/path",
        )

        private val fileMetadata = JemsFileMetadata(
            id = 904L,
            name = "test.xlsx",
            uploaded = ZonedDateTime.now(),
        )

        private val content = mockk<InputStream>()
    }

    @MockK
    lateinit var reportFilePersistence: ProjectPartnerReportFilePersistence

    @MockK
    lateinit var filePersistence: JemsFilePersistence

    @MockK
    lateinit var reportPersistence: ProjectPartnerReportPersistence

    @MockK
    lateinit var partnerPersistence: PartnerPersistence

    @MockK
    lateinit var projectPersistence: ProjectPersistence

    @MockK
    lateinit var securityService: SecurityService

    @MockK
    lateinit var eventPublisher: ApplicationEventPublisher

    @InjectMockKs
    lateinit var interactor: UploadFileToControlReport

    @BeforeEach
    fun reset() {
        clearMocks(partnerPersistence, filePersistence, reportFilePersistence, eventPublisher)
        every { partnerPersistence.getProjectIdForPartnerId(PARTNER_ID) } returns PROJECT_ID
        every { securityService.getUserIdOrThrow() } returns USER_ID
    }

    @ParameterizedTest(name = "uploadToControlReport (status {0})")
    @EnumSource(value = ReportStatus::class, names = ["InControl", "ReOpenInControlLast", "ReOpenInControlLimited", "Certified"])
    fun uploadToControlReport(status: ReportStatus) {
        val reportId = 49L
        val report = mockk<ProjectPartnerReport>()
        every { report.status } returns status
        every { reportPersistence.getPartnerReportById(PARTNER_ID, reportId = reportId) } returns report
        every { filePersistence
            .existsFile("Project/000360/Report/Partner/000434/PartnerControlReport/000049/ControlDocument/", "test.xlsx")
        } returns false
        every { reportPersistence.getProjectPartnerReportSubmissionSummary(PARTNER_ID, reportId = reportId) } returns
            partnerReportSubmissionSummary(status)
        every { projectPersistence.getProjectSummary(PROJECT_ID) } returns projectSummary
        every { securityService.currentUser!!.user.email } returns "test@email.com"
        every { securityService.getUserIdOrThrow() } returns USER_ID
        every { eventPublisher.publishEvent(ofType(ProjectFileChangeEvent::class)) } returns Unit

        val fileToAdd = slot<JemsFileCreate>()
        every { reportFilePersistence.addAttachmentToPartnerReport(capture(fileToAdd)) } returns jemsFile

        val file = ProjectFile(
            stream = content,
            name = "test.xlsx",
            size = 20L,
        )
        val changeEventSlot = slot<ProjectFileChangeEvent>()
        val uploadedFile = interactor.uploadToControlReport(PARTNER_ID, reportId, file)
        assertThat(uploadedFile).isEqualTo(fileMetadata.copy(uploaded = uploadedFile.uploaded))
        verify(exactly = 1) { eventPublisher.publishEvent(capture(changeEventSlot)) }

        assertThat(fileToAdd.captured).isEqualTo(
            JemsFileCreate(
                projectId = PROJECT_ID,
                partnerId = PARTNER_ID,
                name = "test.xlsx",
                path = "Project/000360/Report/Partner/000434/PartnerControlReport/000049/ControlDocument/",
                type = JemsFileType.ControlDocument,
                size = 20L,
                content = content,
                userId = USER_ID,
            )
        )

        assertThat(changeEventSlot.captured).isEqualTo(
            ProjectFileChangeEvent(FileChangeAction.Upload, projectSummary, jemsFile)
        )
    }

    @ParameterizedTest(name = "uploadToControlReport - wrong status (status {0})")
    @EnumSource(value = ReportStatus::class, mode = EnumSource.Mode.EXCLUDE,
        names = ["InControl", "ReOpenInControlLast", "ReOpenInControlLimited", "ReOpenCertified" ,"Certified"])
    fun `uploadToControlReport - wrong status`(status: ReportStatus) {
        val reportId = 56L
        val report = mockk<ProjectPartnerReport>()
        every { report.status } returns status
        every { reportPersistence.getPartnerReportById(PARTNER_ID, reportId = reportId) } returns report
        every { reportPersistence.getProjectPartnerReportSubmissionSummary(PARTNER_ID, reportId = reportId) } returns
            partnerReportSubmissionSummary(status)

        val file = ProjectFile(
            stream = content,
            name = "no-mikey-no.docx",
            size = 15L,
        )
        assertThrows<ReportNotInControl> { interactor.uploadToControlReport(PARTNER_ID, reportId, file) }
        verify(exactly = 0) { reportFilePersistence.addAttachmentToPartnerReport(any()) }
    }

    @ParameterizedTest(name = "uploadToControlReport - wrong file type (status {0})")
    @EnumSource(value = ReportStatus::class, names = ["InControl", "ReOpenInControlLast", "ReOpenInControlLimited", "Certified"])
    fun `uploadToControlReport - wrong file type`(status: ReportStatus) {
        val reportId = 10L
        val report = mockk<ProjectPartnerReport>()
        every { report.status } returns status
        every { reportPersistence.getPartnerReportById(PARTNER_ID, reportId = reportId) } returns report
        every { reportPersistence.getProjectPartnerReportSubmissionSummary(PARTNER_ID, reportId = reportId) } returns
            partnerReportSubmissionSummary(status)

        val file = ProjectFile(
            stream = content,
            name = "no-mikey-no.exe",
            size = 18L,
        )
        assertThrows<FileTypeNotSupported> { interactor.uploadToControlReport(PARTNER_ID, reportId, file) }
        verify(exactly = 0) { reportFilePersistence.addAttachmentToPartnerReport(any()) }
    }

    @ParameterizedTest(name = "uploadToControlReport - file duplicate (status {0})")
    @EnumSource(value = ReportStatus::class, names = ["InControl", "ReOpenInControlLast", "ReOpenInControlLimited", "Certified"])
    fun `uploadToControlReport - file duplicate`(status: ReportStatus) {
        val reportId = 28L
        val report = mockk<ProjectPartnerReport>()
        every { report.status } returns status
        every { reportPersistence.getPartnerReportById(PARTNER_ID, reportId = reportId) } returns report
        every { filePersistence
            .existsFile("Project/000360/Report/Partner/000434/PartnerControlReport/000028/ControlDocument/", "duplicate.xlsx")
        } returns true
        every { reportPersistence.getProjectPartnerReportSubmissionSummary(PARTNER_ID, reportId = reportId) } returns
            partnerReportSubmissionSummary(status)

        val fileToAdd = slot<JemsFileCreate>()
        val mockResult = mockk<JemsFile>()
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
