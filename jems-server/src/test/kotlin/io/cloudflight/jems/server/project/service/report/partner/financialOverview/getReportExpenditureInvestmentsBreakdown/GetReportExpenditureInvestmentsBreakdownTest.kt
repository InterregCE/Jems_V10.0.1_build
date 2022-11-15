package io.cloudflight.jems.server.project.service.report.partner.financialOverview.getReportExpenditureInvestmentsBreakdown

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.currency.repository.CurrencyPersistence
import io.cloudflight.jems.server.currency.service.model.CurrencyConversion
import io.cloudflight.jems.server.project.service.report.ProjectReportPersistence
import io.cloudflight.jems.server.project.service.report.model.partner.ProjectPartnerReport
import io.cloudflight.jems.server.project.service.report.model.partner.ReportStatus
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.ProjectPartnerReportExpenditureCost
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.ReportBudgetCategory
import io.cloudflight.jems.server.project.service.report.model.partner.financialOverview.investments.ExpenditureInvestmentBreakdown
import io.cloudflight.jems.server.project.service.report.model.partner.financialOverview.investments.ExpenditureInvestmentBreakdownLine
import io.cloudflight.jems.server.project.service.report.partner.expenditure.ProjectReportExpenditurePersistence
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.ProjectReportInvestmentPersistence
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.getReportExpenditureInvestementsBreakdown.GetReportExpenditureInvestmentsBreakdown
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

class GetReportExpenditureInvestmentsBreakdownTest : UnitTest() {

    companion object {
        private val LAST_YEAR = ZonedDateTime.now().minusYears(1)

        private val YEAR = LocalDate.now().year
        private val MONTH = LocalDate.now().monthValue

        private fun report(id: Long, status: ReportStatus) = ProjectPartnerReport(
            id = id,
            reportNumber = 1,
            status = status,
            version = "V_4.5",
            identification = mockk(),
            firstSubmission = LAST_YEAR,
        )

        private val investment_1 = ExpenditureInvestmentBreakdownLine(
            reportInvestmentId = 1L,
            investmentId = 101L,
            investmentNumber = 11,
            workPackageNumber = 5,
            title = setOf(InputTranslation(SystemLanguage.EN, "title EN 1")),
            totalEligibleBudget = BigDecimal.valueOf(300L),
            previouslyReported = BigDecimal.valueOf(200L),
            currentReport = BigDecimal.valueOf(100L),
        )

        private val investment_2 = ExpenditureInvestmentBreakdownLine(
            reportInvestmentId = 2L,
            investmentId = 102L,
            investmentNumber = 12,
            workPackageNumber = 5,
            title = setOf(InputTranslation(SystemLanguage.EN, "title EN 2")),
            totalEligibleBudget = BigDecimal.valueOf(60L),
            previouslyReported = BigDecimal.valueOf(40L),
            currentReport = BigDecimal.valueOf(20L),
        )

        private val expenditureWithInvestment = ProjectPartnerReportExpenditureCost(
            id = 205L,
            lumpSumId = null,
            unitCostId = null,
            costCategory = ReportBudgetCategory.EquipmentCosts,
            investmentId = 1L,
            contractId = null,
            internalReferenceNumber = null,
            invoiceNumber = null,
            invoiceDate = null,
            dateOfPayment = null,
            numberOfUnits = BigDecimal.valueOf(2),
            pricePerUnit = BigDecimal.valueOf(135, 0),
            declaredAmount = BigDecimal.valueOf(270, 0),
            currencyCode = "PLN",
            currencyConversionRate = null,
            declaredAmountAfterSubmission = null,
            attachment = null,
        )

        private val expectedDraftResult = ExpenditureInvestmentBreakdown(
            investments = listOf(
                ExpenditureInvestmentBreakdownLine(
                    reportInvestmentId = 1L,
                    investmentId = 101L,
                    investmentNumber = 11,
                    workPackageNumber = 5,
                    title = setOf(InputTranslation(SystemLanguage.EN, "title EN 1")),
                    totalEligibleBudget = BigDecimal.valueOf(300L),
                    previouslyReported = BigDecimal.valueOf(200L),
                    currentReport = BigDecimal.valueOf(15429, 2),
                    totalReportedSoFar = BigDecimal.valueOf(35429, 2),
                    totalReportedSoFarPercentage = BigDecimal.valueOf(11810, 2),
                    remainingBudget = BigDecimal.valueOf(-5429, 2),
                ),
                ExpenditureInvestmentBreakdownLine(
                    reportInvestmentId = 2L,
                    investmentId = 102L,
                    investmentNumber = 12,
                    workPackageNumber = 5,
                    title = setOf(InputTranslation(SystemLanguage.EN, "title EN 2")),
                    totalEligibleBudget = BigDecimal.valueOf(60L),
                    previouslyReported = BigDecimal.valueOf(40L),
                    currentReport = BigDecimal.ZERO,
                    totalReportedSoFar = BigDecimal.valueOf(40L),
                    totalReportedSoFarPercentage = BigDecimal.valueOf(6667, 2),
                    remainingBudget = BigDecimal.valueOf(20),
                ),
            ),
            total = ExpenditureInvestmentBreakdownLine(
                reportInvestmentId = 0L,
                investmentId = 0L,
                investmentNumber = 0,
                workPackageNumber = 0,
                title = emptySet(),
                totalEligibleBudget = BigDecimal.valueOf(360L),
                previouslyReported = BigDecimal.valueOf(240L),
                currentReport = BigDecimal.valueOf(15429, 2),
                totalReportedSoFar = BigDecimal.valueOf(39429, 2),
                totalReportedSoFarPercentage = BigDecimal.valueOf(10953, 2),
                remainingBudget = BigDecimal.valueOf(-3429, 2),
            )
        )

        private val expectedNonDraftResult = ExpenditureInvestmentBreakdown(
            investments = listOf(
                ExpenditureInvestmentBreakdownLine(
                    reportInvestmentId = 1L,
                    investmentId = 101L,
                    investmentNumber = 11,
                    workPackageNumber = 5,
                    title = setOf(InputTranslation(SystemLanguage.EN, "title EN 1")),
                    totalEligibleBudget = BigDecimal.valueOf(300L),
                    previouslyReported = BigDecimal.valueOf(200L),
                    currentReport = BigDecimal.valueOf(100),
                    totalReportedSoFar = BigDecimal.valueOf(300),
                    totalReportedSoFarPercentage = BigDecimal.valueOf(10000, 2),
                    remainingBudget = BigDecimal.ZERO,
                ),
                ExpenditureInvestmentBreakdownLine(
                    reportInvestmentId = 2L,
                    investmentId = 102L,
                    investmentNumber = 12,
                    workPackageNumber = 5,
                    title = setOf(InputTranslation(SystemLanguage.EN, "title EN 2")),
                    totalEligibleBudget = BigDecimal.valueOf(60L),
                    previouslyReported = BigDecimal.valueOf(40L),
                    currentReport = BigDecimal.valueOf(20L),
                    totalReportedSoFar = BigDecimal.valueOf(60L),
                    totalReportedSoFarPercentage = BigDecimal.valueOf(10000, 2),
                    remainingBudget = BigDecimal.ZERO,
                ),
            ),
            total = ExpenditureInvestmentBreakdownLine(
                reportInvestmentId = 0L,
                investmentId = 0L,
                investmentNumber = 0,
                workPackageNumber = 0,
                title = emptySet(),
                totalEligibleBudget = BigDecimal.valueOf(360L),
                previouslyReported = BigDecimal.valueOf(240L),
                currentReport = BigDecimal.valueOf(120),
                totalReportedSoFar = BigDecimal.valueOf(360),
                totalReportedSoFarPercentage = BigDecimal.valueOf(10000, 2),
                remainingBudget = BigDecimal.ZERO,
            ),
        )

        private val currency = CurrencyConversion(
            code = "PLN",
            year = YEAR,
            month = MONTH,
            name = "test",
            conversionRate = BigDecimal.valueOf(175, 2),
        )
    }

