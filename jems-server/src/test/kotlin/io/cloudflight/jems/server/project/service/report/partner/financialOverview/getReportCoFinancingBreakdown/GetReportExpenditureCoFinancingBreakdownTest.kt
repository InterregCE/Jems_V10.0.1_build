package io.cloudflight.jems.server.project.service.report.partner.financialOverview.getReportCoFinancingBreakdown

import io.cloudflight.jems.api.project.dto.partner.cofinancing.ProjectPartnerCoFinancingFundTypeDTO.MainFund
import io.cloudflight.jems.api.project.dto.partner.cofinancing.ProjectPartnerCoFinancingFundTypeDTO.PartnerContribution
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFund
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerCoFinancing
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerContributionStatus
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerContributionStatus.AutomaticPublic
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerContributionStatus.Private
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerContributionStatus.Public
import io.cloudflight.jems.server.project.service.report.ProjectReportPersistence
import io.cloudflight.jems.server.project.service.report.model.PartnerReportIdentification
import io.cloudflight.jems.server.project.service.report.model.ProjectPartnerReport
import io.cloudflight.jems.server.project.service.report.model.ReportStatus
import io.cloudflight.jems.server.project.service.report.model.contribution.withoutCalculations.ProjectPartnerReportEntityContribution
import io.cloudflight.jems.server.project.service.report.model.financialOverview.coFinancing.ExpenditureCoFinancingBreakdown
import io.cloudflight.jems.server.project.service.report.model.financialOverview.coFinancing.ExpenditureCoFinancingBreakdownLine
import io.cloudflight.jems.server.project.service.report.model.financialOverview.coFinancing.ReportExpenditureCoFinancing
import io.cloudflight.jems.server.project.service.report.model.financialOverview.coFinancing.ReportExpenditureCoFinancingColumn
import io.cloudflight.jems.server.project.service.report.model.financialOverview.costCategory.ExpenditureCostCategoryBreakdown
import io.cloudflight.jems.server.project.service.report.model.financialOverview.costCategory.ExpenditureCostCategoryBreakdownLine
import io.cloudflight.jems.server.project.service.report.partner.contribution.ProjectReportContributionPersistence
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.ProjectReportExpenditureCoFinancingPersistence
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.getReportExpenditureBreakdown.GetReportExpenditureCostCategoryCalculatorService
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
import java.util.*

internal class GetReportExpenditureCoFinancingBreakdownTest : UnitTest() {

