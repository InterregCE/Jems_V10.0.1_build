package io.cloudflight.jems.server.project.service.report.partner.financialOverview.getReportExpenditureInvestmentsBreakdown

import io.cloudflight.jems.api.project.dto.partner.cofinancing.ProjectPartnerCoFinancingFundTypeDTO
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.currency.repository.CurrencyPersistence
import io.cloudflight.jems.server.currency.service.model.CurrencyConversion
import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFund
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerCoFinancing
import io.cloudflight.jems.server.project.service.report.ProjectReportPersistence
import io.cloudflight.jems.server.project.service.report.model.PartnerReportIdentification
import io.cloudflight.jems.server.project.service.report.model.ProjectPartnerReport
import io.cloudflight.jems.server.project.service.report.model.ReportStatus
import io.cloudflight.jems.server.project.service.report.model.expenditure.ProjectPartnerReportExpenditureCost
import io.cloudflight.jems.server.project.service.report.model.expenditure.ReportBudgetCategory
import io.cloudflight.jems.server.project.service.report.model.financialOverview.investments.ExpenditureInvestmentBreakdown
import io.cloudflight.jems.server.project.service.report.model.financialOverview.investments.ExpenditureInvestmentBreakdownLine
import io.cloudflight.jems.server.project.service.report.partner.expenditure.ProjectReportExpenditurePersistence
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.ProjectReportExpenditureInvestmentPersistence
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.getReportExpenditureInvestementsBreakdown.GetReportExpenditureInvestmentsBreakdown
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.ZonedDateTime

class GetReportExpenditureInvestmentsBreakdownTest : UnitTest() {

    companion object {
        private val LAST_YEAR = ZonedDateTime.now().minusYears(1)

        private fun fund(id: Long): ProgrammeFund {
            val fundMock = mockk<ProgrammeFund>()
            every { fundMock.id } returns id
            return fundMock
        }
        private fun report(id: Long, status: ReportStatus): ProjectPartnerReport {
            val identification = mockk<PartnerReportIdentification>()

            every { identification.coFinancing } returns listOf(
                ProjectPartnerCoFinancing(ProjectPartnerCoFinancingFundTypeDTO.MainFund, fund(id = 15L), percentage = BigDecimal.valueOf(15)),
                ProjectPartnerCoFinancing(ProjectPartnerCoFinancingFundTypeDTO.MainFund, fund(id = 16L), percentage = BigDecimal.valueOf(25)),
                ProjectPartnerCoFinancing(ProjectPartnerCoFinancingFundTypeDTO.PartnerContribution, null, percentage = BigDecimal.valueOf(60)),
            )

            return ProjectPartnerReport(
                id = id,
                reportNumber = 1,
                status = status,
                version = "V_4.5",
                identification = identification,
                firstSubmission = LAST_YEAR,
            )
        }
    }

    private val investments = listOf(
        ExpenditureInvestmentBreakdownLine(
            investmentId = 1L,
            investmentNumber = 1,
            workPackageNumber = 1,
            totalEligibleBudget = BigDecimal.valueOf(300L),
            previouslyReported = BigDecimal.valueOf(100L),
            currentReport = BigDecimal.valueOf(100L)
        ),
        ExpenditureInvestmentBreakdownLine(
            investmentId = 2L,
            investmentNumber = 2,
            workPackageNumber = 1,
            totalEligibleBudget = BigDecimal.ZERO,
            previouslyReported = BigDecimal.valueOf(100L),
            currentReport = BigDecimal.valueOf(100L)
        ),
        ExpenditureInvestmentBreakdownLine(
            investmentId = 3L,
            investmentNumber = 3,
            workPackageNumber = 1,
            totalEligibleBudget = BigDecimal.ZERO,
            previouslyReported = BigDecimal.ZERO,
            currentReport = BigDecimal.valueOf(100L)
        ),
        ExpenditureInvestmentBreakdownLine(
            investmentId = 4L,
            investmentNumber = 4,
            workPackageNumber = 1,
            totalEligibleBudget = BigDecimal.valueOf(100L),
            previouslyReported = BigDecimal.ZERO,
            currentReport = BigDecimal.valueOf(100L)
        ),
    )

