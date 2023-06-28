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
import io.cloudflight.jems.server.project.service.report.model.partner.PartnerReportIdentification
import io.cloudflight.jems.server.project.service.report.model.partner.ProjectPartnerReport
import io.cloudflight.jems.server.project.service.report.model.partner.ReportStatus
import io.cloudflight.jems.server.project.service.report.model.partner.contribution.withoutCalculations.ProjectPartnerReportEntityContribution
import io.cloudflight.jems.server.project.service.report.model.partner.financialOverview.coFinancing.ExpenditureCoFinancingBreakdown
import io.cloudflight.jems.server.project.service.report.model.partner.financialOverview.coFinancing.ExpenditureCoFinancingBreakdownLine
import io.cloudflight.jems.server.project.service.report.model.partner.financialOverview.coFinancing.ReportExpenditureCoFinancing
import io.cloudflight.jems.server.project.service.report.model.partner.financialOverview.coFinancing.ReportExpenditureCoFinancingColumn
import io.cloudflight.jems.server.project.service.report.model.partner.financialOverview.costCategory.ExpenditureCostCategoryBreakdown
import io.cloudflight.jems.server.project.service.report.model.partner.financialOverview.costCategory.ExpenditureCostCategoryBreakdownLine
import io.cloudflight.jems.server.project.service.report.partner.ProjectPartnerReportPersistence
import io.cloudflight.jems.server.project.service.report.partner.contribution.ProjectPartnerReportContributionPersistence
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.ProjectPartnerReportExpenditureCoFinancingPersistence
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.getReportExpenditureBreakdown.GetReportExpenditureCostCategoryCalculatorService
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.verify
import java.math.BigDecimal
import java.time.ZonedDateTime
import java.util.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class GetReportExpenditureCoFinancingBreakdownCalculatorTest : UnitTest() {

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
                firstSubmission = LAST_YEAR,
                lastResubmission = null,
                controlEnd = null,
                lastControlReopening = null,
                projectReportId = 26L,
                projectReportNumber = 260,
                identification = identification,
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
            currentlyReportedReIncluded = ReportExpenditureCoFinancingColumn(
                funds = mapOf(
                    15L to BigDecimal.valueOf(7L),
                    16L to BigDecimal.valueOf(10L),
                    null to BigDecimal.valueOf(21L),
                ),
                partnerContribution = BigDecimal.valueOf(50),
                publicContribution = BigDecimal.valueOf(100),
                automaticPublicContribution = BigDecimal.valueOf(150),
                privateContribution = BigDecimal.valueOf(200),
                sum = BigDecimal.valueOf(250),
            ),
            totalEligibleAfterControl = ReportExpenditureCoFinancingColumn(
                funds = mapOf(
                    15L to BigDecimal.valueOf(15L),
                    16L to BigDecimal.valueOf(20L),
                    null to BigDecimal.valueOf(70L),
                ),
                partnerContribution = BigDecimal.valueOf(51),
                publicContribution = BigDecimal.valueOf(101),
                automaticPublicContribution = BigDecimal.valueOf(151),
                privateContribution = BigDecimal.valueOf(201),
                sum = BigDecimal.valueOf(251),
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
            previouslyReportedParked = ReportExpenditureCoFinancingColumn(
                funds = mapOf(
                    15L to BigDecimal.valueOf(22L),
                    16L to BigDecimal.valueOf(30L),
                    null to BigDecimal.valueOf(100L),
                ),
                partnerContribution = BigDecimal.valueOf(1),
                publicContribution = BigDecimal.valueOf(2),
                automaticPublicContribution = BigDecimal.valueOf(3),
                privateContribution = BigDecimal.valueOf(4),
                sum = BigDecimal.valueOf(5),
            ),
            previouslyValidated = ReportExpenditureCoFinancingColumn(
                funds = mapOf(
                    15L to BigDecimal.valueOf(455L, 1),
                    16L to BigDecimal.valueOf(755L, 1),
                    null to BigDecimal.valueOf(2255L, 1),
                ),
                partnerContribution = BigDecimal.valueOf(25L, 1),
                publicContribution = BigDecimal.valueOf(35L, 1),
                automaticPublicContribution = BigDecimal.valueOf(45L, 1),
                privateContribution = BigDecimal.valueOf(55L, 1),
                sum = BigDecimal.valueOf(65L, 1),
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
            previouslyReportedParked = BigDecimal.valueOf(-1),
            currentReport = BigDecimal.valueOf(1000),
            currentReportReIncluded = BigDecimal.valueOf(1000),
            totalEligibleAfterControl = BigDecimal.valueOf(950),
            previouslyValidated = BigDecimal.valueOf(5)
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
                    previouslyReportedParked = BigDecimal.valueOf(22),
                    currentReport = BigDecimal.valueOf(15000, 2),
                    currentReportReIncluded = BigDecimal.valueOf(15000, 2),
                    totalEligibleAfterControl = BigDecimal.valueOf(15),
                    totalReportedSoFar = BigDecimal.valueOf(19500, 2),
                    totalReportedSoFarPercentage = BigDecimal.valueOf(13000, 2),
                    remainingBudget = BigDecimal.valueOf(-4500, 2),
                    previouslyValidated = BigDecimal.valueOf(455L, 1),
                    previouslyPaid = BigDecimal.valueOf(0),
                ),
                ExpenditureCoFinancingBreakdownLine(
                    fundId = 16L,
                    totalEligibleBudget = BigDecimal.valueOf(250),
                    previouslyReported = BigDecimal.valueOf(75),
                    previouslyReportedParked = BigDecimal.valueOf(30),
                    currentReport = BigDecimal.valueOf(25000, 2),
                    currentReportReIncluded = BigDecimal.valueOf(25000, 2),
                    totalEligibleAfterControl = BigDecimal.valueOf(20),
                    totalReportedSoFar = BigDecimal.valueOf(32500, 2),
                    totalReportedSoFarPercentage = BigDecimal.valueOf(13000, 2),
                    remainingBudget = BigDecimal.valueOf(-7500, 2),
                    previouslyValidated = BigDecimal.valueOf(755L, 1),
                    previouslyPaid = BigDecimal.valueOf(17),
                ),
                ExpenditureCoFinancingBreakdownLine(
                    fundId = null,
                    totalEligibleBudget = BigDecimal.valueOf(750),
                    previouslyReported = BigDecimal.valueOf(225),
                    previouslyReportedParked = BigDecimal.valueOf(100),
                    currentReport = BigDecimal.valueOf(60000, 2),
                    currentReportReIncluded = BigDecimal.valueOf(60000, 2),
                    totalEligibleAfterControl = BigDecimal.valueOf(70),
                    totalReportedSoFar = BigDecimal.valueOf(82500, 2),
                    totalReportedSoFarPercentage = BigDecimal.valueOf(11000, 2),
                    remainingBudget = BigDecimal.valueOf(-7500, 2),
                    previouslyValidated = BigDecimal.valueOf(2255L, 1),
                    previouslyPaid = BigDecimal.valueOf(150),
                ),
            ),
            partnerContribution = ExpenditureCoFinancingBreakdownLine(
                fundId = null,
                totalEligibleBudget = if (zeroTotals) BigDecimal.ZERO else BigDecimal.valueOf(900),
                previouslyReported = BigDecimal.valueOf(2),
                previouslyReportedParked = BigDecimal.valueOf(1),
                currentReport = BigDecimal.valueOf(60000, 2),
                currentReportReIncluded = BigDecimal.valueOf(60000, 2),
                totalEligibleAfterControl = BigDecimal.valueOf(51),
                totalReportedSoFar = BigDecimal.valueOf(60200, 2),
                totalReportedSoFarPercentage = if (zeroTotals) BigDecimal.ZERO else BigDecimal.valueOf(6689, 2),
                remainingBudget = if (zeroTotals) BigDecimal.valueOf(-60200, 2) else BigDecimal.valueOf(29800, 2),
                previouslyValidated = BigDecimal.valueOf(25L, 1),
                previouslyPaid = BigDecimal.valueOf(0),
            ),
            publicContribution = ExpenditureCoFinancingBreakdownLine(
                fundId = null,
                totalEligibleBudget = if (zeroTotals) BigDecimal.ZERO else BigDecimal.valueOf(200),
                previouslyReported = BigDecimal.valueOf(3),
                previouslyReportedParked = BigDecimal.valueOf(2),
                currentReport = BigDecimal.valueOf(2000, 2),
                currentReportReIncluded = BigDecimal.valueOf(2000, 2),
                totalEligibleAfterControl = BigDecimal.valueOf(101),
                totalReportedSoFar = BigDecimal.valueOf(2300, 2),
                totalReportedSoFarPercentage = if (zeroTotals) BigDecimal.ZERO else BigDecimal.valueOf(1150, 2),
                remainingBudget = if (zeroTotals) BigDecimal.valueOf(-2300, 2) else BigDecimal.valueOf(17700, 2),
                previouslyValidated = BigDecimal.valueOf(35L, 1),
                previouslyPaid = BigDecimal.valueOf(0),
            ),
            automaticPublicContribution = ExpenditureCoFinancingBreakdownLine(
                fundId = null,
                totalEligibleBudget = if (zeroTotals) BigDecimal.ZERO else BigDecimal.valueOf(300),
                previouslyReported = BigDecimal.valueOf(4),
                previouslyReportedParked = BigDecimal.valueOf(3),
                currentReport = BigDecimal.valueOf(3000, 2),
                currentReportReIncluded = BigDecimal.valueOf(3000, 2),
                totalEligibleAfterControl = BigDecimal.valueOf(151),
                totalReportedSoFar = BigDecimal.valueOf(3400, 2),
                totalReportedSoFarPercentage = if (zeroTotals) BigDecimal.ZERO else BigDecimal.valueOf(1133, 2),
                remainingBudget = if (zeroTotals) BigDecimal.valueOf(-3400, 2) else BigDecimal.valueOf(26600, 2),
                previouslyValidated = BigDecimal.valueOf(45L, 1),
                previouslyPaid = BigDecimal.valueOf(0),
            ),
            privateContribution = ExpenditureCoFinancingBreakdownLine(
                fundId = null,
                totalEligibleBudget = if (zeroTotals) BigDecimal.ZERO else BigDecimal.valueOf(400),
                previouslyReported = BigDecimal.valueOf(5),
                previouslyReportedParked = BigDecimal.valueOf(4),
                currentReport = BigDecimal.valueOf(4000, 2),
                currentReportReIncluded = BigDecimal.valueOf(4000, 2),
                totalEligibleAfterControl = BigDecimal.valueOf(201),
                totalReportedSoFar = BigDecimal.valueOf(4500, 2),
                totalReportedSoFarPercentage = if (zeroTotals) BigDecimal.ZERO else BigDecimal.valueOf(1125, 2),
                remainingBudget = if (zeroTotals) BigDecimal.valueOf(-4500, 2) else BigDecimal.valueOf(35500, 2),
                previouslyValidated = BigDecimal.valueOf(55L, 1),
                previouslyPaid = BigDecimal.valueOf(0),
            ),
            total = ExpenditureCoFinancingBreakdownLine(
                fundId = null,
                totalEligibleBudget = if (zeroTotals) BigDecimal.ZERO else BigDecimal.valueOf(1000),
                previouslyReported = BigDecimal.valueOf(6),
                previouslyReportedParked = BigDecimal.valueOf(5),
                currentReport = BigDecimal.valueOf(1000),
                currentReportReIncluded = BigDecimal.valueOf(1000),
                totalEligibleAfterControl = BigDecimal.valueOf(251),
                totalReportedSoFar = BigDecimal.valueOf(1006),
                totalReportedSoFarPercentage = if (zeroTotals) BigDecimal.ZERO else BigDecimal.valueOf(10060, 2),
                remainingBudget = if (zeroTotals) BigDecimal.valueOf(-1006) else BigDecimal.valueOf(-6),
                previouslyValidated = BigDecimal.valueOf(65L, 1),
                previouslyPaid = BigDecimal.valueOf(320),
            ),
        )

        private val expectedDraftResult = ExpenditureCoFinancingBreakdown(
            funds = listOf(
                ExpenditureCoFinancingBreakdownLine(
                    fundId = 15L,
                    totalEligibleBudget = BigDecimal.valueOf(150),
                    previouslyReported = BigDecimal.valueOf(45),
                    previouslyReportedParked = BigDecimal.valueOf(22),
                    currentReport = BigDecimal.valueOf(15),
                    currentReportReIncluded = BigDecimal.valueOf(7),
                    totalEligibleAfterControl = BigDecimal.valueOf(15),
                    totalReportedSoFar = BigDecimal.valueOf(60),
                    totalReportedSoFarPercentage = BigDecimal.valueOf(4000, 2),
                    remainingBudget = BigDecimal.valueOf(90),
                    previouslyValidated = BigDecimal.valueOf(455L, 1),
                    previouslyPaid = BigDecimal.valueOf(0),
                ),
                ExpenditureCoFinancingBreakdownLine(
                    fundId = 16L,
                    totalEligibleBudget = BigDecimal.valueOf(250),
                    previouslyReported = BigDecimal.valueOf(75),
                    previouslyReportedParked = BigDecimal.valueOf(30),
                    currentReport = BigDecimal.valueOf(25),
                    currentReportReIncluded = BigDecimal.valueOf(10),
                    totalEligibleAfterControl = BigDecimal.valueOf(20),
                    totalReportedSoFar = BigDecimal.valueOf(100),
                    totalReportedSoFarPercentage = BigDecimal.valueOf(4000, 2),
                    remainingBudget = BigDecimal.valueOf(150),
                    previouslyValidated = BigDecimal.valueOf(755L, 1),
                    previouslyPaid = BigDecimal.valueOf(17),
                ),
                ExpenditureCoFinancingBreakdownLine(
                    fundId = null,
                    totalEligibleBudget = BigDecimal.valueOf(750),
                    previouslyReported = BigDecimal.valueOf(225),
                    previouslyReportedParked = BigDecimal.valueOf(100),
                    currentReport = BigDecimal.valueOf(75),
                    currentReportReIncluded = BigDecimal.valueOf(21),
                    totalEligibleAfterControl = BigDecimal.valueOf(70),
                    totalReportedSoFar = BigDecimal.valueOf(300),
                    totalReportedSoFarPercentage = BigDecimal.valueOf(4000, 2),
                    remainingBudget = BigDecimal.valueOf(450),
                    previouslyValidated = BigDecimal.valueOf(2255L, 1),
                    previouslyPaid = BigDecimal.valueOf(150),
                ),
            ),
            partnerContribution = ExpenditureCoFinancingBreakdownLine(
                fundId = null,
                totalEligibleBudget = BigDecimal.valueOf(900),
                previouslyReported = BigDecimal.valueOf(2),
                previouslyReportedParked = BigDecimal.valueOf(1),
                currentReport = BigDecimal.valueOf(50),
                currentReportReIncluded = BigDecimal.valueOf(50),
                totalEligibleAfterControl = BigDecimal.valueOf(51),
                totalReportedSoFar = BigDecimal.valueOf(52),
                totalReportedSoFarPercentage = BigDecimal.valueOf(578, 2),
                remainingBudget = BigDecimal.valueOf(848),
                previouslyValidated = BigDecimal.valueOf(25L, 1),
                previouslyPaid = BigDecimal.valueOf(0),
            ),
            publicContribution = ExpenditureCoFinancingBreakdownLine(
                fundId = null,
                totalEligibleBudget = BigDecimal.valueOf(200),
                previouslyReported = BigDecimal.valueOf(3),
                previouslyReportedParked = BigDecimal.valueOf(2),
                currentReport = BigDecimal.valueOf(100),
                currentReportReIncluded = BigDecimal.valueOf(100),
                totalEligibleAfterControl = BigDecimal.valueOf(101),
                totalReportedSoFar = BigDecimal.valueOf(103),
                totalReportedSoFarPercentage = BigDecimal.valueOf(5150, 2),
                remainingBudget = BigDecimal.valueOf(97),
                previouslyValidated = BigDecimal.valueOf(35L, 1),
                previouslyPaid = BigDecimal.valueOf(0),
            ),
            automaticPublicContribution = ExpenditureCoFinancingBreakdownLine(
                fundId = null,
                totalEligibleBudget = BigDecimal.valueOf(300),
                previouslyReported = BigDecimal.valueOf(4),
                previouslyReportedParked = BigDecimal.valueOf(3),
                currentReport = BigDecimal.valueOf(150),
                currentReportReIncluded = BigDecimal.valueOf(150),
                totalEligibleAfterControl = BigDecimal.valueOf(151),
                totalReportedSoFar = BigDecimal.valueOf(154),
                totalReportedSoFarPercentage = BigDecimal.valueOf(5133, 2),
                remainingBudget = BigDecimal.valueOf(146),
                previouslyValidated = BigDecimal.valueOf(45L, 1),
                previouslyPaid = BigDecimal.valueOf(0),
            ),
            privateContribution = ExpenditureCoFinancingBreakdownLine(
                fundId = null,
                totalEligibleBudget = BigDecimal.valueOf(400),
                previouslyReported = BigDecimal.valueOf(5),
                previouslyReportedParked = BigDecimal.valueOf(4),
                currentReport = BigDecimal.valueOf(200),
                currentReportReIncluded = BigDecimal.valueOf(200),
                totalEligibleAfterControl = BigDecimal.valueOf(201),
                totalReportedSoFar = BigDecimal.valueOf(205),
                totalReportedSoFarPercentage = BigDecimal.valueOf(5125, 2),
                remainingBudget = BigDecimal.valueOf(195),
                previouslyValidated = BigDecimal.valueOf(55L, 1),
                previouslyPaid = BigDecimal.valueOf(0),
            ),
            total = ExpenditureCoFinancingBreakdownLine(
                fundId = null,
                totalEligibleBudget = BigDecimal.valueOf(1000),
                previouslyReported = BigDecimal.valueOf(6),
                previouslyReportedParked = BigDecimal.valueOf(5),
                currentReport = BigDecimal.valueOf(250),
                currentReportReIncluded = BigDecimal.valueOf(250),
                totalEligibleAfterControl = BigDecimal.valueOf(251),
                totalReportedSoFar = BigDecimal.valueOf(256),
                totalReportedSoFarPercentage = BigDecimal.valueOf(2560, 2),
                remainingBudget = BigDecimal.valueOf(744),
                previouslyValidated = BigDecimal.valueOf(65L, 1),
                previouslyPaid = BigDecimal.valueOf(320),
            ),
        )

        private val expectedRoundingTestResult = ExpenditureCoFinancingBreakdown(
            funds = listOf(
                ExpenditureCoFinancingBreakdownLine(
                    fundId = 18L,
                    totalEligibleBudget = BigDecimal.valueOf(4_620_000_000L),
                    previouslyReported = BigDecimal.ZERO,
                    previouslyReportedParked = BigDecimal.ZERO,
                    currentReport = BigDecimal.valueOf(660_000_000_000_000_00L, 2),
                    currentReportReIncluded = BigDecimal.valueOf(1_320_000_000_000_000_01L, 2),
                    totalEligibleAfterControl = BigDecimal.valueOf(1),
                    totalReportedSoFar = BigDecimal.valueOf(660_000_000_000_000_00L, 2),
                    totalReportedSoFarPercentage = BigDecimal.valueOf(14_285_714_29L, 2),
                    remainingBudget = BigDecimal.valueOf(-659_995_380_000_000_00L, 2),
                    previouslyValidated = BigDecimal.ZERO,
                    previouslyPaid = BigDecimal.ZERO,
                ),
                ExpenditureCoFinancingBreakdownLine(
                    fundId = null,
                    totalEligibleBudget = BigDecimal.valueOf(2_380_000_000L),
                    previouslyReported = BigDecimal.ZERO,
                    previouslyReportedParked = BigDecimal.ZERO,
                    currentReport = BigDecimal.valueOf(340_000_000_000_000_01L, 2),
                    currentReportReIncluded = BigDecimal.valueOf(680_000_000_000_000_01L, 2),
                    totalEligibleAfterControl = BigDecimal.valueOf(2),
                    totalReportedSoFar = BigDecimal.valueOf(340_000_000_000_000_01L, 2),
                    totalReportedSoFarPercentage = BigDecimal.valueOf(14_285_714_29L, 2),
                    remainingBudget = BigDecimal.valueOf(-339_997_620_000_000_01L, 2),
                    previouslyValidated = BigDecimal.ZERO,
                    previouslyPaid = BigDecimal.ZERO,
                ),
            ),
            partnerContribution = ExpenditureCoFinancingBreakdownLine(
                fundId = null,
                totalEligibleBudget = BigDecimal.valueOf(2_380_000_000L),
                previouslyReported = BigDecimal.ZERO,
                previouslyReportedParked = BigDecimal.ZERO,
                currentReport = BigDecimal.valueOf(340_000_000_000_000_01L, 2),
                currentReportReIncluded = BigDecimal.valueOf(680_000_000_000_000_01L, 2),
                totalEligibleAfterControl = BigDecimal.valueOf(3),
                totalReportedSoFar = BigDecimal.valueOf(340_000_000_000_000_01L, 2),
                totalReportedSoFarPercentage = BigDecimal.valueOf(14_285_714_29L, 2),
                remainingBudget = BigDecimal.valueOf(-339_997_620_000_000_01L, 2),
                previouslyValidated = BigDecimal.ZERO,
                previouslyPaid = BigDecimal.ZERO,
            ),
            publicContribution = ExpenditureCoFinancingBreakdownLine(
                fundId = null,
                totalEligibleBudget = BigDecimal.valueOf(2_200_000_000L),
                previouslyReported = BigDecimal.ZERO,
                previouslyReportedParked = BigDecimal.ZERO,
                currentReport = BigDecimal.valueOf(314_285_714_285_714_28L, 2),
                currentReportReIncluded = BigDecimal.valueOf(628_571_428_571_428_57L, 2),
                totalEligibleAfterControl = BigDecimal.valueOf(4),
                totalReportedSoFar = BigDecimal.valueOf(314_285_714_285_714_28L, 2),
                totalReportedSoFarPercentage = BigDecimal.valueOf(14_285_714_29L, 2),
                remainingBudget = BigDecimal.valueOf(-314_283_514_285_714_28L, 2),
                previouslyValidated = BigDecimal.ZERO,
                previouslyPaid = BigDecimal.ZERO,
            ),
            automaticPublicContribution = ExpenditureCoFinancingBreakdownLine(
                fundId = null,
                totalEligibleBudget = BigDecimal.valueOf(180_000_000L),
                previouslyReported = BigDecimal.ZERO,
                previouslyReportedParked = BigDecimal.ZERO,
                currentReport = BigDecimal.valueOf(25_714_285_714_285_71L, 2),
                currentReportReIncluded = BigDecimal.valueOf(51_428_571_428_571_42L, 2),
                totalEligibleAfterControl = BigDecimal.valueOf(5),
                totalReportedSoFar = BigDecimal.valueOf(25_714_285_714_285_71L, 2),
                totalReportedSoFarPercentage = BigDecimal.valueOf(14_285_714_29L, 2),
                remainingBudget = BigDecimal.valueOf(-25_714_105_714_285_71L, 2),
                previouslyValidated = BigDecimal.ZERO,
                previouslyPaid = BigDecimal.ZERO,
            ),
            privateContribution = ExpenditureCoFinancingBreakdownLine(
                fundId = null,
                totalEligibleBudget = BigDecimal.ZERO,
                previouslyReported = BigDecimal.ZERO,
                previouslyReportedParked = BigDecimal.ZERO,
                currentReport = BigDecimal.valueOf(0, 2),
                currentReportReIncluded = BigDecimal.valueOf(0, 2),
                totalEligibleAfterControl = BigDecimal.valueOf(6),
                totalReportedSoFar = BigDecimal.valueOf(0, 2),
                totalReportedSoFarPercentage = BigDecimal.ZERO,
                remainingBudget = BigDecimal.valueOf(0, 2),
                previouslyValidated = BigDecimal.ZERO,
                previouslyPaid = BigDecimal.ZERO,
            ),
            total = ExpenditureCoFinancingBreakdownLine(
                fundId = null,
                totalEligibleBudget = BigDecimal.valueOf(7_000_000_000L),
                previouslyReported = BigDecimal.ZERO,
                previouslyReportedParked = BigDecimal.ZERO,
                currentReport = BigDecimal.valueOf(1_000_000_000_000_000_01L, 2),
                currentReportReIncluded = BigDecimal.valueOf(2_000_000_000_000_000_02L, 2),
                totalEligibleAfterControl = BigDecimal.valueOf(7),
                totalReportedSoFar = BigDecimal.valueOf(1_000_000_000_000_000_01L, 2),
                totalReportedSoFarPercentage = BigDecimal.valueOf(14_285_714_29L, 2),
                remainingBudget = BigDecimal.valueOf(-999_993_000_000_000_01L, 2),
                previouslyValidated = BigDecimal.ZERO,
                previouslyPaid = BigDecimal.ZERO,
            ),
        )

    }

    @MockK
    lateinit var reportPersistence: ProjectPartnerReportPersistence
    @MockK
    lateinit var reportExpenditureCoFinancingPersistence: ProjectPartnerReportExpenditureCoFinancingPersistence
    @MockK
    lateinit var reportExpenditureCostCategoryCalculatorService: GetReportExpenditureCostCategoryCalculatorService
    @MockK
    lateinit var reportContributionPersistence: ProjectPartnerReportContributionPersistence

    @InjectMockKs
    lateinit var calculator: GetReportExpenditureCoFinancingBreakdownCalculator

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
        every {
            reportExpenditureCostCategoryCalculatorService.getSubmittedOrCalculateCurrent(
                PARTNER_ID,
                reportId = reportId,
            )
        } returns currentCalculation

        every { reportContributionPersistence.getPartnerReportContribution(PARTNER_ID, reportId = reportId) } returns partnerContribution
        assertThat(calculator.get(PARTNER_ID, reportId = reportId)).isEqualTo(expectedSubmittedResult())
    }

    @Test
    fun `get - submitted`() {
        val reportId = 94248L
        every { reportPersistence.getPartnerReportById(partnerId = PARTNER_ID, reportId) } returns report(reportId, ReportStatus.Submitted)
        every { reportExpenditureCoFinancingPersistence.getCoFinancing(PARTNER_ID, reportId = reportId) } returns coFinancing

        assertThat(calculator.get(PARTNER_ID, reportId = reportId)).isEqualTo(expectedDraftResult)

        verify(exactly = 0) { reportExpenditureCostCategoryCalculatorService.getSubmittedOrCalculateCurrent(any(), any()) }
        verify(exactly = 0) { reportContributionPersistence.getPartnerReportContribution(any(), any()) }
    }

    @Test
    fun `get - not submitted - zero total partner budget`() {
        val reportId = 24597L
        every { reportPersistence.getPartnerReportById(partnerId = PARTNER_ID, reportId) } returns report(
            reportId,
            ReportStatus.Draft
        )
        every { reportExpenditureCoFinancingPersistence.getCoFinancing(PARTNER_ID, reportId = reportId) } returns
            coFinancing.copy(
                totalsFromAF = ReportExpenditureCoFinancingColumn(
                    funds = coFinancing.totalsFromAF.funds,
                    partnerContribution = BigDecimal.ZERO,
                    publicContribution = BigDecimal.ZERO,
                    automaticPublicContribution = BigDecimal.ZERO,
                    privateContribution = BigDecimal.ZERO,
                    sum = BigDecimal.ZERO,
                )
            )

        val currentCalculation = mockk<ExpenditureCostCategoryBreakdown>()
        every { currentCalculation.total } returns total
        every {
            reportExpenditureCostCategoryCalculatorService.getSubmittedOrCalculateCurrent(
                PARTNER_ID,
                reportId = reportId,
            )
        } returns currentCalculation

        every {
            reportContributionPersistence.getPartnerReportContribution(
                PARTNER_ID,
                reportId = reportId
            )
        } returns partnerContribution
        assertThat(
            calculator.get(
                PARTNER_ID,
                reportId = reportId
            )
        ).isEqualTo(expectedSubmittedResult(zeroTotals = true))
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
        assertThat(calculator.get(PARTNER_ID, reportId = reportId)).isEqualTo(expectedSubmittedResult().copy(funds = emptyList()))
    }

    @Test
    fun `get - not submitted - test rounding on lot of decimal places`() {
        val reportId = 52048L

        val identification = mockk<PartnerReportIdentification>()
        every { identification.coFinancing } returns listOf(
            ProjectPartnerCoFinancing(MainFund, fund(id = 18L), percentage = BigDecimal.valueOf(66)),
            ProjectPartnerCoFinancing(PartnerContribution, null, percentage = BigDecimal.valueOf(34)),
        )

        val report = mockk<ProjectPartnerReport>()
        every { report.status } returns ReportStatus.Draft
        every { report.identification } returns identification

        every { reportPersistence.getPartnerReportById(partnerId = PARTNER_ID, reportId) } returns report
        val coFinancing = ReportExpenditureCoFinancing(
            totalsFromAF = ReportExpenditureCoFinancingColumn(
                funds = mapOf(
                    18L to BigDecimal.valueOf(4_620_000_000L),
                    null to BigDecimal.valueOf(2_380_000_000L),
                ),
                partnerContribution = BigDecimal.valueOf(2_380_000_000L),
                publicContribution = BigDecimal.valueOf(2_200_000_000L),
                automaticPublicContribution = BigDecimal.valueOf(180_000_000L),
                privateContribution = BigDecimal.ZERO,
                sum = BigDecimal.valueOf(7_000_000_000L),
            ),
            currentlyReported = ReportExpenditureCoFinancingColumn(
                funds = mapOf(
                    18L to BigDecimal.valueOf(660_000_000_000_000L),
                    null to BigDecimal.valueOf(340_000_000_000_000L),
                ),
                partnerContribution = BigDecimal.valueOf(340_000_000_000_000L),
                publicContribution = BigDecimal.valueOf(314_285_714_285_714_28L, 2),
                automaticPublicContribution = BigDecimal.valueOf(25_714_285_714_285_71L, 2),
                privateContribution = BigDecimal.ZERO,
                sum = BigDecimal.valueOf(1_000_000_000_000_000_01, 2),
            ),
            currentlyReportedReIncluded = ReportExpenditureCoFinancingColumn(
                funds = mapOf(
                    18L to BigDecimal.valueOf(660_000_000_000_000L),
                    null to BigDecimal.valueOf(340_000_000_000_000L),
                ),
                partnerContribution = BigDecimal.valueOf(340_000_000_000_000L),
                publicContribution = BigDecimal.valueOf(314_285_714_285_714_28L, 2),
                automaticPublicContribution = BigDecimal.valueOf(25_714_285_714_285_71L, 2),
                privateContribution = BigDecimal.ZERO,
                sum = BigDecimal.valueOf(1_000_000_000_000_000_01, 2),
            ),
            totalEligibleAfterControl = ReportExpenditureCoFinancingColumn(
                funds = mapOf(
                    18L to BigDecimal.valueOf(1L),
                    null to BigDecimal.valueOf(2L),
                ),
                partnerContribution = BigDecimal.valueOf(3L),
                publicContribution = BigDecimal.valueOf(4L),
                automaticPublicContribution = BigDecimal.valueOf(5L),
                privateContribution = BigDecimal.valueOf(6),
                sum = BigDecimal.valueOf(7),
            ),
            previouslyReported = ReportExpenditureCoFinancingColumn(
                funds = mapOf(
                    18L to BigDecimal.ZERO,
                    null to BigDecimal.ZERO,
                ),
                partnerContribution = BigDecimal.ZERO,
                publicContribution = BigDecimal.ZERO,
                automaticPublicContribution = BigDecimal.ZERO,
                privateContribution = BigDecimal.ZERO,
                sum = BigDecimal.ZERO,
            ),
            previouslyReportedParked = ReportExpenditureCoFinancingColumn(
                funds = mapOf(
                    18L to BigDecimal.ZERO,
                    null to BigDecimal.ZERO,
                ),
                partnerContribution = BigDecimal.ZERO,
                publicContribution = BigDecimal.ZERO,
                automaticPublicContribution = BigDecimal.ZERO,
                privateContribution = BigDecimal.ZERO,
                sum = BigDecimal.ZERO,
            ),
            previouslyValidated = ReportExpenditureCoFinancingColumn(
                funds = mapOf(
                    18L to BigDecimal.ZERO,
                    null to BigDecimal.ZERO,
                ),
                partnerContribution = BigDecimal.ZERO,
                publicContribution = BigDecimal.ZERO,
                automaticPublicContribution = BigDecimal.ZERO,
                privateContribution = BigDecimal.ZERO,
                sum = BigDecimal.ZERO,
            ),
            previouslyPaid = ReportExpenditureCoFinancingColumn(
                funds = mapOf(
                    18L to BigDecimal.ZERO,
                    null to BigDecimal.ZERO,
                ),
                partnerContribution = BigDecimal.ZERO,
                publicContribution = BigDecimal.ZERO,
                automaticPublicContribution = BigDecimal.ZERO,
                privateContribution = BigDecimal.ZERO,
                sum = BigDecimal.ZERO,
            ),
        )

        every { reportExpenditureCoFinancingPersistence.getCoFinancing(PARTNER_ID, reportId = reportId) } returns coFinancing

        val currentCalculation = mockk<ExpenditureCostCategoryBreakdown>()
        every { currentCalculation.total } returns ExpenditureCostCategoryBreakdownLine(
            flatRate = null,
            totalEligibleBudget = BigDecimal.valueOf(7_000_000_000L),
            previouslyReported = BigDecimal.ZERO,
            previouslyReportedParked = BigDecimal.ZERO,
            currentReport = BigDecimal.valueOf(1_000_000_000_000_000_01L, 2),
            currentReportReIncluded = BigDecimal.valueOf(2_000_000_000_000_000_02L, 2),
            totalEligibleAfterControl = BigDecimal.valueOf(333_333_333_333_333_33L, 2),
            totalReportedSoFar = BigDecimal.valueOf(1_000_000_000_000_000_01L, 2),
            totalReportedSoFarPercentage = BigDecimal.valueOf(14_285_714_29L, 2),
            remainingBudget = BigDecimal.valueOf(-999_993_000_000_000_01L, 2),
            previouslyValidated = BigDecimal.valueOf(5)
        )
        every { reportExpenditureCostCategoryCalculatorService.getSubmittedOrCalculateCurrent(PARTNER_ID, reportId = reportId) } returns currentCalculation

        every { reportContributionPersistence.getPartnerReportContribution(PARTNER_ID, reportId = reportId) } returns listOf(
            contrib(Public, amount = BigDecimal.valueOf(2_200_000_000L), prev = BigDecimal.ZERO, current = BigDecimal.ZERO),
            contrib(AutomaticPublic, amount = BigDecimal.valueOf(180_000_000L), prev = BigDecimal.ZERO, current = BigDecimal.ZERO),
        )
        assertThat(calculator.get(PARTNER_ID, reportId = reportId)).isEqualTo(expectedRoundingTestResult)
    }

}
