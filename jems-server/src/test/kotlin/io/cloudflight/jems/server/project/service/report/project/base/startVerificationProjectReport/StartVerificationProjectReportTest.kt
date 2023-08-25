package io.cloudflight.jems.server.project.service.report.project.base.startVerificationProjectReport

import io.cloudflight.jems.api.audit.dto.AuditAction
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.audit.model.AuditCandidateEvent
import io.cloudflight.jems.server.audit.model.AuditProject
import io.cloudflight.jems.server.audit.service.AuditCandidate
import io.cloudflight.jems.server.notification.handler.ProjectReportStatusChanged
import io.cloudflight.jems.server.project.service.report.model.project.ProjectReportStatus
import io.cloudflight.jems.server.project.service.report.model.project.ProjectReportSubmissionSummary
import io.cloudflight.jems.server.project.service.report.model.project.base.ProjectReportModel
import io.cloudflight.jems.server.project.service.report.project.base.ProjectReportPersistence
import io.cloudflight.jems.server.project.service.report.project.verification.expenditure.ProjectReportVerificationExpenditurePersistence
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
import org.springframework.context.ApplicationEventPublisher
import java.time.ZonedDateTime

internal class StartVerificationProjectReportTest : UnitTest() {

    companion object {
        private const val PROJECT_ID = 256L

        private val mockedResult = ProjectReportSubmissionSummary(
            id = 37L,
            reportNumber = 4,
            status = ProjectReportStatus.InVerification,
            version = "5.6.1",
            // not important
            firstSubmission = ZonedDateTime.now(),
            createdAt = ZonedDateTime.now(),
            projectIdentifier = "FG01_654",
            projectAcronym = "acronym",
            projectId = PROJECT_ID,
        )
    }

    @MockK
    lateinit var reportPersistence: ProjectReportPersistence

    @MockK
    lateinit var auditPublisher: ApplicationEventPublisher

    @MockK
    lateinit var projectReportExpenditureVerificationPersistence: ProjectReportVerificationExpenditurePersistence

    @InjectMockKs
    lateinit var interactor: StartVerificationProjectReport

    @BeforeEach
    fun reset() {
        clearMocks(reportPersistence)
        clearMocks(auditPublisher)
    }

    @ParameterizedTest(name = "startVerification (status {0})")
    @EnumSource(value = ProjectReportStatus::class, names = ["Submitted"])
    fun startVerification(status: ProjectReportStatus) {
        val report = report(37L, status)
        every { reportPersistence.getReportById(PROJECT_ID, 37L) } returns report

        every { reportPersistence.startVerificationOnReportById(any(), any()) } returns mockedResult
        every { projectReportExpenditureVerificationPersistence.initiateEmptyVerificationForProjectReport(any()) } returns Unit

        val slotAudit = slot<ProjectReportStatusChanged>()
        every { auditPublisher.publishEvent(capture(slotAudit)) } answers { }
        val slotStatusChanged = slot<AuditCandidateEvent>()
        every { auditPublisher.publishEvent(capture(slotStatusChanged)) } answers { }

        interactor.startVerification(PROJECT_ID, 37L)

        verify(exactly = 1) { reportPersistence.startVerificationOnReportById(PROJECT_ID, 37L) }

        assertThat(slotAudit.captured.projectReportSummary).isEqualTo(mockedResult)
        assertThat(slotAudit.captured.previousReportStatus).isEqualTo(ProjectReportStatus.Submitted)
        assertThat(slotStatusChanged.captured.auditCandidate).isEqualTo(
            AuditCandidate(AuditAction.PROJECT_REPORT_VERIFICATION_ONGOING,
                AuditProject("256", "FG01_654", "acronym"), 37L,
                "[FG01_654] Project report PR.4 verification started")
        )
    }

    @ParameterizedTest(name = "startVerification - wrong status (status {0})")
    @EnumSource(value = ProjectReportStatus::class, names = ["Submitted"], mode = EnumSource.Mode.EXCLUDE)
    fun `startVerification - wrong status`(status: ProjectReportStatus) {
        val report = report(39L, status)
        every { reportPersistence.getReportById(PROJECT_ID, 39L) } returns report

        assertThrows<ReportNotSubmitted> { interactor.startVerification(PROJECT_ID, 39L) }

        verify(exactly = 0) { reportPersistence.startVerificationOnReportById(any(), any()) }
        verify(exactly = 0) { auditPublisher.publishEvent(any()) }
    }

    private fun report(id: Long, status: ProjectReportStatus): ProjectReportModel {
        val report = mockk<ProjectReportModel>()
        every { report.id } returns id
        every { report.status } returns status
        return report
    }

}
