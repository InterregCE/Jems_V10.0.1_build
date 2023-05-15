package io.cloudflight.jems.server.project.service.report.partner.financialOverview.getReportExpenditureInvestmentsBreakdown

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.currency.repository.CurrencyPersistence
import io.cloudflight.jems.server.currency.service.model.CurrencyConversion
import io.cloudflight.jems.server.project.service.report.model.partner.ProjectPartnerReportStatusAndVersion
import io.cloudflight.jems.server.project.service.report.model.partner.ReportStatus
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.ExpenditureParkingMetadata
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.ProjectPartnerReportExpenditureCost
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.ReportBudgetCategory
import io.cloudflight.jems.server.project.service.report.model.partner.financialOverview.investments.ExpenditureInvestmentBreakdown
import io.cloudflight.jems.server.project.service.report.model.partner.financialOverview.investments.ExpenditureInvestmentBreakdownLine
import io.cloudflight.jems.server.project.service.report.partner.ProjectPartnerReportPersistence
import io.cloudflight.jems.server.project.service.report.partner.expenditure.ProjectPartnerReportExpenditurePersistence
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.ProjectPartnerReportInvestmentPersistence
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.getReportExpenditureInvestementsBreakdown.GetReportExpenditureInvestmentsBreakdownCalculator
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import java.math.BigDecimal
import java.time.LocalDate

class GetReportExpenditureInvestmentsBreakdownCalculatorTest : UnitTest() {

