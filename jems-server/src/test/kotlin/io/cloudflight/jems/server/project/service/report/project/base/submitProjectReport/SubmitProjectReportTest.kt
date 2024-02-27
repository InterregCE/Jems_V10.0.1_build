package io.cloudflight.jems.server.project.service.report.project.base.submitProjectReport

import io.cloudflight.jems.api.audit.dto.AuditAction
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.audit.model.AuditCandidateEvent
import io.cloudflight.jems.server.audit.model.AuditProject
import io.cloudflight.jems.server.audit.service.AuditCandidate
import io.cloudflight.jems.server.notification.handler.ProjectReportStatusChanged
import io.cloudflight.jems.server.project.service.budget.model.BudgetCostsCalculationResultFull
import io.cloudflight.jems.server.project.service.contracting.model.reporting.ContractingDeadlineType
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerRole
import io.cloudflight.jems.server.project.service.report.model.partner.ProjectPartnerReportSubmissionSummary
import io.cloudflight.jems.server.project.service.report.model.project.ProjectReportStatus
import io.cloudflight.jems.server.project.service.report.model.project.ProjectReportSubmissionSummary
import io.cloudflight.jems.server.project.service.report.model.project.base.ProjectReportModel
import io.cloudflight.jems.server.project.service.report.model.project.financialOverview.coFinancing.ReportCertificateCoFinancingColumn
import io.cloudflight.jems.server.project.service.report.model.project.financialOverview.costCategory.CertificateCostCategoryCurrentlyReported
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.ProjectPartnerReportExpenditureCoFinancingPersistence
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.ProjectPartnerReportExpenditureCostCategoryPersistence
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.ProjectPartnerReportInvestmentPersistence
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.ProjectPartnerReportLumpSumPersistence
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.ProjectPartnerReportUnitCostPersistence
import io.cloudflight.jems.server.project.service.report.project.base.ProjectReportPersistence
import io.cloudflight.jems.server.project.service.report.project.base.runProjectReportPreSubmissionCheck.RunProjectReportPreSubmissionCheckService
import io.cloudflight.jems.server.project.service.report.project.certificate.ProjectReportCertificatePersistence
import io.cloudflight.jems.server.project.service.report.project.closure.ProjectReportProjectClosurePersistence
import io.cloudflight.jems.server.project.service.report.project.financialOverview.ProjectReportCertificateCoFinancingPersistence
import io.cloudflight.jems.server.project.service.report.project.financialOverview.ProjectReportCertificateCostCategoryPersistence
import io.cloudflight.jems.server.project.service.report.project.financialOverview.ProjectReportCertificateInvestmentPersistence
import io.cloudflight.jems.server.project.service.report.project.financialOverview.ProjectReportCertificateLumpSumPersistence
import io.cloudflight.jems.server.project.service.report.project.financialOverview.ProjectReportCertificateUnitCostPersistence
import io.cloudflight.jems.server.project.service.report.project.identification.ProjectReportIdentificationPersistence
import io.cloudflight.jems.server.project.service.report.project.resultPrinciple.ProjectReportResultPrinciplePersistence
import io.cloudflight.jems.server.project.service.report.project.spfContributionClaim.ProjectReportSpfContributionClaimPersistence
import io.cloudflight.jems.server.project.service.report.project.workPlan.ProjectReportWorkPlanPersistence
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
import org.junit.jupiter.params.provider.CsvSource
import org.junit.jupiter.params.provider.EnumSource
import org.springframework.context.ApplicationEventPublisher
import java.math.BigDecimal
import java.time.ZonedDateTime

internal class SubmitProjectReportTest : UnitTest() {

