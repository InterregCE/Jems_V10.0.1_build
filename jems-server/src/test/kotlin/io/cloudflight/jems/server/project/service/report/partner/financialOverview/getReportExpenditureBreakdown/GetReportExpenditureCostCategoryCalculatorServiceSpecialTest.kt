package io.cloudflight.jems.server.project.service.report.partner.financialOverview.getReportExpenditureBreakdown

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.currency.repository.CurrencyPersistence
import io.cloudflight.jems.server.currency.service.model.CurrencyConversion
import io.cloudflight.jems.server.project.service.report.partner.ProjectPartnerReportPersistence
import io.cloudflight.jems.server.project.service.report.model.partner.ProjectPartnerReportStatusAndVersion
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
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.getReportExpenditureBreakdown.GetReportExpenditureCostCategoryCalculatorServiceTest.Companion.expenditureSpfCost
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.getReportExpenditureBreakdown.GetReportExpenditureCostCategoryCalculatorServiceTest.Companion.expenditureStaffCost
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.getReportExpenditureBreakdown.GetReportExpenditureCostCategoryCalculatorServiceTest.Companion.expenditureUnitCost
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.math.BigDecimal.ZERO
import java.math.BigDecimal.valueOf
import java.time.ZonedDateTime

internal class GetReportExpenditureCostCategoryCalculatorServiceSpecialTest : UnitTest() {

