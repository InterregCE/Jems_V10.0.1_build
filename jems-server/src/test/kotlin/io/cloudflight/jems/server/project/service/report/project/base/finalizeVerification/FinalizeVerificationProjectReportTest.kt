package io.cloudflight.jems.server.project.service.report.project.base.finalizeVerification

import io.cloudflight.jems.api.audit.dto.AuditAction
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.audit.model.AuditCandidateEvent
import io.cloudflight.jems.server.notification.handler.ProjectReportStatusChanged
import io.cloudflight.jems.server.project.service.report.model.project.ProjectReportStatus
import io.cloudflight.jems.server.project.service.report.model.project.ProjectReportStatus.Finalized
import io.cloudflight.jems.server.project.service.report.model.project.ProjectReportStatus.InVerification
import io.cloudflight.jems.server.project.service.report.model.project.ProjectReportSubmissionSummary
import io.cloudflight.jems.server.project.service.report.model.project.base.ProjectReportModel
import io.cloudflight.jems.server.project.service.report.project.base.ProjectReportPersistence
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import java.time.ZonedDateTime
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import org.springframework.context.ApplicationEventPublisher

class FinalizeVerificationProjectReportTest : UnitTest() {

    companion object {
        private const val PROJECT_ID = 21L

        private val reportSubmissionSummary = ProjectReportSubmissionSummary(
            id = 52L,
            reportNumber = 4,
            status = Finalized,
            version = "5.6.1",
            firstSubmission = ZonedDateTime.now(),
            createdAt = ZonedDateTime.now(),
            projectIdentifier = "NS-AQ01",
            projectAcronym = "acronym",
            projectId = PROJECT_ID,
        )

        private fun report(id: Long, status: ProjectReportStatus): ProjectReportModel {
            val report = mockk<ProjectReportModel>()
            every { report.id } returns id
            every { report.status } returns status
            return report
        }
    }

    @MockK
    lateinit var reportPersistence: ProjectReportPersistence

    @MockK
    lateinit var auditPublisher: ApplicationEventPublisher

    @InjectMockKs
    lateinit var interactor: FinalizeVerificationProjectReport

    @BeforeEach
    fun reset() {
        clearMocks(reportPersistence)
        clearMocks(auditPublisher)
    }

    @Test
    fun finalizeVerification() {
        val reportId = 52L
        val report = report(reportId, InVerification)

        every { reportPersistence.getReportById(PROJECT_ID, reportId) } returns report

        every { reportPersistence.finalizeVerificationOnReportById(PROJECT_ID, reportId) } returns reportSubmissionSummary
        val auditSlot = slot<AuditCandidateEvent>()
        every { auditPublisher.publishEvent(capture(auditSlot)) } returns Unit
        every { auditPublisher.publishEvent(ofType(ProjectReportStatusChanged::class)) } returns Unit

        assertThat(interactor.finalizeVerification(PROJECT_ID, reportId)).isEqualTo(Finalized)

        verify(exactly = 1) { reportPersistence.finalizeVerificationOnReportById(PROJECT_ID, reportId) }

        assertThat(auditSlot.captured.auditCandidate.action).isEqualTo(AuditAction.PROJECT_REPORT_VERIFICATION_FINALIZED)
        assertThat(auditSlot.captured.auditCandidate.project?.id).isEqualTo(PROJECT_ID.toString())
        assertThat(auditSlot.captured.auditCandidate.project?.customIdentifier).isEqualTo("NS-AQ01")
        assertThat(auditSlot.captured.auditCandidate.project?.name).isEqualTo("acronym")
        assertThat(auditSlot.captured.auditCandidate.entityRelatedId).isEqualTo(reportId)
        assertThat(auditSlot.captured.auditCandidate.description).isEqualTo("[NS-AQ01] Project report R.4 verification was finalised")
    }

    @ParameterizedTest(name = "startVerification - wrong status (status {0})")
    @EnumSource(value = ProjectReportStatus::class, names = ["InVerification"], mode = EnumSource.Mode.EXCLUDE)
    fun `finalizeVerification - wrong status`(reportStatus: ProjectReportStatus) {
        val reportId = 52L
        val report = report(reportId, reportStatus)

        every { reportPersistence.getReportById(PROJECT_ID, reportId) } returns report
        assertThrows<ReportVerificationNotStartedException> {interactor.finalizeVerification(PROJECT_ID, reportId)}

        verify(exactly = 0) { reportPersistence.finalizeVerificationOnReportById(any(), any()) }
        verify(exactly = 0) { auditPublisher.publishEvent(any()) }
    }
}
