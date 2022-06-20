package io.cloudflight.jems.server.project.service.report.partner.financialOverview.getReportExpenditureBreakdown

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.currency.repository.CurrencyPersistence
import io.cloudflight.jems.server.currency.service.model.CurrencyConversion
import io.cloudflight.jems.server.project.service.budget.model.BudgetCostsCalculationResultFull
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerBudgetOptions
import io.cloudflight.jems.server.project.service.report.ProjectReportPersistence
import io.cloudflight.jems.server.project.service.report.model.ProjectPartnerReport
import io.cloudflight.jems.server.project.service.report.model.ReportStatus
import io.cloudflight.jems.server.project.service.report.model.expenditure.ProjectPartnerReportExpenditureCost
import io.cloudflight.jems.server.project.service.report.model.expenditure.ReportBudgetCategory
import io.cloudflight.jems.server.project.service.report.model.financialOverview.ExpenditureCostCategoryBreakdown
import io.cloudflight.jems.server.project.service.report.model.financialOverview.ExpenditureCostCategoryBreakdownLine
import io.cloudflight.jems.server.project.service.report.model.financialOverview.costCategory.ReportExpenditureCostCategory
import io.cloudflight.jems.server.project.service.report.partner.expenditure.ProjectReportExpenditurePersistence
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.ProjectReportExpenditureCostCategoryPersistence
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.ZonedDateTime

internal class GetReportExpenditureCostCategoryCalculatorServiceTest : UnitTest() {

