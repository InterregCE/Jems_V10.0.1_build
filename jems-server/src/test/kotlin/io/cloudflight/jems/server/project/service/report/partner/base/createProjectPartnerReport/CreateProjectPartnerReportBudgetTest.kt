package io.cloudflight.jems.server.project.service.report.partner.base.createProjectPartnerReport

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.api.project.dto.partner.cofinancing.ProjectPartnerCoFinancingFundTypeDTO.MainFund
import io.cloudflight.jems.api.project.dto.partner.cofinancing.ProjectPartnerCoFinancingFundTypeDTO.PartnerContribution
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.common.file.service.model.JemsFileMetadata
import io.cloudflight.jems.server.payments.model.regular.PaymentPartnerInstallment
import io.cloudflight.jems.server.payments.service.regular.PaymentPersistence
import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFund
import io.cloudflight.jems.server.project.service.budget.get_partner_budget_per_period.GetPartnerBudgetPerPeriodInteractor
import io.cloudflight.jems.server.project.service.budget.get_project_budget.GetProjectBudget
import io.cloudflight.jems.server.project.service.budget.model.BudgetCostsCalculationResultFull
import io.cloudflight.jems.server.project.service.budget.model.ExpenditureCostCategoryPreviouslyReportedWithParked
import io.cloudflight.jems.server.project.service.budget.model.PartnerBudget
import io.cloudflight.jems.server.project.service.contracting.model.reporting.ContractingDeadlineType
import io.cloudflight.jems.server.project.service.lumpsum.ProjectLumpSumPersistence
import io.cloudflight.jems.server.project.service.lumpsum.model.ProjectLumpSum
import io.cloudflight.jems.server.project.service.lumpsum.model.ProjectPartnerLumpSum
import io.cloudflight.jems.server.project.service.model.ProjectBudgetOverviewPerPartnerPerPeriod
import io.cloudflight.jems.server.project.service.model.ProjectPartnerBudgetPerPeriod
import io.cloudflight.jems.server.project.service.model.ProjectPartnerCostType
import io.cloudflight.jems.server.project.service.model.ProjectPeriodBudget
import io.cloudflight.jems.server.project.service.partner.budget.ProjectPartnerBudgetCostsPersistence
import io.cloudflight.jems.server.project.service.partner.budget.ProjectPartnerBudgetOptionsPersistence
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerCoFinancing
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerCoFinancingAndContribution
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerCoFinancingAndContributionSpf
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerContribution
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerContributionSpf
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerContributionStatus
import io.cloudflight.jems.server.project.service.partner.model.BudgetGeneralCostEntry
import io.cloudflight.jems.server.project.service.partner.model.BudgetStaffCostEntry
import io.cloudflight.jems.server.project.service.partner.model.BudgetTravelAndAccommodationCostEntry
import io.cloudflight.jems.server.project.service.partner.model.BudgetUnitCostEntry
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerBudgetOptions
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerSummary
import io.cloudflight.jems.server.project.service.report.model.partner.ProjectPartnerReportStatusAndVersion
import io.cloudflight.jems.server.project.service.report.model.partner.ReportStatus
import io.cloudflight.jems.server.project.service.report.model.partner.base.create.PartnerReportCoFinancing
import io.cloudflight.jems.server.project.service.report.model.partner.base.create.PartnerReportInvestment
import io.cloudflight.jems.server.project.service.report.model.partner.base.create.PartnerReportLumpSum
import io.cloudflight.jems.server.project.service.report.model.partner.base.create.PreviouslyReportedCoFinancing
import io.cloudflight.jems.server.project.service.report.model.partner.base.create.PreviouslyReportedFund
import io.cloudflight.jems.server.project.service.report.model.partner.contribution.create.CreateProjectPartnerReportContribution
import io.cloudflight.jems.server.project.service.report.model.partner.contribution.withoutCalculations.ProjectPartnerReportEntityContribution
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.PartnerReportInvestmentSummary
import io.cloudflight.jems.server.project.service.report.model.partner.financialOverview.coFinancing.ExpenditureCoFinancingPrevious
import io.cloudflight.jems.server.project.service.report.model.partner.financialOverview.coFinancing.ReportExpenditureCoFinancingColumn
import io.cloudflight.jems.server.project.service.report.model.partner.financialOverview.investments.ExpenditureInvestmentCurrent
import io.cloudflight.jems.server.project.service.report.model.partner.financialOverview.lumpSum.ExpenditureLumpSumCurrent
import io.cloudflight.jems.server.project.service.report.model.partner.financialOverview.unitCost.ExpenditureUnitCostCurrent
import io.cloudflight.jems.server.project.service.report.model.partner.identification.ProjectPartnerReportPeriod
import io.cloudflight.jems.server.project.service.report.model.project.ProjectReportStatus
import io.cloudflight.jems.server.project.service.report.model.project.base.create.ProjectReportStatusAndType
import io.cloudflight.jems.server.project.service.report.model.project.financialOverview.coFinancing.ReportCertificateCoFinancingColumn
import io.cloudflight.jems.server.project.service.report.partner.ProjectPartnerReportPersistence
import io.cloudflight.jems.server.project.service.report.partner.contribution.ProjectPartnerReportContributionPersistence
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.ProjectPartnerReportExpenditureCoFinancingPersistence
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.ProjectPartnerReportExpenditureCostCategoryPersistence
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.ProjectPartnerReportInvestmentPersistence
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.ProjectPartnerReportLumpSumPersistence
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.ProjectPartnerReportUnitCostPersistence
import io.cloudflight.jems.server.project.service.report.project.base.ProjectReportPersistence
import io.cloudflight.jems.server.project.service.report.project.spfContributionClaim.ProjectReportSpfContributionClaimPersistence
import io.cloudflight.jems.server.project.service.workpackage.model.InvestmentSummary
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.util.UUID

internal class CreateProjectPartnerReportBudgetTest : UnitTest() {

    private val HISTORY_CONTRIBUTION_UUID_1 = UUID.randomUUID()
    private val HISTORY_CONTRIBUTION_UUID_2 = UUID.randomUUID()
    private val HISTORY_CONTRIBUTION_UUID_3 = UUID.randomUUID()
    private val SUBMITTED_PROJECT_REPORT_ID = 8857L

    private val contribPartner = ProjectPartnerContribution(
        id = 100L,
        name = "A",
        status = null,
        amount = BigDecimal.ONE,
        isPartner = true,
    )

