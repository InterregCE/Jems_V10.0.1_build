package io.cloudflight.jems.server.project.service.report.project.verification.file.delete

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.common.file.service.JemsFilePersistence
import io.cloudflight.jems.server.common.file.service.model.JemsFile
import io.cloudflight.jems.server.common.file.service.model.JemsFileType.VerificationDocument
import io.cloudflight.jems.server.common.file.service.model.UserSimple
import io.cloudflight.jems.server.notification.handler.ProjectFileChangeEvent
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.project.service.contracting.model.reporting.ContractingDeadlineType
import io.cloudflight.jems.server.project.service.model.ProjectSummary
import io.cloudflight.jems.server.project.service.report.model.project.ProjectReportStatus
import io.cloudflight.jems.server.project.service.report.model.project.base.ProjectReportModel
import io.cloudflight.jems.server.project.service.report.project.base.ProjectReportPersistence
import io.cloudflight.jems.server.project.service.report.project.base.getProjectReport.GetProjectReportTest
import io.cloudflight.jems.server.project.service.report.project.base.reOpenVerificationProjectReport.ReOpenVerificationProjectReportTest
import io.cloudflight.jems.server.project.service.report.project.base.reOpenVerificationProjectReport.VerificationReportNotFinalized
import io.mockk.*
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import org.springframework.context.ApplicationEventPublisher
import java.math.BigDecimal
import java.time.LocalDate
import java.time.ZonedDateTime

class DeleteProjectReportVerificationFileTest : UnitTest() {

    companion object {
        const val PROJECT_ID = 51L
        const val REPORT_ID = 55L
        const val FILE_ID = 59L

        private val YESTERDAY = LocalDate.now().minusDays(1)
        private val TOMORROW = LocalDate.now().plusDays(1)
        private val MONTH_AGO = LocalDate.now().minusMonths(1)
        private val NOW = ZonedDateTime.now()
        private val WEEK_AGO = ZonedDateTime.now().minusWeeks(1)
        private val DAY_AGO = ZonedDateTime.now().minusDays(1)
        private val NEXT_MONTH = ZonedDateTime.now().plusMonths(1)

        fun filePath() = VerificationDocument.generatePath(PROJECT_ID, REPORT_ID)

        private  val dummyFile = JemsFile(
            id = 15L,
            name = "attachment.pdf",
            type = VerificationDocument,
            uploaded = ZonedDateTime.now(),
            author = UserSimple(45L, email = "admin@cloudflight.io", name = "Admin", surname = "Big"),
            size = 47889L,
            description = "desc",
            indexedPath = ""
        )

        private val projectSummary = ProjectSummary(
            id = PROJECT_ID,
            customIdentifier = "01",
            callId = 1L,
            callName = "",
            acronym = "project acronym",
            status = ApplicationStatus.CONTRACTED
        )

        private val projectReportModel = ProjectReportModel(
            id = REPORT_ID,
            reportNumber = 4,
            status = ProjectReportStatus.InVerification,
            linkedFormVersion = "v4",
            startDate = YESTERDAY,
            endDate = TOMORROW,
            deadlineId = 14L,
            finalReport = false,
            type = ContractingDeadlineType.Both,
            periodNumber = 7,
            reportingDate = MONTH_AGO,
            projectId = 114L,
            projectIdentifier = "proj identifier",
            projectAcronym = "project acronym",
            leadPartnerNameInOriginalLanguage = "LP orig",
            leadPartnerNameInEnglish = "LP english",
            spfPartnerId = null,
            createdAt = NOW,
            firstSubmission = WEEK_AGO,
            lastReSubmission = DAY_AGO,
            verificationDate = NEXT_MONTH.toLocalDate(),
            verificationEndDate = NEXT_MONTH,
            amountRequested = BigDecimal.valueOf(15L),
            totalEligibleAfterVerification = BigDecimal.valueOf(19L),
            lastVerificationReOpening = null,
            riskBasedVerification = false,
            riskBasedVerificationDescription = "Description"
        )
    }

    @MockK
    lateinit var filePersistence: JemsFilePersistence

    @MockK
    lateinit var projectPersistence: ProjectPersistence

    @MockK
    lateinit var reportPersistence: ProjectReportPersistence

    @MockK
    lateinit var auditPublisher: ApplicationEventPublisher

    @InjectMockKs
    lateinit var interactor: DeleteProjectReportVerificationFile


    @BeforeEach
    fun setup() {
        clearMocks(filePersistence)
    }

    @Test()
    fun delete() {
        every { filePersistence.existsFile(exactPath = filePath(), fileId = FILE_ID) } returns true
        every { filePersistence.deleteFile(VerificationDocument, FILE_ID) } returns Unit
        every { filePersistence.getFile(PROJECT_ID, FILE_ID) } returns dummyFile
        every { projectPersistence.getProjectSummary(PROJECT_ID) } answers { projectSummary }
        every { auditPublisher.publishEvent(ofType(ProjectFileChangeEvent::class)) } returns Unit
        every { reportPersistence.getReportById(PROJECT_ID, REPORT_ID)} returns projectReportModel

        val changeEventSlot = slot<ProjectFileChangeEvent>()
        interactor.delete(PROJECT_ID, REPORT_ID, FILE_ID)

        verify(exactly = 1) { filePersistence.deleteFile(VerificationDocument, FILE_ID) }
        verify(exactly = 1) { auditPublisher.publishEvent(capture(changeEventSlot)) }
    }

    @Test()
    fun `delete - FileNotFound`() {
        every { filePersistence.getFile(PROJECT_ID, FILE_ID) } throws FileNotFound()
        every { reportPersistence.getReportById(PROJECT_ID, REPORT_ID)} returns projectReportModel

        assertThrows<FileNotFound> { interactor.delete(PROJECT_ID, REPORT_ID, FILE_ID) }
    }

    @ParameterizedTest(name = "delete verification file - VerificationReportNotOngoing with status {0}")
    @EnumSource(value = ProjectReportStatus::class, names = ["InVerification", "ReOpenFinalized"], mode = EnumSource.Mode.EXCLUDE)
    fun `delete verification file - VerificationReportNotOngoing`(status: ProjectReportStatus) {
        val report = mockk<ProjectReportModel>()
        every { report.status } returns status
        every { reportPersistence.getReportById(PROJECT_ID, REPORT_ID) } returns report

        assertThrows<VerificationReportNotOngoing> { interactor.delete(PROJECT_ID, REPORT_ID, FILE_ID) }
    }
}
