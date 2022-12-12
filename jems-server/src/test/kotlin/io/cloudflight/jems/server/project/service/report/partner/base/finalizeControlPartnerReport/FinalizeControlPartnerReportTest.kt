package io.cloudflight.jems.server.project.service.report.partner.base.finalizeControlPartnerReport

import io.cloudflight.jems.api.audit.dto.AuditAction
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.audit.model.AuditCandidateEvent
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerRole
import io.cloudflight.jems.server.project.service.report.partner.ProjectPartnerReportPersistence
import io.cloudflight.jems.server.project.service.report.model.partner.ProjectPartnerReport
import io.cloudflight.jems.server.project.service.report.model.partner.ProjectPartnerReportSubmissionSummary
import io.cloudflight.jems.server.project.service.report.model.partner.ReportStatus
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

internal class FinalizeControlPartnerReportTest : UnitTest() {

    companion object {
        private const val PROJECT_ID = 256L
        private const val PARTNER_ID = 581L

        private val mockedResult = ProjectPartnerReportSubmissionSummary(
            id = 42L,
            reportNumber = 7,
            status = ReportStatus.Certified,
            version = "5.6.1",
            // not important
            firstSubmission = ZonedDateTime.now(),
            controlEnd = ZonedDateTime.now(),
            createdAt = ZonedDateTime.now(),
            projectIdentifier = "FG01_654",
            projectAcronym = "acronym",
            partnerNumber = 1,
            partnerRole = ProjectPartnerRole.LEAD_PARTNER,
        )

    }

    @MockK
    private lateinit var reportPersistence: ProjectPartnerReportPersistence

    @MockK
    private lateinit var partnerPersistence: PartnerPersistence

    @MockK
    private lateinit var auditPublisher: ApplicationEventPublisher

    @InjectMockKs
    private lateinit var interactor: FinalizeControlPartnerReport

    @BeforeEach
    fun reset() {
        clearMocks(reportPersistence)
        clearMocks(partnerPersistence)
        clearMocks(auditPublisher)
    }

    @ParameterizedTest(name = "finalizeControl (status {0})")
    @EnumSource(value = ReportStatus::class, names = ["InControl"])
    fun finalizeControl(status: ReportStatus) {
        val report = report(42L, status)
        every { reportPersistence.getPartnerReportById(PARTNER_ID, 42L) } returns report
        every { partnerPersistence.getProjectIdForPartnerId(PARTNER_ID, "5.6.1") } returns PROJECT_ID
        every { reportPersistence.finalizeControlOnReportById(any(), any(), any()) } returns mockedResult

        val auditSlot = slot<AuditCandidateEvent>()
        every { auditPublisher.publishEvent(capture(auditSlot)) } returns Unit

        interactor.finalizeControl(PARTNER_ID, 42L)

        verify(exactly = 1) { reportPersistence.finalizeControlOnReportById(PARTNER_ID, 42L, any()) }

        assertThat(auditSlot.captured.auditCandidate.action).isEqualTo(AuditAction.PARTNER_REPORT_CONTROL_FINALIZED)
        assertThat(auditSlot.captured.auditCandidate.project?.id).isEqualTo(PROJECT_ID.toString())
        assertThat(auditSlot.captured.auditCandidate.project?.customIdentifier).isEqualTo("FG01_654")
        assertThat(auditSlot.captured.auditCandidate.project?.name).isEqualTo("acronym")
        assertThat(auditSlot.captured.auditCandidate.entityRelatedId).isEqualTo(42L)
        assertThat(auditSlot.captured.auditCandidate.description).isEqualTo("[FG01_654] [LP1] Partner report R.7 control work finalized")
    }

    @ParameterizedTest(name = "finalizeControl - wrong status (status {0})")
    @EnumSource(value = ReportStatus::class, names = ["InControl"], mode = EnumSource.Mode.EXCLUDE)
    fun `finalizeControl - wrong status`(status: ReportStatus) {
        val report = report(44L, status)
        every { reportPersistence.getPartnerReportById(PARTNER_ID, 44L) } returns report

        assertThrows<ReportNotInControl> { interactor.finalizeControl(PARTNER_ID, 44L) }

        verify(exactly = 0) { reportPersistence.finalizeControlOnReportById(any(), any(), any()) }
        verify(exactly = 0) { auditPublisher.publishEvent(any()) }
    }

    private fun report(id: Long, status: ReportStatus): ProjectPartnerReport {
        val report = mockk<ProjectPartnerReport>()
        every { report.id } returns id
        every { report.status } returns status
        return report
    }

}
