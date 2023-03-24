package io.cloudflight.jems.server.project.service.report.project.base.submitProjectReport

import io.cloudflight.jems.api.audit.dto.AuditAction
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.audit.model.AuditCandidateEvent
import io.cloudflight.jems.server.audit.model.AuditProject
import io.cloudflight.jems.server.audit.service.AuditCandidate
import io.cloudflight.jems.server.project.service.budget.model.BudgetCostsCalculationResultFull
import io.cloudflight.jems.server.project.service.contracting.model.reporting.ContractingDeadlineType
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerRole
import io.cloudflight.jems.server.project.service.report.model.partner.ProjectPartnerReportSubmissionSummary
import io.cloudflight.jems.server.project.service.report.model.partner.ReportStatus
import io.cloudflight.jems.server.project.service.report.model.project.ProjectReportStatus
import io.cloudflight.jems.server.project.service.report.model.project.ProjectReportSubmissionSummary
import io.cloudflight.jems.server.project.service.report.model.project.base.ProjectReportModel
import io.cloudflight.jems.server.project.service.report.model.project.financialOverview.coFinancing.ReportCertificateCoFinancing
import io.cloudflight.jems.server.project.service.report.model.project.financialOverview.coFinancing.ReportCertificateCoFinancingColumn
import io.cloudflight.jems.server.project.service.report.model.project.financialOverview.costCategory.ReportCertificateCostCategory
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.ProjectPartnerReportExpenditureCoFinancingPersistence
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.ProjectPartnerReportExpenditureCostCategoryPersistence
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.ProjectPartnerReportInvestmentPersistence
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.ProjectPartnerReportLumpSumPersistence
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.ProjectPartnerReportUnitCostPersistence
import io.cloudflight.jems.server.project.service.report.project.base.ProjectReportPersistence
import io.cloudflight.jems.server.project.service.report.project.certificate.ProjectReportCertificatePersistence
import io.cloudflight.jems.server.project.service.report.project.financialOverview.ProjectReportCertificateCoFinancingPersistence
import io.cloudflight.jems.server.project.service.report.project.financialOverview.ProjectReportCertificateCostCategoryPersistence
import io.cloudflight.jems.server.project.service.report.project.financialOverview.ProjectReportCertificateInvestmentPersistence
import io.cloudflight.jems.server.project.service.report.project.financialOverview.ProjectReportCertificateLumpSumPersistence
import io.cloudflight.jems.server.project.service.report.project.financialOverview.ProjectReportCertificateUnitCostPersistence
import io.cloudflight.jems.server.project.service.report.project.identification.ProjectReportIdentificationPersistence
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
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import org.springframework.context.ApplicationEventPublisher
import java.math.BigDecimal
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
            partnerId = 1L
        )

        private val certificateCoFin = ReportCertificateCoFinancing(
            totalsFromAF = ReportCertificateCoFinancingColumn(
                funds = mapOf(20L to BigDecimal.valueOf(250L), null to BigDecimal.valueOf(750L)),
                partnerContribution = BigDecimal.valueOf(900),
                publicContribution = BigDecimal.valueOf(200),
                automaticPublicContribution = BigDecimal.valueOf(300),
                privateContribution = BigDecimal.valueOf(400),
                sum = BigDecimal.valueOf(1000),
            ),
            currentlyReported = ReportCertificateCoFinancingColumn(
                funds = mapOf(20L to BigDecimal.valueOf(125L), null to BigDecimal.valueOf(375L)),
                partnerContribution = BigDecimal.valueOf(50),
                publicContribution = BigDecimal.valueOf(100),
                automaticPublicContribution = BigDecimal.valueOf(150),
                privateContribution = BigDecimal.valueOf(200),
                sum = BigDecimal.valueOf(250),
            ),
            previouslyReported = ReportCertificateCoFinancingColumn(
                funds = mapOf(20L to BigDecimal.valueOf(50L), null to BigDecimal.valueOf(150L)),
                partnerContribution = BigDecimal.valueOf(2),
                publicContribution = BigDecimal.valueOf(3),
                automaticPublicContribution = BigDecimal.valueOf(4),
                privateContribution = BigDecimal.valueOf(5),
                sum = BigDecimal.valueOf(6),
            ),
            previouslyPaid = ReportCertificateCoFinancingColumn(
                funds = mapOf(20L to BigDecimal.valueOf(81L), null to BigDecimal.valueOf(123L)),
                partnerContribution = BigDecimal.ZERO,
                publicContribution = BigDecimal.ZERO,
                automaticPublicContribution = BigDecimal.ZERO,
                privateContribution = BigDecimal.ZERO,
                sum = BigDecimal.valueOf(204),
            ),
        )

        private val totalEligibleAfterControl = ReportCertificateCoFinancingColumn(
            funds = mapOf(20L to BigDecimal.valueOf(126L), null to BigDecimal.valueOf(376L)),
            partnerContribution = BigDecimal.valueOf(51),
            publicContribution = BigDecimal.valueOf(101),
            automaticPublicContribution = BigDecimal.valueOf(151),
            privateContribution = BigDecimal.valueOf(201),
            sum = BigDecimal.valueOf(251),
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
            sum = BigDecimal.valueOf(19),
        )

        private val certificateCostCategory = ReportCertificateCostCategory(
            totalsFromAF = budgetCostFull,
            currentlyReported = budgetCostFull,
            previouslyReported = budgetCostFull
        )

    }

    @MockK
    lateinit var reportPersistence: ProjectReportPersistence
    @MockK
    lateinit var reportCertificatePersistence: ProjectReportCertificatePersistence
    @MockK
    lateinit var reportIdentificationPersistence: ProjectReportIdentificationPersistence
    @MockK
    lateinit var reportCertificateCoFinancingPersistence: ProjectReportCertificateCoFinancingPersistence
    @MockK
    lateinit var reportExpenditureCoFinancingPersistence: ProjectPartnerReportExpenditureCoFinancingPersistence
    @MockK
    lateinit var reportCertificateLumpSumPersistence: ProjectReportCertificateLumpSumPersistence
    @MockK
    lateinit var reportCertificateCostCategoryPersistence: ProjectReportCertificateCostCategoryPersistence
    @MockK
    lateinit var reportExpenditureCostCategoryPersistence: ProjectPartnerReportExpenditureCostCategoryPersistence
    @MockK
    lateinit var reportLumpSumPersistence: ProjectPartnerReportLumpSumPersistence
    @MockK
    lateinit var reportWorkPlanPersistence: ProjectReportWorkPlanPersistence
    @MockK
    lateinit var reportExpenditureUnitCostPersistence: ProjectPartnerReportUnitCostPersistence
    @MockK
    lateinit var reportCertificateUnitCostPersistence: ProjectReportCertificateUnitCostPersistence
    @MockK
    lateinit var reportExpenditureInvestmentPersistence: ProjectPartnerReportInvestmentPersistence
    @MockK
    lateinit var reportCertificateInvestmentPersistence: ProjectReportCertificateInvestmentPersistence
    @MockK
    lateinit var auditPublisher: ApplicationEventPublisher

    @InjectMockKs
    lateinit var submitReport: SubmitProjectReport

    @BeforeEach
    fun reset() {
        clearMocks(reportPersistence)
        clearMocks(reportWorkPlanPersistence)
        clearMocks(auditPublisher)
    }

    @ParameterizedTest(name = "submit (type {0})")
    @EnumSource(value = ContractingDeadlineType::class)
    fun submit(type: ContractingDeadlineType) {
        val report = mockk<ProjectReportModel>()
        every { report.status } returns ProjectReportStatus.Draft
        every { report.id } returns REPORT_ID
        every { report.type } returns type
        every { reportPersistence.getReportById(PROJECT_ID, REPORT_ID) } returns report

        if (type == ContractingDeadlineType.Finance) {
            every { reportWorkPlanPersistence.deleteWorkPlan(PROJECT_ID, REPORT_ID) } answers { }
        }

        val submissionTime = slot<ZonedDateTime>()
        every { reportPersistence.submitReport(any(), any(), capture(submissionTime)) } returns mockedResult

        every { reportCertificatePersistence.listCertificatesOfProjectReport(REPORT_ID) } returns listOf(certificate)
        val auditSlot = slot<AuditCandidateEvent>()
        every { auditPublisher.publishEvent(capture(auditSlot)) } returns Unit
        every { reportIdentificationPersistence.getSpendingProfileCurrentValues(REPORT_ID) } returns mapOf()
        every { reportIdentificationPersistence.updateSpendingProfile(REPORT_ID, mapOf()) } returnsArgument 0
        every { reportCertificateCoFinancingPersistence.getCoFinancing(PROJECT_ID, REPORT_ID) } returns certificateCoFin
        every { reportExpenditureCoFinancingPersistence.getCoFinancingTotalEligible(setOf(42L)) } returns totalEligibleAfterControl
        every { reportCertificateCoFinancingPersistence.updateCurrentlyReportedValues(any(), any(), any()) } returnsArgument 0

        every { reportCertificateCostCategoryPersistence.getCostCategories(PROJECT_ID, REPORT_ID) } returns certificateCostCategory
        every { reportExpenditureCostCategoryPersistence.getCostCategoriesCumulativeTotalEligible(setOf(42L)) } returns budgetCostFull
        every { reportCertificateCostCategoryPersistence.updateCurrentlyReportedValues(any(), any(), any()) } returnsArgument 0

        every { reportLumpSumPersistence.getLumpSumCumulativeAfterControl(setOf(42L)) } returns mapOf(Pair(1, BigDecimal.TEN))
        every { reportCertificateLumpSumPersistence.updateCurrentlyReportedValues(any(), any(), any()) } returnsArgument 0

        every { reportExpenditureUnitCostPersistence.getUnitCostCumulativeAfterControl(setOf(42L)) } returns mapOf(Pair(1L, BigDecimal.TEN))
        every { reportCertificateUnitCostPersistence.updateCurrentlyReportedValues(any(), any(), any()) } returnsArgument 0

        every { reportExpenditureInvestmentPersistence.getInvestmentsCumulativeAfterControl(setOf(42L)) } returns mapOf(Pair(1L, BigDecimal.TEN))
        val investmentSlot = slot<Map<Long, BigDecimal>>()
        every { reportCertificateInvestmentPersistence.updateCurrentlyReportedValues(PROJECT_ID, reportId = REPORT_ID, capture(investmentSlot)) } answers { }

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

        assertThat(investmentSlot.captured).containsExactlyEntriesOf(
            mapOf(
                1L to BigDecimal.TEN
            )
        )

        val expectDeletions = if (type == ContractingDeadlineType.Finance) 1 else 0
        verify(exactly = expectDeletions) { reportWorkPlanPersistence.deleteWorkPlan(any(), any()) }
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
