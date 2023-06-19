package io.cloudflight.jems.server.project.service.report.partner.financialOverview.getReportExpenditureBreakdown

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.currency.repository.CurrencyPersistence
import io.cloudflight.jems.server.currency.service.model.CurrencyConversion
import io.cloudflight.jems.server.project.service.budget.model.BudgetCostsCalculationResultFull
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerBudgetOptions
import io.cloudflight.jems.server.project.service.report.model.partner.ProjectPartnerReportStatusAndVersion
import io.cloudflight.jems.server.project.service.report.model.partner.ReportStatus
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.ProjectPartnerReportExpenditureCost
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.ReportBudgetCategory
import io.cloudflight.jems.server.project.service.report.model.partner.financialOverview.costCategory.ExpenditureCostCategoryBreakdown
import io.cloudflight.jems.server.project.service.report.model.partner.financialOverview.costCategory.ExpenditureCostCategoryBreakdownLine
import io.cloudflight.jems.server.project.service.report.model.partner.financialOverview.costCategory.ReportExpenditureCostCategory
import io.cloudflight.jems.server.project.service.report.partner.ProjectPartnerReportPersistence
import io.cloudflight.jems.server.project.service.report.partner.expenditure.ProjectPartnerReportExpenditurePersistence
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.ProjectPartnerReportExpenditureCostCategoryPersistence
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

        private fun reportWithStatus(reportId: Long, status: ReportStatus) = ProjectPartnerReportStatusAndVersion(
            reportId = reportId,
            status = status,
            version = "",
        )

        val data = ReportExpenditureCostCategory(
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
            currentlyReportedParked = BudgetCostsCalculationResultFull(
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
            currentlyReportedReIncluded = BudgetCostsCalculationResultFull(
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
            totalEligibleAfterControl = BudgetCostsCalculationResultFull(
                staff = BigDecimal.valueOf(400),
                office = BigDecimal.valueOf(410),
                travel = BigDecimal.valueOf(420),
                external = BigDecimal.valueOf(430),
                equipment = BigDecimal.valueOf(440),
                infrastructure = BigDecimal.valueOf(450),
                other = BigDecimal.valueOf(460),
                lumpSum = BigDecimal.valueOf(470),
                unitCost = BigDecimal.valueOf(480),
                sum = BigDecimal.valueOf(4160),
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
            previouslyReportedParked = BudgetCostsCalculationResultFull(
                staff = BigDecimal.valueOf(400),
                office = BigDecimal.valueOf(410),
                travel = BigDecimal.valueOf(420),
                external = BigDecimal.valueOf(430),
                equipment = BigDecimal.valueOf(440),
                infrastructure = BigDecimal.valueOf(450),
                other = BigDecimal.valueOf(460),
                lumpSum = BigDecimal.valueOf(470),
                unitCost = BigDecimal.valueOf(480),
                sum = BigDecimal.valueOf(490),
            ),
            previouslyValidated =  BudgetCostsCalculationResultFull(
                staff = BigDecimal.valueOf(500),
                office = BigDecimal.valueOf(510),
                travel = BigDecimal.valueOf(520),
                external = BigDecimal.valueOf(530),
                equipment = BigDecimal.valueOf(540),
                infrastructure = BigDecimal.valueOf(550),
                other = BigDecimal.valueOf(560),
                lumpSum = BigDecimal.valueOf(570),
                unitCost = BigDecimal.valueOf(580),
                sum = BigDecimal.valueOf(590),
            ),
        )

        val expenditureLumpSum = ProjectPartnerReportExpenditureCost(
            id = 201L,
            number = 1,
            lumpSumId = 45L,
            unitCostId = null,
            costCategory = ReportBudgetCategory.Multiple,
            gdpr = false,
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
            parkingMetadata = null,
        )

        val expenditureUnitCost = ProjectPartnerReportExpenditureCost(
            id = 202L,
            number = 2,
            lumpSumId = null,
            unitCostId = 46L,
            costCategory = ReportBudgetCategory.Multiple,
            gdpr = false,
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
            currencyConversionRate = BigDecimal.ONE,
            declaredAmountAfterSubmission = null,
            attachment = null,
            parkingMetadata = mockk(),
        )

        val expenditureStaffCost = ProjectPartnerReportExpenditureCost(
            id = 203L,
            number = 3,
            lumpSumId = null,
            unitCostId = null,
            costCategory = ReportBudgetCategory.StaffCosts,
            gdpr = false,
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
            currencyConversionRate = BigDecimal.ONE,
            declaredAmountAfterSubmission = null,
            attachment = null,
            parkingMetadata = mockk(),
        )

        val expenditureExternalCost = ProjectPartnerReportExpenditureCost(
            id = 204L,
            number = 4,
            lumpSumId = null,
            unitCostId = null,
            costCategory = ReportBudgetCategory.ExternalCosts,
            gdpr = false,
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
            parkingMetadata = null,
        )

        val expenditureEquipmentCost = ProjectPartnerReportExpenditureCost(
            id = 205L,
            number = 5,
            lumpSumId = null,
            unitCostId = null,
            costCategory = ReportBudgetCategory.EquipmentCosts,
            gdpr = false,
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
            parkingMetadata = null,
        )

        val expenditureInfrastructureCost = ProjectPartnerReportExpenditureCost(
            id = 206L,
            number = 6,
            lumpSumId = null,
            unitCostId = null,
            costCategory = ReportBudgetCategory.InfrastructureCosts,
            gdpr = false,
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
            parkingMetadata = null,
        )

        val expenditureMultipleCost = ProjectPartnerReportExpenditureCost(
            id = 207L,
            number = 7,
            lumpSumId = null,
            unitCostId = null,
            costCategory = ReportBudgetCategory.Multiple,
            gdpr = false,
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
            parkingMetadata = null,
        )

        private val expectedOutput = ExpenditureCostCategoryBreakdown(
            staff = ExpenditureCostCategoryBreakdownLine(
                flatRate = null,
                totalEligibleBudget = BigDecimal.valueOf(100),
                previouslyReported = BigDecimal.valueOf(300),
                previouslyReportedParked = BigDecimal.valueOf(400),
                currentReport = BigDecimal.valueOf(40000, 2),
                currentReportReIncluded = BigDecimal.valueOf(40000, 2),
                totalEligibleAfterControl = BigDecimal.valueOf(400),
                totalReportedSoFar = BigDecimal.valueOf(70000, 2),
                totalReportedSoFarPercentage = BigDecimal.valueOf(70000, 2),
                remainingBudget = BigDecimal.valueOf(-60000, 2),
                previouslyValidated = BigDecimal.valueOf(500)
            ),
            office = ExpenditureCostCategoryBreakdownLine(
                flatRate = null,
                totalEligibleBudget = BigDecimal.valueOf(110),
                previouslyReported = BigDecimal.valueOf(310),
                previouslyReportedParked = BigDecimal.valueOf(410),
                currentReport = BigDecimal.ZERO,
                currentReportReIncluded = BigDecimal.ZERO,
                totalEligibleAfterControl = BigDecimal.valueOf(410),
                totalReportedSoFar = BigDecimal.valueOf(310),
                totalReportedSoFarPercentage = BigDecimal.valueOf(28182, 2),
                remainingBudget = BigDecimal.valueOf(-200),
                previouslyValidated = BigDecimal.valueOf(510)
            ),
            travel = ExpenditureCostCategoryBreakdownLine(
                flatRate = 15,
                totalEligibleBudget = BigDecimal.valueOf(120),
                previouslyReported = BigDecimal.valueOf(320),
                previouslyReportedParked = BigDecimal.valueOf(420),
                currentReport = BigDecimal.valueOf(6000, 2),
                currentReportReIncluded = BigDecimal.valueOf(6000, 2),
                totalEligibleAfterControl = BigDecimal.valueOf(420),
                totalReportedSoFar = BigDecimal.valueOf(38000, 2),
                totalReportedSoFarPercentage = BigDecimal.valueOf(31667, 2),
                remainingBudget = BigDecimal.valueOf(-26000, 2),
                previouslyValidated = BigDecimal.valueOf(520)
            ),
            external = ExpenditureCostCategoryBreakdownLine(
                flatRate = null,
                totalEligibleBudget = BigDecimal.valueOf(130),
                previouslyReported = BigDecimal.valueOf(330),
                previouslyReportedParked = BigDecimal.valueOf(430),
                currentReport = BigDecimal.valueOf(23000, 2),
                currentReportReIncluded = BigDecimal.ZERO,
                totalEligibleAfterControl = BigDecimal.valueOf(430),
                totalReportedSoFar = BigDecimal.valueOf(56000, 2),
                totalReportedSoFarPercentage = BigDecimal.valueOf(43077, 2),
                remainingBudget = BigDecimal.valueOf(-43000, 2),
                previouslyValidated = BigDecimal.valueOf(530)
            ),
            equipment = ExpenditureCostCategoryBreakdownLine(
                flatRate = null,
                totalEligibleBudget = BigDecimal.valueOf(140),
                previouslyReported = BigDecimal.valueOf(340),
                previouslyReportedParked = BigDecimal.valueOf(440),
                currentReport = BigDecimal.valueOf(13500, 2),
                currentReportReIncluded = BigDecimal.ZERO,
                totalEligibleAfterControl = BigDecimal.valueOf(440),
                totalReportedSoFar = BigDecimal.valueOf(47500, 2),
                totalReportedSoFarPercentage = BigDecimal.valueOf(33929, 2),
                remainingBudget = BigDecimal.valueOf(-33500, 2),
                previouslyValidated = BigDecimal.valueOf(540)
            ),
            infrastructure = ExpenditureCostCategoryBreakdownLine(
                flatRate = null,
                totalEligibleBudget = BigDecimal.valueOf(150),
                previouslyReported = BigDecimal.valueOf(350),
                previouslyReportedParked = BigDecimal.valueOf(450),
                currentReport = BigDecimal.valueOf(12250, 2),
                currentReportReIncluded = BigDecimal.ZERO,
                totalEligibleAfterControl = BigDecimal.valueOf(450),
                totalReportedSoFar = BigDecimal.valueOf(47250, 2),
                totalReportedSoFarPercentage = BigDecimal.valueOf(31500, 2),
                remainingBudget = BigDecimal.valueOf(-32250, 2),
                previouslyValidated = BigDecimal.valueOf(550)
            ),
            other = ExpenditureCostCategoryBreakdownLine(
                flatRate = null,
                totalEligibleBudget = BigDecimal.valueOf(160),
                previouslyReported = BigDecimal.valueOf(360),
                previouslyReportedParked = BigDecimal.valueOf(460),
                currentReport = BigDecimal.ZERO,
                currentReportReIncluded = BigDecimal.ZERO,
                totalEligibleAfterControl = BigDecimal.valueOf(460),
                totalReportedSoFar = BigDecimal.valueOf(360),
                totalReportedSoFarPercentage = BigDecimal.valueOf(22500, 2),
                remainingBudget = BigDecimal.valueOf(-200),
                previouslyValidated = BigDecimal.valueOf(560)
            ),
            lumpSum = ExpenditureCostCategoryBreakdownLine(
                flatRate = null,
                totalEligibleBudget = BigDecimal.valueOf(170),
                previouslyReported = BigDecimal.valueOf(370),
                previouslyReportedParked = BigDecimal.valueOf(470),
                currentReport = BigDecimal.valueOf(2431, 2),
                currentReportReIncluded = BigDecimal.ZERO,
                totalEligibleAfterControl = BigDecimal.valueOf(470),
                totalReportedSoFar = BigDecimal.valueOf(39431, 2),
                totalReportedSoFarPercentage = BigDecimal.valueOf(23195, 2),
                remainingBudget = BigDecimal.valueOf(-22431, 2),
                previouslyValidated = BigDecimal.valueOf(570)
            ),
            unitCost = ExpenditureCostCategoryBreakdownLine(
                flatRate = null,
                totalEligibleBudget = BigDecimal.valueOf(180),
                previouslyReported = BigDecimal.valueOf(380),
                previouslyReportedParked = BigDecimal.valueOf(480),
                currentReport = BigDecimal.valueOf(14319, 2),
                currentReportReIncluded = BigDecimal.valueOf(3069, 2),
                totalEligibleAfterControl = BigDecimal.valueOf(480),
                totalReportedSoFar = BigDecimal.valueOf(52319, 2),
                totalReportedSoFarPercentage = BigDecimal.valueOf(29066, 2),
                remainingBudget = BigDecimal.valueOf(-34319, 2),
                previouslyValidated = BigDecimal.valueOf(580)
            ),
            total = ExpenditureCostCategoryBreakdownLine(
                flatRate = null,
                totalEligibleBudget = BigDecimal.valueOf(1260),
                previouslyReported = BigDecimal.valueOf(3060),
                previouslyReportedParked = BigDecimal.valueOf(490),
                currentReport = BigDecimal.valueOf(111500, 2),
                currentReportReIncluded = BigDecimal.valueOf(49069, 2),
                totalEligibleAfterControl = BigDecimal.valueOf(4160),
                totalReportedSoFar = BigDecimal.valueOf(417500, 2),
                totalReportedSoFarPercentage = BigDecimal.valueOf(33135, 2),
                remainingBudget = BigDecimal.valueOf(-291500, 2),
                previouslyValidated = BigDecimal.valueOf(590)
            ),
        )

        private val expectedOnDirectOutput = ExpenditureCostCategoryBreakdown(
            staff = ExpenditureCostCategoryBreakdownLine(
                flatRate = null,
                totalEligibleBudget = BigDecimal.valueOf(100),
                previouslyReported = BigDecimal.valueOf(300),
                previouslyReportedParked = BigDecimal.valueOf(400),
                currentReport = BigDecimal.valueOf(40000, 2),
                currentReportReIncluded = BigDecimal.valueOf(40000, 2),
                totalEligibleAfterControl = BigDecimal.valueOf(400),
                totalReportedSoFar = BigDecimal.valueOf(70000, 2),
                totalReportedSoFarPercentage = BigDecimal.valueOf(70000, 2),
                remainingBudget = BigDecimal.valueOf(-60000, 2),
                previouslyValidated = BigDecimal.valueOf(500)
            ),
            office = ExpenditureCostCategoryBreakdownLine(
                flatRate = 10,
                totalEligibleBudget = BigDecimal.valueOf(110),
                previouslyReported = BigDecimal.valueOf(310),
                previouslyReportedParked = BigDecimal.valueOf(410),
                currentReport = BigDecimal.valueOf(9475, 2),
                currentReportReIncluded = BigDecimal.valueOf(4600, 2),
                totalEligibleAfterControl = BigDecimal.valueOf(410),
                totalReportedSoFar = BigDecimal.valueOf(40475, 2),
                totalReportedSoFarPercentage = BigDecimal.valueOf(36795, 2),
                remainingBudget = BigDecimal.valueOf(-29475, 2),
                previouslyValidated = BigDecimal.valueOf(510)
            ),
            travel = ExpenditureCostCategoryBreakdownLine(
                flatRate = 15,
                totalEligibleBudget = BigDecimal.valueOf(120),
                previouslyReported = BigDecimal.valueOf(320),
                previouslyReportedParked = BigDecimal.valueOf(420),
                currentReport = BigDecimal.valueOf(6000, 2),
                currentReportReIncluded = BigDecimal.valueOf(6000, 2),
                totalEligibleAfterControl = BigDecimal.valueOf(420),
                totalReportedSoFar = BigDecimal.valueOf(38000, 2),
                totalReportedSoFarPercentage = BigDecimal.valueOf(31667, 2),
                remainingBudget = BigDecimal.valueOf(-26000, 2),
                previouslyValidated = BigDecimal.valueOf(520)
            ),
            external = ExpenditureCostCategoryBreakdownLine(
                flatRate = null,
                totalEligibleBudget = BigDecimal.valueOf(130),
                previouslyReported = BigDecimal.valueOf(330),
                previouslyReportedParked = BigDecimal.valueOf(430),
                currentReport = BigDecimal.valueOf(23000, 2),
                currentReportReIncluded = BigDecimal.ZERO,
                totalEligibleAfterControl = BigDecimal.valueOf(430),
                totalReportedSoFar = BigDecimal.valueOf(56000, 2),
                totalReportedSoFarPercentage = BigDecimal.valueOf(43077, 2),
                remainingBudget = BigDecimal.valueOf(-43000, 2),
                previouslyValidated = BigDecimal.valueOf(530)
            ),
            equipment = ExpenditureCostCategoryBreakdownLine(
                flatRate = null,
                totalEligibleBudget = BigDecimal.valueOf(140),
                previouslyReported = BigDecimal.valueOf(340),
                previouslyReportedParked = BigDecimal.valueOf(440),
                currentReport = BigDecimal.valueOf(13500, 2),
                currentReportReIncluded = BigDecimal.ZERO,
                totalEligibleAfterControl = BigDecimal.valueOf(440),
                totalReportedSoFar = BigDecimal.valueOf(47500, 2),
                totalReportedSoFarPercentage = BigDecimal.valueOf(33929, 2),
                remainingBudget = BigDecimal.valueOf(-33500, 2),
                previouslyValidated = BigDecimal.valueOf(540)
            ),
            infrastructure = ExpenditureCostCategoryBreakdownLine(
                flatRate = null,
                totalEligibleBudget = BigDecimal.valueOf(150),
                previouslyReported = BigDecimal.valueOf(350),
                previouslyReportedParked = BigDecimal.valueOf(450),
                currentReport = BigDecimal.valueOf(12250, 2),
                currentReportReIncluded = BigDecimal.ZERO,
                totalEligibleAfterControl = BigDecimal.valueOf(450),
                totalReportedSoFar = BigDecimal.valueOf(47250, 2),
                totalReportedSoFarPercentage = BigDecimal.valueOf(31500, 2),
                remainingBudget = BigDecimal.valueOf(-32250, 2),
                previouslyValidated = BigDecimal.valueOf(550)
            ),
            other = ExpenditureCostCategoryBreakdownLine(
                flatRate = null,
                totalEligibleBudget = BigDecimal.valueOf(160),
                previouslyReported = BigDecimal.valueOf(360),
                previouslyReportedParked = BigDecimal.valueOf(460),
                currentReport = BigDecimal.ZERO,
                currentReportReIncluded = BigDecimal.ZERO,
                totalEligibleAfterControl = BigDecimal.valueOf(460),
                totalReportedSoFar = BigDecimal.valueOf(360),
                totalReportedSoFarPercentage = BigDecimal.valueOf(22500, 2),
                remainingBudget = BigDecimal.valueOf(-200),
                previouslyValidated = BigDecimal.valueOf(560)
            ),
            lumpSum = ExpenditureCostCategoryBreakdownLine(
                flatRate = null,
                totalEligibleBudget = BigDecimal.valueOf(170),
                previouslyReported = BigDecimal.valueOf(370),
                previouslyReportedParked = BigDecimal.valueOf(470),
                currentReport = BigDecimal.valueOf(2431, 2),
                currentReportReIncluded = BigDecimal.ZERO,
                totalEligibleAfterControl = BigDecimal.valueOf(470),
                totalReportedSoFar = BigDecimal.valueOf(39431, 2),
                totalReportedSoFarPercentage = BigDecimal.valueOf(23195, 2),
                remainingBudget = BigDecimal.valueOf(-22431, 2),
                previouslyValidated = BigDecimal.valueOf(570)
            ),
            unitCost = ExpenditureCostCategoryBreakdownLine(
                flatRate = null,
                totalEligibleBudget = BigDecimal.valueOf(180),
                previouslyReported = BigDecimal.valueOf(380),
                previouslyReportedParked = BigDecimal.valueOf(480),
                currentReport = BigDecimal.valueOf(14319, 2),
                currentReportReIncluded = BigDecimal.valueOf(3069, 2),
                totalEligibleAfterControl = BigDecimal.valueOf(480),
                totalReportedSoFar = BigDecimal.valueOf(52319, 2),
                totalReportedSoFarPercentage = BigDecimal.valueOf(29066, 2),
                remainingBudget = BigDecimal.valueOf(-34319, 2),
                previouslyValidated = BigDecimal.valueOf(580)
            ),
            total = ExpenditureCostCategoryBreakdownLine(
                flatRate = null,
                totalEligibleBudget = BigDecimal.valueOf(1260),
                previouslyReported = BigDecimal.valueOf(3060),
                previouslyReportedParked = BigDecimal.valueOf(490),
                currentReport = BigDecimal.valueOf(120975, 2),
                currentReportReIncluded = BigDecimal.valueOf(53669, 2),
                totalEligibleAfterControl = BigDecimal.valueOf(4160),
                totalReportedSoFar = BigDecimal.valueOf(426975, 2),
                totalReportedSoFarPercentage = BigDecimal.valueOf(33887, 2),
                remainingBudget = BigDecimal.valueOf(-300975, 2),
                previouslyValidated = BigDecimal.valueOf(590)
            ),
        )

        private val expectedOnStaffOutput = ExpenditureCostCategoryBreakdown(
            staff = ExpenditureCostCategoryBreakdownLine(
                flatRate = null,
                totalEligibleBudget = BigDecimal.valueOf(100),
                previouslyReported = BigDecimal.valueOf(300),
                previouslyReportedParked = BigDecimal.valueOf(400),
                currentReport = BigDecimal.valueOf(40000, 2),
                currentReportReIncluded = BigDecimal.valueOf(40000, 2),
                totalEligibleAfterControl = BigDecimal.valueOf(400),
                totalReportedSoFar = BigDecimal.valueOf(70000, 2),
                totalReportedSoFarPercentage = BigDecimal.valueOf(70000, 2),
                remainingBudget = BigDecimal.valueOf(-60000, 2),
                previouslyValidated = BigDecimal.valueOf(500)
            ),
            office = ExpenditureCostCategoryBreakdownLine(
                flatRate = 15,
                totalEligibleBudget = BigDecimal.valueOf(110),
                previouslyReported = BigDecimal.valueOf(310),
                previouslyReportedParked = BigDecimal.valueOf(410),
                currentReport = BigDecimal.valueOf(6000, 2),
                currentReportReIncluded = BigDecimal.valueOf(6000, 2),
                totalEligibleAfterControl = BigDecimal.valueOf(410),
                totalReportedSoFar = BigDecimal.valueOf(37000, 2),
                totalReportedSoFarPercentage = BigDecimal.valueOf(33636, 2),
                remainingBudget = BigDecimal.valueOf(-26000, 2),
                previouslyValidated = BigDecimal.valueOf(510)
            ),
            travel = ExpenditureCostCategoryBreakdownLine(
                flatRate = 15,
                totalEligibleBudget = BigDecimal.valueOf(120),
                previouslyReported = BigDecimal.valueOf(320),
                previouslyReportedParked = BigDecimal.valueOf(420),
                currentReport = BigDecimal.valueOf(6000, 2),
                currentReportReIncluded = BigDecimal.valueOf(6000, 2),
                totalEligibleAfterControl = BigDecimal.valueOf(420),
                totalReportedSoFar = BigDecimal.valueOf(38000, 2),
                totalReportedSoFarPercentage = BigDecimal.valueOf(31667, 2),
                remainingBudget = BigDecimal.valueOf(-26000, 2),
                previouslyValidated = BigDecimal.valueOf(520)
            ),
            external = ExpenditureCostCategoryBreakdownLine(
                flatRate = null,
                totalEligibleBudget = BigDecimal.valueOf(130),
                previouslyReported = BigDecimal.valueOf(330),
                previouslyReportedParked = BigDecimal.valueOf(430),
                currentReport = BigDecimal.valueOf(23000, 2),
                currentReportReIncluded = BigDecimal.ZERO,
                totalEligibleAfterControl = BigDecimal.valueOf(430),
                totalReportedSoFar = BigDecimal.valueOf(56000, 2),
                totalReportedSoFarPercentage = BigDecimal.valueOf(43077, 2),
                remainingBudget = BigDecimal.valueOf(-43000, 2),
                previouslyValidated = BigDecimal.valueOf(530)
            ),
            equipment = ExpenditureCostCategoryBreakdownLine(
                flatRate = null,
                totalEligibleBudget = BigDecimal.valueOf(140),
                previouslyReported = BigDecimal.valueOf(340),
                previouslyReportedParked = BigDecimal.valueOf(440),
                currentReport = BigDecimal.valueOf(13500, 2),
                currentReportReIncluded = BigDecimal.ZERO,
                totalEligibleAfterControl = BigDecimal.valueOf(440),
                totalReportedSoFar = BigDecimal.valueOf(47500, 2),
                totalReportedSoFarPercentage = BigDecimal.valueOf(33929, 2),
                remainingBudget = BigDecimal.valueOf(-33500, 2),
                previouslyValidated = BigDecimal.valueOf(540)
            ),
            infrastructure = ExpenditureCostCategoryBreakdownLine(
                flatRate = null,
                totalEligibleBudget = BigDecimal.valueOf(150),
                previouslyReported = BigDecimal.valueOf(350),
                previouslyReportedParked = BigDecimal.valueOf(450),
                currentReport = BigDecimal.valueOf(12250, 2),
                currentReportReIncluded = BigDecimal.ZERO,
                totalEligibleAfterControl = BigDecimal.valueOf(450),
                totalReportedSoFar = BigDecimal.valueOf(47250, 2),
                totalReportedSoFarPercentage = BigDecimal.valueOf(31500, 2),
                remainingBudget = BigDecimal.valueOf(-32250, 2),
                previouslyValidated = BigDecimal.valueOf(550)
            ),
            other = ExpenditureCostCategoryBreakdownLine(
                flatRate = null,
                totalEligibleBudget = BigDecimal.valueOf(160),
                previouslyReported = BigDecimal.valueOf(360),
                previouslyReportedParked = BigDecimal.valueOf(460),
                currentReport = BigDecimal.ZERO,
                currentReportReIncluded = BigDecimal.ZERO,
                totalEligibleAfterControl = BigDecimal.valueOf(460),
                totalReportedSoFar = BigDecimal.valueOf(360),
                totalReportedSoFarPercentage = BigDecimal.valueOf(22500, 2),
                remainingBudget = BigDecimal.valueOf(-200),
                previouslyValidated = BigDecimal.valueOf(560)
            ),
            lumpSum = ExpenditureCostCategoryBreakdownLine(
                flatRate = null,
                totalEligibleBudget = BigDecimal.valueOf(170),
                previouslyReported = BigDecimal.valueOf(370),
                previouslyReportedParked = BigDecimal.valueOf(470),
                currentReport = BigDecimal.valueOf(2431, 2),
                currentReportReIncluded = BigDecimal.ZERO,
                totalEligibleAfterControl = BigDecimal.valueOf(470),
                totalReportedSoFar = BigDecimal.valueOf(39431, 2),
                totalReportedSoFarPercentage = BigDecimal.valueOf(23195, 2),
                remainingBudget = BigDecimal.valueOf(-22431, 2),
                previouslyValidated = BigDecimal.valueOf(570)
            ),
            unitCost = ExpenditureCostCategoryBreakdownLine(
                flatRate = null,
                totalEligibleBudget = BigDecimal.valueOf(180),
                previouslyReported = BigDecimal.valueOf(380),
                previouslyReportedParked = BigDecimal.valueOf(480),
                currentReport = BigDecimal.valueOf(14319, 2),
                currentReportReIncluded = BigDecimal.valueOf(3069, 2),
                totalEligibleAfterControl = BigDecimal.valueOf(480),
                totalReportedSoFar = BigDecimal.valueOf(52319, 2),
                totalReportedSoFarPercentage = BigDecimal.valueOf(29066, 2),
                remainingBudget = BigDecimal.valueOf(-34319, 2),
                previouslyValidated = BigDecimal.valueOf(580)
            ),
            total = ExpenditureCostCategoryBreakdownLine(
                flatRate = null,
                totalEligibleBudget = BigDecimal.valueOf(1260),
                previouslyReported = BigDecimal.valueOf(3060),
                previouslyReportedParked = BigDecimal.valueOf(490),
                currentReport = BigDecimal.valueOf(117500, 2),
                currentReportReIncluded = BigDecimal.valueOf(55069, 2),
                totalEligibleAfterControl = BigDecimal.valueOf(4160),
                totalReportedSoFar = BigDecimal.valueOf(423500, 2),
                totalReportedSoFarPercentage = BigDecimal.valueOf(33611, 2),
                remainingBudget = BigDecimal.valueOf(-297500, 2),
                previouslyValidated = BigDecimal.valueOf(590)
            ),
        )

        private val expectedOutputWhenSubmitted = expectedOutput.copy(
            staff = expectedOutput.staff.copy(
                currentReport = BigDecimal.valueOf(200),
                currentReportReIncluded = BigDecimal.valueOf(200),
                totalReportedSoFar = BigDecimal.valueOf(500),
                totalReportedSoFarPercentage = BigDecimal.valueOf(50000, 2),
                remainingBudget = BigDecimal.valueOf(-400),
            ),
            office = expectedOutput.office.copy(
                currentReport = BigDecimal.valueOf(210),
                currentReportReIncluded = BigDecimal.valueOf(210),
                totalReportedSoFar = BigDecimal.valueOf(520),
                totalReportedSoFarPercentage = BigDecimal.valueOf(47273, 2),
                remainingBudget = BigDecimal.valueOf(-410),
            ),
            travel = expectedOutput.travel.copy(
                currentReport = BigDecimal.valueOf(220),
                currentReportReIncluded = BigDecimal.valueOf(220),
                totalReportedSoFar = BigDecimal.valueOf(540),
                totalReportedSoFarPercentage = BigDecimal.valueOf(45000, 2),
                remainingBudget = BigDecimal.valueOf(-420),
            ),
            external = expectedOutput.external.copy(
                currentReport = BigDecimal.valueOf(230),
                currentReportReIncluded = BigDecimal.valueOf(230),
                totalReportedSoFar = BigDecimal.valueOf(560),
                totalReportedSoFarPercentage = BigDecimal.valueOf(43077, 2),
                remainingBudget = BigDecimal.valueOf(-430),
            ),
            equipment = expectedOutput.equipment.copy(
                currentReport = BigDecimal.valueOf(240),
                currentReportReIncluded = BigDecimal.valueOf(240),
                totalReportedSoFar = BigDecimal.valueOf(580),
                totalReportedSoFarPercentage = BigDecimal.valueOf(41429, 2),
                remainingBudget = BigDecimal.valueOf(-440),
            ),
            infrastructure = expectedOutput.infrastructure.copy(
                currentReport = BigDecimal.valueOf(250),
                currentReportReIncluded = BigDecimal.valueOf(250),
                totalReportedSoFar = BigDecimal.valueOf(600),
                totalReportedSoFarPercentage = BigDecimal.valueOf(40000, 2),
                remainingBudget = BigDecimal.valueOf(-450),
            ),
            other = expectedOutput.other.copy(
                currentReport = BigDecimal.valueOf(260),
                currentReportReIncluded = BigDecimal.valueOf(260),
                totalReportedSoFar = BigDecimal.valueOf(620),
                totalReportedSoFarPercentage = BigDecimal.valueOf(38750, 2),
                remainingBudget = BigDecimal.valueOf(-460),
            ),
            lumpSum = expectedOutput.lumpSum.copy(
                currentReport = BigDecimal.valueOf(270),
                currentReportReIncluded = BigDecimal.valueOf(270),
                totalReportedSoFar = BigDecimal.valueOf(640),
                totalReportedSoFarPercentage = BigDecimal.valueOf(37647, 2),
                remainingBudget = BigDecimal.valueOf(-470),
            ),
            unitCost = expectedOutput.unitCost.copy(
                currentReport = BigDecimal.valueOf(280),
                currentReportReIncluded = BigDecimal.valueOf(280),
                totalReportedSoFar = BigDecimal.valueOf(660),
                totalReportedSoFarPercentage = BigDecimal.valueOf(36667, 2),
                remainingBudget = BigDecimal.valueOf(-480),
            ),
            total = expectedOutput.total.copy(
                currentReport = BigDecimal.valueOf(2160),
                currentReportReIncluded = BigDecimal.valueOf(2160),
                totalReportedSoFar = BigDecimal.valueOf(5220),
                totalReportedSoFarPercentage = BigDecimal.valueOf(41429, 2),
                remainingBudget = BigDecimal.valueOf(-3960),
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
    fun `get - is not submitted - no office`() {
        every { reportPersistence.getPartnerReportStatusAndVersion(PARTNER_ID, reportId = 18L) } returns
            reportWithStatus(18L, status = ReportStatus.Draft)
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
        every { reportPersistence.getPartnerReportStatusAndVersion(PARTNER_ID, reportId = 19L) } returns
            reportWithStatus(19L, status = ReportStatus.Draft)
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

        assertThat(service.getSubmittedOrCalculateCurrent(PARTNER_ID, reportId = 19L)).isEqualTo(expectedOnDirectOutput)
    }

    @Test
    fun `get - is not submitted - office on staff`() {
        every { reportPersistence.getPartnerReportStatusAndVersion(PARTNER_ID, reportId = 20L) } returns
            reportWithStatus(20L, status = ReportStatus.Draft)
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

        assertThat(service.getSubmittedOrCalculateCurrent(PARTNER_ID, reportId = 20L)).isEqualTo(expectedOnStaffOutput)
    }

    @Test
    fun `get - is submitted`() {
        every { reportPersistence.getPartnerReportStatusAndVersion(PARTNER_ID, reportId = 25L) } returns
            reportWithStatus(25L, status = ReportStatus.Submitted)
        every { reportExpenditureCostCategoryPersistence.getCostCategories(PARTNER_ID, reportId = 25L) } returns data

        assertThat(service.getSubmittedOrCalculateCurrent(PARTNER_ID, reportId = 25L)).isEqualTo(expectedOutputWhenSubmitted)
        verify(exactly = 0) { reportExpenditurePersistence.getPartnerReportExpenditureCosts(PARTNER_ID, any()) }
        verify(exactly = 0) { currencyPersistence.findAllByIdYearAndIdMonth(any(), any()) }
    }

}
