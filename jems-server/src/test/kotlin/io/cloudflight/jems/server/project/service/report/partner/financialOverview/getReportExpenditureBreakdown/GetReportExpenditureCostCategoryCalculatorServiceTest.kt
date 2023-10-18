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
                staff = BigDecimal.valueOf(500),
                office = BigDecimal.valueOf(510),
                travel = BigDecimal.valueOf(520),
                external = BigDecimal.valueOf(530),
                equipment = BigDecimal.valueOf(540),
                infrastructure = BigDecimal.valueOf(550),
                other = BigDecimal.valueOf(560),
                lumpSum = BigDecimal.valueOf(570),
                unitCost = BigDecimal.valueOf(580),
                spfCost = BigDecimal.valueOf(590),
                sum = BigDecimal.valueOf(5450),
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
                spfCost = BigDecimal.valueOf(290),
                sum = BigDecimal.valueOf(2450),
            ),
            currentlyReportedParked = BudgetCostsCalculationResultFull(
                staff = BigDecimal.valueOf(50),
                office = BigDecimal.valueOf(60),
                travel = BigDecimal.valueOf(70),
                external = BigDecimal.valueOf(80),
                equipment = BigDecimal.valueOf(90),
                infrastructure = BigDecimal.valueOf(100),
                other = BigDecimal.valueOf(110),
                lumpSum = BigDecimal.valueOf(120),
                unitCost = BigDecimal.valueOf(130),
                spfCost = BigDecimal.valueOf(140),
                sum = BigDecimal.valueOf(950),
            ),
            currentlyReportedReIncluded = BudgetCostsCalculationResultFull(
                staff = BigDecimal.valueOf(20),
                office = BigDecimal.valueOf(21),
                travel = BigDecimal.valueOf(22),
                external = BigDecimal.valueOf(23),
                equipment = BigDecimal.valueOf(24),
                infrastructure = BigDecimal.valueOf(25),
                other = BigDecimal.valueOf(26),
                lumpSum = BigDecimal.valueOf(27),
                unitCost = BigDecimal.valueOf(28),
                spfCost = BigDecimal.valueOf(29),
                sum = BigDecimal.valueOf(245),
            ),
            totalEligibleAfterControl = BudgetCostsCalculationResultFull(
                staff = BigDecimal.valueOf(160),
                office = BigDecimal.valueOf(170),
                travel = BigDecimal.valueOf(180),
                external = BigDecimal.valueOf(190),
                equipment = BigDecimal.valueOf(200),
                infrastructure = BigDecimal.valueOf(210),
                other = BigDecimal.valueOf(220),
                lumpSum = BigDecimal.valueOf(230),
                unitCost = BigDecimal.valueOf(240),
                spfCost = BigDecimal.valueOf(250),
                sum = BigDecimal.valueOf(2050),
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
                spfCost = BigDecimal.valueOf(390),
                sum = BigDecimal.valueOf(3450),
            ),
            previouslyReportedParked = BudgetCostsCalculationResultFull(
                staff = BigDecimal.valueOf(30),
                office = BigDecimal.valueOf(31),
                travel = BigDecimal.valueOf(32),
                external = BigDecimal.valueOf(33),
                equipment = BigDecimal.valueOf(34),
                infrastructure = BigDecimal.valueOf(35),
                other = BigDecimal.valueOf(36),
                lumpSum = BigDecimal.valueOf(37),
                unitCost = BigDecimal.valueOf(38),
                spfCost = BigDecimal.valueOf(39),
                sum = BigDecimal.valueOf(345),
            ),
            previouslyValidated =  BudgetCostsCalculationResultFull(
                staff = BigDecimal.valueOf(400),
                office = BigDecimal.valueOf(410),
                travel = BigDecimal.valueOf(420),
                external = BigDecimal.valueOf(430),
                equipment = BigDecimal.valueOf(440),
                infrastructure = BigDecimal.valueOf(450),
                other = BigDecimal.valueOf(460),
                lumpSum = BigDecimal.valueOf(470),
                unitCost = BigDecimal.valueOf(480),
                spfCost = BigDecimal.valueOf(490),
                sum = BigDecimal.valueOf(4010),
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

        val expenditureSpfCost = ProjectPartnerReportExpenditureCost(
            id = 208L,
            number = 8,
            lumpSumId = null,
            unitCostId = null,
            costCategory = ReportBudgetCategory.SpfCosts,
            gdpr = false,
            investmentId = null,
            contractId = null,
            internalReferenceNumber = null,
            invoiceNumber = null,
            invoiceDate = TODAY.toLocalDate(),
            dateOfPayment = TODAY.toLocalDate(),
            numberOfUnits = BigDecimal.valueOf(6),
            pricePerUnit = BigDecimal.valueOf(32, 0),
            declaredAmount = BigDecimal.valueOf(192, 0),
            currencyCode = "CST",
            currencyConversionRate = null,
            declaredAmountAfterSubmission = null,
            attachment = null,
            parkingMetadata = null,
        )

        private val expectedOutput = ExpenditureCostCategoryBreakdown(
            staff = ExpenditureCostCategoryBreakdownLine(
                flatRate = null,
                totalEligibleBudget = BigDecimal.valueOf(500),
                previouslyReported = BigDecimal.valueOf(300),
                previouslyReportedParked = BigDecimal.valueOf(30),
                currentReport = BigDecimal.valueOf(40000L, 2),
                currentReportReIncluded = BigDecimal.valueOf(40000L, 2),
                totalEligibleAfterControl = BigDecimal.valueOf(160),
                totalReportedSoFar = BigDecimal.valueOf(70000L, 2),
                totalReportedSoFarPercentage = BigDecimal.valueOf(14000L, 2),
                remainingBudget = BigDecimal.valueOf(-20000, 2),
                previouslyValidated = BigDecimal.valueOf(400),
            ),
            office = ExpenditureCostCategoryBreakdownLine(
                flatRate = null,
                totalEligibleBudget = BigDecimal.valueOf(510),
                previouslyReported = BigDecimal.valueOf(310),
                previouslyReportedParked = BigDecimal.valueOf(31),
                currentReport = BigDecimal.valueOf(0),
                currentReportReIncluded = BigDecimal.valueOf(0),
                totalEligibleAfterControl = BigDecimal.valueOf(170),
                totalReportedSoFar = BigDecimal.valueOf(310),
                totalReportedSoFarPercentage = BigDecimal.valueOf(6078, 2),
                remainingBudget = BigDecimal.valueOf(200),
                previouslyValidated = BigDecimal.valueOf(410)
            ),
            travel = ExpenditureCostCategoryBreakdownLine(
                flatRate = 15,
                totalEligibleBudget = BigDecimal.valueOf(520),
                previouslyReported = BigDecimal.valueOf(320),
                previouslyReportedParked = BigDecimal.valueOf(32),
                currentReport = BigDecimal.valueOf(6000L, 2),
                currentReportReIncluded = BigDecimal.valueOf(6000L, 2),
                totalEligibleAfterControl = BigDecimal.valueOf(180),
                totalReportedSoFar = BigDecimal.valueOf(38000, 2),
                totalReportedSoFarPercentage = BigDecimal.valueOf(7308, 2),
                remainingBudget = BigDecimal.valueOf(14000, 2),
                previouslyValidated = BigDecimal.valueOf(420),
            ),
            external = ExpenditureCostCategoryBreakdownLine(
                flatRate = null,
                totalEligibleBudget = BigDecimal.valueOf(530),
                previouslyReported = BigDecimal.valueOf(330),
                previouslyReportedParked = BigDecimal.valueOf(33),
                currentReport = BigDecimal.valueOf(23000, 2),
                currentReportReIncluded = BigDecimal.valueOf(0),
                totalEligibleAfterControl = BigDecimal.valueOf(190),
                totalReportedSoFar = BigDecimal.valueOf(56000L, 2),
                totalReportedSoFarPercentage = BigDecimal.valueOf(10566, 2),
                remainingBudget = BigDecimal.valueOf(-3000L, 2),
                previouslyValidated = BigDecimal.valueOf(430),
            ),
            equipment = ExpenditureCostCategoryBreakdownLine(
                flatRate = null,
                totalEligibleBudget = BigDecimal.valueOf(540),
                previouslyReported = BigDecimal.valueOf(340),
                previouslyReportedParked = BigDecimal.valueOf(34),
                currentReport = BigDecimal.valueOf(13500, 2),
                currentReportReIncluded = BigDecimal.ZERO,
                totalEligibleAfterControl = BigDecimal.valueOf(200),
                totalReportedSoFar = BigDecimal.valueOf(47500, 2),
                totalReportedSoFarPercentage = BigDecimal.valueOf(8796, 2),
                remainingBudget = BigDecimal.valueOf(6500, 2),
                previouslyValidated = BigDecimal.valueOf(440),
            ),
            infrastructure = ExpenditureCostCategoryBreakdownLine(
                flatRate = null,
                totalEligibleBudget = BigDecimal.valueOf(550),
                previouslyReported = BigDecimal.valueOf(350),
                previouslyReportedParked = BigDecimal.valueOf(35),
                currentReport = BigDecimal.valueOf(12250, 2),
                currentReportReIncluded = BigDecimal.ZERO,
                totalEligibleAfterControl = BigDecimal.valueOf(210),
                totalReportedSoFar = BigDecimal.valueOf(47250, 2),
                totalReportedSoFarPercentage = BigDecimal.valueOf(8591, 2),
                remainingBudget = BigDecimal.valueOf(7750, 2),
                previouslyValidated = BigDecimal.valueOf(450),
            ),
            other = ExpenditureCostCategoryBreakdownLine(
                flatRate = null,
                totalEligibleBudget = BigDecimal.valueOf(560),
                previouslyReported = BigDecimal.valueOf(360),
                previouslyReportedParked = BigDecimal.valueOf(36),
                currentReport = BigDecimal.ZERO,
                currentReportReIncluded = BigDecimal.ZERO,
                totalEligibleAfterControl = BigDecimal.valueOf(220),
                totalReportedSoFar = BigDecimal.valueOf(360),
                totalReportedSoFarPercentage = BigDecimal.valueOf(6429, 2),
                remainingBudget = BigDecimal.valueOf(200),
                previouslyValidated = BigDecimal.valueOf(460),
            ),
            lumpSum = ExpenditureCostCategoryBreakdownLine(
                flatRate = null,
                totalEligibleBudget = BigDecimal.valueOf(570),
                previouslyReported = BigDecimal.valueOf(370),
                previouslyReportedParked = BigDecimal.valueOf(37),
                currentReport = BigDecimal.valueOf(2431, 2),
                currentReportReIncluded = BigDecimal.ZERO,
                totalEligibleAfterControl = BigDecimal.valueOf(230),
                totalReportedSoFar = BigDecimal.valueOf(39431, 2),
                totalReportedSoFarPercentage = BigDecimal.valueOf(6918, 2),
                remainingBudget = BigDecimal.valueOf(17569, 2),
                previouslyValidated = BigDecimal.valueOf(470),
            ),
            unitCost = ExpenditureCostCategoryBreakdownLine(
                flatRate = null,
                totalEligibleBudget = BigDecimal.valueOf(580),
                previouslyReported = BigDecimal.valueOf(380),
                previouslyReportedParked = BigDecimal.valueOf(38),
                currentReport = BigDecimal.valueOf(14319, 2),
                currentReportReIncluded = BigDecimal.valueOf(3069, 2),
                totalEligibleAfterControl = BigDecimal.valueOf(240),
                totalReportedSoFar = BigDecimal.valueOf(52319, 2),
                totalReportedSoFarPercentage = BigDecimal.valueOf(9021, 2),
                remainingBudget = BigDecimal.valueOf(5681, 2),
                previouslyValidated = BigDecimal.valueOf(480),
            ),
            spfCost = ExpenditureCostCategoryBreakdownLine(
                flatRate = null,
                totalEligibleBudget = BigDecimal.valueOf(590),
                previouslyReported = BigDecimal.valueOf(390),
                previouslyReportedParked = BigDecimal.valueOf(39),
                currentReport = BigDecimal.valueOf(9600L, 2),
                currentReportReIncluded = BigDecimal.valueOf(0),
                totalEligibleAfterControl = BigDecimal.valueOf(250),
                totalReportedSoFar = BigDecimal.valueOf(48600L, 2),
                totalReportedSoFarPercentage = BigDecimal.valueOf(8237L, 2),
                remainingBudget = BigDecimal.valueOf(10400L, 2),
                previouslyValidated = BigDecimal.valueOf(490),
            ),
            total = ExpenditureCostCategoryBreakdownLine(
                flatRate = null,
                totalEligibleBudget = BigDecimal.valueOf(5450),
                previouslyReported = BigDecimal.valueOf(3450),
                previouslyReportedParked = BigDecimal.valueOf(345),
                currentReport = BigDecimal.valueOf(121100, 2),
                currentReportReIncluded = BigDecimal.valueOf(49069, 2),
                totalEligibleAfterControl = BigDecimal.valueOf(2050),
                totalReportedSoFar = BigDecimal.valueOf(466100, 2),
                totalReportedSoFarPercentage = BigDecimal.valueOf(8552, 2),
                remainingBudget = BigDecimal.valueOf(78900, 2),
                previouslyValidated = BigDecimal.valueOf(4010),
            ),
        )

        private val expectedOnDirectOutput = ExpenditureCostCategoryBreakdown(
            staff = ExpenditureCostCategoryBreakdownLine(
                flatRate = null,
                totalEligibleBudget = BigDecimal.valueOf(500),
                previouslyReported = BigDecimal.valueOf(300),
                previouslyReportedParked = BigDecimal.valueOf(30),
                currentReport = BigDecimal.valueOf(40000, 2),
                currentReportReIncluded = BigDecimal.valueOf(40000, 2),
                totalEligibleAfterControl = BigDecimal.valueOf(160),
                totalReportedSoFar = BigDecimal.valueOf(70000, 2),
                totalReportedSoFarPercentage = BigDecimal.valueOf(14000, 2),
                remainingBudget = BigDecimal.valueOf(-20000, 2),
                previouslyValidated = BigDecimal.valueOf(400),
            ),
            office = ExpenditureCostCategoryBreakdownLine(
                flatRate = 10,
                totalEligibleBudget = BigDecimal.valueOf(510),
                previouslyReported = BigDecimal.valueOf(310),
                previouslyReportedParked = BigDecimal.valueOf(31),
                currentReport = BigDecimal.valueOf(9475, 2),
                currentReportReIncluded = BigDecimal.valueOf(4600, 2),
                totalEligibleAfterControl = BigDecimal.valueOf(170),
                totalReportedSoFar = BigDecimal.valueOf(40475, 2),
                totalReportedSoFarPercentage = BigDecimal.valueOf(7936L, 2),
                remainingBudget = BigDecimal.valueOf(10525L, 2),
                previouslyValidated = BigDecimal.valueOf(410),
            ),
            travel = ExpenditureCostCategoryBreakdownLine(
                flatRate = 15,
                totalEligibleBudget = BigDecimal.valueOf(520),
                previouslyReported = BigDecimal.valueOf(320),
                previouslyReportedParked = BigDecimal.valueOf(32),
                currentReport = BigDecimal.valueOf(6000, 2),
                currentReportReIncluded = BigDecimal.valueOf(6000, 2),
                totalEligibleAfterControl = BigDecimal.valueOf(180),
                totalReportedSoFar = BigDecimal.valueOf(38000, 2),
                totalReportedSoFarPercentage = BigDecimal.valueOf(7308, 2),
                remainingBudget = BigDecimal.valueOf(14000, 2),
                previouslyValidated = BigDecimal.valueOf(420),
            ),
            external = ExpenditureCostCategoryBreakdownLine(
                flatRate = null,
                totalEligibleBudget = BigDecimal.valueOf(530),
                previouslyReported = BigDecimal.valueOf(330),
                previouslyReportedParked = BigDecimal.valueOf(33),
                currentReport = BigDecimal.valueOf(23000, 2),
                currentReportReIncluded = BigDecimal.ZERO,
                totalEligibleAfterControl = BigDecimal.valueOf(190),
                totalReportedSoFar = BigDecimal.valueOf(56000, 2),
                totalReportedSoFarPercentage = BigDecimal.valueOf(10566L, 2),
                remainingBudget = BigDecimal.valueOf(-3000, 2),
                previouslyValidated = BigDecimal.valueOf(430),
            ),
            equipment = ExpenditureCostCategoryBreakdownLine(
                flatRate = null,
                totalEligibleBudget = BigDecimal.valueOf(540),
                previouslyReported = BigDecimal.valueOf(340),
                previouslyReportedParked = BigDecimal.valueOf(34),
                currentReport = BigDecimal.valueOf(13500, 2),
                currentReportReIncluded = BigDecimal.ZERO,
                totalEligibleAfterControl = BigDecimal.valueOf(200),
                totalReportedSoFar = BigDecimal.valueOf(47500, 2),
                totalReportedSoFarPercentage = BigDecimal.valueOf(8796L, 2),
                remainingBudget = BigDecimal.valueOf(6500, 2),
                previouslyValidated = BigDecimal.valueOf(440),
            ),
            infrastructure = ExpenditureCostCategoryBreakdownLine(
                flatRate = null,
                totalEligibleBudget = BigDecimal.valueOf(550),
                previouslyReported = BigDecimal.valueOf(350),
                previouslyReportedParked = BigDecimal.valueOf(35),
                currentReport = BigDecimal.valueOf(12250, 2),
                currentReportReIncluded = BigDecimal.ZERO,
                totalEligibleAfterControl = BigDecimal.valueOf(210),
                totalReportedSoFar = BigDecimal.valueOf(47250, 2),
                totalReportedSoFarPercentage = BigDecimal.valueOf(8591, 2),
                remainingBudget = BigDecimal.valueOf(7750, 2),
                previouslyValidated = BigDecimal.valueOf(450),
            ),
            other = ExpenditureCostCategoryBreakdownLine(
                flatRate = null,
                totalEligibleBudget = BigDecimal.valueOf(560),
                previouslyReported = BigDecimal.valueOf(360),
                previouslyReportedParked = BigDecimal.valueOf(36),
                currentReport = BigDecimal.ZERO,
                currentReportReIncluded = BigDecimal.ZERO,
                totalEligibleAfterControl = BigDecimal.valueOf(220),
                totalReportedSoFar = BigDecimal.valueOf(360),
                totalReportedSoFarPercentage = BigDecimal.valueOf(6429, 2),
                remainingBudget = BigDecimal.valueOf(200),
                previouslyValidated = BigDecimal.valueOf(460),
            ),
            lumpSum = ExpenditureCostCategoryBreakdownLine(
                flatRate = null,
                totalEligibleBudget = BigDecimal.valueOf(570),
                previouslyReported = BigDecimal.valueOf(370),
                previouslyReportedParked = BigDecimal.valueOf(37),
                currentReport = BigDecimal.valueOf(2431, 2),
                currentReportReIncluded = BigDecimal.ZERO,
                totalEligibleAfterControl = BigDecimal.valueOf(230),
                totalReportedSoFar = BigDecimal.valueOf(39431, 2),
                totalReportedSoFarPercentage = BigDecimal.valueOf(6918, 2),
                remainingBudget = BigDecimal.valueOf(17569, 2),
                previouslyValidated = BigDecimal.valueOf(470),
            ),
            unitCost = ExpenditureCostCategoryBreakdownLine(
                flatRate = null,
                totalEligibleBudget = BigDecimal.valueOf(580),
                previouslyReported = BigDecimal.valueOf(380),
                previouslyReportedParked = BigDecimal.valueOf(38),
                currentReport = BigDecimal.valueOf(14319, 2),
                currentReportReIncluded = BigDecimal.valueOf(3069, 2),
                totalEligibleAfterControl = BigDecimal.valueOf(240),
                totalReportedSoFar = BigDecimal.valueOf(52319, 2),
                totalReportedSoFarPercentage = BigDecimal.valueOf(9021, 2),
                remainingBudget = BigDecimal.valueOf(5681, 2),
                previouslyValidated = BigDecimal.valueOf(480),
            ),
            spfCost = ExpenditureCostCategoryBreakdownLine(
                flatRate = null,
                totalEligibleBudget = BigDecimal.valueOf(590),
                previouslyReported = BigDecimal.valueOf(390),
                previouslyReportedParked = BigDecimal.valueOf(39),
                currentReport = BigDecimal.valueOf(9600, 2),
                currentReportReIncluded = BigDecimal.valueOf(0),
                totalEligibleAfterControl = BigDecimal.valueOf(250),
                totalReportedSoFar = BigDecimal.valueOf(48600, 2),
                totalReportedSoFarPercentage = BigDecimal.valueOf(8237, 2),
                remainingBudget = BigDecimal.valueOf(10400, 2),
                previouslyValidated = BigDecimal.valueOf(490),
            ),
            total = ExpenditureCostCategoryBreakdownLine(
                flatRate = null,
                totalEligibleBudget = BigDecimal.valueOf(5450),
                previouslyReported = BigDecimal.valueOf(3450),
                previouslyReportedParked = BigDecimal.valueOf(345),
                currentReport = BigDecimal.valueOf(130575, 2),
                currentReportReIncluded = BigDecimal.valueOf(53669, 2),
                totalEligibleAfterControl = BigDecimal.valueOf(2050),
                totalReportedSoFar = BigDecimal.valueOf(475575, 2),
                totalReportedSoFarPercentage = BigDecimal.valueOf(8726L, 2),
                remainingBudget = BigDecimal.valueOf(69425L, 2),
                previouslyValidated = BigDecimal.valueOf(4010),
            ),
        )

        private val expectedOnStaffOutput = ExpenditureCostCategoryBreakdown(
            staff = expectedOutput.staff,
            office = expectedOutput.office.copy(
                flatRate = 15,
                currentReport = BigDecimal.valueOf(6000, 2),
                currentReportReIncluded = BigDecimal.valueOf(6000, 2),
                totalReportedSoFar = BigDecimal.valueOf(37000, 2),
                totalReportedSoFarPercentage = BigDecimal.valueOf(7255, 2),
                remainingBudget = BigDecimal.valueOf(14000, 2),
            ),
            travel = expectedOutput.travel,
            external = expectedOutput.external,
            equipment = expectedOutput.equipment,
            infrastructure = expectedOutput.infrastructure,
            other = expectedOutput.other,
            lumpSum = expectedOutput.lumpSum,
            unitCost = expectedOutput.unitCost,
            spfCost = expectedOutput.spfCost,
            total = expectedOutput.total.copy(
                currentReport = BigDecimal.valueOf(1271_00L, 2),
                currentReportReIncluded = BigDecimal.valueOf(550_69L, 2),
                totalReportedSoFar = BigDecimal.valueOf(4721_00, 2),
                totalReportedSoFarPercentage = BigDecimal.valueOf(86_62L, 2),
                remainingBudget = BigDecimal.valueOf(729_00L, 2),
            ),
        )

        private val expectedOutputWhenSubmitted = expectedOutput.copy(
            staff = expectedOutput.staff.copy(
                currentReport = BigDecimal.valueOf(200),
                currentReportReIncluded = BigDecimal.valueOf(20),
                totalReportedSoFar = BigDecimal.valueOf(500),
                totalReportedSoFarPercentage = BigDecimal.valueOf(10000L, 2),
                remainingBudget = BigDecimal.valueOf(0),
            ),
            office = expectedOutput.office.copy(
                currentReport = BigDecimal.valueOf(210),
                currentReportReIncluded = BigDecimal.valueOf(21),
                totalReportedSoFar = BigDecimal.valueOf(520),
                totalReportedSoFarPercentage = BigDecimal.valueOf(10196L, 2),
                remainingBudget = BigDecimal.valueOf(-10),
            ),
            travel = expectedOutput.travel.copy(
                currentReport = BigDecimal.valueOf(220),
                currentReportReIncluded = BigDecimal.valueOf(22),
                totalReportedSoFar = BigDecimal.valueOf(540),
                totalReportedSoFarPercentage = BigDecimal.valueOf(10385L, 2),
                remainingBudget = BigDecimal.valueOf(-20),
            ),
            external = expectedOutput.external.copy(
                currentReport = BigDecimal.valueOf(230),
                currentReportReIncluded = BigDecimal.valueOf(23),
                totalReportedSoFar = BigDecimal.valueOf(560),
                totalReportedSoFarPercentage = BigDecimal.valueOf(10566L, 2),
                remainingBudget = BigDecimal.valueOf(-30),
            ),
            equipment = expectedOutput.equipment.copy(
                currentReport = BigDecimal.valueOf(240),
                currentReportReIncluded = BigDecimal.valueOf(24),
                totalReportedSoFar = BigDecimal.valueOf(580),
                totalReportedSoFarPercentage = BigDecimal.valueOf(10741L, 2),
                remainingBudget = BigDecimal.valueOf(-40),
            ),
            infrastructure = expectedOutput.infrastructure.copy(
                currentReport = BigDecimal.valueOf(250),
                currentReportReIncluded = BigDecimal.valueOf(25),
                totalReportedSoFar = BigDecimal.valueOf(600),
                totalReportedSoFarPercentage = BigDecimal.valueOf(10909L, 2),
                remainingBudget = BigDecimal.valueOf(-50),
            ),
            other = expectedOutput.other.copy(
                currentReport = BigDecimal.valueOf(260),
                currentReportReIncluded = BigDecimal.valueOf(26),
                totalReportedSoFar = BigDecimal.valueOf(620),
                totalReportedSoFarPercentage = BigDecimal.valueOf(11071L, 2),
                remainingBudget = BigDecimal.valueOf(-60),
            ),
            lumpSum = expectedOutput.lumpSum.copy(
                currentReport = BigDecimal.valueOf(270),
                currentReportReIncluded = BigDecimal.valueOf(27),
                totalReportedSoFar = BigDecimal.valueOf(640),
                totalReportedSoFarPercentage = BigDecimal.valueOf(11228L, 2),
                remainingBudget = BigDecimal.valueOf(-70),
            ),
            unitCost = expectedOutput.unitCost.copy(
                currentReport = BigDecimal.valueOf(280),
                currentReportReIncluded = BigDecimal.valueOf(28),
                totalReportedSoFar = BigDecimal.valueOf(660),
                totalReportedSoFarPercentage = BigDecimal.valueOf(11379L, 2),
                remainingBudget = BigDecimal.valueOf(-80),
            ),
            spfCost = expectedOutput.spfCost.copy(
                currentReport = BigDecimal.valueOf(290),
                currentReportReIncluded = BigDecimal.valueOf(29),
                totalReportedSoFar = BigDecimal.valueOf(680),
                totalReportedSoFarPercentage = BigDecimal.valueOf(11525L, 2),
                remainingBudget = BigDecimal.valueOf(-90),
            ),
            total = expectedOutput.total.copy(
                currentReport = BigDecimal.valueOf(2450),
                currentReportReIncluded = BigDecimal.valueOf(245),
                totalReportedSoFar = BigDecimal.valueOf(5900),
                totalReportedSoFarPercentage = BigDecimal.valueOf(10826L, 2),
                remainingBudget = BigDecimal.valueOf(-450),
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
            expenditureSpfCost,
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
            expenditureSpfCost,
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
            expenditureSpfCost,
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
