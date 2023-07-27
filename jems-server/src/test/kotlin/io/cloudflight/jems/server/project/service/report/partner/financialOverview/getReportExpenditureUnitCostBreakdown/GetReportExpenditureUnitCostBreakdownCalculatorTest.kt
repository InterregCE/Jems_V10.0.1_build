package io.cloudflight.jems.server.project.service.report.partner.financialOverview.getReportExpenditureUnitCostBreakdown

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
import io.cloudflight.jems.server.project.service.report.model.partner.financialOverview.unitCost.ExpenditureUnitCostBreakdown
import io.cloudflight.jems.server.project.service.report.model.partner.financialOverview.unitCost.ExpenditureUnitCostBreakdownLine
import io.cloudflight.jems.server.project.service.report.partner.ProjectPartnerReportPersistence
import io.cloudflight.jems.server.project.service.report.partner.expenditure.ProjectPartnerReportExpenditurePersistence
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.ProjectPartnerReportUnitCostPersistence
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

internal class GetReportExpenditureUnitCostBreakdownCalculatorTest : UnitTest() {

    companion object {
        private const val PARTNER_ID = 591L
        private val YEAR = LocalDate.now().year
        private val MONTH = LocalDate.now().monthValue

        private fun report(reportId: Long, status: ReportStatus) =
            ProjectPartnerReportStatusAndVersion(
                reportId = reportId,
                status = status,
                version = "V_1.1",
            )

        private val unitCost_1 = ExpenditureUnitCostBreakdownLine(
            reportUnitCostId = 1L,
            unitCostId = 101L,
            name = setOf(InputTranslation(SystemLanguage.GA, "name 1 GA")),
            totalEligibleBudget = BigDecimal.valueOf(52),
            previouslyReported = BigDecimal.valueOf(23),
            currentReport = BigDecimal.valueOf(39, 1),
            totalEligibleAfterControl = BigDecimal.ZERO,
            previouslyReportedParked = BigDecimal.ZERO,
            currentReportReIncluded = BigDecimal.ZERO,
            previouslyValidated = BigDecimal.valueOf(5)
        )

        private val unitCost_2 = ExpenditureUnitCostBreakdownLine(
            reportUnitCostId = 2L,
            unitCostId = 102L,
            name = setOf(InputTranslation(SystemLanguage.MK, "name 2 MK")),
            totalEligibleBudget = BigDecimal.valueOf(18),
            previouslyReported = BigDecimal.valueOf(7),
            currentReport = BigDecimal.valueOf(11, 1),
            totalEligibleAfterControl = BigDecimal.ZERO,
            previouslyReportedParked = BigDecimal.ZERO,
            currentReportReIncluded = BigDecimal.ZERO,
            previouslyValidated = BigDecimal.valueOf(5)
        )

        private val expenditureWithUnitCost = ProjectPartnerReportExpenditureCost(
            id = 2965L,
            number = 1,
            lumpSumId = null,
            unitCostId = 1L,
            costCategory = ReportBudgetCategory.StaffCosts,
            gdpr = false,
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
            parkingMetadata = null,
        )

        private val expectedDraftBreakdown = ExpenditureUnitCostBreakdown(
            unitCosts = listOf(
                ExpenditureUnitCostBreakdownLine(
                    reportUnitCostId = 1L,
                    unitCostId = 101L,
                    name = setOf(InputTranslation(SystemLanguage.GA, "name 1 GA")),
                    totalEligibleBudget = BigDecimal.valueOf(52),
                    previouslyReported = BigDecimal.valueOf(23),
                    currentReport = BigDecimal.valueOf(2528, 2),
                    totalEligibleAfterControl = BigDecimal.ZERO,
                    totalReportedSoFar = BigDecimal.valueOf(4828, 2),
                    totalReportedSoFarPercentage = BigDecimal.valueOf(9285, 2),
                    remainingBudget = BigDecimal.valueOf(372, 2),
                    currentReportReIncluded = BigDecimal.valueOf(12.64),
                    previouslyReportedParked = BigDecimal.ZERO,
                    previouslyValidated = BigDecimal.valueOf(5)
                ),
                ExpenditureUnitCostBreakdownLine(
                    reportUnitCostId = 2L,
                    unitCostId = 102L,
                    name = setOf(InputTranslation(SystemLanguage.MK, "name 2 MK")),
                    totalEligibleBudget = BigDecimal.valueOf(18),
                    previouslyReported = BigDecimal.valueOf(7),
                    currentReport = BigDecimal.ZERO,
                    totalEligibleAfterControl = BigDecimal.ZERO,
                    totalReportedSoFar = BigDecimal.valueOf(7),
                    totalReportedSoFarPercentage = BigDecimal.valueOf(3889, 2),
                    remainingBudget = BigDecimal.valueOf(11),
                    currentReportReIncluded = BigDecimal.ZERO,
                    previouslyReportedParked = BigDecimal.valueOf(100),
                    previouslyValidated = BigDecimal.valueOf(5)
                ),
            ),
            total = ExpenditureUnitCostBreakdownLine(
                reportUnitCostId = 0L,
                unitCostId = 0L,
                name = emptySet(),
                totalEligibleBudget = BigDecimal.valueOf(70),
                previouslyReported = BigDecimal.valueOf(30),
                currentReport = BigDecimal.valueOf(2528, 2),
                totalEligibleAfterControl = BigDecimal.ZERO,
                totalReportedSoFar = BigDecimal.valueOf(5528, 2),
                totalReportedSoFarPercentage = BigDecimal.valueOf(7897, 2),
                remainingBudget = BigDecimal.valueOf(1472, 2),
                previouslyReportedParked = BigDecimal.valueOf(100),
                currentReportReIncluded = BigDecimal.valueOf(12.64),
                previouslyValidated = BigDecimal.valueOf(10)
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
                    totalEligibleAfterControl = BigDecimal.valueOf(38, 1),
                    totalReportedSoFar = BigDecimal.valueOf(269, 1),
                    totalReportedSoFarPercentage = BigDecimal.valueOf(5173, 2),
                    remainingBudget = BigDecimal.valueOf(251, 1),
                    currentReportReIncluded = BigDecimal.ZERO,
                    previouslyReportedParked = BigDecimal.valueOf(50),
                    previouslyValidated = BigDecimal.valueOf(5)
                ),
                ExpenditureUnitCostBreakdownLine(
                    reportUnitCostId = 2L,
                    unitCostId = 102L,
                    name = setOf(InputTranslation(SystemLanguage.MK, "name 2 MK")),
                    totalEligibleBudget = BigDecimal.valueOf(18),
                    previouslyReported = BigDecimal.valueOf(7),
                    currentReport = BigDecimal.valueOf(11, 1),
                    totalEligibleAfterControl = BigDecimal.valueOf(10, 1),
                    totalReportedSoFar = BigDecimal.valueOf(81, 1),
                    totalReportedSoFarPercentage = BigDecimal.valueOf(4500, 2),
                    remainingBudget = BigDecimal.valueOf(99, 1),
                    currentReportReIncluded = BigDecimal.ZERO,
                    previouslyReportedParked = BigDecimal.valueOf(100),
                    previouslyValidated = BigDecimal.valueOf(5)
                ),
            ),
            total = ExpenditureUnitCostBreakdownLine(
                reportUnitCostId = 0L,
                unitCostId = 0L,
                name = emptySet(),
                totalEligibleBudget = BigDecimal.valueOf(70),
                previouslyReported = BigDecimal.valueOf(30),
                currentReport = BigDecimal.valueOf(50, 1),
                totalEligibleAfterControl = BigDecimal.valueOf(48, 1),
                totalReportedSoFar = BigDecimal.valueOf(350, 1),
                totalReportedSoFarPercentage = BigDecimal.valueOf(5000, 2),
                remainingBudget = BigDecimal.valueOf(350, 1),
                currentReportReIncluded = BigDecimal.ZERO,
                previouslyReportedParked = BigDecimal.valueOf(150),
                previouslyValidated = BigDecimal.valueOf(10)
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
    @EnumSource(value = ReportStatus::class, names = ["Draft", "ReOpenSubmittedLast", "ReOpenInControlLast"])
    fun getOpen(status: ReportStatus) {
        val reportId = 97658L
        every { reportPersistence.getPartnerReportStatusAndVersion(partnerId = PARTNER_ID, reportId) } returns report(reportId, status)
        every { reportUnitCostPersistence.getUnitCost(partnerId = PARTNER_ID, reportId = reportId) } returns
            listOf(
                unitCost_1.copy(currentReport = BigDecimal.ZERO),
                unitCost_2.copy(currentReport = BigDecimal.ZERO, previouslyReportedParked = BigDecimal.valueOf(100))
            )
        every { currencyPersistence.findAllByIdYearAndIdMonth(YEAR, MONTH) } returns
            listOf(CurrencyConversion("GBP", YEAR, MONTH, "", BigDecimal.valueOf(87, 2)))
        every {
            reportExpenditurePersistence.getPartnerReportExpenditureCosts(
                partnerId = PARTNER_ID,
                reportId = reportId
            )
        } returns
            listOf(
                expenditureWithUnitCost,
                expenditureWithUnitCost.copy(
                    parkingMetadata = ExpenditureParkingMetadata(
                        reportOfOriginId = 70L,
                        reportProjectOfOriginId = null,
                        reportOfOriginNumber = 5,
                        originalExpenditureNumber = 3
                    ),
                    currencyConversionRate = BigDecimal.valueOf(87, 2)
                )
            )

        assertThat(calculator.get(PARTNER_ID, reportId = reportId)).isEqualTo(expectedDraftBreakdown)
    }

    @ParameterizedTest(name = "get closed (status {0})")
    @EnumSource(value = ReportStatus::class, names = ["Draft", "ReOpenSubmittedLast", "ReOpenInControlLast"], mode = EnumSource.Mode.EXCLUDE)
    fun getClosed(status: ReportStatus) {
        val reportId = 97658L
        every { reportPersistence.getPartnerReportStatusAndVersion(partnerId = PARTNER_ID, reportId) } returns report(reportId, status)
        every { reportUnitCostPersistence.getUnitCost(partnerId = PARTNER_ID, reportId = reportId) } returns
            listOf(
                unitCost_1.copy(
                    totalEligibleAfterControl = BigDecimal.valueOf(38, 1),
                    previouslyReportedParked = BigDecimal(50)
                ),
                unitCost_2.copy(
                    totalEligibleAfterControl = BigDecimal.valueOf(10, 1),
                    previouslyReportedParked = BigDecimal(100)
                ),
            )
        assertThat(calculator.get(PARTNER_ID, reportId = reportId)).isEqualTo(expectedNonDraftBreakdown.copy())
    }

}