    private val contribNonPartner1 = ProjectPartnerContribution(
        id = null,
        name = "B",
        status = null,
        amount = BigDecimal.ONE,
        isPartner = false,
    )

    private val contribNonPartner2 = ProjectPartnerContribution(
        id = 300L,
        name = "C - this will be merged with contribution id=3",
        status = ProjectPartnerContributionStatus.AutomaticPublic,
        amount = BigDecimal.ONE,
        isPartner = false,
    )

    private val contribPartnerSpf = ProjectPartnerContributionSpf(
        id = 110L,
        name = "G",
        status = null,
        amount = BigDecimal.ONE,
    )

    private val contribNonPartner1Spf = ProjectPartnerContributionSpf(
        id = null,
        name = "H",
        status = null,
        amount = BigDecimal.ONE,
    )

    private val contribNonPartner2Spf = ProjectPartnerContributionSpf(
        id = 310L,
        name = "I",
        status = ProjectPartnerContributionStatus.AutomaticPublic,
        amount = BigDecimal.ONE,
    )

    private val fund = mockk<ProgrammeFund>().also {
        every { it.id } returns 8L
    }

    private val coFinancing = PartnerReportCoFinancing(
        coFinancing = ProjectPartnerCoFinancingAndContribution(
            finances = listOf(
                ProjectPartnerCoFinancing(MainFund, fund, BigDecimal.valueOf(30)),
                ProjectPartnerCoFinancing(PartnerContribution, null, BigDecimal.valueOf(70)),
            ),
            partnerContributions = listOf(contribNonPartner2, contribPartner, contribNonPartner1),
            partnerAbbreviation = "not needed",
        ),
        coFinancingSpf = ProjectPartnerCoFinancingAndContributionSpf(
            finances = listOf(
                ProjectPartnerCoFinancing(MainFund, fund, BigDecimal.valueOf(42)),
                ProjectPartnerCoFinancing(PartnerContribution, null, BigDecimal.valueOf(58)),
            ),
            partnerContributions = listOf(contribNonPartner2Spf, contribPartnerSpf, contribNonPartner1Spf),
        ),
    )

    private val investments = listOf(
        PartnerReportInvestmentSummary(
            investmentId = 485L,
            investmentNumber = 5,
            workPackageNumber = 2,
            title = setOf(InputTranslation(SystemLanguage.EN, "investment title EN")),
            deactivated = false,
        ),
    )

    private val previousContributions = listOf(
        ProjectPartnerReportEntityContribution(
            id = 1L,
            reportId = 408L,
            sourceOfContribution = "old source, should be not forgotten",
            legalStatus = ProjectPartnerContributionStatus.Public, // should also be ignored
            idFromApplicationForm = 200L,
            historyIdentifier = HISTORY_CONTRIBUTION_UUID_1,
            createdInThisReport = false,
            amount = BigDecimal.ZERO, // should be ignored
            previouslyReported = BigDecimal.ZERO,
            currentlyReported = BigDecimal.valueOf(444L),
            attachment = JemsFileMetadata(780L, "this_is_ignored", mockk()),
        ),
        ProjectPartnerReportEntityContribution(
            id = 2L,
            reportId = 408L,
            sourceOfContribution = "this has been added inside reporting (not linked to AF)",
            legalStatus = ProjectPartnerContributionStatus.Private,
            idFromApplicationForm = null,
            historyIdentifier = HISTORY_CONTRIBUTION_UUID_2,
            createdInThisReport = true,
            amount = BigDecimal.ZERO,
            previouslyReported = BigDecimal.ZERO,
            currentlyReported = BigDecimal.ONE,
            attachment = null,
        ),
        ProjectPartnerReportEntityContribution(
            id = 3L,
            reportId = 408L,
            sourceOfContribution = "this is coming from AF",
            legalStatus = ProjectPartnerContributionStatus.Private,
            idFromApplicationForm = 300L,
            historyIdentifier = HISTORY_CONTRIBUTION_UUID_3,
            createdInThisReport = true,
            amount = BigDecimal.ZERO,
            previouslyReported = BigDecimal.ZERO,
            currentlyReported = BigDecimal.TEN,
            attachment = null,
        ),
    )

    private fun lumpSums(partnerId: Long) = listOf(
        ProjectLumpSum(
            orderNr = 14,
            programmeLumpSumId = 44L,
            period = 3,
            lumpSumContributions = listOf(
                ProjectPartnerLumpSum(
                    partnerId = partnerId,
                    amount = BigDecimal.TEN,
                ),
            ),
            fastTrack = false,
            readyForPayment = false,
        ),
        ProjectLumpSum(
            orderNr = 15,
            programmeLumpSumId = 45L,
            period = 4,
            lumpSumContributions = listOf(
                ProjectPartnerLumpSum(
                    partnerId = partnerId,
                    amount = BigDecimal.valueOf(13),
                ),
            ),
            fastTrack = true,
            readyForPayment = false,
        ),
        ProjectLumpSum(
            orderNr = 16,
            programmeLumpSumId = 45L,
            period = 4,
            lumpSumContributions = listOf(
                ProjectPartnerLumpSum(
                    partnerId = partnerId,
                    amount = BigDecimal.valueOf(1033, 2),
                ),
            ),
            fastTrack = true,
            readyForPayment = true,
        ),
    )

    private fun staffCost(unitCostId: Long): BudgetStaffCostEntry {
        val staffCost = mockk<BudgetStaffCostEntry>()
        every { staffCost.unitCostId } returns unitCostId
        every { staffCost.rowSum } returns BigDecimal.ZERO
        every { staffCost.numberOfUnits } returns BigDecimal.ZERO
        return staffCost
    }

    private fun travelCost(unitCostId: Long): BudgetTravelAndAccommodationCostEntry {
        val cost = mockk<BudgetTravelAndAccommodationCostEntry>()
        every { cost.unitCostId } returns unitCostId
        every { cost.rowSum } returns BigDecimal.ZERO
        every { cost.numberOfUnits } returns BigDecimal.ZERO
        return cost
    }