    companion object {
        private const val PARTNER_ID = 591L
        private val TODAY = ZonedDateTime.now()

        private fun reportWithStatus(status: ReportStatus) = ProjectPartnerReport(
            id = 0L,
            reportNumber = 1,
            status = status,
            version = "",
            identification = mockk(),
        )

        private val data = ReportExpenditureCostCategory(
            options = ProjectPartnerBudgetOptions(
                partnerId = PARTNER_ID,
                officeAndAdministrationOnStaffCostsFlatRate = null,
                officeAndAdministrationOnDirectCostsFlatRate = null,
                travelAndAccommodationOnStaffCostsFlatRate = 15,
                staffCostsFlatRate = null,
                otherCostsOnStaffCostsFlatRate = null,
            ),
            totalsFromAF = BudgetCostsCalculationResultFull(
                staff = BigDecimal.valueOf(100),
                office = BigDecimal.valueOf(110),
                travel = BigDecimal.valueOf(120),
                external = BigDecimal.valueOf(130),
                equipment = BigDecimal.valueOf(140),
                infrastructure = BigDecimal.valueOf(150),
                other = BigDecimal.valueOf(160),
                lumpSum = BigDecimal.valueOf(170),
                unitCost = BigDecimal.valueOf(180),
                sum = BigDecimal.valueOf(1260),
            ),
            currentlyReported = BudgetCostsCalculationResultFull(
                staff = BigDecimal.valueOf(200),
                office = BigDecimal.valueOf(210),
                travel = BigDecimal.valueOf(220),
                external = BigDecimal.valueOf(230),
                equipment = BigDecimal.valueOf(240),
                infrastructure = BigDecimal.valueOf(250),
                other = BigDecimal.valueOf(260),
                lumpSum = BigDecimal.valueOf(270),
                unitCost = BigDecimal.valueOf(280),
                sum = BigDecimal.valueOf(2160),
            ),
            previouslyReported = BudgetCostsCalculationResultFull(
                staff = BigDecimal.valueOf(300),
                office = BigDecimal.valueOf(310),
                travel = BigDecimal.valueOf(320),
                external = BigDecimal.valueOf(330),
                equipment = BigDecimal.valueOf(340),
                infrastructure = BigDecimal.valueOf(350),
                other = BigDecimal.valueOf(360),
                lumpSum = BigDecimal.valueOf(370),
                unitCost = BigDecimal.valueOf(380),
                sum = BigDecimal.valueOf(3060),
            ),
        )

        private val expenditureLumpSum = ProjectPartnerReportExpenditureCost(
            id = 201L,
            lumpSumId = 45L,
            unitCostId = null,
            costCategory = ReportBudgetCategory.Multiple,
            investmentId = null,
            contractId = null,
            internalReferenceNumber = null,
            invoiceNumber = null,
            invoiceDate = TODAY.toLocalDate(),
            dateOfPayment = TODAY.toLocalDate(),
            numberOfUnits = BigDecimal.valueOf(2),
            pricePerUnit = BigDecimal.valueOf(2431, 2),
            declaredAmount = BigDecimal.valueOf(4862, 2),
            currencyCode = "CST",
            currencyConversionRate = null,
            declaredAmountAfterSubmission = null,
            attachment = null,
        )

        private val expenditureUnitCost = ProjectPartnerReportExpenditureCost(
            id = 202L,
            lumpSumId = null,
            unitCostId = 46L,
            costCategory = ReportBudgetCategory.Multiple,
            investmentId = null,
            contractId = null,
            internalReferenceNumber = null,
            invoiceNumber = null,
            invoiceDate = TODAY.toLocalDate(),
            dateOfPayment = TODAY.toLocalDate(),
            numberOfUnits = BigDecimal.valueOf(3),
            pricePerUnit = BigDecimal.valueOf(1023, 2),
            declaredAmount = BigDecimal.valueOf(3069, 2),
            currencyCode = "CST",
            currencyConversionRate = null,
            declaredAmountAfterSubmission = null,
            attachment = null,
        )

        private val expenditureStaffCost = ProjectPartnerReportExpenditureCost(
            id = 203L,
            lumpSumId = null,
            unitCostId = null,
            costCategory = ReportBudgetCategory.StaffCosts,
            investmentId = null,
            contractId = null,
            internalReferenceNumber = null,
            invoiceNumber = null,
            invoiceDate = TODAY.toLocalDate(),
            dateOfPayment = TODAY.toLocalDate(),
            numberOfUnits = BigDecimal.valueOf(8),
            pricePerUnit = BigDecimal.valueOf(50, 0),
            declaredAmount = BigDecimal.valueOf(400, 0),
            currencyCode = "CST",
            currencyConversionRate = null,
            declaredAmountAfterSubmission = null,
            attachment = null,
        )

        private val expenditureExternalCost = ProjectPartnerReportExpenditureCost(
            id = 204L,
            lumpSumId = null,
            unitCostId = null,
            costCategory = ReportBudgetCategory.ExternalCosts,
            investmentId = null,
            contractId = null,
            internalReferenceNumber = null,
            invoiceNumber = null,
            invoiceDate = TODAY.toLocalDate(),
            dateOfPayment = TODAY.toLocalDate(),
            numberOfUnits = BigDecimal.valueOf(4),
            pricePerUnit = BigDecimal.valueOf(115, 0),
            declaredAmount = BigDecimal.valueOf(460, 0),
            currencyCode = "CST",
            currencyConversionRate = null,
            declaredAmountAfterSubmission = null,
            attachment = null,
        )

        private val expenditureEquipmentCost = ProjectPartnerReportExpenditureCost(
            id = 205L,
            lumpSumId = null,
            unitCostId = null,
            costCategory = ReportBudgetCategory.EquipmentCosts,
            investmentId = null,
            contractId = null,
            internalReferenceNumber = null,
            invoiceNumber = null,
            invoiceDate = TODAY.toLocalDate(),
            dateOfPayment = TODAY.toLocalDate(),
            numberOfUnits = BigDecimal.valueOf(2),
            pricePerUnit = BigDecimal.valueOf(135, 0),
            declaredAmount = BigDecimal.valueOf(270, 0),
            currencyCode = "CST",
            currencyConversionRate = null,
            declaredAmountAfterSubmission = null,
            attachment = null,
        )

        private val expenditureInfrastructureCost = ProjectPartnerReportExpenditureCost(
            id = 206L,
            lumpSumId = null,
            unitCostId = null,
            costCategory = ReportBudgetCategory.InfrastructureCosts,
            investmentId = null,
            contractId = null,
            internalReferenceNumber = null,
            invoiceNumber = null,
            invoiceDate = TODAY.toLocalDate(),
            dateOfPayment = TODAY.toLocalDate(),
            numberOfUnits = BigDecimal.valueOf(7),
            pricePerUnit = BigDecimal.valueOf(35, 0),
            declaredAmount = BigDecimal.valueOf(245, 0),
            currencyCode = "CST",
            currencyConversionRate = null,
            declaredAmountAfterSubmission = null,
            attachment = null,
        )

        private val expenditureMultipleCost = ProjectPartnerReportExpenditureCost(
            id = 207L,
            lumpSumId = null,
            unitCostId = null,
            costCategory = ReportBudgetCategory.Multiple,
            investmentId = null,
            contractId = null,
            internalReferenceNumber = null,
            invoiceNumber = null,
            invoiceDate = TODAY.toLocalDate(),
            dateOfPayment = TODAY.toLocalDate(),
            numberOfUnits = BigDecimal.valueOf(5),
            pricePerUnit = BigDecimal.valueOf(45, 0),
            declaredAmount = BigDecimal.valueOf(225, 0),
            currencyCode = "CST",
            currencyConversionRate = null,
            declaredAmountAfterSubmission = null,
            attachment = null,
        )

        private val expectedOutput = ExpenditureCostCategoryBreakdown(
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
                flatRate = null,
                totalEligibleBudget = BigDecimal.valueOf(110),
                previouslyReported = BigDecimal.valueOf(310),
                currentReport = BigDecimal.ZERO,
                totalReportedSoFar = BigDecimal.valueOf(310),
                totalReportedSoFarPercentage = BigDecimal.valueOf(28182, 2),
                remainingBudget = BigDecimal.valueOf(-200),
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
                currentReport = BigDecimal.valueOf(13500, 2),
                totalReportedSoFar = BigDecimal.valueOf(47500, 2),
                totalReportedSoFarPercentage = BigDecimal.valueOf(33929, 2),
                remainingBudget = BigDecimal.valueOf(-33500, 2),
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
                currentReport = BigDecimal.valueOf(75716, 2),
                totalReportedSoFar = BigDecimal.valueOf(381716, 2),
                totalReportedSoFarPercentage = BigDecimal.valueOf(30295, 2),
                remainingBudget = BigDecimal.valueOf(-255716, 2),
            ),
        )