    companion object {
        private const val PROJECT_ID = 256L
        private const val REPORT_ID = 35L

        private fun mockedResult(status: ProjectReportStatus) = ProjectReportSubmissionSummary(
            id = REPORT_ID,
            reportNumber = 4,
            projectId = PROJECT_ID,
            status = status,
            version = "5.6.0",
            // not important
            firstSubmission = ZonedDateTime.now(),
            createdAt = ZonedDateTime.now(),
            projectIdentifier = "FG01_654",
            projectAcronym = "acronym",
            periodNumber = 1
        )

        private val spfContrib = ReportCertificateCoFinancingColumn(
            funds = mapOf(20L to BigDecimal.valueOf(11L), null to BigDecimal.valueOf(12L)),
            partnerContribution = BigDecimal.valueOf(13),
            publicContribution = BigDecimal.valueOf(14),
            automaticPublicContribution = BigDecimal.valueOf(15),
            privateContribution = BigDecimal.valueOf(16),
            sum = BigDecimal.valueOf(17),
        )

        private val totalEligibleAfterControl = ReportCertificateCoFinancingColumn(
            funds = mapOf(20L to BigDecimal.valueOf(126L), null to BigDecimal.valueOf(376L)),
            partnerContribution = BigDecimal.valueOf(51),
            publicContribution = BigDecimal.valueOf(101),
            automaticPublicContribution = BigDecimal.valueOf(151),
            privateContribution = BigDecimal.valueOf(201),
            sum = BigDecimal.valueOf(251),
        )

        private val totalWithSpfExpected = ReportCertificateCoFinancingColumn(
            funds = mapOf(20L to BigDecimal.valueOf(137L), null to BigDecimal.valueOf(388L)),
            partnerContribution = BigDecimal.valueOf(64),
            publicContribution = BigDecimal.valueOf(115),
            automaticPublicContribution = BigDecimal.valueOf(166),
            privateContribution = BigDecimal.valueOf(217),
            sum = BigDecimal.valueOf(268),
        )

        private val budgetCostFull = BudgetCostsCalculationResultFull(
            staff = BigDecimal.valueOf(10),
            office = BigDecimal.valueOf(11),
            travel = BigDecimal.valueOf(12),
            external = BigDecimal.valueOf(13),
            equipment = BigDecimal.valueOf(14),
            infrastructure = BigDecimal.valueOf(15),
            other = BigDecimal.valueOf(16),
            lumpSum = BigDecimal.valueOf(17),
            unitCost = BigDecimal.valueOf(18),
            spfCost = BigDecimal.valueOf(185, 1),
            sum = BigDecimal.valueOf(19),
        )

        private val budgetCostWithSpfExpected = BudgetCostsCalculationResultFull(
            staff = BigDecimal.valueOf(10),
            office = BigDecimal.valueOf(11),
            travel = BigDecimal.valueOf(12),
            external = BigDecimal.valueOf(13),
            equipment = BigDecimal.valueOf(14),
            infrastructure = BigDecimal.valueOf(15),
            other = BigDecimal.valueOf(16),
            lumpSum = BigDecimal.valueOf(17),
            unitCost = BigDecimal.valueOf(18),
            spfCost = BigDecimal.valueOf(355, 1),
            sum = BigDecimal.valueOf(36),
        )

    }

    @MockK
    lateinit var reportPersistence: ProjectReportPersistence
    @MockK
    lateinit var preSubmissionCheckService: RunProjectReportPreSubmissionCheckService
    @MockK
    lateinit var reportCertificatePersistence: ProjectReportCertificatePersistence
    @MockK
    lateinit var reportIdentificationPersistence: ProjectReportIdentificationPersistence
    @MockK
    lateinit var reportCertificateCoFinancingPersistence: ProjectReportCertificateCoFinancingPersistence
    @MockK
    lateinit var reportCertificateCostCategoryPersistence: ProjectReportCertificateCostCategoryPersistence
    @MockK
    lateinit var reportCertificateLumpSumPersistence: ProjectReportCertificateLumpSumPersistence
    @MockK
    lateinit var reportExpenditureCoFinancingPersistence: ProjectPartnerReportExpenditureCoFinancingPersistence
    @MockK
    lateinit var reportExpenditureCostCategoryPersistence: ProjectPartnerReportExpenditureCostCategoryPersistence
    @MockK
    lateinit var reportLumpSumPersistence: ProjectPartnerReportLumpSumPersistence
    @MockK
    lateinit var reportExpenditureUnitCostPersistence: ProjectPartnerReportUnitCostPersistence
    @MockK
    lateinit var reportCertificateUnitCostPersistence: ProjectReportCertificateUnitCostPersistence
    @MockK
    lateinit var reportExpenditureInvestmentPersistence: ProjectPartnerReportInvestmentPersistence
    @MockK
    lateinit var reportCertificateInvestmentPersistence: ProjectReportCertificateInvestmentPersistence
    @MockK
    lateinit var reportWorkPlanPersistence: ProjectReportWorkPlanPersistence
    @MockK
    lateinit var reportResultPrinciplePersistence: ProjectReportResultPrinciplePersistence
    @MockK
    lateinit var reportSpfClaimPersistence : ProjectReportSpfContributionClaimPersistence
    @MockK
    lateinit var projectReportProjectClosurePersistence: ProjectReportProjectClosurePersistence
    @MockK
    lateinit var auditPublisher: ApplicationEventPublisher

