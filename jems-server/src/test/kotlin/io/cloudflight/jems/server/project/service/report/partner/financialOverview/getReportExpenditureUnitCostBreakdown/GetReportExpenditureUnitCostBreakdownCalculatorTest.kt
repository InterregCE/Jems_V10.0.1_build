package io.cloudflight.jems.server.project.service.report.partner.financialOverview.getReportExpenditureUnitCostBreakdown

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.currency.repository.CurrencyPersistence
import io.cloudflight.jems.server.currency.service.model.CurrencyConversion
import io.cloudflight.jems.server.project.service.report.partner.ProjectPartnerReportPersistence
import io.cloudflight.jems.server.project.service.report.model.partner.ProjectPartnerReport
import io.cloudflight.jems.server.project.service.report.model.partner.ReportStatus
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.ProjectPartnerReportExpenditureCost
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.ReportBudgetCategory
import io.cloudflight.jems.server.project.service.report.model.partner.financialOverview.unitCost.ExpenditureUnitCostBreakdown
import io.cloudflight.jems.server.project.service.report.model.partner.financialOverview.unitCost.ExpenditureUnitCostBreakdownLine
import io.cloudflight.jems.server.project.service.report.partner.expenditure.ProjectPartnerReportExpenditurePersistence
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.ProjectPartnerReportUnitCostPersistence
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
import java.time.LocalDate
import java.time.ZonedDateTime

internal class GetReportExpenditureUnitCostBreakdownCalculatorTest : UnitTest() {

    companion object {
        private const val PARTNER_ID = 591L
        private val LAST_YEAR = ZonedDateTime.now().minusYears(1)
        private val YEAR = LocalDate.now().year
        private val MONTH = LocalDate.now().monthValue

        private fun report(id: Long, status: ReportStatus) =
            ProjectPartnerReport(
                id = id,
                reportNumber = 6,
                status = status,
                version = "V_1.1",
                identification = mockk(),
                firstSubmission = LAST_YEAR,
            )

        private val unitCost_1 = ExpenditureUnitCostBreakdownLine(
            reportUnitCostId = 1L,
            unitCostId = 101L,
            name = setOf(InputTranslation(SystemLanguage.GA, "name 1 GA")),
            totalEligibleBudget = BigDecimal.valueOf(52),
            previouslyReported = BigDecimal.valueOf(23),
            currentReport = BigDecimal.valueOf(39, 1),
        )

        private val unitCost_2 = ExpenditureUnitCostBreakdownLine(
            reportUnitCostId = 2L,
            unitCostId = 102L,
            name = setOf(InputTranslation(SystemLanguage.MK, "name 2 MK")),
            totalEligibleBudget = BigDecimal.valueOf(18),
            previouslyReported = BigDecimal.valueOf(7),
            currentReport = BigDecimal.valueOf(11, 1),
        )

        private val expenditureWithUnitCost = ProjectPartnerReportExpenditureCost(
            id = 2965L,
            lumpSumId = null,
            unitCostId = 1L,
            costCategory = ReportBudgetCategory.StaffCosts,
            investmentId = null,
            contractId = null,
            internalReferenceNumber = null,
            invoiceNumber = null,
            invoiceDate = null,
            dateOfPayment = null,
            totalValueInvoice = null,
            vat = null,
            numberOfUnits = BigDecimal.ONE,
            pricePerUnit = BigDecimal.valueOf(11),
            declaredAmount = BigDecimal.valueOf(11),
            currencyCode = "GBP",
            currencyConversionRate = null,
            declaredAmountAfterSubmission = null /* this value needs to be filled in */,
            attachment = null,
        )

        private val expectedDraftBreakdown = ExpenditureUnitCostBreakdown(
            unitCosts = listOf(
                ExpenditureUnitCostBreakdownLine(
                    reportUnitCostId = 1L,
                    unitCostId = 101L,
                    name = setOf(InputTranslation(SystemLanguage.GA, "name 1 GA")),
                    totalEligibleBudget = BigDecimal.valueOf(52),
                    previouslyReported = BigDecimal.valueOf(23),
                    currentReport = BigDecimal.valueOf(1264, 2),
                    totalReportedSoFar = BigDecimal.valueOf(3564, 2),
                    totalReportedSoFarPercentage = BigDecimal.valueOf(6854, 2),
                    remainingBudget = BigDecimal.valueOf(1636, 2),
                ),
                ExpenditureUnitCostBreakdownLine(
                    reportUnitCostId = 2L,
                    unitCostId = 102L,
                    name = setOf(InputTranslation(SystemLanguage.MK, "name 2 MK")),
                    totalEligibleBudget = BigDecimal.valueOf(18),
                    previouslyReported = BigDecimal.valueOf(7),
                    currentReport = BigDecimal.ZERO,
                    totalReportedSoFar = BigDecimal.valueOf(7),
                    totalReportedSoFarPercentage = BigDecimal.valueOf(3889, 2),
                    remainingBudget = BigDecimal.valueOf(11),
                ),
            ),
            total = ExpenditureUnitCostBreakdownLine(
                reportUnitCostId = 0L,
                unitCostId = 0L,
                name = emptySet(),
                totalEligibleBudget = BigDecimal.valueOf(70),
                previouslyReported = BigDecimal.valueOf(30),
                currentReport = BigDecimal.valueOf(1264, 2),
                totalReportedSoFar = BigDecimal.valueOf(4264, 2),
                totalReportedSoFarPercentage = BigDecimal.valueOf(6091, 2),
                remainingBudget = BigDecimal.valueOf(2736, 2),
            ),
        )

        private val expectedNonDraftBreakdown = ExpenditureUnitCostBreakdown(
            unitCosts = listOf(
                ExpenditureUnitCostBreakdownLine(
                    reportUnitCostId = 1L,
                    unitCostId = 101L,
                    name = setOf(InputTranslation(SystemLanguage.GA, "name 1 GA")),
                    totalEligibleBudget = BigDecimal.valueOf(52),
                    previouslyReported = BigDecimal.valueOf(23),
                    currentReport = BigDecimal.valueOf(39, 1),
                    totalReportedSoFar = BigDecimal.valueOf(269, 1),
                    totalReportedSoFarPercentage = BigDecimal.valueOf(5173, 2),
                    remainingBudget = BigDecimal.valueOf(251, 1),
                ),
                ExpenditureUnitCostBreakdownLine(
                    reportUnitCostId = 2L,
                    unitCostId = 102L,
                    name = setOf(InputTranslation(SystemLanguage.MK, "name 2 MK")),
                    totalEligibleBudget = BigDecimal.valueOf(18),
                    previouslyReported = BigDecimal.valueOf(7),
                    currentReport = BigDecimal.valueOf(11, 1),
                    totalReportedSoFar = BigDecimal.valueOf(81, 1),
                    totalReportedSoFarPercentage = BigDecimal.valueOf(4500, 2),
                    remainingBudget = BigDecimal.valueOf(99, 1),
                ),
            ),
            total = ExpenditureUnitCostBreakdownLine(
                reportUnitCostId = 0L,
                unitCostId = 0L,
                name = emptySet(),
                totalEligibleBudget = BigDecimal.valueOf(70),
                previouslyReported = BigDecimal.valueOf(30),
                currentReport = BigDecimal.valueOf(50, 1),
                totalReportedSoFar = BigDecimal.valueOf(350, 1),
                totalReportedSoFarPercentage = BigDecimal.valueOf(5000, 2),
                remainingBudget = BigDecimal.valueOf(350, 1),
            ),
        )

    }