        private val expectedOnStaffOutput = ExpenditureCostCategoryBreakdown(
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
                flatRate = 10,
                totalEligibleBudget = BigDecimal.valueOf(110),
                previouslyReported = BigDecimal.valueOf(310),
                currentReport = BigDecimal.valueOf(7175, 2),
                totalReportedSoFar = BigDecimal.valueOf(38175, 2),
                totalReportedSoFarPercentage = BigDecimal.valueOf(34705, 2),
                remainingBudget = BigDecimal.valueOf(-27175, 2),
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
                currentReport = BigDecimal.valueOf(13500, 2),
                totalReportedSoFar = BigDecimal.valueOf(47500, 2),
                totalReportedSoFarPercentage = BigDecimal.valueOf(33929, 2),
                remainingBudget = BigDecimal.valueOf(-33500, 2),
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
                currentReport = BigDecimal.valueOf(82891, 2),
                totalReportedSoFar = BigDecimal.valueOf(388891, 2),
                totalReportedSoFarPercentage = BigDecimal.valueOf(30864, 2),
                remainingBudget = BigDecimal.valueOf(-262891, 2),
            ),
        )

        private val expectedOnDirectOutput = ExpenditureCostCategoryBreakdown(
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
                flatRate = 15,
                totalEligibleBudget = BigDecimal.valueOf(110),
                previouslyReported = BigDecimal.valueOf(310),
                currentReport = BigDecimal.valueOf(3000, 2),
                totalReportedSoFar = BigDecimal.valueOf(34000, 2),
                totalReportedSoFarPercentage = BigDecimal.valueOf(30909, 2),
                remainingBudget = BigDecimal.valueOf(-23000, 2),
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
                currentReport = BigDecimal.valueOf(13500, 2),
                totalReportedSoFar = BigDecimal.valueOf(47500, 2),
                totalReportedSoFarPercentage = BigDecimal.valueOf(33929, 2),
                remainingBudget = BigDecimal.valueOf(-33500, 2),
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
                currentReport = BigDecimal.valueOf(78716, 2),
                totalReportedSoFar = BigDecimal.valueOf(384716, 2),
                totalReportedSoFarPercentage = BigDecimal.valueOf(30533, 2),
                remainingBudget = BigDecimal.valueOf(-258716, 2),
            ),
        )

        private val expectedOutputWhenSubmitted = expectedOutput.copy(
            staff = expectedOutput.staff.copy(
                currentReport = BigDecimal.valueOf(200),
                totalReportedSoFar = BigDecimal.valueOf(500),
                remainingBudget = BigDecimal.valueOf(-400),
            ),
            office = expectedOutput.office.copy(
                currentReport = BigDecimal.valueOf(210),
                totalReportedSoFar = BigDecimal.valueOf(520),
                totalReportedSoFarPercentage = BigDecimal.valueOf(47273, 2),
                remainingBudget = BigDecimal.valueOf(-410),
            ),
            travel = expectedOutput.travel.copy(
                currentReport = BigDecimal.valueOf(220),
                totalReportedSoFar = BigDecimal.valueOf(540),
                totalReportedSoFarPercentage = BigDecimal.valueOf(45000, 2),
                remainingBudget = BigDecimal.valueOf(-420),
            ),
            external = expectedOutput.external.copy(
                currentReport = BigDecimal.valueOf(230),
                totalReportedSoFar = BigDecimal.valueOf(560),
                totalReportedSoFarPercentage = BigDecimal.valueOf(43077, 2),
                remainingBudget = BigDecimal.valueOf(-430),
            ),
            equipment = expectedOutput.equipment.copy(
                currentReport = BigDecimal.valueOf(240),
                totalReportedSoFar = BigDecimal.valueOf(580),
                totalReportedSoFarPercentage = BigDecimal.valueOf(41429, 2),
                remainingBudget = BigDecimal.valueOf(-440),
            ),
            infrastructure = expectedOutput.infrastructure.copy(
                currentReport = BigDecimal.valueOf(250),
                totalReportedSoFar = BigDecimal.valueOf(600),
                totalReportedSoFarPercentage = BigDecimal.valueOf(40000, 2),
                remainingBudget = BigDecimal.valueOf(-450),
            ),
            other = expectedOutput.other.copy(
                currentReport = BigDecimal.valueOf(260),
                totalReportedSoFar = BigDecimal.valueOf(620),
                totalReportedSoFarPercentage = BigDecimal.valueOf(38750, 2),
                remainingBudget = BigDecimal.valueOf(-460),
            ),
            lumpSum = expectedOutput.lumpSum.copy(
                currentReport = BigDecimal.valueOf(270),
                totalReportedSoFar = BigDecimal.valueOf(640),
                totalReportedSoFarPercentage = BigDecimal.valueOf(37647, 2),
                remainingBudget = BigDecimal.valueOf(-470),
            ),
            unitCost = expectedOutput.unitCost.copy(
                currentReport = BigDecimal.valueOf(280),
                totalReportedSoFar = BigDecimal.valueOf(660),
                totalReportedSoFarPercentage = BigDecimal.valueOf(36667, 2),
                remainingBudget = BigDecimal.valueOf(-480),
            ),
            total = expectedOutput.total.copy(
                currentReport = BigDecimal.valueOf(2160),
                totalReportedSoFar = BigDecimal.valueOf(5220),
                totalReportedSoFarPercentage = BigDecimal.valueOf(41429, 2),
                remainingBudget = BigDecimal.valueOf(-3960),
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
    fun `get - is not submitted - no office`() {
        every { reportPersistence.getPartnerReportById(PARTNER_ID, reportId = 18L) } returns
            reportWithStatus(status = ReportStatus.Draft)
        every { reportExpenditureCostCategoryPersistence.getCostCategories(PARTNER_ID, reportId = 18L) } returns data
        every { reportExpenditurePersistence.getPartnerReportExpenditureCosts(PARTNER_ID, reportId = 18L) } returns listOf(
            expenditureLumpSum,
            expenditureUnitCost,
            expenditureStaffCost,
            expenditureExternalCost,
            expenditureEquipmentCost,
            expenditureInfrastructureCost,
            expenditureMultipleCost,
        )

        assertThat(service.getSubmittedOrCalculateCurrent(PARTNER_ID, reportId = 18L)).isEqualTo(expectedOutput)
    }

    @Test
    fun `get - is not submitted - office on direct`() {
        every { reportPersistence.getPartnerReportById(PARTNER_ID, reportId = 19L) } returns
            reportWithStatus(status = ReportStatus.Draft)
        every { reportExpenditureCostCategoryPersistence.getCostCategories(PARTNER_ID, reportId = 19L) } returns data
            .copy(options = data.options.copy(officeAndAdministrationOnDirectCostsFlatRate = 10))
        every { reportExpenditurePersistence.getPartnerReportExpenditureCosts(PARTNER_ID, reportId = 19L) } returns listOf(
            expenditureLumpSum,
            expenditureUnitCost,
            expenditureStaffCost,
            expenditureExternalCost,
            expenditureEquipmentCost,
            expenditureInfrastructureCost,
            expenditureMultipleCost,
        )

        assertThat(service.getSubmittedOrCalculateCurrent(PARTNER_ID, reportId = 19L)).isEqualTo(expectedOnStaffOutput)
    }

    @Test
    fun `get - is not submitted - office on staff`() {
        every { reportPersistence.getPartnerReportById(PARTNER_ID, reportId = 20L) } returns
            reportWithStatus(status = ReportStatus.Draft)
        every { reportExpenditureCostCategoryPersistence.getCostCategories(PARTNER_ID, reportId = 20L) } returns data
            .copy(options = data.options.copy(officeAndAdministrationOnStaffCostsFlatRate = 15))
        every { reportExpenditurePersistence.getPartnerReportExpenditureCosts(PARTNER_ID, reportId = 20L) } returns listOf(
            expenditureLumpSum,
            expenditureUnitCost,
            expenditureStaffCost,
            expenditureExternalCost,
            expenditureEquipmentCost,
            expenditureInfrastructureCost,
            expenditureMultipleCost,
        )

        assertThat(service.getSubmittedOrCalculateCurrent(PARTNER_ID, reportId = 20L)).isEqualTo(expectedOnDirectOutput)
    }

    @Test
    fun `get - is submitted`() {
        every { reportPersistence.getPartnerReportById(PARTNER_ID, reportId = 25L) } returns
            reportWithStatus(status = ReportStatus.Submitted)
        every { reportExpenditureCostCategoryPersistence.getCostCategories(PARTNER_ID, reportId = 25L) } returns data

        assertThat(service.getSubmittedOrCalculateCurrent(PARTNER_ID, reportId = 25L)).isEqualTo(expectedOutputWhenSubmitted)
        verify(exactly = 0) { reportExpenditurePersistence.getPartnerReportExpenditureCosts(PARTNER_ID, any()) }
        verify(exactly = 0) { currencyPersistence.findAllByIdYearAndIdMonth(any(), any()) }
    }
}
