package io.cloudflight.jems.server.project.service.report.partner.financialOverview.getReportExpenditureBreakdown

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.currency.repository.CurrencyPersistence
import io.cloudflight.jems.server.currency.service.model.CurrencyConversion
import io.cloudflight.jems.server.project.service.report.partner.ProjectPartnerReportPersistence
import io.cloudflight.jems.server.project.service.report.model.partner.ProjectPartnerReport
import io.cloudflight.jems.server.project.service.report.model.partner.ReportStatus
import io.cloudflight.jems.server.project.service.report.model.partner.financialOverview.costCategory.ExpenditureCostCategoryBreakdown
import io.cloudflight.jems.server.project.service.report.model.partner.financialOverview.costCategory.ExpenditureCostCategoryBreakdownLine
import io.cloudflight.jems.server.project.service.report.partner.expenditure.ProjectPartnerReportExpenditurePersistence
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.ProjectPartnerReportExpenditureCostCategoryPersistence
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
            firstSubmission = TODAY,
        )

        private val expectedOnDirect2Output = ExpenditureCostCategoryBreakdown(
            staff = ExpenditureCostCategoryBreakdownLine(
                flatRate = null,
                totalEligibleBudget = BigDecimal.valueOf(100),
                previouslyReported = BigDecimal.valueOf(300),
                currentReport = BigDecimal.valueOf(20000, 2),
                totalEligibleAfterControl = BigDecimal.valueOf(400),
                totalReportedSoFar = BigDecimal.valueOf(50000, 2),
                totalReportedSoFarPercentage = BigDecimal.valueOf(50000, 2),
                remainingBudget = BigDecimal.valueOf(-40000, 2),
            ),
            office = ExpenditureCostCategoryBreakdownLine(
                flatRate = 25,
                totalEligibleBudget = BigDecimal.valueOf(110),
                previouslyReported = BigDecimal.valueOf(310),
                currentReport = BigDecimal.valueOf(20312, 2),
                totalEligibleAfterControl = BigDecimal.valueOf(410),
                totalReportedSoFar = BigDecimal.valueOf(51312, 2),
                totalReportedSoFarPercentage = BigDecimal.valueOf(46647, 2),
                remainingBudget = BigDecimal.valueOf(-40312, 2),
            ),
            travel = ExpenditureCostCategoryBreakdownLine(
                flatRate = 15,
                totalEligibleBudget = BigDecimal.valueOf(120),
                previouslyReported = BigDecimal.valueOf(320),
                currentReport = BigDecimal.valueOf(3000, 2),
                totalEligibleAfterControl = BigDecimal.valueOf(420),
                totalReportedSoFar = BigDecimal.valueOf(35000, 2),
                totalReportedSoFarPercentage = BigDecimal.valueOf(29167, 2),
                remainingBudget = BigDecimal.valueOf(-23000, 2),
            ),
            external = ExpenditureCostCategoryBreakdownLine(
                flatRate = null,
                totalEligibleBudget = BigDecimal.valueOf(130),
                previouslyReported = BigDecimal.valueOf(330),
                currentReport = BigDecimal.valueOf(23000, 2),
                totalEligibleAfterControl = BigDecimal.valueOf(430),
                totalReportedSoFar = BigDecimal.valueOf(56000, 2),
                totalReportedSoFarPercentage = BigDecimal.valueOf(43077, 2),
                remainingBudget = BigDecimal.valueOf(-43000, 2),
            ),
            equipment = ExpenditureCostCategoryBreakdownLine(
                flatRate = null,
                totalEligibleBudget = BigDecimal.valueOf(140),
                previouslyReported = BigDecimal.valueOf(340),
                currentReport = BigDecimal.valueOf(23000, 2),
                totalEligibleAfterControl = BigDecimal.valueOf(440),
                totalReportedSoFar = BigDecimal.valueOf(57000, 2),
                totalReportedSoFarPercentage = BigDecimal.valueOf(40714, 2),
                remainingBudget = BigDecimal.valueOf(-43000, 2),
            ),
            infrastructure = ExpenditureCostCategoryBreakdownLine(
                flatRate = null,
                totalEligibleBudget = BigDecimal.valueOf(150),
                previouslyReported = BigDecimal.valueOf(350),
                currentReport = BigDecimal.valueOf(12250, 2),
                totalEligibleAfterControl = BigDecimal.valueOf(450),
                totalReportedSoFar = BigDecimal.valueOf(47250, 2),
                totalReportedSoFarPercentage = BigDecimal.valueOf(31500, 2),
                remainingBudget = BigDecimal.valueOf(-32250, 2),
            ),
            other = ExpenditureCostCategoryBreakdownLine(
                flatRate = null,
                totalEligibleBudget = BigDecimal.ZERO,
                previouslyReported = BigDecimal.ZERO,
                currentReport = BigDecimal.ZERO,
                totalEligibleAfterControl = BigDecimal.ZERO,
                totalReportedSoFar = BigDecimal.ZERO,
                totalReportedSoFarPercentage = BigDecimal.ZERO,
                remainingBudget = BigDecimal.ZERO,
            ),
            lumpSum = ExpenditureCostCategoryBreakdownLine(
                flatRate = null,
                totalEligibleBudget = BigDecimal.valueOf(170),
                previouslyReported = BigDecimal.valueOf(370),
                currentReport = BigDecimal.valueOf(2431, 2),
                totalEligibleAfterControl = BigDecimal.valueOf(470),
                totalReportedSoFar = BigDecimal.valueOf(39431, 2),
                totalReportedSoFarPercentage = BigDecimal.valueOf(23195, 2),
                remainingBudget = BigDecimal.valueOf(-22431, 2),
            ),
            unitCost = ExpenditureCostCategoryBreakdownLine(
                flatRate = null,
                totalEligibleBudget = BigDecimal.valueOf(180),
                previouslyReported = BigDecimal.valueOf(380),
                currentReport = BigDecimal.valueOf(12785, 2),
                totalEligibleAfterControl = BigDecimal.valueOf(480),
                totalReportedSoFar = BigDecimal.valueOf(50785, 2),
                totalReportedSoFarPercentage = BigDecimal.valueOf(28214, 2),
                remainingBudget = BigDecimal.valueOf(-32785, 2),
            ),
            total = ExpenditureCostCategoryBreakdownLine(
                flatRate = null,
                totalEligibleBudget = BigDecimal.valueOf(1260),
                previouslyReported = BigDecimal.valueOf(3060),
                currentReport = BigDecimal.valueOf(116778, 2),
                totalEligibleAfterControl = BigDecimal.valueOf(4160),
                totalReportedSoFar = BigDecimal.valueOf(422778, 2),
                totalReportedSoFarPercentage = BigDecimal.valueOf(33554, 2),
                remainingBudget = BigDecimal.valueOf(-296778, 2),
            ),
        )

    }

    @MockK
    lateinit var reportPersistence: ProjectPartnerReportPersistence
    @MockK
    lateinit var reportExpenditureCostCategoryPersistence: ProjectPartnerReportExpenditureCostCategoryPersistence
    @MockK
    lateinit var reportExpenditurePersistence: ProjectPartnerReportExpenditurePersistence
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
            .copy(
                options = data.options.copy(officeAndAdministrationOnDirectCostsFlatRate = 25),
                totalsFromAF = data.totalsFromAF.copy(other = BigDecimal.ZERO),
                previouslyReported = data.previouslyReported.copy(other = BigDecimal.ZERO),
                totalEligibleAfterControl = data.totalEligibleAfterControl.copy(other = BigDecimal.ZERO),
            )
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

        assertThat(service.getSubmittedOrCalculateCurrent(PARTNER_ID, reportId = 28L)).isEqualTo(expectedOnDirect2Output)
    }

}
