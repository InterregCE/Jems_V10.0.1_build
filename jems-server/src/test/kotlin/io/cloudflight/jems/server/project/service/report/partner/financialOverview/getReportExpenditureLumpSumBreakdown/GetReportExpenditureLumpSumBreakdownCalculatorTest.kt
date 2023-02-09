package io.cloudflight.jems.server.project.service.report.partner.financialOverview.getReportExpenditureLumpSumBreakdown

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.service.report.partner.ProjectPartnerReportPersistence
import io.cloudflight.jems.server.project.service.report.model.partner.ProjectPartnerReport
import io.cloudflight.jems.server.project.service.report.model.partner.ReportStatus
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.ExpenditureParkingMetadata
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.ProjectPartnerReportExpenditureCost
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.ReportBudgetCategory
import io.cloudflight.jems.server.project.service.report.model.partner.financialOverview.lumpSum.ExpenditureLumpSumBreakdown
import io.cloudflight.jems.server.project.service.report.model.partner.financialOverview.lumpSum.ExpenditureLumpSumBreakdownLine
import io.cloudflight.jems.server.project.service.report.partner.expenditure.ProjectPartnerReportExpenditurePersistence
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.ProjectPartnerReportLumpSumPersistence
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
import java.time.ZonedDateTime

internal class GetReportExpenditureLumpSumBreakdownCalculatorTest : UnitTest() {