    companion object {
        private const val PARTNER_ID = 592L
        private val TODAY = ZonedDateTime.now()

        private fun reportWithStatus(status: ReportStatus) = ProjectPartnerReportStatusAndVersion(
            reportId = 28L,
            status = status,
            version = "",
        )

        private val expectedOnDirect2Output = ExpenditureCostCategoryBreakdown(
            staff = ExpenditureCostCategoryBreakdownLine(
                flatRate = null,
                totalEligibleBudget = valueOf(500),
                previouslyReported = valueOf(300),
                previouslyReportedParked = valueOf(30),
                currentReport = valueOf(40000, 2),
                currentReportReIncluded = valueOf(40000, 2),
                totalEligibleAfterControl = valueOf(160),
                totalReportedSoFar = valueOf(70000, 2),
                totalReportedSoFarPercentage = valueOf(14000, 2),
                remainingBudget = valueOf(-20000, 2),
                previouslyValidated = valueOf(400)
            ),
            office = ExpenditureCostCategoryBreakdownLine(
                flatRate = 25,
                totalEligibleBudget = valueOf(510),
                previouslyReported = valueOf(310),
                previouslyReportedParked = valueOf(31),
                currentReport = valueOf(26062, 2),
                currentReportReIncluded = valueOf(11500, 2),
                totalEligibleAfterControl = valueOf(170),
                totalReportedSoFar = valueOf(57062, 2),
                totalReportedSoFarPercentage = valueOf(11189, 2),
                remainingBudget = valueOf(-6062, 2),
                previouslyValidated = valueOf(410)
            ),
            travel = ExpenditureCostCategoryBreakdownLine(
                flatRate = 15,
                totalEligibleBudget = valueOf(520),
                previouslyReported = valueOf(320),
                previouslyReportedParked = valueOf(32),
                currentReport = valueOf(6000, 2),
                currentReportReIncluded = valueOf(6000, 2),
                totalEligibleAfterControl = valueOf(180),
                totalReportedSoFar = valueOf(38000, 2),
                totalReportedSoFarPercentage = valueOf(7308, 2),
                remainingBudget = valueOf(14000, 2),
                previouslyValidated = valueOf(420)
            ),
            external = ExpenditureCostCategoryBreakdownLine(
                flatRate = null,
                totalEligibleBudget = valueOf(530),
                previouslyReported = valueOf(330),
                previouslyReportedParked = valueOf(33),
                currentReport = valueOf(23000, 2),
                currentReportReIncluded = ZERO,
                totalEligibleAfterControl = valueOf(190),
                totalReportedSoFar = valueOf(56000, 2),
                totalReportedSoFarPercentage = valueOf(10566, 2),
                remainingBudget = valueOf(-3000, 2),
                previouslyValidated = valueOf(430)
            ),
            equipment = ExpenditureCostCategoryBreakdownLine(
                flatRate = null,
                totalEligibleBudget = valueOf(540),
                previouslyReported = valueOf(340),
                previouslyReportedParked = valueOf(34),
                currentReport = valueOf(23000, 2),
                currentReportReIncluded = ZERO,
                totalEligibleAfterControl = valueOf(200),
                totalReportedSoFar = valueOf(57000, 2),
                totalReportedSoFarPercentage = valueOf(10556, 2),
                remainingBudget = valueOf(-3000, 2),
                previouslyValidated = valueOf(440)
            ),
            infrastructure = ExpenditureCostCategoryBreakdownLine(
                flatRate = null,
                totalEligibleBudget = valueOf(550),
                previouslyReported = valueOf(350),
                previouslyReportedParked = valueOf(35),
                currentReport = valueOf(12250, 2),
                currentReportReIncluded = ZERO,
                totalEligibleAfterControl = valueOf(210),
                totalReportedSoFar = valueOf(47250, 2),
                totalReportedSoFarPercentage = valueOf(8591, 2),
                remainingBudget = valueOf(7750, 2),
                previouslyValidated = valueOf(450)
            ),
            other = ExpenditureCostCategoryBreakdownLine(
                flatRate = null,
                totalEligibleBudget = ZERO,
                previouslyReported = ZERO,
                previouslyReportedParked = valueOf(36),
                currentReport = ZERO,
                currentReportReIncluded = ZERO,
                totalEligibleAfterControl = ZERO,
                totalReportedSoFar = ZERO,
                totalReportedSoFarPercentage = valueOf(100L),
                remainingBudget = ZERO,
                previouslyValidated = valueOf(460)
            ),
            lumpSum = ExpenditureCostCategoryBreakdownLine(
                flatRate = null,
                totalEligibleBudget = valueOf(570),
                previouslyReported = valueOf(370),
                previouslyReportedParked = valueOf(37),
                currentReport = valueOf(2431, 2),
                currentReportReIncluded = ZERO,
                totalEligibleAfterControl = valueOf(230),
                totalReportedSoFar = valueOf(39431, 2),
                totalReportedSoFarPercentage = valueOf(6918, 2),
                remainingBudget = valueOf(17569, 2),
                previouslyValidated = valueOf(470)
            ),
            unitCost = ExpenditureCostCategoryBreakdownLine(
                flatRate = null,
                totalEligibleBudget = valueOf(580),
                previouslyReported = valueOf(380),
                previouslyReportedParked = valueOf(38),
                currentReport = valueOf(14319, 2),
                currentReportReIncluded = valueOf(3069, 2),
                totalEligibleAfterControl = valueOf(240),
                totalReportedSoFar = valueOf(52319, 2),
                totalReportedSoFarPercentage = valueOf(9021, 2),
                remainingBudget = valueOf(5681, 2),
                previouslyValidated = valueOf(480)
            ),
            spfCost = ExpenditureCostCategoryBreakdownLine(
                flatRate = null,
                totalEligibleBudget = valueOf(590),
                previouslyReported = valueOf(390),
                previouslyReportedParked = valueOf(39),
                currentReport = valueOf(9600, 2),
                currentReportReIncluded = valueOf(0),
                totalEligibleAfterControl = valueOf(250),
                totalReportedSoFar = valueOf(48600, 2),
                totalReportedSoFarPercentage = valueOf(8237, 2),
                remainingBudget = valueOf(10400, 2),
                previouslyValidated = valueOf(490),
            ),
            total = ExpenditureCostCategoryBreakdownLine(
                flatRate = null,
                totalEligibleBudget = valueOf(5450),
                previouslyReported = valueOf(3450),
                previouslyReportedParked = valueOf(345),
                currentReport = valueOf(156662, 2),
                currentReportReIncluded = valueOf(60569, 2),
                totalEligibleAfterControl = valueOf(2050),
                totalReportedSoFar = valueOf(501662, 2),
                totalReportedSoFarPercentage = valueOf(9205, 2),
                remainingBudget = valueOf(43338, 2),
                previouslyValidated = valueOf(4010),
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
            CurrencyConversion("CST", TODAY.year, TODAY.monthValue, "Name not important", valueOf(2)),
        )

    }

    @Test
    fun `get - is not submitted - office on direct - prevent case with same numbers in set`() {
        every { reportPersistence.getPartnerReportStatusAndVersion(PARTNER_ID, reportId = 28L) } returns
            reportWithStatus(status = ReportStatus.Draft)
        every { reportExpenditureCostCategoryPersistence.getCostCategories(PARTNER_ID, reportId = 28L) } returns data
            .copy(
                options = data.options.copy(officeAndAdministrationOnDirectCostsFlatRate = 25),
                totalsFromAF = data.totalsFromAF.copy(other = ZERO),
                previouslyReported = data.previouslyReported.copy(other = ZERO),
                totalEligibleAfterControl = data.totalEligibleAfterControl.copy(other = ZERO),
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
            expenditureSpfCost,
        )

        assertThat(service.getSubmittedOrCalculateCurrent(PARTNER_ID, reportId = 28L)).isEqualTo(expectedOnDirect2Output)
    }

}