    private val expenditures = listOf(
        ProjectPartnerReportExpenditureCost(
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
            currencyCode = "CST",
            currencyConversionRate = null,
            declaredAmountAfterSubmission = null,
            attachment = null,
        ),
        ProjectPartnerReportExpenditureCost(
            id = 205L,
            lumpSumId = null,
            unitCostId = null,
            costCategory = ReportBudgetCategory.InfrastructureCosts,
            investmentId = 2L,
            contractId = null,
            internalReferenceNumber = null,
            invoiceNumber = null,
            invoiceDate = null,
            dateOfPayment = null,
            numberOfUnits = BigDecimal.valueOf(2),
            pricePerUnit = BigDecimal.valueOf(135, 0),
            declaredAmount = BigDecimal.valueOf(270, 0),
            currencyCode = "CST",
            currencyConversionRate = null,
            declaredAmountAfterSubmission = null,
            attachment = null,
        )
    )

    private val expectedResult = ExpenditureInvestmentBreakdown(
        investments = listOf(
            ExpenditureInvestmentBreakdownLine(
                investmentId = 1L,
                investmentNumber = 1,
                workPackageNumber = 1,
                totalEligibleBudget = BigDecimal.valueOf(300L),
                previouslyReported = BigDecimal.valueOf(100L),
                currentReport = BigDecimal.valueOf(168.75),
                totalReportedSoFar = BigDecimal.valueOf(268.75),
                totalReportedSoFarPercentage = BigDecimal.valueOf(89.58),
                remainingBudget = BigDecimal.valueOf(31.25),
            ),
            ExpenditureInvestmentBreakdownLine(
                investmentId = 2L,
                investmentNumber = 2,
                workPackageNumber = 1,
                totalEligibleBudget = BigDecimal.ZERO,
                previouslyReported = BigDecimal.valueOf(100L),
                currentReport = BigDecimal.valueOf(168.75),
                totalReportedSoFar = BigDecimal.valueOf(268.75),
                totalReportedSoFarPercentage = BigDecimal.ZERO,
                remainingBudget = BigDecimal.valueOf(-268.75),
            ),
            ExpenditureInvestmentBreakdownLine(
                investmentId = 3L,
                investmentNumber = 3,
                workPackageNumber = 1,
                totalEligibleBudget = BigDecimal.ZERO,
                previouslyReported = BigDecimal.ZERO,
                currentReport = BigDecimal.ZERO,
                totalReportedSoFar = BigDecimal.ZERO,
                totalReportedSoFarPercentage = BigDecimal.ZERO,
                remainingBudget = BigDecimal.ZERO,
            ),
            ExpenditureInvestmentBreakdownLine(
                investmentId = 4L,
                investmentNumber = 4,
                workPackageNumber = 1,
                totalEligibleBudget = BigDecimal.valueOf(100L),
                previouslyReported = BigDecimal.ZERO,
                currentReport = BigDecimal.ZERO,
                totalReportedSoFar = BigDecimal.ZERO,
                totalReportedSoFarPercentage = BigDecimal.ZERO.setScale(2),
                remainingBudget = BigDecimal.valueOf(100L),
            ),
        ),
        total = ExpenditureInvestmentBreakdownLine(
            investmentId = 0L,
            investmentNumber = 0,
            workPackageNumber = 0,
            totalEligibleBudget = BigDecimal.valueOf(400L),
            previouslyReported = BigDecimal.valueOf(200L),
            currentReport = BigDecimal.valueOf(337.50).setScale(2),
            totalReportedSoFar = BigDecimal.valueOf(537.50).setScale(2),
            totalReportedSoFarPercentage = BigDecimal.valueOf(134.38).setScale(2),
            remainingBudget = BigDecimal.valueOf(-137.50).setScale(2),
        )
    )

    private val currency = CurrencyConversion(
        code = "CST",
        year = 2022,
        month = 10,
        name = "test",
        conversionRate = BigDecimal.valueOf(1.6)
    )

    @MockK
    lateinit var expenditureInvestmentPersistence: ProjectReportExpenditureInvestmentPersistence
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

    @Test
    fun `get`() {
        val reportId = 1L
        val partnerId = 2L
        every { reportPersistence.getPartnerReportById(partnerId = partnerId, reportId) } returns report(
            reportId,
            ReportStatus.Draft
        )
        every { expenditureInvestmentPersistence.getInvestments(partnerId, reportId) } returns investments
        every { reportExpenditurePersistence.getPartnerReportExpenditureCosts(partnerId, reportId) } returns expenditures
        every { currencyPersistence.findAllByIdYearAndIdMonth(any(), any()) } returns listOf(currency)

        Assertions.assertThat(
            interactor.get(
                partnerId,
                reportId = reportId
            )
        ).isEqualTo(expectedResult)
    }
}
