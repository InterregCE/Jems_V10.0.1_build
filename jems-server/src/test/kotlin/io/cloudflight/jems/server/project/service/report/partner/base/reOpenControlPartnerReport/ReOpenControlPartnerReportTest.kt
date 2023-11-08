package io.cloudflight.jems.server.project.service.report.partner.base.reOpenControlPartnerReport

import io.cloudflight.jems.api.audit.dto.AuditAction
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.audit.model.AuditCandidateEvent
import io.cloudflight.jems.server.audit.model.AuditProject
import io.cloudflight.jems.server.audit.service.AuditCandidate
import io.cloudflight.jems.server.notification.handler.PartnerReportStatusChanged
import io.cloudflight.jems.server.project.repository.partner.ProjectPartnerRepository
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerRole
import io.cloudflight.jems.server.project.service.report.model.partner.ProjectPartnerReport
import io.cloudflight.jems.server.project.service.report.model.partner.ProjectPartnerReportSubmissionSummary
import io.cloudflight.jems.server.project.service.report.model.partner.ReportStatus
import io.cloudflight.jems.server.project.service.report.partner.ProjectPartnerReportPersistence
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
import org.springframework.context.ApplicationEventPublisher

internal class ReOpenControlPartnerReportTest : UnitTest() {

    companion object {

        private val reportCreatedAt = ZonedDateTime.now().minusDays(1)
        private val reportControlEnd = ZonedDateTime.now()
        private val reportFirstSubmission = ZonedDateTime.now().minusHours(6)

        private const val partnerId = 99L
        private fun mockResult(status: ReportStatus) = ProjectPartnerReportSubmissionSummary(
            id = 26L,
            reportNumber = 4,
            status = status,
            version = "V2.0",
            firstSubmission = reportFirstSubmission,
            controlEnd = reportControlEnd,
            createdAt = reportCreatedAt,
            projectIdentifier = "0000014",
            projectAcronym = "PROJ_acr",
            partnerNumber = 75,
            partnerRole = ProjectPartnerRole.LEAD_PARTNER,
            partnerId = partnerId,
            partnerAbbreviation = "LP-75"
        )

        fun partnerReport(status: ReportStatus, lastControlReopening: ZonedDateTime?, projectReportId: Long?) = ProjectPartnerReport(
            id = 26L,
            reportNumber = 4,
            status = status,
            version = "V2.0",
            firstSubmission = reportFirstSubmission,
            lastResubmission = null,
            controlEnd = reportControlEnd,
            lastControlReopening = lastControlReopening,
            projectReportId = projectReportId,
            projectReportNumber = projectReportId?.toInt(),
            identification = mockk()
        )

    }

    @MockK
    private lateinit var reportPersistence: ProjectPartnerReportPersistence
    @MockK
    private lateinit var projectPartnerRepository: ProjectPartnerRepository
    @MockK
    private lateinit var auditPublisher: ApplicationEventPublisher

    @InjectMockKs
    private lateinit var interactor: ReOpenControlPartnerReport

    @BeforeEach
    fun clear() {
        clearMocks(auditPublisher)
    }

    @Test
    fun reOpen() {
        val projectId = 63L
        val reportId = 26L
        val expectedStatus = ReportStatus.ReOpenCertified

        every { reportPersistence.getPartnerReportById(partnerId = partnerId, reportId = reportId) } returns
                partnerReport(ReportStatus.Certified, null, null)
        every { projectPartnerRepository.getProjectIdForPartner(partnerId) } returns projectId

        val newStatus = slot<ReportStatus>()
        val mockResult = mockResult(expectedStatus)
        val controlReopenedAt = slot<ZonedDateTime>()

        every {
            reportPersistence.updateStatusAndTimes(
                partnerId = partnerId,
                reportId = reportId,
                status = capture(newStatus),
                lastControlReopening = capture(controlReopenedAt)
            )
        } returns mockResult

        val eventAudit = slot<AuditCandidateEvent>()
        val eventNotif = slot<PartnerReportStatusChanged>()
        every { auditPublisher.publishEvent(capture(eventAudit)) } answers { }
        every { auditPublisher.publishEvent(capture(eventNotif)) } answers { }

        assertThat(interactor.reOpen(partnerId, reportId = reportId)).isEqualTo(expectedStatus)
        assertThat(eventAudit.captured.auditCandidate).isEqualTo(
            AuditCandidate(
                action = AuditAction.PARTNER_REPORT_CONTROL_REOPENED,
                project = AuditProject(id = "63", customIdentifier = "0000014", name = "PROJ_acr"),
                entityRelatedId = 26,
                description = "[LP75] Partner report R.4 was reopened"
            )
        )
        assertThat(eventNotif.captured.projectId).isEqualTo(projectId)
        assertThat(eventNotif.captured.partnerReportSummary).isEqualTo(mockResult)
    }

    @Test
    fun `reOpen of already-used report`() {
        val projectId = 64L
        val reportId = 20L

        every { reportPersistence.getPartnerReportById(partnerId = partnerId, reportId = reportId) } returns
                partnerReport(ReportStatus.Certified, null, 14L)
        every { projectPartnerRepository.getProjectIdForPartner(partnerId) } returns projectId

        assertThrows<ReportCertificateException> { interactor.reOpen(partnerId, reportId) }

        verify(exactly = 0) { auditPublisher.publishEvent(any()) }
    }

}
