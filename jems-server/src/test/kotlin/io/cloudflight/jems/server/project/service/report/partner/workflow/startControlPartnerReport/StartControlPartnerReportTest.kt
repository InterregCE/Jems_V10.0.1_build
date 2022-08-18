package io.cloudflight.jems.server.project.service.report.partner.workflow.startControlPartnerReport

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
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import org.springframework.context.ApplicationEventPublisher
import java.time.ZonedDateTime

internal class StartControlPartnerReportTest : UnitTest() {

    companion object {
        private const val PROJECT_ID = 256L
        private const val PARTNER_ID = 581L

        private val mockedResult = ProjectPartnerReportSubmissionSummary(
            id = 37L,
            reportNumber = 4,
            status = ReportStatus.InControl,
            version = "5.6.1",
            // not important
            firstSubmission = ZonedDateTime.now(),
            createdAt = ZonedDateTime.now(),
            projectIdentifier = "FG01_654",
            projectAcronym = "acronym",
            partnerNumber = 1,
            partnerRole = ProjectPartnerRole.LEAD_PARTNER,
        )

    }

    @MockK
    lateinit var reportPersistence: ProjectReportPersistence

    @MockK
    lateinit var partnerPersistence: PartnerPersistence

    @MockK
    lateinit var auditPublisher: ApplicationEventPublisher

    @InjectMockKs
    lateinit var interactor: StartControlPartnerReport

    @BeforeEach
    fun reset() {
        clearMocks(reportPersistence)
        clearMocks(partnerPersistence)
        clearMocks(auditPublisher)
    }

    @ParameterizedTest(name = "startControl (status {0})")
    @EnumSource(value = ReportStatus::class, names = ["Submitted"])
    fun startControl(status: ReportStatus) {
        val report = report(37L, status)
        every { reportPersistence.getPartnerReportById(PARTNER_ID, 37L) } returns report
        every { partnerPersistence.getProjectIdForPartnerId(PARTNER_ID, "5.6.1") } returns PROJECT_ID

        every { reportPersistence.startControlOnReportById(any(), any()) } returns mockedResult

        val auditSlot = slot<AuditCandidateEvent>()
        every { auditPublisher.publishEvent(capture(auditSlot)) } returns Unit

        interactor.startControl(PARTNER_ID, 37L)

        verify(exactly = 1) { reportPersistence.startControlOnReportById(PARTNER_ID, 37L) }

        assertThat(auditSlot.captured.auditCandidate.action).isEqualTo(AuditAction.CONTROL_ONGOING)
        assertThat(auditSlot.captured.auditCandidate.project?.id).isEqualTo(PROJECT_ID.toString())
        assertThat(auditSlot.captured.auditCandidate.project?.customIdentifier).isEqualTo("FG01_654")
        assertThat(auditSlot.captured.auditCandidate.project?.name).isEqualTo("acronym")
        assertThat(auditSlot.captured.auditCandidate.entityRelatedId).isEqualTo(37L)
        assertThat(auditSlot.captured.auditCandidate.description).isEqualTo("[FG01_654] [LP1] Partner report R.4 control ongoing")
    }

    @ParameterizedTest(name = "startControl - wrong status (status {0})")
    @EnumSource(value = ReportStatus::class, names = ["Submitted"], mode = EnumSource.Mode.EXCLUDE)
    fun `startControl - wrong status`(status: ReportStatus) {
        val report = report(39L, status)
        every { reportPersistence.getPartnerReportById(PARTNER_ID, 39L) } returns report

        assertThrows<ReportNotSubmitted> { interactor.startControl(PARTNER_ID, 39L) }

        verify(exactly = 0) { reportPersistence.startControlOnReportById(any(), any()) }
        verify(exactly = 0) { auditPublisher.publishEvent(any()) }
    }

    private fun report(id: Long, status: ReportStatus): ProjectPartnerReport {
        val report = mockk<ProjectPartnerReport>()
        every { report.id } returns id
        every { report.status } returns status
        return report
    }

}