    private fun generalCost(
        unitCostId: Long? = null,
        investmentId: Long? = null,
        rowSum: BigDecimal = BigDecimal.ZERO,
        numberOfUnits: BigDecimal = BigDecimal.ZERO,
    ): BudgetGeneralCostEntry {
        val cost = mockk<BudgetGeneralCostEntry>()
        every { cost.unitCostId } returns unitCostId
        every { cost.rowSum } returns rowSum
        every { cost.numberOfUnits } returns numberOfUnits
        every { cost.investmentId } returns investmentId
        return cost
    }

    private fun unitCost(unitCostId: Long): BudgetUnitCostEntry {
        val cost = mockk<BudgetUnitCostEntry>()
        every { cost.unitCostId } returns unitCostId
        every { cost.rowSum } returns BigDecimal.ZERO
        every { cost.numberOfUnits } returns BigDecimal.ZERO
        return cost
    }

    private val staffCosts = listOf(
        staffCost(4L),
        staffCost(5L),
    )

    private val travelCosts = listOf(
        travelCost(5L),
        travelCost(6L),
    )

    private val externalCosts = listOf(
        generalCost(unitCostId = 7L, investmentId = 485L, rowSum = BigDecimal.valueOf(15)),
    )

    private val equipmentCosts = listOf(
        generalCost(unitCostId = 7L, rowSum = BigDecimal.valueOf(12)),
        generalCost(unitCostId = 8L, investmentId = 485L, rowSum = BigDecimal.valueOf(17)),
    )

    private val infrastructureCosts = listOf(
        generalCost(unitCostId = 9L),
    )

    private val unitCosts = listOf(
        unitCost(10L),
    )

    private fun perPeriodBudget(number: Int, value: BigDecimal) = ProjectPeriodBudget(
        periodNumber = number,
        periodStart = number * 3 - 2,
        periodEnd = number * 3,
        totalBudgetPerPeriod = value,
        budgetPerPeriodDetail = mockk(),
        lastPeriod = false,
    )

    private fun perPeriod(partnerId: Long?): List<ProjectPartnerBudgetPerPeriod> {
        val partner = mockk<ProjectPartnerSummary>()
        every { partner.id } returns partnerId
        return listOf(
            ProjectPartnerBudgetPerPeriod(
                partner = partner,
                periodBudgets = mutableListOf(
                    perPeriodBudget(1, BigDecimal.ONE),
                    perPeriodBudget(2, BigDecimal.TEN),
                ),
                totalPartnerBudget = BigDecimal.ZERO,
                totalPartnerBudgetDetail = mockk(),
                costType = ProjectPartnerCostType.Management
            ),
        )
    }

    private val expectedContribution1 = CreateProjectPartnerReportContribution(
        sourceOfContribution = "A",
        legalStatus = null,
        idFromApplicationForm = 100,
        historyIdentifier = UUID.randomUUID(),
        createdInThisReport = false,
        amount = BigDecimal.ONE,
        previouslyReported = BigDecimal.ZERO,
        currentlyReported = BigDecimal.ZERO,
    )

    private val expectedContribution2 = CreateProjectPartnerReportContribution(
        sourceOfContribution = "B",
        legalStatus = null,
        idFromApplicationForm = null,
        historyIdentifier = UUID.randomUUID(),
        createdInThisReport = false,
        amount = BigDecimal.ONE,
        previouslyReported = BigDecimal.ZERO,
        currentlyReported = BigDecimal.ZERO,
    )

    private val expectedContribution3 = CreateProjectPartnerReportContribution(
        sourceOfContribution = "C - this will be merged with contribution id=3",
        legalStatus = ProjectPartnerContributionStatus.AutomaticPublic,
        idFromApplicationForm = 300,
        historyIdentifier = HISTORY_CONTRIBUTION_UUID_3,
        createdInThisReport = false,
        amount = BigDecimal.ONE,
        previouslyReported = BigDecimal.TEN,
        currentlyReported = BigDecimal.ZERO,
    )

    private val expectedContribution4 = CreateProjectPartnerReportContribution(
        sourceOfContribution = "this has been added inside reporting (not linked to AF)",
        legalStatus = ProjectPartnerContributionStatus.Private,
        idFromApplicationForm = null,
        historyIdentifier = HISTORY_CONTRIBUTION_UUID_2,
        createdInThisReport = false,
        amount = BigDecimal.ZERO,
        previouslyReported = BigDecimal.ONE,
        currentlyReported = BigDecimal.ZERO,
    )

    private val expectedContribution5 = CreateProjectPartnerReportContribution(
        sourceOfContribution = "old source, should be not forgotten",
        legalStatus = ProjectPartnerContributionStatus.Public,
        idFromApplicationForm = 200L,
        historyIdentifier = HISTORY_CONTRIBUTION_UUID_1,
        createdInThisReport = false,
        amount = BigDecimal.ZERO,
        previouslyReported = BigDecimal.valueOf(444L),
        currentlyReported = BigDecimal.ZERO,
    )

    private fun partnerBudget(partner: ProjectPartnerSummary) = PartnerBudget(
        partner = partner,
        staffCosts = BigDecimal.valueOf(10),
        officeAndAdministrationCosts = BigDecimal.valueOf(11),
        travelCosts = BigDecimal.valueOf(12),
        externalCosts = BigDecimal.valueOf(13),
        equipmentCosts = BigDecimal.valueOf(14),
        infrastructureCosts = BigDecimal.valueOf(15),
        otherCosts = BigDecimal.valueOf(16),
        lumpSumContribution = BigDecimal.valueOf(17),
        unitCosts = BigDecimal.valueOf(18),
        spfCosts = BigDecimal.valueOf(185L, 1),
        totalCosts = BigDecimal.valueOf(19),
    )

    private val expectedInvestment = PartnerReportInvestment(
        investmentId = 485L,
        investmentNumber = 5,
        workPackageNumber = 2,
        title = setOf(InputTranslation(SystemLanguage.EN, "investment title EN")),
        total = BigDecimal.valueOf(32),
        previouslyReported = BigDecimal.valueOf(30),
        previouslyReportedParked = BigDecimal.valueOf(29533,2),
        deactivated = false,
        previouslyValidated = BigDecimal.valueOf(5)
    )

    private val expectedTotal = BudgetCostsCalculationResultFull(
        staff = BigDecimal.valueOf(10),
        office = BigDecimal.valueOf(11),
        travel = BigDecimal.valueOf(12),
        external = BigDecimal.valueOf(13),
        equipment = BigDecimal.valueOf(14),
        infrastructure = BigDecimal.valueOf(15),
        other = BigDecimal.valueOf(16),
        lumpSum = BigDecimal.valueOf(17),
        unitCost = BigDecimal.valueOf(18),
        spfCost = BigDecimal.valueOf(185L, 1),
        sum = BigDecimal.valueOf(19),
    )