    @InjectMockKs
    lateinit var submitReport: SubmitProjectReport

    @BeforeEach
    fun reset() {
        clearMocks(reportPersistence)
        clearMocks(preSubmissionCheckService)
        clearMocks(reportWorkPlanPersistence)
        clearMocks(auditPublisher)
    }

    @ParameterizedTest(name = "submit report when no financial changes {0} -> {2}, {1}")
    @CsvSource(value = [
        "ReOpenSubmittedLimited,true,Submitted", // true/false not important
        "ReOpenSubmittedLimited,false,Submitted", // true/false not important
        "VerificationReOpenedLimited,false,Finalized",
        "VerificationReOpenedLimited,true,ReOpenFinalized",
    ])
    fun `submit report when no financial changes`(
        oldStatus: ProjectReportStatus,
        hasVerificationBefore: Boolean,
        expectedStatus: ProjectReportStatus,
    ) {
        val reportId = 300L + oldStatus.ordinal * 10 + expectedStatus.ordinal
        val report = mockk<ProjectReportModel>()
        every { report.status } returns oldStatus
        every { report.projectId } returns PROJECT_ID
        every { report.type } returns ContractingDeadlineType.Both
        every { report.lastVerificationReOpening } returns if (hasVerificationBefore) ZonedDateTime.now() else null
        every { report.linkedFormVersion } returns "v1.0"
        every { report.periodNumber } returns 1

        every { reportPersistence.getReportByIdUnSecured(reportId) } returns report
        every { preSubmissionCheckService.preCheck(PROJECT_ID, reportId).isSubmissionAllowed } returns true

        val newStatus = slot<ProjectReportStatus>()
        val submissionTime = slot<ZonedDateTime>()
        val result = mockedResult(status = expectedStatus)
        every { reportPersistence.reSubmitReport(any(), any(), capture(newStatus), capture(submissionTime)) } returns result

        val certificate = mockk<ProjectPartnerReportSubmissionSummary>()
        every { certificate.partnerRole } returns ProjectPartnerRole.LEAD_PARTNER
        every { certificate.partnerNumber } returns 4
        every { certificate.reportNumber } returns 14
        every { reportCertificatePersistence.listCertificatesOfProjectReport(reportId) } returns listOf(certificate)

        val auditSlot = slot<AuditCandidateEvent>()
        every { auditPublisher.publishEvent(capture(auditSlot)) } returns Unit
        val statusChanged = slot<ProjectReportStatusChanged>()
        every { auditPublisher.publishEvent(capture(statusChanged)) } returns Unit

        // test performed
        assertThat(submitReport.submit(reportId)).isEqualTo(expectedStatus)

        assertThat(auditSlot.captured.auditCandidate).isEqualTo(
            AuditCandidate(
                action = AuditAction.PROJECT_REPORT_SUBMITTED,
                project = AuditProject("256", "FG01_654", "acronym"),
                entityRelatedId = 35L,
                description = "[FG01_654]: Project report PR.4 submitted, certificates included: LP4-R.14",
            )
        )
        assertThat(statusChanged.captured.projectReportSummary).isEqualTo(result)
        assertThat(statusChanged.captured.previousReportStatus).isEqualTo(oldStatus)
    }

