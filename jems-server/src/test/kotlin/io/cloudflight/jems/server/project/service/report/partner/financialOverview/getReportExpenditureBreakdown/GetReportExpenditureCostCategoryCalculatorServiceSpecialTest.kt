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
            status = status,
            version = "",
        )

        private val expectedOnDirect2Output = ExpenditureCostCategoryBreakdown(
            staff = ExpenditureCostCategoryBreakdownLine(
                flatRate = null,
                totalEligibleBudget = valueOf(100),
                previouslyReported = valueOf(300),
                previouslyReportedParked = valueOf(400),
                currentReport = valueOf(40000, 2),
                currentReportReIncluded = valueOf(40000, 2),
                totalEligibleAfterControl = valueOf(400),
                totalReportedSoFar = valueOf(70000, 2),
                totalReportedSoFarPercentage = valueOf(70000, 2),
                remainingBudget = valueOf(-60000, 2),
            ),
            office = ExpenditureCostCategoryBreakdownLine(
                flatRate = 25,
                totalEligibleBudget = valueOf(110),
                previouslyReported = valueOf(310),
                previouslyReportedParked = valueOf(410),
                currentReport = valueOf(26062, 2),
                currentReportReIncluded = valueOf(11500, 2),
                totalEligibleAfterControl = valueOf(410),
                totalReportedSoFar = valueOf(57062, 2),
                totalReportedSoFarPercentage = valueOf(51875, 2),
                remainingBudget = valueOf(-46062, 2),
            ),
            travel = ExpenditureCostCategoryBreakdownLine(
                flatRate = 15,
                totalEligibleBudget = valueOf(120),
                previouslyReported = valueOf(320),
                previouslyReportedParked = valueOf(420),
                currentReport = valueOf(6000, 2),
                currentReportReIncluded = valueOf(6000, 2),
                totalEligibleAfterControl = valueOf(420),
                totalReportedSoFar = valueOf(38000, 2),
                totalReportedSoFarPercentage = valueOf(31667, 2),
                remainingBudget = valueOf(-26000, 2),
            ),
            external = ExpenditureCostCategoryBreakdownLine(
                flatRate = null,
                totalEligibleBudget = valueOf(130),
                previouslyReported = valueOf(330),
                previouslyReportedParked = valueOf(430),
                currentReport = valueOf(23000, 2),
                currentReportReIncluded = ZERO,
                totalEligibleAfterControl = valueOf(430),
                totalReportedSoFar = valueOf(56000, 2),
                totalReportedSoFarPercentage = valueOf(43077, 2),
                remainingBudget = valueOf(-43000, 2),
            ),
            equipment = ExpenditureCostCategoryBreakdownLine(
                flatRate = null,
                totalEligibleBudget = valueOf(140),
                previouslyReported = valueOf(340),
                previouslyReportedParked = valueOf(440),
                currentReport = valueOf(23000, 2),
                currentReportReIncluded = ZERO,
                totalEligibleAfterControl = valueOf(440),
                totalReportedSoFar = valueOf(57000, 2),
                totalReportedSoFarPercentage = valueOf(40714, 2),
                remainingBudget = valueOf(-43000, 2),
            ),
            infrastructure = ExpenditureCostCategoryBreakdownLine(
                flatRate = null,
                totalEligibleBudget = valueOf(150),
                previouslyReported = valueOf(350),
                previouslyReportedParked = valueOf(450),
                currentReport = valueOf(12250, 2),
                currentReportReIncluded = ZERO,
                totalEligibleAfterControl = valueOf(450),
                totalReportedSoFar = valueOf(47250, 2),
                totalReportedSoFarPercentage = valueOf(31500, 2),
                remainingBudget = valueOf(-32250, 2),
            ),
            other = ExpenditureCostCategoryBreakdownLine(
                flatRate = null,
                totalEligibleBudget = ZERO,
                previouslyReported = ZERO,
                previouslyReportedParked = valueOf(460),
                currentReport = ZERO,
                currentReportReIncluded = ZERO,
                totalEligibleAfterControl = ZERO,
                totalReportedSoFar = ZERO,
                totalReportedSoFarPercentage = ZERO,
                remainingBudget = ZERO,
            ),
            lumpSum = ExpenditureCostCategoryBreakdownLine(
                flatRate = null,
                totalEligibleBudget = valueOf(170),
                previouslyReported = valueOf(370),
                previouslyReportedParked = valueOf(470),
                currentReport = valueOf(2431, 2),
                currentReportReIncluded = ZERO,
                totalEligibleAfterControl = valueOf(470),
                totalReportedSoFar = valueOf(39431, 2),
                totalReportedSoFarPercentage = valueOf(23195, 2),
                remainingBudget = valueOf(-22431, 2),
            ),
            unitCost = ExpenditureCostCategoryBreakdownLine(
                flatRate = null,
                totalEligibleBudget = valueOf(180),
                previouslyReported = valueOf(380),
                previouslyReportedParked = valueOf(480),
                currentReport = valueOf(14319, 2),
                currentReportReIncluded = valueOf(3069, 2),
                totalEligibleAfterControl = valueOf(480),
                totalReportedSoFar = valueOf(52319, 2),
                totalReportedSoFarPercentage = valueOf(29066, 2),
                remainingBudget = valueOf(-34319, 2),
            ),
            total = ExpenditureCostCategoryBreakdownLine(
                flatRate = null,
                totalEligibleBudget = valueOf(1260),
                previouslyReported = valueOf(3060),
                previouslyReportedParked = valueOf(490),
                currentReport = valueOf(147062, 2),
                currentReportReIncluded = valueOf(60569, 2),
                totalEligibleAfterControl = valueOf(4160),
                totalReportedSoFar = valueOf(453062, 2),
                totalReportedSoFarPercentage = valueOf(35957, 2),
                remainingBudget = valueOf(-327062, 2),
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
        )

        assertThat(service.getSubmittedOrCalculateCurrent(PARTNER_ID, reportId = 28L)).isEqualTo(expectedOnDirect2Output)
    }

}