    private val previousExpenditures = ExpenditureCostCategoryPreviouslyReportedWithParked(
        previouslyReported = BudgetCostsCalculationResultFull(
            staff = BigDecimal.valueOf(30),
            office = BigDecimal.valueOf(31),
            travel = BigDecimal.valueOf(32),
            external = BigDecimal.valueOf(33),
            equipment = BigDecimal.valueOf(34),
            infrastructure = BigDecimal.valueOf(35),
            other = BigDecimal.valueOf(36),
            lumpSum = BigDecimal.valueOf(37),
            unitCost = BigDecimal.valueOf(38),
            spfCost = BigDecimal.valueOf(385L, 1),
            sum = BigDecimal.valueOf(39),
        ),
        previouslyReportedParked = BudgetCostsCalculationResultFull(
            staff = BigDecimal.valueOf(40),
            office = BigDecimal.valueOf(41),
            travel = BigDecimal.valueOf(42),
            external = BigDecimal.valueOf(43),
            equipment = BigDecimal.valueOf(44),
            infrastructure = BigDecimal.valueOf(45),
            other = BigDecimal.valueOf(46),
            lumpSum = BigDecimal.valueOf(47),
            unitCost = BigDecimal.valueOf(48),
            spfCost = BigDecimal.valueOf(485L, 1),
            sum = BigDecimal.valueOf(49),
        ),
        previouslyValidated = BudgetCostsCalculationResultFull(
            staff = BigDecimal.valueOf(50),
            office = BigDecimal.valueOf(51),
            travel = BigDecimal.valueOf(52),
            external = BigDecimal.valueOf(53),
            equipment = BigDecimal.valueOf(55),
            infrastructure = BigDecimal.valueOf(55),
            other = BigDecimal.valueOf(56),
            lumpSum = BigDecimal.valueOf(57),
            unitCost = BigDecimal.valueOf(58),
            spfCost = BigDecimal.valueOf(585L, 1),
            sum = BigDecimal.valueOf(59),
        )
    )

    private val previousReportedCoFinancing = ReportExpenditureCoFinancingColumn(
        funds = mapOf(
            fund.id to BigDecimal.valueOf(14L), /* original fund */
            -1L to BigDecimal.TEN, /* fund which has been removed in modification */
            null to BigDecimal.valueOf(25L), /* partner contribution */
        ),
        partnerContribution = BigDecimal.valueOf(9),
        publicContribution = BigDecimal.valueOf(2),
        automaticPublicContribution = BigDecimal.valueOf(3),
        privateContribution = BigDecimal.valueOf(4),
        sum = BigDecimal.valueOf(5),
    )

    private val previousValidatedCoFinancing = ReportExpenditureCoFinancingColumn(
        funds = mapOf(
            fund.id to BigDecimal.valueOf(141L, 1), /* original fund */
            -1L to BigDecimal.TEN, /* fund which has been removed in modification */
            null to BigDecimal.valueOf(251L, 1), /* partner contribution */
        ),
        partnerContribution = BigDecimal.valueOf(91, 1),
        publicContribution = BigDecimal.valueOf(21, 1),
        automaticPublicContribution = BigDecimal.valueOf(31, 1),
        privateContribution = BigDecimal.valueOf(41, 1),
        sum = BigDecimal.valueOf(51, 1),
    )

    private val previousPayments = ReportExpenditureCoFinancingColumn(
        funds = mapOf(
            fund.id to BigDecimal.valueOf(309L, 2), /* original fund */
            -1L to BigDecimal.ZERO, /* fund which has been removed in modification */
            null to BigDecimal.valueOf(723L, 2), /* partner contribution */
        ),
        partnerContribution = BigDecimal.valueOf(4031L, 2),
        publicContribution = BigDecimal.valueOf(733L, 2),
        automaticPublicContribution = BigDecimal.valueOf(1033L, 2),
        privateContribution = BigDecimal.valueOf(1233L, 2),
        sum = BigDecimal.valueOf(5063L, 2),
    )

    private val expectedPrevious = BudgetCostsCalculationResultFull(
        staff = BigDecimal.valueOf(30),
        office = BigDecimal.valueOf(31),
        travel = BigDecimal.valueOf(32),
        external = BigDecimal.valueOf(33),
        equipment = BigDecimal.valueOf(34),
        infrastructure = BigDecimal.valueOf(35),
        other = BigDecimal.valueOf(36),
        lumpSum = BigDecimal.valueOf(8763, 2), /* +50.63 from ready FT lump sum */
        unitCost = BigDecimal.valueOf(38),
        spfCost = BigDecimal.valueOf(535L, 1), /* +15 from SPF */
        sum = BigDecimal.valueOf(10463, 2), /* +50.63 from ready FT lump sum + 15 from SPF */
    )

    private val expectedPreviousParked = BudgetCostsCalculationResultFull(
        staff = BigDecimal.valueOf(140),
        office = BigDecimal.valueOf(141),
        travel = BigDecimal.valueOf(142),
        external = BigDecimal.valueOf(143),
        equipment = BigDecimal.valueOf(144),
        infrastructure = BigDecimal.valueOf(145),
        other = BigDecimal.valueOf(146),
        lumpSum = BigDecimal.valueOf(147),
        unitCost = BigDecimal.valueOf(148),
        spfCost = BigDecimal.valueOf(1485L, 1),
        sum = BigDecimal.valueOf(149),
    )

