package io.cloudflight.jems.server.project.service.report.project.financialOverview.getReportCostCategoryBreakdown

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.service.budget.model.BudgetCostsCalculationResultFull
import io.cloudflight.jems.server.project.service.report.model.partner.ProjectPartnerReportSubmissionSummary
import io.cloudflight.jems.server.project.service.report.model.project.ProjectReportStatus
import io.cloudflight.jems.server.project.service.report.model.project.financialOverview.costCategory.CertificateCostCategoryBreakdown
import io.cloudflight.jems.server.project.service.report.model.project.financialOverview.costCategory.CertificateCostCategoryBreakdownLine
import io.cloudflight.jems.server.project.service.report.model.project.financialOverview.costCategory.ReportCertificateCostCategory
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.ProjectPartnerReportExpenditureCostCategoryPersistence
import io.cloudflight.jems.server.project.service.report.project.base.ProjectReportPersistence
import io.cloudflight.jems.server.project.service.report.project.certificate.ProjectReportCertificatePersistence
import io.cloudflight.jems.server.project.service.report.project.financialOverview.ProjectReportCertificateCostCategoryPersistence
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import java.math.BigDecimal

internal class GetReportCertificateCostCategoryBreakdownCalculatorTest: UnitTest() {

    companion object {
        private const val PROJECT_ID = 15L
        private const val REPORT_ID = 190L

        private val reportCostCategory = ReportCertificateCostCategory(
            totalsFromAF = BudgetCostsCalculationResultFull(
                staff = BigDecimal.valueOf(201L),
                office = BigDecimal.valueOf(202L),
                travel = BigDecimal.valueOf(203L),
                external = BigDecimal.valueOf(204L),
                equipment = BigDecimal.valueOf(205L),
                infrastructure = BigDecimal.valueOf(206L),
                other = BigDecimal.valueOf(207L),
                lumpSum = BigDecimal.valueOf(208L),
                unitCost = BigDecimal.valueOf(209L),
                spfCost = BigDecimal.valueOf(210L),
                sum = BigDecimal.valueOf(2055L),
            ),
            currentlyReported = BudgetCostsCalculationResultFull(
                staff = BigDecimal.valueOf(100L),
                office = BigDecimal.valueOf(101L),
                travel = BigDecimal.valueOf(102L),
                external = BigDecimal.valueOf(103L),
                equipment = BigDecimal.valueOf(104L),
                infrastructure = BigDecimal.valueOf(105L),
                other = BigDecimal.valueOf(106L),
                lumpSum = BigDecimal.valueOf(107L),
                unitCost = BigDecimal.valueOf(108L),
                spfCost = BigDecimal.valueOf(109L),
                sum = BigDecimal.valueOf(1045L),
            ),
            previouslyReported = BudgetCostsCalculationResultFull(
                staff = BigDecimal.valueOf(20L),
                office = BigDecimal.valueOf(21L),
                travel = BigDecimal.valueOf(22L),
                external = BigDecimal.valueOf(23L),
                equipment = BigDecimal.valueOf(24L),
                infrastructure = BigDecimal.valueOf(25L),
                other = BigDecimal.valueOf(26L),
                lumpSum = BigDecimal.valueOf(27L),
                unitCost = BigDecimal.valueOf(28L),
                spfCost = BigDecimal.valueOf(29L),
                sum = BigDecimal.valueOf(245L),
            ),
            currentVerified = BudgetCostsCalculationResultFull(
                staff = BigDecimal.ZERO,
                office = BigDecimal.ZERO,
                travel = BigDecimal.ZERO,
                external = BigDecimal.ZERO,
                equipment = BigDecimal.ZERO,
                infrastructure = BigDecimal.ZERO,
                other = BigDecimal.ZERO,
                lumpSum = BigDecimal.ZERO,
                unitCost = BigDecimal.ZERO,
                spfCost = BigDecimal.ZERO,
                sum = BigDecimal.ZERO,
            ),
            previouslyVerified = BudgetCostsCalculationResultFull(
                staff = BigDecimal.valueOf(20L),
                office = BigDecimal.valueOf(21L),
                travel = BigDecimal.valueOf(22L),
                external = BigDecimal.valueOf(23L),
                equipment = BigDecimal.valueOf(24L),
                infrastructure = BigDecimal.valueOf(25L),
                other = BigDecimal.valueOf(26L),
                lumpSum = BigDecimal.valueOf(27L),
                unitCost = BigDecimal.valueOf(28L),
                spfCost = BigDecimal.valueOf(29L),
                sum = BigDecimal.valueOf(245L),
            )
        )

        private val expectedOverview = CertificateCostCategoryBreakdown(
            staff = CertificateCostCategoryBreakdownLine(
                totalEligibleBudget = BigDecimal.valueOf(201L),
                previouslyReported = BigDecimal.valueOf(20L),
                currentReport = BigDecimal.valueOf(100L),
                totalReportedSoFar = BigDecimal.valueOf(120L),
                totalReportedSoFarPercentage = BigDecimal.valueOf(5970L, 2),
                remainingBudget = BigDecimal.valueOf(81L),
                currentVerified = BigDecimal.ZERO,
                previouslyVerified =  BigDecimal.valueOf(20L)
            ),
            office = CertificateCostCategoryBreakdownLine(
                totalEligibleBudget = BigDecimal.valueOf(202L),
                previouslyReported = BigDecimal.valueOf(21L),
                currentReport = BigDecimal.valueOf(101L),
                totalReportedSoFar = BigDecimal.valueOf(122L),
                totalReportedSoFarPercentage = BigDecimal.valueOf(6040L, 2),
                remainingBudget = BigDecimal.valueOf(80L),
                currentVerified = BigDecimal.ZERO,
                previouslyVerified =  BigDecimal.valueOf(21L)
            ),
            travel = CertificateCostCategoryBreakdownLine(
                totalEligibleBudget = BigDecimal.valueOf(203L),
                previouslyReported = BigDecimal.valueOf(22L),
                currentReport = BigDecimal.valueOf(102L),
                totalReportedSoFar = BigDecimal.valueOf(124L),
                totalReportedSoFarPercentage = BigDecimal.valueOf(6108L, 2),
                remainingBudget = BigDecimal.valueOf(79L),
                currentVerified = BigDecimal.ZERO,
                previouslyVerified =  BigDecimal.valueOf(22L)
            ),
            external = CertificateCostCategoryBreakdownLine(
                totalEligibleBudget = BigDecimal.valueOf(204L),
                previouslyReported = BigDecimal.valueOf(23L),
                currentReport = BigDecimal.valueOf(103L),
                totalReportedSoFar = BigDecimal.valueOf(126L),
                totalReportedSoFarPercentage = BigDecimal.valueOf(6176L, 2),
                remainingBudget = BigDecimal.valueOf(78L),
                currentVerified = BigDecimal.ZERO,
                previouslyVerified =  BigDecimal.valueOf(23L)
            ),
            equipment = CertificateCostCategoryBreakdownLine(
                totalEligibleBudget = BigDecimal.valueOf(205L),
                previouslyReported = BigDecimal.valueOf(24L),
                currentReport = BigDecimal.valueOf(104L),
                totalReportedSoFar = BigDecimal.valueOf(128L),
                totalReportedSoFarPercentage = BigDecimal.valueOf(6244L, 2),
                remainingBudget = BigDecimal.valueOf(77L),
                currentVerified = BigDecimal.ZERO,
                previouslyVerified =  BigDecimal.valueOf(24L)
            ),
            infrastructure = CertificateCostCategoryBreakdownLine(
                totalEligibleBudget = BigDecimal.valueOf(206L),
                previouslyReported = BigDecimal.valueOf(25L),
                currentReport = BigDecimal.valueOf(105L),
                totalReportedSoFar = BigDecimal.valueOf(130L),
                totalReportedSoFarPercentage = BigDecimal.valueOf(6311L, 2),
                remainingBudget = BigDecimal.valueOf(76),
                currentVerified = BigDecimal.ZERO,
                previouslyVerified =  BigDecimal.valueOf(25L)
            ),
            other = CertificateCostCategoryBreakdownLine(
                totalEligibleBudget = BigDecimal.valueOf(207L),
                previouslyReported = BigDecimal.valueOf(26L),
                currentReport = BigDecimal.valueOf(106L),
                totalReportedSoFar = BigDecimal.valueOf(132L),
                totalReportedSoFarPercentage = BigDecimal.valueOf(6377L, 2),
                remainingBudget = BigDecimal.valueOf(75L),
                currentVerified = BigDecimal.ZERO,
                previouslyVerified =  BigDecimal.valueOf(26L)
            ),
            lumpSum = CertificateCostCategoryBreakdownLine(
                totalEligibleBudget = BigDecimal.valueOf(208L),
                previouslyReported = BigDecimal.valueOf(27L),
                currentReport = BigDecimal.valueOf(107L),
                totalReportedSoFar = BigDecimal.valueOf(134L),
                totalReportedSoFarPercentage = BigDecimal.valueOf(6442L, 2),
                remainingBudget = BigDecimal.valueOf(74L),
                currentVerified = BigDecimal.ZERO,
                previouslyVerified =  BigDecimal.valueOf(27L)
            ),
            unitCost = CertificateCostCategoryBreakdownLine(
                totalEligibleBudget = BigDecimal.valueOf(209L),
                previouslyReported = BigDecimal.valueOf(28L),
                currentReport = BigDecimal.valueOf(108L),
                totalReportedSoFar = BigDecimal.valueOf(136L),
                totalReportedSoFarPercentage = BigDecimal.valueOf(6507L, 2),
                remainingBudget = BigDecimal.valueOf(73L),
                currentVerified = BigDecimal.ZERO,
                previouslyVerified =  BigDecimal.valueOf(28L)
            ),
            spfCost = CertificateCostCategoryBreakdownLine(
                totalEligibleBudget = BigDecimal.valueOf(210L),
                previouslyReported = BigDecimal.valueOf(29L),
                currentReport = BigDecimal.valueOf(109L),
                totalReportedSoFar = BigDecimal.valueOf(138L),
                totalReportedSoFarPercentage = BigDecimal.valueOf(65_71L, 2),
                remainingBudget = BigDecimal.valueOf(72L),
                currentVerified = BigDecimal.ZERO,
                previouslyVerified =  BigDecimal.valueOf(29L),
            ),
            total = CertificateCostCategoryBreakdownLine(
                totalEligibleBudget = BigDecimal.valueOf(2055L),
                previouslyReported = BigDecimal.valueOf(245L),
                currentReport = BigDecimal.valueOf(1045L),
                totalReportedSoFar = BigDecimal.valueOf(1290L),
                totalReportedSoFarPercentage = BigDecimal.valueOf(6277L, 2),
                remainingBudget = BigDecimal.valueOf(765L),
                currentVerified = BigDecimal.ZERO,
                previouslyVerified =  BigDecimal.valueOf(245L),
            ),
        )

        private val expectedDraftOverview = CertificateCostCategoryBreakdown(
            staff = CertificateCostCategoryBreakdownLine(
                totalEligibleBudget = BigDecimal.valueOf(201L),
                previouslyReported = BigDecimal.valueOf(20L),
                currentReport = BigDecimal.valueOf(50L),
                totalReportedSoFar = BigDecimal.valueOf(70L),
                totalReportedSoFarPercentage = BigDecimal.valueOf(3483L, 2),
                remainingBudget = BigDecimal.valueOf(131L),
                currentVerified = BigDecimal.ZERO,
                previouslyVerified = BigDecimal.valueOf(20L)
            ),
            office = CertificateCostCategoryBreakdownLine(
                totalEligibleBudget = BigDecimal.valueOf(202L),
                previouslyReported = BigDecimal.valueOf(21L),
                currentReport = BigDecimal.valueOf(51L),
                totalReportedSoFar = BigDecimal.valueOf(72L),
                totalReportedSoFarPercentage = BigDecimal.valueOf(3564L, 2),
                remainingBudget = BigDecimal.valueOf(130L),
                currentVerified = BigDecimal.ZERO,
                previouslyVerified = BigDecimal.valueOf(21L)
            ),
            travel = CertificateCostCategoryBreakdownLine(
                totalEligibleBudget = BigDecimal.valueOf(203L),
                previouslyReported = BigDecimal.valueOf(22L),
                currentReport = BigDecimal.valueOf(52L),
                totalReportedSoFar = BigDecimal.valueOf(74L),
                totalReportedSoFarPercentage = BigDecimal.valueOf(3645L, 2),
                remainingBudget = BigDecimal.valueOf(129L),
                currentVerified = BigDecimal.ZERO,
                previouslyVerified = BigDecimal.valueOf(22L)
            ),
            external = CertificateCostCategoryBreakdownLine(
                totalEligibleBudget = BigDecimal.valueOf(204L),
                previouslyReported = BigDecimal.valueOf(23L),
                currentReport = BigDecimal.valueOf(53L),
                totalReportedSoFar = BigDecimal.valueOf(76L),
                totalReportedSoFarPercentage = BigDecimal.valueOf(3725L, 2),
                remainingBudget = BigDecimal.valueOf(128L),
                currentVerified = BigDecimal.ZERO,
                previouslyVerified = BigDecimal.valueOf(23L)
            ),
            equipment = CertificateCostCategoryBreakdownLine(
                totalEligibleBudget = BigDecimal.valueOf(205L),
                previouslyReported = BigDecimal.valueOf(24L),
                currentReport = BigDecimal.valueOf(54L),
                totalReportedSoFar = BigDecimal.valueOf(78L),
                totalReportedSoFarPercentage = BigDecimal.valueOf(3805L, 2),
                remainingBudget = BigDecimal.valueOf(127L),
                currentVerified = BigDecimal.ZERO,
                previouslyVerified = BigDecimal.valueOf(24L)
            ),
            infrastructure = CertificateCostCategoryBreakdownLine(
                totalEligibleBudget = BigDecimal.valueOf(206L),
                previouslyReported = BigDecimal.valueOf(25L),
                currentReport = BigDecimal.valueOf(55L),
                totalReportedSoFar = BigDecimal.valueOf(80L),
                totalReportedSoFarPercentage = BigDecimal.valueOf(3883L, 2),
                remainingBudget = BigDecimal.valueOf(126),
                currentVerified = BigDecimal.ZERO,
                previouslyVerified = BigDecimal.valueOf(25L)
            ),
            other = CertificateCostCategoryBreakdownLine(
                totalEligibleBudget = BigDecimal.valueOf(207L),
                previouslyReported = BigDecimal.valueOf(26L),
                currentReport = BigDecimal.valueOf(56L),
                totalReportedSoFar = BigDecimal.valueOf(82L),
                totalReportedSoFarPercentage = BigDecimal.valueOf(3961L, 2),
                remainingBudget = BigDecimal.valueOf(125L),
                currentVerified = BigDecimal.ZERO,
                previouslyVerified = BigDecimal.valueOf(26L)
            ),
            lumpSum = CertificateCostCategoryBreakdownLine(
                totalEligibleBudget = BigDecimal.valueOf(208L),
                previouslyReported = BigDecimal.valueOf(27L),
                currentReport = BigDecimal.valueOf(57L),
                totalReportedSoFar = BigDecimal.valueOf(84L),
                totalReportedSoFarPercentage = BigDecimal.valueOf(4038L, 2),
                remainingBudget = BigDecimal.valueOf(124L),
                currentVerified = BigDecimal.ZERO,
                previouslyVerified = BigDecimal.valueOf(27L)
            ),
            unitCost = CertificateCostCategoryBreakdownLine(
                totalEligibleBudget = BigDecimal.valueOf(209L),
                previouslyReported = BigDecimal.valueOf(28L),
                currentReport = BigDecimal.valueOf(58L),
                totalReportedSoFar = BigDecimal.valueOf(86L),
                totalReportedSoFarPercentage = BigDecimal.valueOf(4115L, 2),
                remainingBudget = BigDecimal.valueOf(123L),
                currentVerified = BigDecimal.ZERO,
                previouslyVerified = BigDecimal.valueOf(28L)
            ),
            spfCost = CertificateCostCategoryBreakdownLine(
                totalEligibleBudget = BigDecimal.valueOf(210L),
                previouslyReported = BigDecimal.valueOf(29L),
                currentReport = BigDecimal.valueOf(59L),
                totalReportedSoFar = BigDecimal.valueOf(88L),
                totalReportedSoFarPercentage = BigDecimal.valueOf(41_90L, 2),
                remainingBudget = BigDecimal.valueOf(122L),
                currentVerified = BigDecimal.ZERO,
                previouslyVerified = BigDecimal.valueOf(29L),
            ),
            total = CertificateCostCategoryBreakdownLine(
                totalEligibleBudget = BigDecimal.valueOf(2055L),
                previouslyReported = BigDecimal.valueOf(245L),
                currentReport = BigDecimal.valueOf(545L),
                totalReportedSoFar = BigDecimal.valueOf(790L),
                totalReportedSoFarPercentage = BigDecimal.valueOf(38_44L, 2),
                remainingBudget = BigDecimal.valueOf(1265L),
                currentVerified = BigDecimal.ZERO,
                previouslyVerified = BigDecimal.valueOf(245L),
            ),
        )

        fun certificate(): ProjectPartnerReportSubmissionSummary {
            val mock = mockk<ProjectPartnerReportSubmissionSummary>()
            every { mock.id } returns 4478L
            return mock
        }
    }