    companion object {
        private const val PARTNER_ID = 597L
        private val LAST_YEAR = ZonedDateTime.now().minusYears(1)

        private fun report(id: Long, status: ReportStatus) =
            ProjectPartnerReport(
                id = id,
                reportNumber = 1,
                status = status,
                version = "V_7.2",
                identification = mockk(),
                firstSubmission = LAST_YEAR,
            )

        private val lumpSum_1 = ExpenditureLumpSumBreakdownLine(
            reportLumpSumId = 1L,
            lumpSumId = 101L,
            name = setOf(InputTranslation(SystemLanguage.ES, "name 1 ES")),
            period = 11,
            totalEligibleBudget = BigDecimal.valueOf(52),
            previouslyReported = BigDecimal.valueOf(23),
            previouslyPaid = BigDecimal.ZERO,
            currentReport = BigDecimal.valueOf(39, 1),
            totalEligibleAfterControl = BigDecimal.ZERO,
            previouslyReportedParked = BigDecimal.ZERO,
            currentReportReIncluded = BigDecimal.ZERO
        )

        private val lumpSum_2 = ExpenditureLumpSumBreakdownLine(
            reportLumpSumId = 2L,
            lumpSumId = 102L,
            name = setOf(InputTranslation(SystemLanguage.TR, "name 2 TR")),
            period = 12,
            totalEligibleBudget = BigDecimal.valueOf(18),
            previouslyReported = BigDecimal.valueOf(7),
            previouslyPaid = BigDecimal.ZERO,
            currentReport = BigDecimal.valueOf(11, 1),
            totalEligibleAfterControl = BigDecimal.ZERO,
            previouslyReportedParked = BigDecimal.ZERO,
            currentReportReIncluded = BigDecimal.ZERO
        )

        private val lumpSum_ft = ExpenditureLumpSumBreakdownLine(
            reportLumpSumId = 3L,
            lumpSumId = 103L,
            name = setOf(InputTranslation(SystemLanguage.TR, "name 3 TR")),
            period = 13,
            totalEligibleBudget = BigDecimal.valueOf(15),
            previouslyReported = BigDecimal.ZERO,
            previouslyPaid = BigDecimal.valueOf(12),
            currentReport = BigDecimal.ZERO,
            totalEligibleAfterControl = BigDecimal.ZERO,
            previouslyReportedParked = BigDecimal.ZERO,
            currentReportReIncluded = BigDecimal.ZERO
        )

        private val expenditureWithLumpSum = ProjectPartnerReportExpenditureCost(
            id = 2965L,
            number = 1,
            lumpSumId = 1L,
            unitCostId = null,
            costCategory = ReportBudgetCategory.Multiple,
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
            currencyCode = "EUR",
            currencyConversionRate = null,
            declaredAmountAfterSubmission = null,
            attachment = null,
            parkingMetadata = null,
        )

        private val expectedDraftBreakdown = ExpenditureLumpSumBreakdown(
            lumpSums = listOf(
                ExpenditureLumpSumBreakdownLine(
                    reportLumpSumId = 1L,
                    lumpSumId = 101L,
                    name = setOf(InputTranslation(SystemLanguage.ES, "name 1 ES")),
                    period = 11,
                    totalEligibleBudget = BigDecimal.valueOf(52),
                    previouslyReported = BigDecimal.valueOf(23),
                    previouslyPaid = BigDecimal.ZERO,
                    currentReport = BigDecimal.valueOf(22),
                    totalEligibleAfterControl = BigDecimal.ZERO,
                    totalReportedSoFar = BigDecimal.valueOf(45),
                    totalReportedSoFarPercentage = BigDecimal.valueOf(8654, 2),
                    remainingBudget = BigDecimal.valueOf(7),
                    previouslyReportedParked = BigDecimal.ZERO,
                    currentReportReIncluded = BigDecimal.valueOf(11)
                ),
                ExpenditureLumpSumBreakdownLine(
                    reportLumpSumId = 2L,
                    lumpSumId = 102L,
                    name = setOf(InputTranslation(SystemLanguage.TR, "name 2 TR")),
                    period = 12,
                    totalEligibleBudget = BigDecimal.valueOf(18),
                    previouslyReported = BigDecimal.valueOf(7),
                    previouslyPaid = BigDecimal.ZERO,
                    currentReport = BigDecimal.ZERO,
                    totalEligibleAfterControl = BigDecimal.ZERO,
                    totalReportedSoFar = BigDecimal.valueOf(7),
                    totalReportedSoFarPercentage = BigDecimal.valueOf(3889, 2),
                    remainingBudget = BigDecimal.valueOf(11),
                    previouslyReportedParked = BigDecimal.valueOf(100),
                    currentReportReIncluded = BigDecimal.ZERO
                ),
                ExpenditureLumpSumBreakdownLine(
                    reportLumpSumId = 3L,
                    lumpSumId = 103L,
                    name = setOf(InputTranslation(SystemLanguage.TR, "name 3 TR")),
                    period = 13,
                    totalEligibleBudget = BigDecimal.valueOf(15),
                    previouslyReported = BigDecimal.ZERO,
                    previouslyPaid = BigDecimal.valueOf(12),
                    currentReport = BigDecimal.ZERO,
                    totalEligibleAfterControl = BigDecimal.ZERO,
                    totalReportedSoFar = BigDecimal.ZERO,
                    totalReportedSoFarPercentage = BigDecimal.valueOf(0, 2),
                    remainingBudget = BigDecimal.valueOf(15),
                    previouslyReportedParked = BigDecimal.ZERO,
                    currentReportReIncluded = BigDecimal.ZERO
                ),
            ),
            total = ExpenditureLumpSumBreakdownLine(
                reportLumpSumId = 0L,
                lumpSumId = 0L,
                name = emptySet(),
                period = null,
                totalEligibleBudget = BigDecimal.valueOf(85),
                previouslyReported = BigDecimal.valueOf(30),
                previouslyPaid = BigDecimal.valueOf(12),
                currentReport = BigDecimal.valueOf(22),
                totalEligibleAfterControl = BigDecimal.ZERO,
                totalReportedSoFar = BigDecimal.valueOf(52),
                totalReportedSoFarPercentage = BigDecimal.valueOf(6118, 2),
                remainingBudget = BigDecimal.valueOf(33),
                previouslyReportedParked = BigDecimal.valueOf(100),
                currentReportReIncluded = BigDecimal.valueOf(11)
            ),
        )

        private val expectedNonDraftBreakdown = ExpenditureLumpSumBreakdown(
            lumpSums = listOf(
                ExpenditureLumpSumBreakdownLine(
                    reportLumpSumId = 1L,
                    lumpSumId = 101L,
                    name = setOf(InputTranslation(SystemLanguage.ES, "name 1 ES")),
                    period = 11,
                    totalEligibleBudget = BigDecimal.valueOf(52),
                    previouslyReported = BigDecimal.valueOf(23),
                    previouslyPaid = BigDecimal.ZERO,
                    currentReport = BigDecimal.valueOf(39, 1),
                    totalEligibleAfterControl = BigDecimal.valueOf(38, 1),
                    totalReportedSoFar = BigDecimal.valueOf(269, 1),
                    totalReportedSoFarPercentage = BigDecimal.valueOf(5173, 2),
                    remainingBudget = BigDecimal.valueOf(251, 1),
                    previouslyReportedParked = BigDecimal.ZERO,
                    currentReportReIncluded = BigDecimal.ZERO
                ),
                ExpenditureLumpSumBreakdownLine(
                    reportLumpSumId = 2L,
                    lumpSumId = 102L,
                    name = setOf(InputTranslation(SystemLanguage.TR, "name 2 TR")),
                    period = 12,
                    totalEligibleBudget = BigDecimal.valueOf(18),
                    previouslyReported = BigDecimal.valueOf(7),
                    previouslyPaid = BigDecimal.ZERO,
                    currentReport = BigDecimal.valueOf(11, 1),
                    totalEligibleAfterControl = BigDecimal.valueOf(10, 1),
                    totalReportedSoFar = BigDecimal.valueOf(81, 1),
                    totalReportedSoFarPercentage = BigDecimal.valueOf(4500, 2),
                    remainingBudget = BigDecimal.valueOf(99, 1),
                    previouslyReportedParked = BigDecimal.valueOf(50),
                    currentReportReIncluded = BigDecimal.ZERO
                ),
                ExpenditureLumpSumBreakdownLine(
                    reportLumpSumId = 3L,
                    lumpSumId = 103L,
                    name = setOf(InputTranslation(SystemLanguage.TR, "name 3 TR")),
                    period = 13,
                    totalEligibleBudget = BigDecimal.valueOf(15),
                    previouslyReported = BigDecimal.ZERO,
                    previouslyPaid = BigDecimal.valueOf(12),
                    currentReport = BigDecimal.ZERO,
                    totalEligibleAfterControl = BigDecimal.valueOf(5, 1),
                    totalReportedSoFar = BigDecimal.ZERO,
                    totalReportedSoFarPercentage = BigDecimal.valueOf(0, 2),
                    remainingBudget = BigDecimal.valueOf(15),
                    previouslyReportedParked = BigDecimal.valueOf(100),
                    currentReportReIncluded = BigDecimal.ZERO
                ),
            ),
            total = ExpenditureLumpSumBreakdownLine(
                reportLumpSumId = 0L,
                lumpSumId = 0L,
                name = emptySet(),
                period = null,
                totalEligibleBudget = BigDecimal.valueOf(85),
                previouslyReported = BigDecimal.valueOf(30),
                previouslyPaid = BigDecimal.valueOf(12),
                currentReport = BigDecimal.valueOf(50, 1),
                totalEligibleAfterControl = BigDecimal.valueOf(53, 1),
                totalReportedSoFar = BigDecimal.valueOf(350, 1),
                totalReportedSoFarPercentage = BigDecimal.valueOf(4118, 2),
                remainingBudget = BigDecimal.valueOf(500, 1),
                previouslyReportedParked = BigDecimal.valueOf(150),
                currentReportReIncluded = BigDecimal.ZERO
            ),
        )

    }