    private val expectedPreviouslyReportedCoFinancing = PreviouslyReportedCoFinancing(
        fundsSorted = listOf(
            PreviouslyReportedFund(
                fund.id, percentage = BigDecimal.valueOf(30), percentageSpf = BigDecimal.valueOf(42),
                total = BigDecimal.valueOf(792, 2), previouslyReported = BigDecimal.valueOf(3209, 2),
                previouslyPaid = BigDecimal.valueOf(11), previouslyValidated = BigDecimal.valueOf(3219, 2),
                previouslyReportedParked = BigDecimal.valueOf(3799, 2),
                previouslyReportedSpf = BigDecimal.valueOf(15),
                disabled = false,
            ),
            PreviouslyReportedFund(
                -1L, percentage = BigDecimal.ZERO, percentageSpf = BigDecimal.ZERO,
                total = BigDecimal.ZERO, previouslyReported = BigDecimal.valueOf(21L),
                previouslyPaid = BigDecimal.valueOf(0), previouslyValidated = BigDecimal.valueOf(21L),
                previouslyReportedParked = BigDecimal.valueOf(20),
                previouslyReportedSpf = BigDecimal.valueOf(11),
                disabled = true,
            ),
            PreviouslyReportedFund(
                null, percentage = BigDecimal.valueOf(70), percentageSpf = BigDecimal.valueOf(58),
                total = BigDecimal.valueOf(1108, 2), previouslyReported = BigDecimal.valueOf(5823, 2),
                previouslyPaid = BigDecimal.valueOf(0), previouslyValidated = BigDecimal.valueOf(5833, 2),
                previouslyReportedParked = BigDecimal.valueOf(50),
                previouslyReportedSpf = BigDecimal.valueOf(26),
                disabled = false,
            ),
        ),
        totalPartner = BigDecimal.valueOf(2),
        totalPublic = BigDecimal.valueOf(0),
        totalAutoPublic = BigDecimal.valueOf(2),
        totalPrivate = BigDecimal.valueOf(0),
        totalSum = BigDecimal.valueOf(19),
        previouslyReportedPartner = BigDecimal.valueOf(8831L, 2),
        previouslyReportedPublic = BigDecimal.valueOf(2133L, 2),
        previouslyReportedAutoPublic = BigDecimal.valueOf(2633L, 2),
        previouslyReportedPrivate = BigDecimal.valueOf(3033L, 2),
        previouslyReportedSum = BigDecimal.valueOf(7063L, 2),
        previouslyReportedParkedPartner = BigDecimal.valueOf(9),
        previouslyReportedParkedPublic = BigDecimal.valueOf(2),
        previouslyReportedParkedAutoPublic = BigDecimal.valueOf(3),
        previouslyReportedParkedPrivate = BigDecimal.valueOf(4),
        previouslyReportedParkedSum = BigDecimal.valueOf(5),
        previouslyReportedSpfPartner = BigDecimal.valueOf(39),
        previouslyReportedSpfPublic = BigDecimal.valueOf(12),
        previouslyReportedSpfAutoPublic = BigDecimal.valueOf(13),
        previouslyReportedSpfPrivate = BigDecimal.valueOf(14),
        previouslyReportedSpfSum = BigDecimal.valueOf(15),
        previouslyValidatedPartner = BigDecimal.valueOf(8841L, 2),
        previouslyValidatedPublic = BigDecimal.valueOf(2143L, 2),
        previouslyValidatedAutoPublic = BigDecimal.valueOf(2643L, 2),
        previouslyValidatedPrivate = BigDecimal.valueOf(3043L, 2),
        previouslyValidatedSum = BigDecimal.valueOf(7073L, 2),
    )

    private val zeros = BudgetCostsCalculationResultFull(
        staff = BigDecimal.ZERO,
        office = BigDecimal.ZERO,
        travel = BigDecimal.ZERO,
        external = BigDecimal.ZERO,
        equipment = BigDecimal.ZERO,
        infrastructure = BigDecimal.ZERO,
        other = BigDecimal.ZERO,
        lumpSum = BigDecimal.ZERO,
        unitCost = BigDecimal.ZERO,
        spfCost = BigDecimal.ZERO,
        sum = BigDecimal.ZERO,
    )

    private fun paymentInstallment_1(): PaymentPartnerInstallment {
        val installment = mockk<PaymentPartnerInstallment>()
        every { installment.fundId } returns fund.id
        every { installment.lumpSumId } returns 45L
        every { installment.orderNr } returns 15
        every { installment.amountPaid } returns BigDecimal.valueOf(32)
        every { installment.isPaymentConfirmed } returns false
        return installment
    }

    private fun paymentInstallment_2(): PaymentPartnerInstallment {
        val installment = mockk<PaymentPartnerInstallment>()
        every { installment.fundId } returns fund.id
        every { installment.lumpSumId } returns 45L
        every { installment.orderNr } returns 16
        every { installment.amountPaid } returns BigDecimal.valueOf(11)
        every { installment.isPaymentConfirmed } returns true
        return installment
    }

    private val expectedValidatedLumpSums =
        mapOf(
            Pair(14, BigDecimal.valueOf(5)),
            Pair(15, BigDecimal.valueOf(6)),
            Pair(16, BigDecimal.valueOf(10))
        )
     private val expectedValidatedUnitCost = mapOf(Pair(6L, BigDecimal.TEN))
     private val expectedValidatedInvestments = mapOf(Pair(485L, BigDecimal.valueOf(5)))

    @MockK
    lateinit var reportPersistence: ProjectPartnerReportPersistence

    @MockK
    lateinit var reportContributionPersistence: ProjectPartnerReportContributionPersistence

    @MockK
    lateinit var lumpSumPersistence: ProjectLumpSumPersistence

    @MockK
    lateinit var partnerBudgetCostsPersistence: ProjectPartnerBudgetCostsPersistence

    @MockK
    lateinit var getPartnerBudgetPerPeriod: GetPartnerBudgetPerPeriodInteractor

    @MockK
    lateinit var projectPartnerBudgetOptionsPersistence: ProjectPartnerBudgetOptionsPersistence

    @MockK
    lateinit var getProjectBudget: GetProjectBudget

    @MockK
    lateinit var reportExpenditureCostCategoryPersistence: ProjectPartnerReportExpenditureCostCategoryPersistence

    @MockK
    lateinit var reportExpenditureCoFinancingPersistence: ProjectPartnerReportExpenditureCoFinancingPersistence

    @MockK
    lateinit var paymentPersistence: PaymentPersistence

    @MockK
    lateinit var reportLumpSumPersistence: ProjectPartnerReportLumpSumPersistence

    @MockK
    lateinit var reportUnitCostPersistence: ProjectPartnerReportUnitCostPersistence

    @MockK
    lateinit var reportInvestmentPersistence: ProjectPartnerReportInvestmentPersistence

    @MockK
    private lateinit var reportProjectPersistence: ProjectReportPersistence

    @MockK
    private lateinit var reportProjectSpfClaimPersistence: ProjectReportSpfContributionClaimPersistence

