package io.cloudflight.jems.server.project.service.report.partner.financialOverview.getReportExpenditureBreakdown

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.currency.repository.CurrencyPersistence
import io.cloudflight.jems.server.currency.service.model.CurrencyConversion
import io.cloudflight.jems.server.project.service.report.ProjectReportPersistence
import io.cloudflight.jems.server.project.service.report.model.ProjectPartnerReport
import io.cloudflight.jems.server.project.service.report.model.ReportStatus
import io.cloudflight.jems.server.project.service.report.model.financialOverview.costCategory.ExpenditureCostCategoryBreakdown
import io.cloudflight.jems.server.project.service.report.model.financialOverview.costCategory.ExpenditureCostCategoryBreakdownLine
import io.cloudflight.jems.server.project.service.report.partner.expenditure.ProjectReportExpenditurePersistence
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.ProjectReportExpenditureCostCategoryPersistence
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.getReportExpenditureBreakdown.GetReportExpenditureCostCategoryCalculatorServiceTest.Companion.data
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.getReportExpenditureBreakdown.GetReportExpenditureCostCategoryCalculatorServiceTest.Companion.expenditureEquipmentCost
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.getReportExpenditureBreakdown.GetReportExpenditureCostCategoryCalculatorServiceTest.Companion.expenditureExternalCost
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.getReportExpenditureBreakdown.GetReportExpenditureCostCategoryCalculatorServiceTest.Companion.expenditureInfrastructureCost
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.getReportExpenditureBreakdown.GetReportExpenditureCostCategoryCalculatorServiceTest.Companion.expenditureLumpSum
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.getReportExpenditureBreakdown.GetReportExpenditureCostCategoryCalculatorServiceTest.Companion.expenditureMultipleCost
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.getReportExpenditureBreakdown.GetReportExpenditureCostCategoryCalculatorServiceTest.Companion.expenditureStaffCost
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.getReportExpenditureBreakdown.GetReportExpenditureCostCategoryCalculatorServiceTest.Companion.expenditureUnitCost
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.ZonedDateTime

internal class GetReportExpenditureCostCategoryCalculatorServiceSpecialTest : UnitTest() {

    companion object {
        private const val PARTNER_ID = 592L
        private val TODAY = ZonedDateTime.now()

        private fun reportWithStatus(status: ReportStatus) = ProjectPartnerReport(
            id = 0L,
            reportNumber = 1,
            status = status,
            version = "",
            identification = mockk(),
        )

        private val expectedOnDirect2Output = ExpenditureCostCategoryBreakdown(
            staff = ExpenditureCostCategoryBreakdownLine(
                flatRate = null,
                totalEligibleBudget = BigDecimal.valueOf(100),
                previouslyReported = BigDecimal.valueOf(300),
                currentReport = BigDecimal.valueOf(20000, 2),
                totalReportedSoFar = BigDecimal.valueOf(50000, 2),
                totalReportedSoFarPercentage = BigDecimal.valueOf(50000, 2),
                remainingBudget = BigDecimal.valueOf(-40000, 2),
            ),
            office = ExpenditureCostCategoryBreakdownLine(
                flatRate = 25,
                totalEligibleBudget = BigDecimal.valueOf(110),
                previouslyReported = BigDecimal.valueOf(310),
                currentReport = BigDecimal.valueOf(20312, 2),
                totalReportedSoFar = BigDecimal.valueOf(51312, 2),
                totalReportedSoFarPercentage = BigDecimal.valueOf(46647, 2),
                remainingBudget = BigDecimal.valueOf(-40312, 2),
            ),
            travel = ExpenditureCostCategoryBreakdownLine(
                flatRate = 15,
                totalEligibleBudget = BigDecimal.valueOf(120),
                previouslyReported = BigDecimal.valueOf(320),
                currentReport = BigDecimal.valueOf(3000, 2),
                totalReportedSoFar = BigDecimal.valueOf(35000, 2),
                totalReportedSoFarPercentage = BigDecimal.valueOf(29167, 2),
                remainingBudget = BigDecimal.valueOf(-23000, 2),
            ),
            external = ExpenditureCostCategoryBreakdownLine(
                flatRate = null,
                totalEligibleBudget = BigDecimal.valueOf(130),
                previouslyReported = BigDecimal.valueOf(330),
                currentReport = BigDecimal.valueOf(23000, 2),
                totalReportedSoFar = BigDecimal.valueOf(56000, 2),
                totalReportedSoFarPercentage = BigDecimal.valueOf(43077, 2),
                remainingBudget = BigDecimal.valueOf(-43000, 2),
            ),
            equipment = ExpenditureCostCategoryBreakdownLine(
                flatRate = null,
                totalEligibleBudget = BigDecimal.valueOf(140),
                previouslyReported = BigDecimal.valueOf(340),
                currentReport = BigDecimal.valueOf(23000, 2),
                totalReportedSoFar = BigDecimal.valueOf(57000, 2),
                totalReportedSoFarPercentage = BigDecimal.valueOf(40714, 2),
                remainingBudget = BigDecimal.valueOf(-43000, 2),
            ),
            infrastructure = ExpenditureCostCategoryBreakdownLine(
                flatRate = null,
                totalEligibleBudget = BigDecimal.valueOf(150),
                previouslyReported = BigDecimal.valueOf(350),
                currentReport = BigDecimal.valueOf(12250, 2),
                totalReportedSoFar = BigDecimal.valueOf(47250, 2),
                totalReportedSoFarPercentage = BigDecimal.valueOf(31500, 2),
                remainingBudget = BigDecimal.valueOf(-32250, 2),
            ),
            other = ExpenditureCostCategoryBreakdownLine(
                flatRate = null,
                totalEligibleBudget = BigDecimal.valueOf(160),
                previouslyReported = BigDecimal.valueOf(360),
                currentReport = BigDecimal.ZERO,
                totalReportedSoFar = BigDecimal.valueOf(360),
                totalReportedSoFarPercentage = BigDecimal.valueOf(22500, 2),
                remainingBudget = BigDecimal.valueOf(-200),
            ),
            lumpSum = ExpenditureCostCategoryBreakdownLine(
                flatRate = null,
                totalEligibleBudget = BigDecimal.valueOf(170),
                previouslyReported = BigDecimal.valueOf(370),
                currentReport = BigDecimal.valueOf(2431, 2),
                totalReportedSoFar = BigDecimal.valueOf(39431, 2),
                totalReportedSoFarPercentage = BigDecimal.valueOf(23195, 2),
                remainingBudget = BigDecimal.valueOf(-22431, 2),
            ),
            unitCost = ExpenditureCostCategoryBreakdownLine(
                flatRate = null,
                totalEligibleBudget = BigDecimal.valueOf(180),
                previouslyReported = BigDecimal.valueOf(380),
                currentReport = BigDecimal.valueOf(1535, 2),
                totalReportedSoFar = BigDecimal.valueOf(39535, 2),
                totalReportedSoFarPercentage = BigDecimal.valueOf(21964, 2),
                remainingBudget = BigDecimal.valueOf(-21535, 2),
            ),
            total = ExpenditureCostCategoryBreakdownLine(
                flatRate = null,
                totalEligibleBudget = BigDecimal.valueOf(1260),
                previouslyReported = BigDecimal.valueOf(3060),
                currentReport = BigDecimal.valueOf(105528, 2),
                totalReportedSoFar = BigDecimal.valueOf(411528, 2),
                totalReportedSoFarPercentage = BigDecimal.valueOf(32661, 2),
                remainingBudget = BigDecimal.valueOf(-285528, 2),
            ),
        )

    }