    @MockK
    lateinit var expenditureInvestmentPersistence: ProjectReportInvestmentPersistence
    @MockK
    lateinit var reportExpenditurePersistence: ProjectReportExpenditurePersistence
    @MockK
    lateinit var currencyPersistence: CurrencyPersistence
    @MockK
    lateinit var reportPersistence: ProjectReportPersistence

    @InjectMockKs
    lateinit var interactor: GetReportExpenditureInvestmentsBreakdown

    @BeforeEach
    fun resetMocks() {
        clearMocks(expenditureInvestmentPersistence)
        clearMocks(reportExpenditurePersistence)
        clearMocks(currencyPersistence)
        clearMocks(reportPersistence)
    }

    @ParameterizedTest(name = "get open (status {0})")
    @EnumSource(value = ReportStatus::class, names = ["Draft"])
    fun getOpen(status: ReportStatus) {
        val reportId = 1L
        val partnerId = 2L

        every { reportPersistence.getPartnerReportById(partnerId = partnerId, reportId) } returns report(reportId, status)

        every { expenditureInvestmentPersistence.getInvestments(partnerId, reportId) } returns
            listOf(investment_1.copy(currentReport = BigDecimal.ZERO), investment_2.copy(currentReport = BigDecimal.ZERO))
        every { reportExpenditurePersistence.getPartnerReportExpenditureCosts(partnerId, reportId) } returns
            listOf(expenditureWithInvestment)
        every { currencyPersistence.findAllByIdYearAndIdMonth(YEAR, MONTH) } returns listOf(currency)

        assertThat(interactor.get(partnerId, reportId = reportId)).isEqualTo(expectedDraftResult.copy())
    }

    @ParameterizedTest(name = "get closed (status {0})")
    @EnumSource(value = ReportStatus::class, names = ["Draft"], mode = EnumSource.Mode.EXCLUDE)
    fun getClosed(status: ReportStatus) {
        val reportId = 3L
        val partnerId = 4L

        every { reportPersistence.getPartnerReportById(partnerId = partnerId, reportId) } returns report(reportId, status)

        every { expenditureInvestmentPersistence.getInvestments(partnerId, reportId) } returns
            listOf(investment_1, investment_2)

        assertThat(interactor.get(partnerId, reportId = reportId)).isEqualTo(expectedNonDraftResult.copy())
    }

}