    @InjectMockKs
    lateinit var service: CreateProjectPartnerReportBudget

    @BeforeEach
    fun resetMocks() {
        clearMocks(reportProjectSpfClaimPersistence)
    }

    @Test
    fun createReportBudget() {
        val partnerId = 76L
        val projectId = 30L
        val version = "v4.2"
        val budgetOptions = mockk<ProjectPartnerBudgetOptions>()
        val partner = mockInputsAndGetPartner(projectId, partnerId = partnerId, version, budgetOptions)

        val result = service.retrieveBudgetDataFor(projectId, partner, version, coFinancing, investments)

        assertThat(result.availableLumpSums).containsExactly(
            PartnerReportLumpSum(
                lumpSumId = 44L,
                orderNr = 14,
                period = 3,
                total = BigDecimal.TEN,
                previouslyReported = BigDecimal.TEN,
                previouslyPaid = BigDecimal.ZERO,
                previouslyReportedParked = BigDecimal.valueOf(8399, 2),
                previouslyValidated = BigDecimal.valueOf(5)
            ),
            PartnerReportLumpSum(
                lumpSumId = 45L,
                orderNr = 15,
                period = 4,
                total = BigDecimal.valueOf(13),
                previouslyReported = BigDecimal.valueOf(100),
                previouslyPaid = BigDecimal.ZERO,
                previouslyReportedParked = BigDecimal.TEN,
                previouslyValidated = BigDecimal.valueOf(6)
            ),
            PartnerReportLumpSum(
                lumpSumId = 45L,
                orderNr = 16,
                period = 4,
                // is getting 200 from previous reports and 10.33 from ready fast track
                total = BigDecimal.valueOf(1033, 2),
                previouslyReported = BigDecimal.valueOf(21033, 2),
                previouslyPaid = BigDecimal.valueOf(11),
                previouslyReportedParked = BigDecimal.valueOf(110),
                previouslyValidated = BigDecimal.valueOf(20.33)
            ),
        )
        assertThat(result.unitCosts.map { it.unitCostId }).containsExactlyInAnyOrder(4, 5, 6, 7, 8, 9, 10)
        assertThat(result.unitCosts.first { it.unitCostId == 6L }.previouslyReported).isEqualTo(BigDecimal.TEN)
        assertThat(result.unitCosts.first { it.unitCostId == 6L }.previouslyReportedParked).isEqualTo(BigDecimal.valueOf(100))
        assertThat(result.unitCosts.first { it.unitCostId == 7L }.previouslyReported).isEqualTo(BigDecimal.valueOf(100))
        assertThat(result.unitCosts.first { it.unitCostId == 7L }.previouslyReportedParked).isEqualTo(BigDecimal.valueOf(1399,2))
        assertThat(result.unitCosts.first { it.unitCostId == 8L }.previouslyReported).isEqualTo(BigDecimal.ZERO)
        assertThat(result.unitCosts.first { it.unitCostId == 8L }.previouslyReportedParked).isEqualTo(BigDecimal.ZERO)
        assertThat(result.investments).containsExactly(expectedInvestment)
        assertThat(result.budgetPerPeriod).containsExactly(
            ProjectPartnerReportPeriod(1, BigDecimal.ONE, BigDecimal.ONE, 1, 3),
            ProjectPartnerReportPeriod(2, BigDecimal.TEN, BigDecimal.valueOf(11, 0), 4, 6),
        )
        assertThat(result.expenditureSetup.options).isEqualTo(budgetOptions)
        assertThat(result.expenditureSetup.totalsFromAF).isEqualTo(expectedTotal)
        assertThat(result.expenditureSetup.currentlyReported).isEqualTo(zeros)
        assertThat(result.expenditureSetup.currentlyReportedParked).isEqualTo(zeros)
        assertThat(result.expenditureSetup.previouslyReported).isEqualTo(expectedPrevious)
        assertThat(result.expenditureSetup.previouslyReportedParked).isEqualTo(expectedPreviousParked)
        assertThat(result.expenditureSetup.currentlyReportedReIncluded).isEqualTo(zeros)
        assertThat(result.expenditureSetup.totalEligibleAfterControl).isEqualTo(zeros)

        assertThat(result.previouslyReportedCoFinancing).isEqualTo(expectedPreviouslyReportedCoFinancing)

        assertThat(result.contributions.contributions).hasSize(5)
        // this we cannot mock
        val newUuid1 = result.contributions.contributions[0].historyIdentifier
        val newUuid2 = result.contributions.contributions[1].historyIdentifier
        assertThat(result.contributions.contributions[0]).isEqualTo(expectedContribution1.copy(historyIdentifier = newUuid1))
        assertThat(result.contributions.contributions[1]).isEqualTo(expectedContribution2.copy(historyIdentifier = newUuid2))
        assertThat(result.contributions.contributions[2]).isEqualTo(expectedContribution3)
        assertThat(result.contributions.contributions[3]).isEqualTo(expectedContribution4)
        assertThat(result.contributions.contributions[4]).isEqualTo(expectedContribution5)

        assertThat(result.contributions.contributionsSpf).hasSize(3)
        val newUuidSpf1 = result.contributions.contributionsSpf[0].historyIdentifier
        val newUuidSpf2 = result.contributions.contributionsSpf[1].historyIdentifier
        val newUuidSpf3 = result.contributions.contributionsSpf[2].historyIdentifier
        assertThat(result.contributions.contributionsSpf[0]).isEqualTo(expectedContribution2.copy(
            sourceOfContribution = "H", historyIdentifier = newUuidSpf1, idFromApplicationForm = null
        ))
        assertThat(result.contributions.contributionsSpf[1]).isEqualTo(expectedContribution1.copy(
            sourceOfContribution = "G", historyIdentifier = newUuidSpf2, idFromApplicationForm = 110L
        ))
        assertThat(result.contributions.contributionsSpf[2]).isEqualTo(expectedContribution3.copy(
            sourceOfContribution = "I", historyIdentifier = newUuidSpf3, idFromApplicationForm = 310L, previouslyReported = BigDecimal.ZERO
        ))
    }

