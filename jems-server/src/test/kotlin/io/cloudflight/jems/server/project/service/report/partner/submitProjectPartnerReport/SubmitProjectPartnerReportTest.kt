package io.cloudflight.jems.server.project.service.report.partner.submitProjectPartnerReport

import io.cloudflight.jems.api.audit.dto.AuditAction
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.audit.model.AuditCandidateEvent
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerRole
import io.cloudflight.jems.server.project.service.report.ProjectReportPersistence
import io.cloudflight.jems.server.project.service.report.model.ProjectPartnerReport
import io.cloudflight.jems.server.project.service.report.model.ProjectPartnerReportSubmissionSummary
import io.cloudflight.jems.server.project.service.report.model.ReportStatus
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

internal class SubmitProjectPartnerReportTest : UnitTest() {

    companion object {
        private const val PROJECT_ID = 256L
        private const val PARTNER_ID = 579L

        private val mockedResult = ProjectPartnerReportSubmissionSummary(
            id = 888L,
            reportNumber = 4,
            status = ReportStatus.Submitted,
            version = "5.6.0",
            // not important
            firstSubmission = ZonedDateTime.now(),
            createdAt = ZonedDateTime.now(),
            projectIdentifier = "FG01_654",
            projectAcronym = "acronym",
            partnerNumber = 1,
            partnerRole = ProjectPartnerRole.PARTNER,
        )
    }

    @MockK
    lateinit var reportPersistence: ProjectReportPersistence

    @MockK
    lateinit var partnerPersistence: PartnerPersistence

    @MockK
    lateinit var auditPublisher: ApplicationEventPublisher

    @InjectMockKs
    lateinit var submitReport: SubmitProjectPartnerReport

    @BeforeEach
    fun reset() {
        clearMocks(reportPersistence)
        clearMocks(auditPublisher)
    }

    @Test
    fun submit() {
        val report = mockk<ProjectPartnerReport>()
        every { report.status } returns ReportStatus.Draft

        val submissionTime = slot<ZonedDateTime>()

        every { reportPersistence.getPartnerReportById(PARTNER_ID, 35L) } returns report
        every { reportPersistence.submitReportById(any(), any(), capture(submissionTime)) } returns mockedResult
        every { partnerPersistence.getProjectIdForPartnerId(PARTNER_ID, "5.6.0") } returns PROJECT_ID

        val auditSlot = slot<AuditCandidateEvent>()
        every { auditPublisher.publishEvent(capture(auditSlot)) } returns Unit

        submitReport.submit(PARTNER_ID, 35L)

        verify(exactly = 1) { reportPersistence.submitReportById(PARTNER_ID, 35L, any()) }
        assertThat(submissionTime.captured).isAfter(ZonedDateTime.now().minusMinutes(1))
        assertThat(submissionTime.captured).isBefore(ZonedDateTime.now().plusMinutes(1))

        assertThat(auditSlot.captured.auditCandidate.action).isEqualTo(AuditAction.PARTNER_REPORT_SUBMITTED)
        assertThat(auditSlot.captured.auditCandidate.project?.id).isEqualTo(PROJECT_ID.toString())
        assertThat(auditSlot.captured.auditCandidate.project?.customIdentifier).isEqualTo("FG01_654")
        assertThat(auditSlot.captured.auditCandidate.project?.name).isEqualTo("acronym")
        assertThat(auditSlot.captured.auditCandidate.entityRelatedId).isEqualTo(888L)
        assertThat(auditSlot.captured.auditCandidate.description).isEqualTo("[FG01_654] [PP1] Partner report R.4 submitted")
    }

    @Test
    fun `submit - report is not draft`() {
        val report = mockk<ProjectPartnerReport>()
        every { report.status } returns ReportStatus.Submitted

        every { reportPersistence.getPartnerReportById(PARTNER_ID, 36L) } returns report

        assertThrows<ReportAlreadyClosed> { submitReport.submit(PARTNER_ID, 36L) }
        verify(exactly = 0) { reportPersistence.submitReportById(any(), any(), any()) }
        verify(exactly = 0) { auditPublisher.publishEvent(any()) }
    }

}