    @MockK
    private lateinit var reportPersistence: ProjectReportPersistence
    @MockK
    private lateinit var reportCertificateCostCategoryPersistence: ProjectReportCertificateCostCategoryPersistence
    @MockK
    private lateinit var reportCertificatePersistence: ProjectReportCertificatePersistence
    @MockK
    private lateinit var reportExpenditureCostCategoryPersistence: ProjectPartnerReportExpenditureCostCategoryPersistence

    @InjectMockKs
    private lateinit var service: GetReportCertificateCostCategoryBreakdownCalculator

    @BeforeEach
    fun reset() {
        clearMocks(
            reportPersistence,
            reportCertificateCostCategoryPersistence,
            reportCertificatePersistence,
            reportExpenditureCostCategoryPersistence,
        )
    }

    @ParameterizedTest(name = "getSubmittedOrCalculateCurrent (status {0})")
    @EnumSource(value = ProjectReportStatus::class, names = ["Draft", "ReOpenSubmittedLast", "VerificationReOpenedLast"], mode = EnumSource.Mode.EXCLUDE)
    fun getSubmittedOrCalculateCurrent(status: ProjectReportStatus) {
        every { reportPersistence.getReportById(projectId = PROJECT_ID, REPORT_ID).status } returns status
        every { reportCertificateCostCategoryPersistence.getCostCategories(projectId = PROJECT_ID, REPORT_ID) } returns
            reportCostCategory
        assertThat(service.getSubmittedOrCalculateCurrent(projectId = PROJECT_ID, REPORT_ID)).isEqualTo(expectedOverview)
    }