    @Test
    fun `createReportBudget - empty co-financing in AF`() {
        val partnerId = 79L
        val projectId = 35L
        val version = "v8.1"
        val budgetOptions = mockk<ProjectPartnerBudgetOptions>()
        val partner = mockInputsAndGetPartner(projectId, partnerId = partnerId, version, budgetOptions)

        every { reportProjectSpfClaimPersistence.getPreviouslyReportedSpfContributions(setOf(SUBMITTED_PROJECT_REPORT_ID)) } returns
                ReportExpenditureCoFinancingColumn(
                    funds = emptyMap(),
                    partnerContribution = BigDecimal.ZERO,
                    publicContribution = BigDecimal.ZERO,
                    automaticPublicContribution = BigDecimal.ZERO,
                    privateContribution = BigDecimal.ZERO,
                    sum = BigDecimal.ZERO,
                )

        every { reportExpenditureCoFinancingPersistence.getCoFinancingCumulative(setOf(408L, 409L), setOf(409L)) } returns ExpenditureCoFinancingPrevious(
            previous = previousReportedCoFinancing.copy(funds = emptyMap()),
            previousParked = previousReportedCoFinancing.copy(funds = emptyMap()),
            previousValidated = previousValidatedCoFinancing.copy(funds = emptyMap()),
        )

        val result = service.retrieveBudgetDataFor(
            projectId,
            partner,
            version,
            PartnerReportCoFinancing(
                coFinancing = coFinancing.coFinancing.copy(finances = emptyList()),
                coFinancingSpf = coFinancing.coFinancingSpf.copy(finances = emptyList()),
            ),
            emptyList(),
        )

        assertThat(result.previouslyReportedCoFinancing)
            .isEqualTo(
                PreviouslyReportedCoFinancing(
                    fundsSorted = listOf(
                        PreviouslyReportedFund(
                            fundId = -1L,
                            percentage = BigDecimal.ZERO,
                            percentageSpf = BigDecimal.ZERO,
                            total = BigDecimal.ZERO,
                            previouslyReported = BigDecimal.ZERO,
                            previouslyReportedParked = BigDecimal.ZERO,
                            previouslyReportedSpf = BigDecimal.ZERO,
                            previouslyValidated = BigDecimal.ZERO,
                            previouslyPaid = BigDecimal.ZERO,
                            disabled = true,
                        ),
                        PreviouslyReportedFund(
                            fundId = 8L,
                            percentage = BigDecimal.ZERO,
                            percentageSpf = BigDecimal.ZERO,
                            total = BigDecimal.ZERO,
                            previouslyReported = BigDecimal.valueOf(309L, 2),
                            previouslyReportedParked = BigDecimal.ZERO,
                            previouslyReportedSpf = BigDecimal.ZERO,
                            previouslyValidated = BigDecimal.valueOf(309L, 2),
                            previouslyPaid = BigDecimal.valueOf(11L),
                            disabled = true,
                        ),
                    ),
                    totalPartner = BigDecimal.valueOf(2),
                    totalPublic = BigDecimal.valueOf(0),
                    totalAutoPublic = BigDecimal.valueOf(2),
                    totalPrivate = BigDecimal.valueOf(0),
                    totalSum = BigDecimal.valueOf(19),

                    previouslyReportedPartner = BigDecimal.valueOf(4931, 2),
                    previouslyReportedPublic = BigDecimal.valueOf(933, 2),
                    previouslyReportedAutoPublic = BigDecimal.valueOf(1333, 2),
                    previouslyReportedPrivate = BigDecimal.valueOf(1633, 2),
                    previouslyReportedSum = BigDecimal.valueOf(5563, 2),

                    previouslyReportedParkedPartner = BigDecimal.valueOf(9),
                    previouslyReportedParkedPublic = BigDecimal.valueOf(2),
                    previouslyReportedParkedAutoPublic = BigDecimal.valueOf(3),
                    previouslyReportedParkedPrivate = BigDecimal.valueOf(4),
                    previouslyReportedParkedSum = BigDecimal.valueOf(5),

                    previouslyReportedSpfPartner = BigDecimal.ZERO,
                    previouslyReportedSpfPublic = BigDecimal.ZERO,
                    previouslyReportedSpfAutoPublic = BigDecimal.ZERO,
                    previouslyReportedSpfPrivate = BigDecimal.ZERO,
                    previouslyReportedSpfSum = BigDecimal.ZERO,

                    previouslyValidatedPartner = BigDecimal.valueOf(4941L, 2),
                    previouslyValidatedPublic = BigDecimal.valueOf(943L, 2),
                    previouslyValidatedAutoPublic = BigDecimal.valueOf(1343L, 2),
                    previouslyValidatedPrivate = BigDecimal.valueOf(1643L, 2),
                    previouslyValidatedSum = BigDecimal.valueOf(5573L, 2),
                )
            )
    }

