package io.cloudflight.jems.server.project.service.report.project.base.submitProjectReport

import io.cloudflight.jems.api.audit.dto.AuditAction
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.audit.model.AuditCandidateEvent
import io.cloudflight.jems.server.project.service.report.model.project.ProjectReportStatus
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
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.context.ApplicationEventPublisher
import java.time.ZonedDateTime

internal class SubmitProjectReportTest : UnitTest() {

    companion object {
        private const val PROJECT_ID = 256L
        private const val REPORT_ID = 35L

        private val mockedResult = ProjectReportSubmissionSummary(
            id = REPORT_ID,
            reportNumber = 4,
            projectId = PROJECT_ID,
            status = ProjectReportStatus.Submitted,
            version = "5.6.0",
            // not important
            firstSubmission = ZonedDateTime.now(),
            createdAt = ZonedDateTime.now(),
            projectIdentifier = "FG01_654",
            projectAcronym = "acronym",
        )
    }

    @MockK
    lateinit var reportPersistence: ProjectReportPersistence

    @MockK
    lateinit var auditPublisher: ApplicationEventPublisher

    @InjectMockKs
    lateinit var submitReport: SubmitProjectReport

    @BeforeEach
    fun reset() {
        clearMocks(reportPersistence)
        clearMocks(auditPublisher)
    }

    @Test
    fun submit() {
        val report = mockk<ProjectReportModel>()
        every { report.status } returns ProjectReportStatus.Draft
        every { report.id } returns REPORT_ID
        every { reportPersistence.getReportById(PROJECT_ID, REPORT_ID) } returns report

        val submissionTime = slot<ZonedDateTime>()
        every { reportPersistence.submitReport(any(), any(), capture(submissionTime)) } returns mockedResult

        val auditSlot = slot<AuditCandidateEvent>()
        every { auditPublisher.publishEvent(capture(auditSlot)) } returns Unit

        submitReport.submit(PROJECT_ID, REPORT_ID)

        verify(exactly = 1) { reportPersistence.submitReport(PROJECT_ID, REPORT_ID, any()) }
        assertThat(submissionTime.captured).isAfter(ZonedDateTime.now().minusMinutes(1))
        assertThat(submissionTime.captured).isBefore(ZonedDateTime.now().plusMinutes(1))
        assertThat(auditSlot.captured.auditCandidate.action).isEqualTo(AuditAction.PROJECT_REPORT_SUBMITTED)
        assertThat(auditSlot.captured.auditCandidate.project?.id).isEqualTo(PROJECT_ID.toString())
        assertThat(auditSlot.captured.auditCandidate.project?.customIdentifier).isEqualTo("FG01_654")
        assertThat(auditSlot.captured.auditCandidate.project?.name).isEqualTo("acronym")
        assertThat(auditSlot.captured.auditCandidate.entityRelatedId).isEqualTo(REPORT_ID)
        assertThat(auditSlot.captured.auditCandidate.description).isEqualTo("[FG01_654]: Project report [35] submitted.")
    }

    @Test
    fun `submit - report is not draft`() {
        val report = mockk<ProjectReportModel>()
        every { report.status } returns ProjectReportStatus.Submitted
        every { reportPersistence.getReportById(PROJECT_ID, REPORT_ID) } returns report

        assertThrows<ProjectReportAlreadyClosed> { submitReport.submit(PROJECT_ID, REPORT_ID) }
        verify(exactly = 0) { reportPersistence.submitReport(any(), any(), any()) }
        verify(exactly = 0) { auditPublisher.publishEvent(any()) }
    }
}
