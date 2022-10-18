package io.cloudflight.jems.server.project.service.report.partner.financialOverview.getReportExpenditureLumpSumBreakdown

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.service.report.ProjectReportPersistence
import io.cloudflight.jems.server.project.service.report.model.ProjectPartnerReport
import io.cloudflight.jems.server.project.service.report.model.ReportStatus
import io.cloudflight.jems.server.project.service.report.model.expenditure.ProjectPartnerReportExpenditureCost
import io.cloudflight.jems.server.project.service.report.model.expenditure.ReportBudgetCategory
import io.cloudflight.jems.server.project.service.report.model.financialOverview.lumpSum.ExpenditureLumpSumBreakdown
import io.cloudflight.jems.server.project.service.report.model.financialOverview.lumpSum.ExpenditureLumpSumBreakdownLine
import io.cloudflight.jems.server.project.service.report.partner.expenditure.ProjectReportExpenditurePersistence
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.ProjectReportLumpSumPersistence
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

internal class GetReportExpenditureLumpSumBreakdownTest : UnitTest() {

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
            currentReport = BigDecimal.valueOf(39, 1),
        )

        private val lumpSum_2 = ExpenditureLumpSumBreakdownLine(
            reportLumpSumId = 2L,
            lumpSumId = 102L,
            name = setOf(InputTranslation(SystemLanguage.TR, "name 2 TR")),
            period = 12,
            totalEligibleBudget = BigDecimal.valueOf(18),
            previouslyReported = BigDecimal.valueOf(7),
            currentReport = BigDecimal.valueOf(11, 1),
        )

        private val expenditureWithLumpSum = ProjectPartnerReportExpenditureCost(
            id = 2965L,
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
                    currentReport = BigDecimal.valueOf(11),
                    totalReportedSoFar = BigDecimal.valueOf(34),
                    totalReportedSoFarPercentage = BigDecimal.valueOf(6538, 2),
                    remainingBudget = BigDecimal.valueOf(18),
                ),
                ExpenditureLumpSumBreakdownLine(
                    reportLumpSumId = 2L,
                    lumpSumId = 102L,
                    name = setOf(InputTranslation(SystemLanguage.TR, "name 2 TR")),
                    period = 12,
                    totalEligibleBudget = BigDecimal.valueOf(18),
                    previouslyReported = BigDecimal.valueOf(7),
                    currentReport = BigDecimal.ZERO,
                    totalReportedSoFar = BigDecimal.valueOf(7),
                    totalReportedSoFarPercentage = BigDecimal.valueOf(3889, 2),
                    remainingBudget = BigDecimal.valueOf(11),
                ),
            ),
            total = ExpenditureLumpSumBreakdownLine(
                reportLumpSumId = 0L,
                lumpSumId = 0L,
                name = emptySet(),
                period = null,
                totalEligibleBudget = BigDecimal.valueOf(70),
                previouslyReported = BigDecimal.valueOf(30),
                currentReport = BigDecimal.valueOf(11),
                totalReportedSoFar = BigDecimal.valueOf(41),
                totalReportedSoFarPercentage = BigDecimal.valueOf(5857, 2),
                remainingBudget = BigDecimal.valueOf(29),
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
                    currentReport = BigDecimal.valueOf(39, 1),
                    totalReportedSoFar = BigDecimal.valueOf(269, 1),
                    totalReportedSoFarPercentage = BigDecimal.valueOf(5173, 2),
                    remainingBudget = BigDecimal.valueOf(251, 1),
                ),
                ExpenditureLumpSumBreakdownLine(
                    reportLumpSumId = 2L,
                    lumpSumId = 102L,
                    name = setOf(InputTranslation(SystemLanguage.TR, "name 2 TR")),
                    period = 12,
                    totalEligibleBudget = BigDecimal.valueOf(18),
                    previouslyReported = BigDecimal.valueOf(7),
                    currentReport = BigDecimal.valueOf(11, 1),
                    totalReportedSoFar = BigDecimal.valueOf(81, 1),
                    totalReportedSoFarPercentage = BigDecimal.valueOf(4500, 2),
                    remainingBudget = BigDecimal.valueOf(99, 1),
                ),
            ),
            total = ExpenditureLumpSumBreakdownLine(
                reportLumpSumId = 0L,
                lumpSumId = 0L,
                name = emptySet(),
                period = null,
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
    lateinit var reportPersistence: ProjectReportPersistence
    @MockK
    lateinit var reportLumpSumPersistence: ProjectReportLumpSumPersistence
    @MockK
    lateinit var reportExpenditurePersistence: ProjectReportExpenditurePersistence

    @InjectMockKs
    lateinit var interactor: GetReportExpenditureLumpSumBreakdown

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
            listOf(lumpSum_1.copy(currentReport = BigDecimal.ZERO), lumpSum_2.copy(currentReport = BigDecimal.ZERO))
        every { reportExpenditurePersistence.getPartnerReportExpenditureCosts(partnerId = PARTNER_ID, reportId = reportId) } returns
            listOf(expenditureWithLumpSum)
        assertThat(interactor.get(PARTNER_ID, reportId = reportId)).isEqualTo(expectedDraftBreakdown.copy())
    }

    @ParameterizedTest(name = "get closed (status {0})")
    @EnumSource(value = ReportStatus::class, names = ["Draft"], mode = EnumSource.Mode.EXCLUDE)
    fun getClosed(status: ReportStatus) {
        val reportId = 97658L
        every { reportPersistence.getPartnerReportById(partnerId = PARTNER_ID, reportId) } returns
            report(reportId, status)
        every { reportLumpSumPersistence.getLumpSum(partnerId = PARTNER_ID, reportId = reportId) } returns
            listOf(lumpSum_1, lumpSum_2)
        assertThat(interactor.get(PARTNER_ID, reportId = reportId)).isEqualTo(expectedNonDraftBreakdown.copy())
    }

}