    private fun mockInputsAndGetPartner(
        projectId: Long,
        partnerId: Long,
        version: String,
        budgetOptions: ProjectPartnerBudgetOptions
    ): ProjectPartnerSummary {
        val partner = mockk<ProjectPartnerSummary>()
        every { partner.id } returns partnerId

        val submittedReports = listOf(
            ProjectPartnerReportStatusAndVersion(
                408L, ReportStatus.Submitted, "AFv4"
            ),
            ProjectPartnerReportStatusAndVersion(
                409L, ReportStatus.Certified, "AFv5"
            )
        )

        // contribution
        every { reportPersistence.getSubmittedPartnerReports(partnerId) } returns submittedReports
        every { reportContributionPersistence.getAllContributionsForReportIds(setOf(408L, 409L)) } returns previousContributions
        // lump sums
        every { lumpSumPersistence.getLumpSums(projectId, version) } returns lumpSums(partnerId)
        every { reportLumpSumPersistence.getCumulativeVerificationParked(partnerId = partnerId, any()) } returns mapOf(
            14 to BigDecimal.valueOf(7399, 2),
            15 to BigDecimal.ZERO,
            16 to BigDecimal.valueOf(100)
        )
        every { reportLumpSumPersistence.getLumpSumCumulative(setOf(408L, 409L)) } returns
            mapOf(
                14 to ExpenditureLumpSumCurrent(current = BigDecimal.TEN, currentParked = BigDecimal.TEN),
                15 to ExpenditureLumpSumCurrent(current = BigDecimal.valueOf(100), currentParked = BigDecimal.TEN),
                16 to ExpenditureLumpSumCurrent(current = BigDecimal.valueOf(200), currentParked = BigDecimal.TEN)
            )
        // unit costs
        every { partnerBudgetCostsPersistence.getBudgetStaffCosts(setOf(partnerId), version) } returns staffCosts
        every {
            partnerBudgetCostsPersistence.getBudgetTravelAndAccommodationCosts(
                setOf(partnerId),
                version
            )
        } returns travelCosts
        every {
            partnerBudgetCostsPersistence.getBudgetExternalExpertiseAndServicesCosts(
                setOf(partnerId),
                version
            )
        } returns externalCosts
        every { partnerBudgetCostsPersistence.getBudgetEquipmentCosts(setOf(partnerId), version) } returns equipmentCosts
        every {
            partnerBudgetCostsPersistence.getBudgetInfrastructureAndWorksCosts(
                setOf(partnerId),
                version
            )
        } returns infrastructureCosts
        every { partnerBudgetCostsPersistence.getBudgetUnitCosts(setOf(partnerId), version) } returns unitCosts

        every { reportUnitCostPersistence.getUnitCostCumulative(setOf(408L, 409L)) } returns
            mapOf(
                6L to ExpenditureUnitCostCurrent(current = BigDecimal.TEN, currentParked = BigDecimal.ZERO),
                7L to ExpenditureUnitCostCurrent(current = BigDecimal.valueOf(100), currentParked = BigDecimal.ZERO),
                8L to ExpenditureUnitCostCurrent(current = BigDecimal.ZERO, currentParked = BigDecimal.ZERO)
            )

        every { reportUnitCostPersistence.getVerificationParkedUnitCostCumulative(partnerId, any()) } returns mapOf(
            6L to BigDecimal.valueOf(100),
            7L to BigDecimal.valueOf(1399, 2),
            8L to BigDecimal.ZERO
        )
        // investments
        every {
            reportInvestmentPersistence.getVerificationParkedInvestmentsCumulative(partnerId, any())
        } returns mapOf(485L to BigDecimal.valueOf(19533, 2))
        every { reportInvestmentPersistence.getInvestmentsCumulative(setOf(408L, 409L)) } returns mapOf(
            485L to ExpenditureInvestmentCurrent(
                current = BigDecimal.valueOf(30),
                currentParked = BigDecimal.valueOf(100)
            )
        )
        // budget per period
        every { getPartnerBudgetPerPeriod.getPartnerBudgetPerPeriod(projectId, version) } returns
            ProjectBudgetOverviewPerPartnerPerPeriod(
                partnersBudgetPerPeriod = perPeriod(partnerId),
                totals = emptyList(),
                totalsPercentage = emptyList()
            )
        // options
        every { projectPartnerBudgetOptionsPersistence.getBudgetOptions(partnerId, version) } returns budgetOptions
        every { getProjectBudget.getBudget(listOf(partner), projectId, version) } returns listOf(partnerBudget(partner))
        every { paymentPersistence.findByPartnerId(partnerId) } returns listOf(
            paymentInstallment_1(),
            paymentInstallment_2()
        )
        // SPF
        every { reportProjectPersistence.getSubmittedProjectReports(projectId) } returns listOf(
            ProjectReportStatusAndType(SUBMITTED_PROJECT_REPORT_ID, ProjectReportStatus.Submitted, ContractingDeadlineType.Both)
        )
        every { reportProjectSpfClaimPersistence.getPreviouslyReportedSpfContributions(setOf(SUBMITTED_PROJECT_REPORT_ID)) } returns
                ReportExpenditureCoFinancingColumn(
                    funds = mapOf(
                        fund.id to BigDecimal.valueOf(15L),
                        -1L to BigDecimal.valueOf(11L),
                        null to BigDecimal.valueOf(26L),
                    ),
                    partnerContribution = BigDecimal.valueOf(39),
                    publicContribution = BigDecimal.valueOf(12),
                    automaticPublicContribution = BigDecimal.valueOf(13),
                    privateContribution = BigDecimal.valueOf(14),
                    sum = BigDecimal.valueOf(15),
                )
        // previously reported
        every { paymentPersistence.getFtlsCumulativeForPartner(partnerId) } returns previousPayments

        every { reportExpenditureCostCategoryPersistence.getVerificationCostCategoriesCumulative(partnerId,  any()) } returns
                BudgetCostsCalculationResultFull(
                    staff = BigDecimal.valueOf(100),
                    office = BigDecimal.valueOf(100),
                    travel = BigDecimal.valueOf(100),
                    external = BigDecimal.valueOf(100),
                    equipment = BigDecimal.valueOf(100),
                    infrastructure = BigDecimal.valueOf(100),
                    other = BigDecimal.valueOf(100),
                    lumpSum = BigDecimal.valueOf(100),
                    unitCost = BigDecimal.valueOf(100),
                    spfCost = BigDecimal.valueOf(100),
                    sum = BigDecimal.valueOf(100)
                )

        every { reportExpenditureCoFinancingPersistence.getVerificationParkedCoFinancingCumulative(partnerId, any()) } returns
                ReportCertificateCoFinancingColumn(
                    funds = mapOf(
                        fund.id to BigDecimal.valueOf(2399, 2),
                        -1L to BigDecimal.TEN,
                        null to BigDecimal.valueOf(25L)
                    ),
                    partnerContribution = BigDecimal.ZERO,
                    publicContribution = BigDecimal.ZERO,
                    automaticPublicContribution = BigDecimal.ZERO,
                    privateContribution = BigDecimal.ZERO,
                    sum = BigDecimal.ZERO
                )

        every { reportExpenditureCostCategoryPersistence.getCostCategoriesCumulative(setOf(408L, 409L), setOf(409L)) } returns previousExpenditures
        every { reportExpenditureCoFinancingPersistence.getCoFinancingCumulative(setOf(408L, 409L), setOf(409L)) } returns ExpenditureCoFinancingPrevious(
            previous = previousReportedCoFinancing,
            previousParked = previousReportedCoFinancing,
            previousValidated = previousValidatedCoFinancing,
        )

        every { reportLumpSumPersistence.getLumpSumCumulativeAfterControl(setOf(409L)) } returns expectedValidatedLumpSums

        every { reportUnitCostPersistence.getValidatedUnitCostCumulative(setOf(409L)) } returns expectedValidatedUnitCost

        every { reportInvestmentPersistence.getInvestmentsCumulativeAfterControl(setOf(409L)) } returns expectedValidatedInvestments

        return partner
    }

}