    @ParameterizedTest(name = "submit financial report {0} -> {1}")
    @CsvSource(value = [
        "Draft,Submitted",
        "ReOpenSubmittedLast,Submitted",
        "VerificationReOpenedLast,InVerification",
    ])
    fun `submit financial report`(oldStatus: ProjectReportStatus, expectedStatus: ProjectReportStatus) {
        val reportId = 400L + oldStatus.ordinal * 10 + expectedStatus.ordinal
        val report = mockk<ProjectReportModel>()
        every { report.status } returns oldStatus
        every { report.id } returns reportId
        every { report.type } returns ContractingDeadlineType.Finance
        every { report.projectId } returns PROJECT_ID
        every { report.lastVerificationReOpening } returns null
        every { report.linkedFormVersion } returns "v1.0"
        every { report.periodNumber } returns 1
        every { report.finalReport } returns false

        every { reportPersistence.getReportByIdUnSecured(reportId) } returns report
        every { preSubmissionCheckService.preCheck(PROJECT_ID, reportId).isSubmissionAllowed } returns true

        val certificate65 = mockk<ProjectPartnerReportSubmissionSummary> {
            every { id } returns 65L
            every { partnerRole } returns ProjectPartnerRole.PARTNER
            every { partnerNumber } returns 3
            every { reportNumber } returns 35
        }
        every { reportCertificatePersistence.listCertificatesOfProjectReport(reportId) } returns listOf(certificate65)
        every { reportSpfClaimPersistence.getCurrentSpfContribution(reportId) } returns spfContrib

        val spendingProfile = mockk<Map<Long, BigDecimal>>()
        every { reportIdentificationPersistence.getSpendingProfileCurrentValues(reportId) } returns spendingProfile
        every { reportIdentificationPersistence.updateSpendingProfile(reportId, spendingProfile) } returnsArgument 0

        val currentCoFinancing = slot<ReportCertificateCoFinancingColumn>()
        every { reportExpenditureCoFinancingPersistence.getCoFinancingTotalEligible(setOf(65L)) } returns totalEligibleAfterControl
        every { reportCertificateCoFinancingPersistence.updateCurrentlyReportedValues(any(), reportId, capture(currentCoFinancing)) } answers { }

        val currentCostCategories = slot<CertificateCostCategoryCurrentlyReported>()
        every { reportExpenditureCostCategoryPersistence.getCostCategoriesTotalEligible(setOf(65L)) } returns budgetCostFull
        every { reportCertificateCostCategoryPersistence.updateCurrentlyReportedValues(any(), reportId, capture(currentCostCategories)) } answers { }

        val unitCosts = mockk<Map<Long, BigDecimal>>()
        every { reportExpenditureUnitCostPersistence.getUnitCostCumulativeAfterControl(setOf(65L)) } returns unitCosts
        every { reportCertificateUnitCostPersistence.updateCurrentlyReportedValues(any(), reportId, unitCosts) } answers { }

        val lumpSums = mockk<Map<Int, BigDecimal>>()
        every { reportLumpSumPersistence.getLumpSumCumulativeAfterControl(setOf(65L)) } returns lumpSums
        every { reportCertificateLumpSumPersistence.updateCurrentlyReportedValues(any(), reportId, lumpSums) } answers { }

        val investments = mockk<Map<Long, BigDecimal>>()
        every { reportExpenditureInvestmentPersistence.getInvestmentsCumulativeAfterControl(setOf(65L)) } returns investments
        every { reportCertificateInvestmentPersistence.updateCurrentlyReportedValues(PROJECT_ID, reportId, investments) } answers { }

        every { reportResultPrinciplePersistence.deleteProjectResultPrinciplesIfExist(reportId) } answers { }
        every { reportWorkPlanPersistence.deleteWorkPlan(reportId) } answers { }

        val newStatus = slot<ProjectReportStatus>()
        val submissionTime = slot<ZonedDateTime>()
        val result = mockedResult(status = expectedStatus)
        every { reportPersistence.reSubmitReport(PROJECT_ID, reportId, capture(newStatus), capture(submissionTime)) } returns result
        every { reportPersistence.submitReportInitially(PROJECT_ID, reportId, capture(submissionTime)) } returns result

        every { projectReportProjectClosurePersistence.deleteProjectReportProjectClosure(reportId) } returnsArgument 0

        val auditSlot = slot<AuditCandidateEvent>()
        every { auditPublisher.publishEvent(capture(auditSlot)) } returns Unit
        val statusChanged = slot<ProjectReportStatusChanged>()
        every { auditPublisher.publishEvent(capture(statusChanged)) } returns Unit

        // test performed
        assertThat(submitReport.submit(reportId)).isEqualTo(expectedStatus)

        if (oldStatus == ProjectReportStatus.Draft)
            verify(exactly = 0) { reportPersistence.reSubmitReport(any(), any(), any(), any()) }
        else {
            verify(exactly = 0) { reportPersistence.submitReportInitially(any(), any(), any()) }
            assertThat(newStatus.captured).isEqualTo(expectedStatus)
        }

        assertThat(submissionTime.captured).isAfter(ZonedDateTime.now().minusMinutes(1))
        assertThat(submissionTime.captured).isBefore(ZonedDateTime.now().plusMinutes(1))

        assertThat(currentCoFinancing.captured).isEqualTo(totalWithSpfExpected)
        assertThat(currentCostCategories.captured.currentlyReported).isEqualTo(budgetCostWithSpfExpected)

        verify(exactly = 0) { reportCertificatePersistence.deselectCertificatesOfProjectReport(reportId) }
        verify(exactly = 0) { reportSpfClaimPersistence.resetSpfContributionClaims(reportId) }
        verify(exactly = 1) { projectReportProjectClosurePersistence.deleteProjectReportProjectClosure(reportId) }

        assertThat(auditSlot.captured.auditCandidate).isEqualTo(
            AuditCandidate(
                action = AuditAction.PROJECT_REPORT_SUBMITTED,
                project = AuditProject("256", "FG01_654", "acronym"),
                entityRelatedId = 35L,
                description = "[FG01_654]: Project report PR.4 submitted, certificates included: PP3-R.35",
            )
        )
        assertThat(statusChanged.captured.projectReportSummary).isEqualTo(result)
        assertThat(statusChanged.captured.previousReportStatus).isEqualTo(oldStatus)
    }