    companion object {
        private const val PARTNER_ID = 595L
        private val LAST_YEAR = ZonedDateTime.now().minusYears(1)

        private fun fund(id: Long): ProgrammeFund {
            val fundMock = mockk<ProgrammeFund>()
            every { fundMock.id } returns id
            return fundMock
        }
        private fun report(id: Long, status: ReportStatus): ProjectPartnerReport {
            val identification = mockk<PartnerReportIdentification>()

            every { identification.coFinancing } returns listOf(
                ProjectPartnerCoFinancing(MainFund, fund(id = 15L), percentage = BigDecimal.valueOf(15)),
                ProjectPartnerCoFinancing(MainFund, fund(id = 16L), percentage = BigDecimal.valueOf(25)),
                ProjectPartnerCoFinancing(PartnerContribution, null, percentage = BigDecimal.valueOf(60)),
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

        private val coFinancing = ReportExpenditureCoFinancing(
            totalsFromAF = ReportExpenditureCoFinancingColumn(
                funds = mapOf(
                    15L to BigDecimal.valueOf(150L),
                    16L to BigDecimal.valueOf(250L),
                    null to BigDecimal.valueOf(750L),
                ),
                partnerContribution = BigDecimal.valueOf(900),
                publicContribution = BigDecimal.valueOf(200),
                automaticPublicContribution = BigDecimal.valueOf(300),
                privateContribution = BigDecimal.valueOf(400),
                sum = BigDecimal.valueOf(1000),
            ),
            currentlyReported = ReportExpenditureCoFinancingColumn(
                funds = mapOf(
                    15L to BigDecimal.valueOf(15L),
                    16L to BigDecimal.valueOf(25L),
                    null to BigDecimal.valueOf(75L),
                ),
                partnerContribution = BigDecimal.valueOf(50),
                publicContribution = BigDecimal.valueOf(100),
                automaticPublicContribution = BigDecimal.valueOf(150),
                privateContribution = BigDecimal.valueOf(200),
                sum = BigDecimal.valueOf(250),
            ),
            previouslyReported = ReportExpenditureCoFinancingColumn(
                funds = mapOf(
                    15L to BigDecimal.valueOf(45L),
                    16L to BigDecimal.valueOf(75L),
                    null to BigDecimal.valueOf(225L),
                ),
                partnerContribution = BigDecimal.valueOf(2),
                publicContribution = BigDecimal.valueOf(3),
                automaticPublicContribution = BigDecimal.valueOf(4),
                privateContribution = BigDecimal.valueOf(5),
                sum = BigDecimal.valueOf(6),
            ),
            previouslyPaid = ReportExpenditureCoFinancingColumn(
                funds = mapOf(
                    15L to BigDecimal.ZERO,
                    16L to BigDecimal.valueOf(17L),
                    null to BigDecimal.valueOf(150L),
                ),
                partnerContribution = BigDecimal.ZERO,
                publicContribution = BigDecimal.ZERO,
                automaticPublicContribution = BigDecimal.ZERO,
                privateContribution = BigDecimal.ZERO,
                sum = BigDecimal.valueOf(320),
            ),
        )

        private val total = ExpenditureCostCategoryBreakdownLine(
            flatRate = -1 /* not important */,
            totalEligibleBudget = BigDecimal.valueOf(1000),
            previouslyReported = BigDecimal.valueOf(-1) /* not important */,
            currentReport = BigDecimal.valueOf(1000),
        )

        private fun contrib(
            status: ProjectPartnerContributionStatus,
            amount: BigDecimal,
            prev: BigDecimal,
            current: BigDecimal,
        ) = ProjectPartnerReportEntityContribution(
            legalStatus = status,
            amount = amount,
            previouslyReported = prev,
            currentlyReported = current,
            /* not important: */
            attachment = null,
            createdInThisReport = true,
            historyIdentifier = UUID.randomUUID(),
            id = 0L,
            idFromApplicationForm = null,
            sourceOfContribution = null,
        )

        private val partnerContribution = listOf(
            contrib(Public, amount = BigDecimal.valueOf(20), prev = BigDecimal.valueOf(2), current = BigDecimal.valueOf(4)),
            contrib(AutomaticPublic, amount = BigDecimal.valueOf(30), prev = BigDecimal.valueOf(3), current = BigDecimal.valueOf(6)),
            contrib(Private, amount = BigDecimal.valueOf(40), prev = BigDecimal.valueOf(4), current = BigDecimal.valueOf(8)),
        )

        private fun expectedSubmittedResult(zeroTotals: Boolean = false) = ExpenditureCoFinancingBreakdown(
            funds = listOf(
                ExpenditureCoFinancingBreakdownLine(
                    fundId = 15L,
                    totalEligibleBudget = BigDecimal.valueOf(150),
                    previouslyReported = BigDecimal.valueOf(45),
                    previouslyPaid = BigDecimal.valueOf(0),
                    currentReport = BigDecimal.valueOf(15000, 2),
                    totalReportedSoFar = BigDecimal.valueOf(19500, 2),
                    totalReportedSoFarPercentage = BigDecimal.valueOf(13000, 2),
                    remainingBudget = BigDecimal.valueOf(-4500, 2),
                ),
                ExpenditureCoFinancingBreakdownLine(
                    fundId = 16L,
                    totalEligibleBudget = BigDecimal.valueOf(250),
                    previouslyReported = BigDecimal.valueOf(75),
                    previouslyPaid = BigDecimal.valueOf(17),
                    currentReport = BigDecimal.valueOf(25000, 2),
                    totalReportedSoFar = BigDecimal.valueOf(32500, 2),
                    totalReportedSoFarPercentage = BigDecimal.valueOf(13000, 2),
                    remainingBudget = BigDecimal.valueOf(-7500, 2),
                ),
                ExpenditureCoFinancingBreakdownLine(
                    fundId = null,
                    totalEligibleBudget = BigDecimal.valueOf(750),
                    previouslyReported = BigDecimal.valueOf(225),
                    previouslyPaid = BigDecimal.valueOf(150),
                    currentReport = BigDecimal.valueOf(60000, 2),
                    totalReportedSoFar = BigDecimal.valueOf(82500, 2),
                    totalReportedSoFarPercentage = BigDecimal.valueOf(11000, 2),
                    remainingBudget = BigDecimal.valueOf(-7500, 2),
                ),
            ),
            partnerContribution = ExpenditureCoFinancingBreakdownLine(
                fundId = null,
                totalEligibleBudget = if (zeroTotals) BigDecimal.ZERO else BigDecimal.valueOf(900),
                previouslyReported = BigDecimal.valueOf(2),
                previouslyPaid = BigDecimal.valueOf(0),
                currentReport = BigDecimal.valueOf(60000, 2),
                totalReportedSoFar = BigDecimal.valueOf(60200, 2),
                totalReportedSoFarPercentage = if (zeroTotals) BigDecimal.ZERO else BigDecimal.valueOf(6689, 2),
                remainingBudget = if (zeroTotals) BigDecimal.valueOf(-60200, 2) else BigDecimal.valueOf(29800, 2),
            ),
            publicContribution = ExpenditureCoFinancingBreakdownLine(
                fundId = null,
                totalEligibleBudget = if (zeroTotals) BigDecimal.ZERO else BigDecimal.valueOf(200),
                previouslyReported = BigDecimal.valueOf(3),
                previouslyPaid = BigDecimal.valueOf(0),
                currentReport = BigDecimal.valueOf(2000, 2),
                totalReportedSoFar = BigDecimal.valueOf(2300, 2),
                totalReportedSoFarPercentage = if (zeroTotals) BigDecimal.ZERO else BigDecimal.valueOf(1150, 2),
                remainingBudget = if (zeroTotals) BigDecimal.valueOf(-2300, 2) else BigDecimal.valueOf(17700, 2),
            ),
            automaticPublicContribution = ExpenditureCoFinancingBreakdownLine(
                fundId = null,
                totalEligibleBudget = if (zeroTotals) BigDecimal.ZERO else BigDecimal.valueOf(300),
                previouslyReported = BigDecimal.valueOf(4),
                previouslyPaid = BigDecimal.valueOf(0),
                currentReport = BigDecimal.valueOf(3000, 2),
                totalReportedSoFar = BigDecimal.valueOf(3400, 2),
                totalReportedSoFarPercentage = if (zeroTotals) BigDecimal.ZERO else BigDecimal.valueOf(1133, 2),
                remainingBudget = if (zeroTotals) BigDecimal.valueOf(-3400, 2) else BigDecimal.valueOf(26600, 2),
            ),
            privateContribution = ExpenditureCoFinancingBreakdownLine(
                fundId = null,
                totalEligibleBudget = if (zeroTotals) BigDecimal.ZERO else BigDecimal.valueOf(400),
                previouslyReported = BigDecimal.valueOf(5),
                previouslyPaid = BigDecimal.valueOf(0),
                currentReport = BigDecimal.valueOf(4000, 2),
                totalReportedSoFar = BigDecimal.valueOf(4500, 2),
                totalReportedSoFarPercentage = if (zeroTotals) BigDecimal.ZERO else BigDecimal.valueOf(1125, 2),
                remainingBudget = if (zeroTotals) BigDecimal.valueOf(-4500, 2) else BigDecimal.valueOf(35500, 2),
            ),
            total = ExpenditureCoFinancingBreakdownLine(
                fundId = null,
                totalEligibleBudget = if (zeroTotals) BigDecimal.ZERO else BigDecimal.valueOf(1000),
                previouslyReported = BigDecimal.valueOf(6),
                previouslyPaid = BigDecimal.valueOf(320),
                currentReport = BigDecimal.valueOf(1000),
                totalReportedSoFar = BigDecimal.valueOf(1006),
                totalReportedSoFarPercentage = if (zeroTotals) BigDecimal.ZERO else BigDecimal.valueOf(10060, 2),
                remainingBudget = if (zeroTotals) BigDecimal.valueOf(-1006) else BigDecimal.valueOf(-6),
            ),
        )

        private val expectedDraftResult = ExpenditureCoFinancingBreakdown(
            funds = listOf(
                ExpenditureCoFinancingBreakdownLine(
                    fundId = 15L,
                    totalEligibleBudget = BigDecimal.valueOf(150),
                    previouslyReported = BigDecimal.valueOf(45),
                    previouslyPaid = BigDecimal.valueOf(0),
                    currentReport = BigDecimal.valueOf(15),
                    totalReportedSoFar = BigDecimal.valueOf(60),
                    totalReportedSoFarPercentage = BigDecimal.valueOf(4000, 2),
                    remainingBudget = BigDecimal.valueOf(90),
                ),
                ExpenditureCoFinancingBreakdownLine(
                    fundId = 16L,
                    totalEligibleBudget = BigDecimal.valueOf(250),
                    previouslyReported = BigDecimal.valueOf(75),
                    previouslyPaid = BigDecimal.valueOf(17),
                    currentReport = BigDecimal.valueOf(25),
                    totalReportedSoFar = BigDecimal.valueOf(100),
                    totalReportedSoFarPercentage = BigDecimal.valueOf(4000, 2),
                    remainingBudget = BigDecimal.valueOf(150),
                ),
                ExpenditureCoFinancingBreakdownLine(
                    fundId = null,
                    totalEligibleBudget = BigDecimal.valueOf(750),
                    previouslyReported = BigDecimal.valueOf(225),
                    previouslyPaid = BigDecimal.valueOf(150),
                    currentReport = BigDecimal.valueOf(75),
                    totalReportedSoFar = BigDecimal.valueOf(300),
                    totalReportedSoFarPercentage = BigDecimal.valueOf(4000, 2),
                    remainingBudget = BigDecimal.valueOf(450),
                ),
            ),
            partnerContribution = ExpenditureCoFinancingBreakdownLine(
                fundId = null,
                totalEligibleBudget = BigDecimal.valueOf(900),
                previouslyReported = BigDecimal.valueOf(2),
                previouslyPaid = BigDecimal.valueOf(0),
                currentReport = BigDecimal.valueOf(50),
                totalReportedSoFar = BigDecimal.valueOf(52),
                totalReportedSoFarPercentage = BigDecimal.valueOf(578, 2),
                remainingBudget = BigDecimal.valueOf(848),
            ),
            publicContribution = ExpenditureCoFinancingBreakdownLine(
                fundId = null,
                totalEligibleBudget = BigDecimal.valueOf(200),
                previouslyReported = BigDecimal.valueOf(3),
                previouslyPaid = BigDecimal.valueOf(0),
                currentReport = BigDecimal.valueOf(100),
                totalReportedSoFar = BigDecimal.valueOf(103),
                totalReportedSoFarPercentage = BigDecimal.valueOf(5150, 2),
                remainingBudget = BigDecimal.valueOf(97),
            ),
            automaticPublicContribution = ExpenditureCoFinancingBreakdownLine(
                fundId = null,
                totalEligibleBudget = BigDecimal.valueOf(300),
                previouslyReported = BigDecimal.valueOf(4),
                previouslyPaid = BigDecimal.valueOf(0),
                currentReport = BigDecimal.valueOf(150),
                totalReportedSoFar = BigDecimal.valueOf(154),
                totalReportedSoFarPercentage = BigDecimal.valueOf(5133, 2),
                remainingBudget = BigDecimal.valueOf(146),
            ),
            privateContribution = ExpenditureCoFinancingBreakdownLine(
                fundId = null,
                totalEligibleBudget = BigDecimal.valueOf(400),
                previouslyReported = BigDecimal.valueOf(5),
                previouslyPaid = BigDecimal.valueOf(0),
                currentReport = BigDecimal.valueOf(200),
                totalReportedSoFar = BigDecimal.valueOf(205),
                totalReportedSoFarPercentage = BigDecimal.valueOf(5125, 2),
                remainingBudget = BigDecimal.valueOf(195),
            ),
            total = ExpenditureCoFinancingBreakdownLine(
                fundId = null,
                totalEligibleBudget = BigDecimal.valueOf(1000),
                previouslyReported = BigDecimal.valueOf(6),
                previouslyPaid = BigDecimal.valueOf(320),
                currentReport = BigDecimal.valueOf(250),
                totalReportedSoFar = BigDecimal.valueOf(256),
                totalReportedSoFarPercentage = BigDecimal.valueOf(2560, 2),
                remainingBudget = BigDecimal.valueOf(744),
            ),
        )
    }

    @MockK
    lateinit var reportPersistence: ProjectReportPersistence
    @MockK
    lateinit var reportExpenditureCoFinancingPersistence: ProjectReportExpenditureCoFinancingPersistence
    @MockK
    lateinit var reportExpenditureCostCategoryCalculatorService: GetReportExpenditureCostCategoryCalculatorService
    @MockK
    lateinit var reportContributionPersistence: ProjectReportContributionPersistence

    @InjectMockKs
    lateinit var interactor: GetReportExpenditureCoFinancingBreakdown

    @BeforeEach
    fun resetMocks() {
        clearMocks(reportPersistence)
        clearMocks(reportExpenditureCoFinancingPersistence)
        clearMocks(reportExpenditureCostCategoryCalculatorService)
        clearMocks(reportContributionPersistence)
    }

    @Test
    fun `get - not submitted`() {
        val reportId = 97658L
        every { reportPersistence.getPartnerReportById(partnerId = PARTNER_ID, reportId) } returns report(reportId, ReportStatus.Draft)
        every { reportExpenditureCoFinancingPersistence.getCoFinancing(PARTNER_ID, reportId = reportId) } returns coFinancing

        val currentCalculation = mockk<ExpenditureCostCategoryBreakdown>()
        every { currentCalculation.total } returns total
        every { reportExpenditureCostCategoryCalculatorService.getSubmittedOrCalculateCurrent(PARTNER_ID, reportId = reportId) } returns currentCalculation

        every { reportContributionPersistence.getPartnerReportContribution(PARTNER_ID, reportId = reportId) } returns partnerContribution
        assertThat(interactor.get(PARTNER_ID, reportId = reportId)).isEqualTo(expectedSubmittedResult())
    }

    @Test
    fun `get - submitted`() {
        val reportId = 94248L
        every { reportPersistence.getPartnerReportById(partnerId = PARTNER_ID, reportId) } returns report(reportId, ReportStatus.Submitted)
        every { reportExpenditureCoFinancingPersistence.getCoFinancing(PARTNER_ID, reportId = reportId) } returns coFinancing

        assertThat(interactor.get(PARTNER_ID, reportId = reportId)).isEqualTo(expectedDraftResult)

        verify(exactly = 0) { reportExpenditureCostCategoryCalculatorService.getSubmittedOrCalculateCurrent(any(), any()) }
        verify(exactly = 0) { reportContributionPersistence.getPartnerReportContribution(any(), any()) }
    }

    @Test
    fun `get - not submitted - zero total partner budget`() {
        val reportId = 24597L
        every { reportPersistence.getPartnerReportById(partnerId = PARTNER_ID, reportId) } returns report(reportId, ReportStatus.Draft)
        every { reportExpenditureCoFinancingPersistence.getCoFinancing(PARTNER_ID, reportId = reportId) } returns
            coFinancing.copy(totalsFromAF = ReportExpenditureCoFinancingColumn(
                funds = coFinancing.totalsFromAF.funds,
                partnerContribution = BigDecimal.ZERO,
                publicContribution = BigDecimal.ZERO,
                automaticPublicContribution = BigDecimal.ZERO,
                privateContribution = BigDecimal.ZERO,
                sum = BigDecimal.ZERO,
            ))

        val currentCalculation = mockk<ExpenditureCostCategoryBreakdown>()
        every { currentCalculation.total } returns total
        every { reportExpenditureCostCategoryCalculatorService.getSubmittedOrCalculateCurrent(PARTNER_ID, reportId = reportId) } returns currentCalculation

        every { reportContributionPersistence.getPartnerReportContribution(PARTNER_ID, reportId = reportId) } returns partnerContribution
        assertThat(interactor.get(PARTNER_ID, reportId = reportId)).isEqualTo(expectedSubmittedResult(zeroTotals = true))
    }

    @Test
    fun `get - not submitted - missing funding`() {
        val reportId = 52044L
        every { reportPersistence.getPartnerReportById(partnerId = PARTNER_ID, reportId) } returns report(reportId, ReportStatus.Draft)
        every { reportExpenditureCoFinancingPersistence.getCoFinancing(PARTNER_ID, reportId = reportId) } returns
            coFinancing.copy(totalsFromAF = ReportExpenditureCoFinancingColumn(
                funds = emptyMap(),
                partnerContribution = coFinancing.totalsFromAF.partnerContribution,
                publicContribution = coFinancing.totalsFromAF.publicContribution,
                automaticPublicContribution = coFinancing.totalsFromAF.automaticPublicContribution,
                privateContribution = coFinancing.totalsFromAF.privateContribution,
                sum = coFinancing.totalsFromAF.sum,
            ))

        val currentCalculation = mockk<ExpenditureCostCategoryBreakdown>()
        every { currentCalculation.total } returns total
        every { reportExpenditureCostCategoryCalculatorService.getSubmittedOrCalculateCurrent(PARTNER_ID, reportId = reportId) } returns currentCalculation

        every { reportContributionPersistence.getPartnerReportContribution(PARTNER_ID, reportId = reportId) } returns partnerContribution
        assertThat(interactor.get(PARTNER_ID, reportId = reportId)).isEqualTo(expectedSubmittedResult().copy(funds = emptyList()))
    }

}