    @MockK
    lateinit var reportPersistence: ProjectPartnerReportPersistence
    @MockK
    lateinit var reportUnitCostPersistence: ProjectPartnerReportUnitCostPersistence
    @MockK
    lateinit var reportExpenditurePersistence: ProjectPartnerReportExpenditurePersistence
    @MockK
    lateinit var currencyPersistence: CurrencyPersistence

    @InjectMockKs
    lateinit var calculator: GetReportExpenditureUnitCostBreakdownCalculator

    @BeforeEach
    fun resetMocks() {
        clearMocks(reportPersistence)
        clearMocks(reportUnitCostPersistence)
        clearMocks(reportExpenditurePersistence)
        clearMocks(currencyPersistence)
    }

    @ParameterizedTest(name = "get open (status {0})")
    @EnumSource(value = ReportStatus::class, names = ["Draft"])
    fun getOpen(status: ReportStatus) {
        val reportId = 97658L
        every { reportPersistence.getPartnerReportById(partnerId = PARTNER_ID, reportId) } returns
            report(reportId, status)
        every { reportUnitCostPersistence.getUnitCost(partnerId = PARTNER_ID, reportId = reportId) } returns
            listOf(unitCost_1.copy(currentReport = BigDecimal.ZERO), unitCost_2.copy(currentReport = BigDecimal.ZERO))
        every { reportExpenditurePersistence.getPartnerReportExpenditureCosts(partnerId = PARTNER_ID, reportId = reportId) } returns
            listOf(expenditureWithUnitCost)
        every { currencyPersistence.findAllByIdYearAndIdMonth(YEAR, MONTH) } returns
            listOf(CurrencyConversion("GBP", YEAR, MONTH, "", BigDecimal.valueOf(87, 2)))
        assertThat(calculator.get(PARTNER_ID, reportId = reportId)).isEqualTo(expectedDraftBreakdown.copy())
    }

    @ParameterizedTest(name = "get closed (status {0})")
    @EnumSource(value = ReportStatus::class, names = ["Draft"], mode = EnumSource.Mode.EXCLUDE)
    fun getClosed(status: ReportStatus) {
        val reportId = 97658L
        every { reportPersistence.getPartnerReportById(partnerId = PARTNER_ID, reportId) } returns
            report(reportId, status)
        every { reportUnitCostPersistence.getUnitCost(partnerId = PARTNER_ID, reportId = reportId) } returns
            listOf(unitCost_1, unitCost_2)
        assertThat(calculator.get(PARTNER_ID, reportId = reportId)).isEqualTo(expectedNonDraftBreakdown.copy())
    }

}
