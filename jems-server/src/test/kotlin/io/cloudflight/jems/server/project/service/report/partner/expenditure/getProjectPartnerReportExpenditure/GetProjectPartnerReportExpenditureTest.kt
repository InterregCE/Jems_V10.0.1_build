package io.cloudflight.jems.server.project.service.report.partner.expenditure.getProjectPartnerReportExpenditure

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.currency.repository.CurrencyPersistence
import io.cloudflight.jems.server.currency.service.model.CurrencyConversion
import io.cloudflight.jems.server.project.service.report.ProjectReportPersistence
import io.cloudflight.jems.server.project.service.report.model.ProjectPartnerReport
import io.cloudflight.jems.server.project.service.report.model.ReportStatus
import io.cloudflight.jems.server.project.service.report.model.expenditure.ProjectPartnerReportExpenditureCost
import io.cloudflight.jems.server.project.service.report.model.expenditure.ReportBudgetCategory
import io.cloudflight.jems.server.project.service.report.model.file.ProjectReportFileMetadata
import io.cloudflight.jems.server.project.service.report.partner.expenditure.ProjectReportExpenditurePersistence
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import java.math.BigDecimal
import java.time.LocalDate
import java.time.ZonedDateTime

internal class GetProjectPartnerReportExpenditureTest : UnitTest() {

    companion object {
        private const val PARTNER_ID = 520L
        private val TODAY = LocalDate.now()
        private val YEAR = LocalDate.now().year
        private val MONTH = LocalDate.now().monthValue
        private val MOMENT = ZonedDateTime.now()

        private fun expenditure(id: Long) = ProjectPartnerReportExpenditureCost(
            id = id,
            lumpSumId = 45L,
            unitCostId = 46L,
            costCategory = ReportBudgetCategory.TravelAndAccommodationCosts,
            investmentId = 89L,
            contractId = 54L,
            internalReferenceNumber = "145",
            invoiceNumber = "1",
            invoiceDate = TODAY,
            dateOfPayment = TODAY,
            numberOfUnits = BigDecimal.ONE,
            pricePerUnit = BigDecimal.ZERO,
            declaredAmount = BigDecimal.valueOf(2431, 2),
            currencyCode = "CST",
            currencyConversionRate = null,
            declaredAmountAfterSubmission = null,
            attachment = ProjectReportFileMetadata(45L, "file.txt", MOMENT),
        )

        private fun filledInExpenditure(id: Long) = ProjectPartnerReportExpenditureCost(
            id = id,
            lumpSumId = 45L,
            unitCostId = 46L,
            costCategory = ReportBudgetCategory.TravelAndAccommodationCosts,
            investmentId = 89L,
            contractId = 54L,
            internalReferenceNumber = "145",
            invoiceNumber = "1",
            invoiceDate = TODAY,
            dateOfPayment = TODAY,
            numberOfUnits = BigDecimal.ONE,
            pricePerUnit = BigDecimal.ZERO,
            declaredAmount = BigDecimal.valueOf(2431, 2),
            currencyCode = "CST",
            currencyConversionRate = BigDecimal.valueOf(24302, 4),
            declaredAmountAfterSubmission = BigDecimal.valueOf(1000, 2),
            attachment = ProjectReportFileMetadata(45L, "file.txt", MOMENT),
        )

        private val cstCurrency = CurrencyConversion(
            code = "CST",
            year = YEAR,
            month = MONTH,
            name = "Custom Currency",
            conversionRate = BigDecimal.valueOf(24302, 4),
        )
    }

    @MockK
    lateinit var reportPersistence: ProjectReportPersistence

    @MockK
    lateinit var reportExpenditurePersistence: ProjectReportExpenditurePersistence

    @MockK
    lateinit var currencyPersistence: CurrencyPersistence

    @InjectMockKs
    lateinit var getReportContribution: GetProjectPartnerReportExpenditure

    @BeforeEach
    fun reset() {
        clearMocks(currencyPersistence)
    }

    @ParameterizedTest(name = "getContribution and fill in currencies (status {0})")
    @EnumSource(value = ReportStatus::class, names = ["Draft"])
    fun `getContribution and fill in`(status: ReportStatus) {
        val report = mockk<ProjectPartnerReport>()
        every { report.status } returns status
        every { reportPersistence.getPartnerReportById(PARTNER_ID, 74L) } returns report

        every { reportExpenditurePersistence.getPartnerReportExpenditureCosts(PARTNER_ID, reportId = 74L) } returns
            listOf(expenditure(10L))

        every { currencyPersistence.findAllByIdYearAndIdMonth(year = YEAR, month = MONTH) } returns listOf(cstCurrency)

        assertThat(getReportContribution.getExpenditureCosts(PARTNER_ID, reportId = 74L))
            .containsExactly(filledInExpenditure(10L))
    }

    @Test
    fun `getContribution and fill in, but empty rates`() {
        val report = mockk<ProjectPartnerReport>()
        every { report.status } returns ReportStatus.Draft
        every { reportPersistence.getPartnerReportById(PARTNER_ID, 76L) } returns report

        every { reportExpenditurePersistence.getPartnerReportExpenditureCosts(PARTNER_ID, reportId = 76L) } returns
            // those should be removed, if persisted by accident
            listOf(expenditure(10L)
                .copy(currencyConversionRate = BigDecimal.ONE, declaredAmountAfterSubmission = BigDecimal.TEN)
            )

        every { currencyPersistence.findAllByIdYearAndIdMonth(year = YEAR, month = MONTH) } returns emptyList()

        assertThat(getReportContribution.getExpenditureCosts(PARTNER_ID, reportId = 76L))
            .containsExactly(filledInExpenditure(10L).copy(
                currencyConversionRate = null,
                declaredAmountAfterSubmission = null,
            ))
    }

    @ParameterizedTest(name = "getContribution and NOT fill in currencies (status {0})")
    @EnumSource(value = ReportStatus::class, names = ["Draft"], mode = EnumSource.Mode.EXCLUDE)
    fun `getContribution and NOT fill in`(status: ReportStatus) {
        val report = mockk<ProjectPartnerReport>()
        every { report.status } returns status
        every { reportPersistence.getPartnerReportById(PARTNER_ID, 78L) } returns report

        val notFilledInExpenditure = expenditure(15L)
        every { reportExpenditurePersistence.getPartnerReportExpenditureCosts(PARTNER_ID, reportId = 78L) } returns
            listOf(notFilledInExpenditure)

        assertThat(getReportContribution.getExpenditureCosts(PARTNER_ID, reportId = 78L))
            .containsExactly(notFilledInExpenditure)

        verify(exactly = 0) { currencyPersistence.getConversionForCodeAndMonth(any(), any(), any()) }
    }

    @Test
    fun `test rounding`() {
        val notFilledInExpenditure = expenditure(30L)
        notFilledInExpenditure.fillInRate(BigDecimal.valueOf(24305, 4))

        assertThat(notFilledInExpenditure.declaredAmountAfterSubmission)
            .isEqualTo(BigDecimal.valueOf(1000, 2))
    }

}
