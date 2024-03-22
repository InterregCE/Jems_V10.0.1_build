package io.cloudflight.jems.server.project.service.report.project.base.reOpenVerificationProjectReport

import io.cloudflight.jems.api.audit.dto.AuditAction
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.audit.model.AuditCandidateEvent
import io.cloudflight.jems.server.audit.model.AuditProject
import io.cloudflight.jems.server.audit.service.AuditCandidate
import io.cloudflight.jems.server.notification.handler.ProjectReportStatusChanged
import io.cloudflight.jems.server.payments.service.ecPayment.linkToPayment.PaymentApplicationToEcLinkPersistence
import io.cloudflight.jems.server.payments.service.regular.PaymentPersistence
import io.cloudflight.jems.server.project.service.report.model.project.ProjectReportStatus
import io.cloudflight.jems.server.project.service.report.model.project.ProjectReportSubmissionSummary
import io.cloudflight.jems.server.project.service.report.model.project.base.ProjectReportModel
import io.cloudflight.jems.server.project.service.report.project.base.ProjectReportPersistence
import io.mockk.*
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import org.springframework.context.ApplicationEventPublisher
import java.time.ZonedDateTime

class ReOpenVerificationProjectReportTest: UnitTest() {

    companion object {
        private const val PROJECT_ID = 10L
        private const val REPORT_ID = 99L

        private fun submissionSummary() = ProjectReportSubmissionSummary(
            id = REPORT_ID,
            reportNumber = 17,
            status = ProjectReportStatus.ReOpenFinalized,
            version = "V7.7",
            firstSubmission = mockk(),
            createdAt = mockk(),
            projectId = PROJECT_ID,
            projectIdentifier = "proj-iden",
            projectAcronym = "proj-acr",
            periodNumber = 1
        )
    }

    @MockK
    private lateinit var reportPersistence: ProjectReportPersistence
    @MockK
    private lateinit var eventPublisher: ApplicationEventPublisher
    @MockK
    private lateinit var paymentPersistence: PaymentPersistence
    @MockK
    private lateinit var paymentApplicationToEcLinkPersistence: PaymentApplicationToEcLinkPersistence

    @InjectMockKs
    private lateinit var interactor: ReOpenVerificationProjectReport

    @BeforeEach
    fun reset() {
        clearMocks(reportPersistence, paymentPersistence, paymentApplicationToEcLinkPersistence, eventPublisher)
    }

    @Test
    fun reOpenFinalizedReport() {
        val report = mockk<ProjectReportModel>()
        every { report.status } returns ProjectReportStatus.Finalized
        every { report.id } returns REPORT_ID
        every { report.linkedFormVersion } returns "v1.0"
        every { report.periodNumber } returns 1
        every { reportPersistence.getReportById(PROJECT_ID, REPORT_ID) } returns report
        every { paymentPersistence.getPaymentIdsInstallmentsExistsByProjectReportId(REPORT_ID) } returns setOf()
        every { paymentApplicationToEcLinkPersistence.getPaymentToEcIdsProjectReportIncluded(REPORT_ID) } returns setOf()
        every { paymentPersistence.deleteRegularPayments(REPORT_ID) } returns Unit

        val slotTime = slot<ZonedDateTime>()
        val result = submissionSummary()
        every { reportPersistence.reOpenFinalizedVerificationAndResetDate(REPORT_ID, capture(slotTime)) } returns result

        val slotAudit = slot<AuditCandidateEvent>()
        every { eventPublisher.publishEvent(capture(slotAudit)) } answers { }
        val slotStatusChanged = slot<ProjectReportStatusChanged>()
        every { eventPublisher.publishEvent(capture(slotStatusChanged)) } answers { }

        interactor.reOpen(PROJECT_ID, REPORT_ID)

        verify(exactly = 1) { reportPersistence.reOpenFinalizedVerificationAndResetDate(REPORT_ID, any()) }

        assertThat(slotStatusChanged.captured.projectReportSummary).isEqualTo(result)
        assertThat(slotStatusChanged.captured.previousReportStatus).isEqualTo(ProjectReportStatus.Finalized)
        assertThat(slotAudit.captured.auditCandidate).isEqualTo(
            AuditCandidate(
                AuditAction.PROJECT_REPORT_VERIFICATION_REOPENED,
                AuditProject("10", "proj-iden", "proj-acr"), REPORT_ID, "PR.17 verification was reopened"
            )
        )
    }

    @ParameterizedTest(name = "reOpen forbidden from {0}")
    @EnumSource(value = ProjectReportStatus::class, names = ["Finalized"], mode = EnumSource.Mode.EXCLUDE)
    fun reOpen(statusFrom: ProjectReportStatus) {
        val report = mockk<ProjectReportModel>()
        every { report.status } returns statusFrom
        every { reportPersistence.getReportById(PROJECT_ID, REPORT_ID) } returns report

        assertThrows<VerificationReportNotFinalized> { interactor.reOpen(PROJECT_ID, REPORT_ID) }
    }

    @Test
    fun reOpenFinalizedReportFailureDueToPaymentInstallments() {
        val report = mockk<ProjectReportModel>()
        every { report.status } returns ProjectReportStatus.Finalized
        every { report.id } returns REPORT_ID

        every { reportPersistence.getReportById(PROJECT_ID, REPORT_ID) } returns report
        every { paymentPersistence.getPaymentIdsInstallmentsExistsByProjectReportId(REPORT_ID) } returns setOf(50L)

        assertThrows<VerificationReportHasPaymentInstallments> { interactor.reOpen(PROJECT_ID, REPORT_ID) }
    }

    @Test
    fun reOpenFinalizedReportFailureDueToIncludedInPaymentToEc() {
        val report = mockk<ProjectReportModel>()
        every { report.status } returns ProjectReportStatus.Finalized
        every { report.id } returns REPORT_ID

        every { reportPersistence.getReportById(PROJECT_ID, REPORT_ID) } returns report
        every { paymentPersistence.getPaymentIdsInstallmentsExistsByProjectReportId(REPORT_ID) } returns setOf()
        every { paymentApplicationToEcLinkPersistence.getPaymentToEcIdsProjectReportIncluded(REPORT_ID) } returns setOf(50L)

        assertThrows<VerificationReportIncludedInPaymentToEc> { interactor.reOpen(PROJECT_ID, REPORT_ID) }
    }
}