    @MockK
    lateinit var reportPersistence: ProjectPartnerReportPersistence
    @MockK
    lateinit var reportLumpSumPersistence: ProjectPartnerReportLumpSumPersistence
    @MockK
    lateinit var reportExpenditurePersistence: ProjectPartnerReportExpenditurePersistence

    @InjectMockKs
    lateinit var calculator: GetReportExpenditureLumpSumBreakdownCalculator

    @BeforeEach
    fun resetMocks() {
        clearMocks(reportPersistence)
        clearMocks(reportLumpSumPersistence)
        clearMocks(reportExpenditurePersistence)
    }

    @ParameterizedTest(name = "get open (status {0})")
    @EnumSource(value = ReportStatus::class, names = ["Draft"])
    fun getOpen(status: ReportStatus) {
        val reportId = 97658L
        every { reportPersistence.getPartnerReportById(partnerId = PARTNER_ID, reportId) } returns
            report(reportId, status)
        every { reportLumpSumPersistence.getLumpSum(partnerId = PARTNER_ID, reportId = reportId) } returns
            listOf(
                lumpSum_1.copy(currentReport = BigDecimal.ZERO),
                lumpSum_2.copy(currentReport = BigDecimal.ZERO, previouslyReportedParked = BigDecimal.valueOf(100)),
                lumpSum_ft
            )
        every {
            reportExpenditurePersistence.getPartnerReportExpenditureCosts(
                partnerId = PARTNER_ID,
                reportId = reportId
            )
        } returns
            listOf(
                expenditureWithLumpSum,
                expenditureWithLumpSum.copy(
                    parkingMetadata = ExpenditureParkingMetadata(
                        reportOfOriginId = 70L,
                        reportOfOriginNumber = 5,
                        originalExpenditureNumber = 3
                    )
                )
            )
        assertThat(calculator.get(PARTNER_ID, reportId = reportId)).isEqualTo(expectedDraftBreakdown)
    }

    @ParameterizedTest(name = "get closed (status {0})")
    @EnumSource(value = ReportStatus::class, names = ["Draft"], mode = EnumSource.Mode.EXCLUDE)
    fun getClosed(status: ReportStatus) {
        val reportId = 97658L
        every { reportPersistence.getPartnerReportById(partnerId = PARTNER_ID, reportId) } returns
            report(reportId, status)
        every { reportLumpSumPersistence.getLumpSum(partnerId = PARTNER_ID, reportId = reportId) } returns
            listOf(
                lumpSum_1.copy(totalEligibleAfterControl = BigDecimal.valueOf(38, 1)),
                lumpSum_2.copy(
                    totalEligibleAfterControl = BigDecimal.valueOf(10, 1),
                    previouslyReportedParked = BigDecimal.valueOf(50)
                ),
                lumpSum_ft.copy(
                    totalEligibleAfterControl = BigDecimal.valueOf(5, 1),
                    previouslyReportedParked = BigDecimal.valueOf(100)
                )
            )
        assertThat(calculator.get(PARTNER_ID, reportId = reportId)).isEqualTo(expectedNonDraftBreakdown.copy())
    }

}