    @MockK
    lateinit var reportPersistence: ProjectReportPersistence
    @MockK
    lateinit var reportExpenditureCostCategoryPersistence: ProjectReportExpenditureCostCategoryPersistence
    @MockK
    lateinit var reportExpenditurePersistence: ProjectReportExpenditurePersistence
    @MockK
    lateinit var currencyPersistence: CurrencyPersistence

    @InjectMockKs
    lateinit var service: GetReportExpenditureCostCategoryCalculatorService

    @BeforeEach
    fun setup() {
        clearMocks(reportPersistence)
        clearMocks(reportExpenditureCostCategoryPersistence)
        clearMocks(reportExpenditurePersistence)
        every { currencyPersistence.findAllByIdYearAndIdMonth(TODAY.year, TODAY.monthValue) } returns listOf(
            CurrencyConversion("CST", TODAY.year, TODAY.monthValue, "Name not important", BigDecimal.valueOf(2)),
        )

    }

    @Test
    fun `get - is not submitted - office on direct - prevent case with same numbers in set`() {
        every { reportPersistence.getPartnerReportById(PARTNER_ID, reportId = 28L) } returns
            reportWithStatus(status = ReportStatus.Draft)
        every { reportExpenditureCostCategoryPersistence.getCostCategories(PARTNER_ID, reportId = 28L) } returns data
            .copy(options = data.options.copy(officeAndAdministrationOnDirectCostsFlatRate = 25))
        every { reportExpenditurePersistence.getPartnerReportExpenditureCosts(PARTNER_ID, reportId = 28L) } returns listOf(
            expenditureLumpSum,
            expenditureUnitCost,
            expenditureStaffCost,
            expenditureExternalCost,
            expenditureEquipmentCost.copy(
                numberOfUnits = expenditureExternalCost.numberOfUnits,
                pricePerUnit = expenditureExternalCost.pricePerUnit,
                declaredAmount = expenditureExternalCost.declaredAmount,
            ),
            expenditureInfrastructureCost,
            expenditureMultipleCost,
        )

        assertThat(service.getSubmittedOrCalculateCurrent(PARTNER_ID, reportId = 28L)).isEqualTo(
            expectedOnDirect2Output
        )
    }

}
