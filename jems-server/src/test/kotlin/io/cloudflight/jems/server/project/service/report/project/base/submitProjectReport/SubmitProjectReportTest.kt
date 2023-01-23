package io.cloudflight.jems.server.project.service.report.project.base.submitProjectReport

import io.cloudflight.jems.api.audit.dto.AuditAction
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.audit.model.AuditCandidateEvent
import io.cloudflight.jems.server.audit.model.AuditProject
import io.cloudflight.jems.server.audit.service.AuditCandidate
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerRole
import io.cloudflight.jems.server.project.service.report.model.partner.ProjectPartnerReportSubmissionSummary
import io.cloudflight.jems.server.project.service.report.model.partner.ReportStatus
import io.cloudflight.jems.server.project.service.report.model.project.ProjectReportStatus
import io.cloudflight.jems.server.project.service.report.model.project.ProjectReportSubmissionSummary
import io.cloudflight.jems.server.project.service.report.model.project.base.ProjectReportModel
import io.cloudflight.jems.server.project.service.report.project.base.ProjectReportPersistence
import io.cloudflight.jems.server.project.service.report.project.certificate.ProjectReportCertificatePersistence
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


        private val certificate = ProjectPartnerReportSubmissionSummary(
            id = 42L,
            reportNumber = 7,
            status = ReportStatus.Certified,
            version = "5.6.1",
            firstSubmission = null,
            controlEnd = null,
            createdAt = ZonedDateTime.now(),
            projectIdentifier = "not-needed",
            projectAcronym = "not-needed-as-well",
            partnerNumber = 5,
            partnerRole = ProjectPartnerRole.LEAD_PARTNER,
        )

    }

    @MockK
    lateinit var reportPersistence: ProjectReportPersistence
    @MockK
    lateinit var reportCertificatePersistence: ProjectReportCertificatePersistence
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

        every { reportCertificatePersistence.listCertificatesOfProjectReport(REPORT_ID) } returns listOf(certificate)
        val auditSlot = slot<AuditCandidateEvent>()
        every { auditPublisher.publishEvent(capture(auditSlot)) } returns Unit

        submitReport.submit(PROJECT_ID, REPORT_ID)

        verify(exactly = 1) { reportPersistence.submitReport(PROJECT_ID, REPORT_ID, any()) }
        assertThat(submissionTime.captured).isAfter(ZonedDateTime.now().minusMinutes(1))
        assertThat(submissionTime.captured).isBefore(ZonedDateTime.now().plusMinutes(1))
        assertThat(auditSlot.captured.auditCandidate).isEqualTo(
            AuditCandidate(
                action = AuditAction.PROJECT_REPORT_SUBMITTED,
                project = AuditProject("256", "FG01_654", "acronym"),
                entityRelatedId = REPORT_ID,
                description = "[FG01_654]: Project report PR.4 submitted, certificates included: LP5-R.7"
            )
        )
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
