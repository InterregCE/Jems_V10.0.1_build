package io.cloudflight.jems.server.project.service.report.partner.base.reOpenProjectPartnerReport

import io.cloudflight.jems.api.audit.dto.AuditAction
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.audit.model.AuditCandidateEvent
import io.cloudflight.jems.server.audit.model.AuditProject
import io.cloudflight.jems.server.audit.service.AuditCandidate
import io.cloudflight.jems.server.notification.handler.PartnerReportStatusChanged
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.model.ProjectSummary
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerRole
import io.cloudflight.jems.server.project.service.report.model.partner.ProjectPartnerReport
import io.cloudflight.jems.server.project.service.report.model.partner.ProjectPartnerReportStatusAndVersion
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
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.junit.jupiter.params.provider.EnumSource
import org.springframework.context.ApplicationEventPublisher

internal class ReOpenProjectPartnerReportTest : UnitTest() {

    private fun mockResult(status: ReportStatus) = ProjectPartnerReportSubmissionSummary(
        id = 160L,
        reportNumber = 7,
        status = status,
        version = "V7.4",
        firstSubmission = ZonedDateTime.now(),
        controlEnd = ZonedDateTime.now(),
        createdAt = ZonedDateTime.now(),
        projectIdentifier = "0000014",
        projectAcronym = "PROJ_acr",
        partnerNumber = 75,
        partnerRole = ProjectPartnerRole.LEAD_PARTNER,
        partnerId = 18L,
        partnerAbbreviation = "LP-75"
    )

    @MockK private lateinit var reportPersistence: ProjectPartnerReportPersistence
    @MockK private lateinit var partnerPersistence: PartnerPersistence
    @MockK private lateinit var auditPublisher: ApplicationEventPublisher
    @MockK private lateinit var projectPersistence: ProjectPersistence

    @InjectMockKs
    private lateinit var interactor: ReOpenProjectPartnerReport

    @BeforeEach
    fun resetMocks() {
        clearMocks(reportPersistence, partnerPersistence, auditPublisher, projectPersistence)
    }

    @ParameterizedTest(name = "reOpen - cannot be reopened {0}")
    @EnumSource(value = ReportStatus::class, names = ["Submitted", "InControl", "ReOpenCertified"], mode = EnumSource.Mode.EXCLUDE)
    fun `reOpen - cannot be reopened`(status: ReportStatus) {
        every { reportPersistence.getPartnerReportStatusAndVersion(partnerId = 15L, reportId = 150L) } returns
                ProjectPartnerReportStatusAndVersion(status, "V1")
        assertThrows<ReportCanNotBeReOpened> { interactor.reOpen(15L, reportId = 150L) }
        verify(exactly = 0) { auditPublisher.publishEvent(any()) }
    }

    @ParameterizedTest(name = "reOpen {0} -> {2}")
    @CsvSource(
        value = [
            "Submitted,160,ReOpenSubmittedLast",
            "Submitted,999,ReOpenSubmittedLimited",
            "InControl,160,ReOpenInControlLast",
            "InControl,999,ReOpenInControlLimited",
            "ReOpenCertified,160,ReOpenInControlLast",
            "ReOpenCertified,999,ReOpenInControlLimited",
        ]
    )
    fun reOpen(status: ReportStatus, lastReportId: Long, expectedStatus: ReportStatus) {
        every { reportPersistence.getPartnerReportStatusAndVersion(partnerId = 18L, reportId = 160L) } returns
                ProjectPartnerReportStatusAndVersion(status, "V1")

        val latestReport = mockk<ProjectPartnerReport>()
        every { latestReport.id } returns lastReportId
        every { reportPersistence.getCurrentLatestReportForPartner(partnerId = 18L) } returns latestReport

        every { partnerPersistence.getProjectIdForPartnerId(id = 18L, "V7.4") } returns 886L
        val projectSummary = mockk<ProjectSummary>()
        every { projectPersistence.getProjectSummary(886L) } returns projectSummary

        val newStatus = slot<ReportStatus>()
        val mockResult = mockResult(expectedStatus)
        every { reportPersistence.updateStatusAndTimes(18L, reportId = 160L, capture(newStatus)) } returns mockResult

        val eventAudit = slot<AuditCandidateEvent>()
        val eventNotif = slot<PartnerReportStatusChanged>()
        every { auditPublisher.publishEvent(capture(eventAudit)) } answers { }
        every { auditPublisher.publishEvent(capture(eventNotif)) } answers { }

        assertThat(interactor.reOpen(18L, reportId = 160L)).isEqualTo(expectedStatus)
        assertThat(newStatus.captured).isEqualTo(expectedStatus)

        assertThat(eventAudit.captured.auditCandidate).isEqualTo(
            AuditCandidate(
                action = AuditAction.PARTNER_REPORT_REOPENED,
                project = AuditProject("886", "0000014", "PROJ_acr"),
                entityRelatedId = 160L,
                description = "[LP75] Partner report R.7 was reopened",
            )
        )
        assertThat(eventNotif.captured.projectSummary).isEqualTo(projectSummary)
        assertThat(eventNotif.captured.partnerReportSummary).isEqualTo(mockResult)
    }

}