    companion object {
        private val YEAR = LocalDate.now().year
        private val MONTH = LocalDate.now().monthValue

        private fun report(status: ReportStatus) = ProjectPartnerReportStatusAndVersion(
            status = status,
            version = "V_4.5",
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
            totalEligibleAfterControl = BigDecimal.ZERO,
            previouslyReportedParked = BigDecimal.ZERO,
            currentReportReIncluded = BigDecimal.ZERO,
            deactivated = false,
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
            totalEligibleAfterControl = BigDecimal.ZERO,
            previouslyReportedParked = BigDecimal.ZERO,
            currentReportReIncluded = BigDecimal.ZERO,
            deactivated = true,
        )

        private val expenditureWithInvestment = ProjectPartnerReportExpenditureCost(
            id = 205L,
            number = 1,
            lumpSumId = null,
            unitCostId = null,
            costCategory = ReportBudgetCategory.EquipmentCosts,
            gdpr = false,
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
            parkingMetadata = null,
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
                    currentReport = BigDecimal.valueOf(30858, 2),
                    totalEligibleAfterControl = BigDecimal.ZERO,
                    totalReportedSoFar = BigDecimal.valueOf(50858, 2),
                    totalReportedSoFarPercentage = BigDecimal.valueOf(16953, 2),
                    remainingBudget = BigDecimal.valueOf(-20858, 2),
                    previouslyReportedParked = BigDecimal.ZERO,
                    currentReportReIncluded = BigDecimal.valueOf(154.29),
                    deactivated = false,
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
                    totalEligibleAfterControl = BigDecimal.ZERO,
                    totalReportedSoFar = BigDecimal.valueOf(40L),
                    totalReportedSoFarPercentage = BigDecimal.valueOf(6667, 2),
                    remainingBudget = BigDecimal.valueOf(20),
                    previouslyReportedParked = BigDecimal.valueOf(123),
                    currentReportReIncluded = BigDecimal.ZERO,
                    deactivated = true,
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
                currentReport = BigDecimal.valueOf(30858, 2),
                totalEligibleAfterControl = BigDecimal.ZERO,
                totalReportedSoFar = BigDecimal.valueOf(54858, 2),
                totalReportedSoFarPercentage = BigDecimal.valueOf(15238, 2),
                remainingBudget = BigDecimal.valueOf(-18858, 2),
                previouslyReportedParked = BigDecimal.valueOf(123),
                currentReportReIncluded = BigDecimal.valueOf(154.29),
                deactivated = false,
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
                    totalEligibleAfterControl = BigDecimal.valueOf(80),
                    totalReportedSoFar = BigDecimal.valueOf(300),
                    totalReportedSoFarPercentage = BigDecimal.valueOf(10000, 2),
                    remainingBudget = BigDecimal.ZERO,
                    previouslyReportedParked = BigDecimal.ZERO,
                    currentReportReIncluded = BigDecimal.ZERO,
                    deactivated = false,
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
                    totalEligibleAfterControl = BigDecimal.valueOf(16),
                    totalReportedSoFar = BigDecimal.valueOf(60L),
                    totalReportedSoFarPercentage = BigDecimal.valueOf(10000, 2),
                    remainingBudget = BigDecimal.ZERO,
                    previouslyReportedParked = BigDecimal.valueOf(120),
                    currentReportReIncluded = BigDecimal.ZERO,
                    deactivated = true,
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
                totalEligibleAfterControl = BigDecimal.valueOf(96),
                totalReportedSoFar = BigDecimal.valueOf(360),
                totalReportedSoFarPercentage = BigDecimal.valueOf(10000, 2),
                remainingBudget = BigDecimal.ZERO,
                previouslyReportedParked = BigDecimal.valueOf(120),
                currentReportReIncluded = BigDecimal.ZERO,
                deactivated = false,
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
    lateinit var expenditureInvestmentPersistence: ProjectPartnerReportInvestmentPersistence
    @MockK
    lateinit var reportExpenditurePersistence: ProjectPartnerReportExpenditurePersistence
    @MockK
    lateinit var currencyPersistence: CurrencyPersistence
    @MockK
    lateinit var reportPersistence: ProjectPartnerReportPersistence

    @InjectMockKs
    lateinit var interactor: GetReportExpenditureInvestmentsBreakdownCalculator

    @BeforeEach
    fun resetMocks() {
        clearMocks(expenditureInvestmentPersistence)
        clearMocks(reportExpenditurePersistence)
        clearMocks(currencyPersistence)
        clearMocks(reportPersistence)
    }

    @ParameterizedTest(name = "get open (status {0})")
    @EnumSource(value = ReportStatus::class, names = ["Draft", "ReOpenSubmittedLast", "ReOpenInControlLast"])
    fun getOpen(status: ReportStatus) {
        val reportId = 1L
        val partnerId = 2L

        every { reportPersistence.getPartnerReportStatusAndVersion(partnerId = partnerId, reportId) } returns report(status)

        every { expenditureInvestmentPersistence.getInvestments(partnerId, reportId) } returns
            listOf(
                investment_1.copy(
                    currentReport = BigDecimal.ZERO,
                    currentReportReIncluded = BigDecimal.valueOf(154.29)
                ),
                investment_2.copy(currentReport = BigDecimal.ZERO, previouslyReportedParked = BigDecimal.valueOf(123))
            )
        every { currencyPersistence.findAllByIdYearAndIdMonth(YEAR, MONTH) } returns listOf(currency)

        every { reportExpenditurePersistence.getPartnerReportExpenditureCosts(partnerId, reportId) } returns
            listOf(
                expenditureWithInvestment,
                expenditureWithInvestment.copy(
                parkingMetadata = ExpenditureParkingMetadata(
                    reportOfOriginId = 70L,
                    reportOfOriginNumber = 5,
                    originalExpenditureNumber = 3
                ),
                currencyConversionRate = BigDecimal.valueOf(175, 2)
            ))

        assertThat(interactor.get(partnerId, reportId = reportId)).isEqualTo(expectedDraftResult)
    }

    @ParameterizedTest(name = "get closed (status {0})")
    @EnumSource(value = ReportStatus::class, names = ["Draft", "ReOpenSubmittedLast", "ReOpenInControlLast"], mode = EnumSource.Mode.EXCLUDE)
    fun getClosed(status: ReportStatus) {
        val reportId = 3L
        val partnerId = 4L

        every { reportPersistence.getPartnerReportStatusAndVersion(partnerId = partnerId, reportId) } returns report(status)
        every { expenditureInvestmentPersistence.getInvestments(partnerId, reportId) } returns
            listOf(
                investment_1.copy(totalEligibleAfterControl = BigDecimal.valueOf(80L)),
                investment_2.copy(totalEligibleAfterControl = BigDecimal.valueOf(16L), previouslyReportedParked = BigDecimal.valueOf(120)),
            )
        assertThat(interactor.get(partnerId, reportId = reportId)).isEqualTo(expectedNonDraftResult.copy())
    }

}