    @ParameterizedTest(name = "getSubmittedOrCalculateCurrent - closed (status {0})")
    @EnumSource(value = ProjectReportStatus::class, names = ["Draft", "ReOpenSubmittedLast", "VerificationReOpenedLast"])
    fun `getSubmittedOrCalculateCurrent - closed`(status: ProjectReportStatus) {
        every { reportPersistence.getReportById(projectId = PROJECT_ID, REPORT_ID).status } returns status
        every { reportCertificateCostCategoryPersistence.getCostCategories(projectId = PROJECT_ID, REPORT_ID) } returns
            reportCostCategory
        every { reportCertificatePersistence.listCertificatesOfProjectReport(REPORT_ID) } returns listOf(certificate())
        every { reportExpenditureCostCategoryPersistence.getCostCategoriesCumulativeTotalEligible(setOf(4478L)) } returns
            BudgetCostsCalculationResultFull(
                staff = BigDecimal.valueOf(50L),
                office = BigDecimal.valueOf(51L),
                travel = BigDecimal.valueOf(52L),
                external = BigDecimal.valueOf(53L),
                equipment = BigDecimal.valueOf(54L),
                infrastructure = BigDecimal.valueOf(55L),
                other = BigDecimal.valueOf(56L),
                lumpSum = BigDecimal.valueOf(57L),
                unitCost = BigDecimal.valueOf(58L),
                spfCost = BigDecimal.valueOf(59L),
                sum = BigDecimal.valueOf(545L),
            )

        assertThat(service.getSubmittedOrCalculateCurrent(projectId = PROJECT_ID, REPORT_ID))
            .isEqualTo(expectedDraftOverview)
    }

}