    @ParameterizedTest(name = "submit - report is not open - {0}")
    @EnumSource(value = ProjectReportStatus::class, names = ["Submitted", "InVerification", "Finalized", "ReOpenFinalized"])
    fun `submit - report is not open`(status: ProjectReportStatus) {
        val reportId = 100L + status.ordinal
        val report = mockk<ProjectReportModel>()
        every { report.status } returns status
        every { report.projectId } returns PROJECT_ID
        every { reportPersistence.getReportByIdUnSecured(reportId) } returns report

        assertThrows<ProjectReportAlreadyClosed> { submitReport.submit(reportId) }
        verify(exactly = 0) { reportPersistence.submitReportInitially(any(), any(), any()) }
        verify(exactly = 0) { auditPublisher.publishEvent(any()) }
    }

    @ParameterizedTest(name = "submit - report is not open - {0}")
    @EnumSource(value = ProjectReportStatus::class, names = ["Submitted", "InVerification", "Finalized", "ReOpenFinalized"], mode = EnumSource.Mode.EXCLUDE)
    fun `submit - report fails pre-check`(status: ProjectReportStatus) {
        val report = mockk<ProjectReportModel>()
        val reportId = 200L + status.ordinal
        every { report.status } returns status
        every { report.projectId } returns PROJECT_ID
        every { reportPersistence.getReportByIdUnSecured(reportId) } returns report
        every { preSubmissionCheckService.preCheck(PROJECT_ID, reportId).isSubmissionAllowed } returns false

        assertThrows<SubmissionNotAllowed> { submitReport.submit(reportId) }
        verify(exactly = 0) { reportPersistence.submitReportInitially(any(), any(), any()) }
        verify(exactly = 0) { auditPublisher.publishEvent(any()) }
    }
}
