package io.cloudflight.jems.server.project.service.report.partner.base.createProjectPartnerReport

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.api.project.dto.partner.cofinancing.ProjectPartnerCoFinancingFundTypeDTO.MainFund
import io.cloudflight.jems.api.project.dto.partner.cofinancing.ProjectPartnerCoFinancingFundTypeDTO.PartnerContribution
import io.cloudflight.jems.api.project.dto.partner.cofinancing.ProjectPartnerContributionStatusDTO
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.payments.model.regular.PaymentPartnerInstallment
import io.cloudflight.jems.server.payments.service.regular.PaymentRegularPersistence
import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFund
import io.cloudflight.jems.server.project.service.budget.get_partner_budget_per_period.GetPartnerBudgetPerPeriodInteractor
import io.cloudflight.jems.server.project.service.budget.get_project_budget.GetProjectBudget
import io.cloudflight.jems.server.project.service.budget.model.BudgetCostsCalculationResultFull
import io.cloudflight.jems.server.project.service.budget.model.ExpenditureCostCategoryPreviouslyReportedWithParked
import io.cloudflight.jems.server.project.service.budget.model.PartnerBudget
import io.cloudflight.jems.server.project.service.lumpsum.ProjectLumpSumPersistence
import io.cloudflight.jems.server.project.service.lumpsum.model.ProjectLumpSum
import io.cloudflight.jems.server.project.service.lumpsum.model.ProjectPartnerLumpSum
import io.cloudflight.jems.server.project.service.model.*
import io.cloudflight.jems.server.project.service.partner.budget.ProjectPartnerBudgetCostsPersistence
import io.cloudflight.jems.server.project.service.partner.budget.ProjectPartnerBudgetOptionsPersistence
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerCoFinancing
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerCoFinancingAndContribution
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerContribution
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerContributionStatus
import io.cloudflight.jems.server.project.service.partner.model.*
import io.cloudflight.jems.server.project.service.report.model.file.JemsFileMetadata
import io.cloudflight.jems.server.project.service.report.model.partner.base.create.PartnerReportInvestment
import io.cloudflight.jems.server.project.service.report.model.partner.base.create.PartnerReportLumpSum
import io.cloudflight.jems.server.project.service.report.model.partner.base.create.PreviouslyReportedCoFinancing
import io.cloudflight.jems.server.project.service.report.model.partner.base.create.PreviouslyReportedFund
import io.cloudflight.jems.server.project.service.report.model.partner.contribution.create.CreateProjectPartnerReportContribution
import io.cloudflight.jems.server.project.service.report.model.partner.contribution.withoutCalculations.ProjectPartnerReportEntityContribution
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.PartnerReportInvestmentSummary
import io.cloudflight.jems.server.project.service.report.model.partner.financialOverview.coFinancing.ReportExpenditureCoFinancingColumn
import io.cloudflight.jems.server.project.service.report.model.partner.financialOverview.investments.ExpenditureInvestmentCurrent
import io.cloudflight.jems.server.project.service.report.model.partner.financialOverview.lumpSum.ExpenditureLumpSumCurrent
import io.cloudflight.jems.server.project.service.report.model.partner.financialOverview.unitCost.ExpenditureUnitCostCurrent
import io.cloudflight.jems.server.project.service.report.model.partner.identification.ProjectPartnerReportPeriod
import io.cloudflight.jems.server.project.service.report.partner.ProjectPartnerReportPersistence
import io.cloudflight.jems.server.project.service.report.partner.contribution.ProjectPartnerReportContributionPersistence
import io.cloudflight.jems.server.project.service.report.partner.expenditure.ProjectPartnerReportExpenditurePersistence
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.ProjectPartnerReportExpenditureCoFinancingPersistence
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.ProjectPartnerReportExpenditureCostCategoryPersistence
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.ProjectPartnerReportInvestmentPersistence
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.ProjectPartnerReportLumpSumPersistence
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.ProjectPartnerReportUnitCostPersistence
import io.cloudflight.jems.server.project.service.workpackage.WorkPackagePersistence
import io.cloudflight.jems.server.project.service.workpackage.model.InvestmentSummary
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.util.*

internal class CreateProjectPartnerReportBudgetTest : UnitTest() {

    private val HISTORY_CONTRIBUTION_UUID_1 = UUID.randomUUID()
    private val HISTORY_CONTRIBUTION_UUID_2 = UUID.randomUUID()
    private val HISTORY_CONTRIBUTION_UUID_3 = UUID.randomUUID()

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
        status = ProjectPartnerContributionStatusDTO.AutomaticPublic,
        amount = BigDecimal.ONE,
        isPartner = false,
    )

    private val fund = mockk<ProgrammeFund>().also {
        every { it.id } returns 8L
    }

    private val coFinancing = ProjectPartnerCoFinancingAndContribution(
        finances = listOf(
            ProjectPartnerCoFinancing(MainFund, fund, BigDecimal.valueOf(30)),
            ProjectPartnerCoFinancing(PartnerContribution, null, BigDecimal.valueOf(70)),
        ),
        partnerContributions = listOf(contribNonPartner2, contribPartner, contribNonPartner1),
        partnerAbbreviation = "not needed",
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
            sourceOfContribution = "old source, should be ignored and taken from AF",
            legalStatus = ProjectPartnerContributionStatus.Public, // should also be ignored
            idFromApplicationForm = 200L,
            historyIdentifier = HISTORY_CONTRIBUTION_UUID_1,
            createdInThisReport = false,
            amount = BigDecimal.ZERO, // should be ignored
            previouslyReported = BigDecimal.ZERO,
            currentlyReported = BigDecimal.ONE,
            attachment = JemsFileMetadata(780L, "this_is_ignored", mockk()),
        ),
        ProjectPartnerReportEntityContribution(
            id = 2L,
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
        sourceOfContribution = "C - this will be merged with contribution id=3",
        legalStatus = ProjectPartnerContributionStatus.AutomaticPublic,
        idFromApplicationForm = 300,
        historyIdentifier = HISTORY_CONTRIBUTION_UUID_3,
        createdInThisReport = false,
        amount = BigDecimal.ONE,
        previouslyReported = BigDecimal.TEN,
        currentlyReported = BigDecimal.ZERO,
    )

    private val expectedContribution3 = CreateProjectPartnerReportContribution(
        sourceOfContribution = "this has been added inside reporting (not linked to AF)",
        legalStatus = ProjectPartnerContributionStatus.Private,
        idFromApplicationForm = null,
        historyIdentifier = HISTORY_CONTRIBUTION_UUID_2,
        createdInThisReport = false,
        amount = BigDecimal.ZERO,
        previouslyReported = BigDecimal.ONE,
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
        totalCosts = BigDecimal.valueOf(19),
    )

    private val expectedInvestment = PartnerReportInvestment(
        investmentId = 485L,
        investmentNumber = 5,
        workPackageNumber = 2,
        title = setOf(InputTranslation(SystemLanguage.EN, "investment title EN")),
        total = BigDecimal.valueOf(32),
        previouslyReported = BigDecimal.valueOf(30),
        previouslyReportedParked = BigDecimal.valueOf(100),
        deactivated = false,
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
        sum = BigDecimal.valueOf(19),
    )

    private val previousExpenditures = ExpenditureCostCategoryPreviouslyReportedWithParked(
        BudgetCostsCalculationResultFull(
            staff = BigDecimal.valueOf(30),
            office = BigDecimal.valueOf(31),
            travel = BigDecimal.valueOf(32),
            external = BigDecimal.valueOf(33),
            equipment = BigDecimal.valueOf(34),
            infrastructure = BigDecimal.valueOf(35),
            other = BigDecimal.valueOf(36),
            lumpSum = BigDecimal.valueOf(37),
            unitCost = BigDecimal.valueOf(38),
            sum = BigDecimal.valueOf(39),
        ),
        BudgetCostsCalculationResultFull(
            staff = BigDecimal.valueOf(40),
            office = BigDecimal.valueOf(41),
            travel = BigDecimal.valueOf(42),
            external = BigDecimal.valueOf(43),
            equipment = BigDecimal.valueOf(44),
            infrastructure = BigDecimal.valueOf(45),
            other = BigDecimal.valueOf(46),
            lumpSum = BigDecimal.valueOf(47),
            unitCost = BigDecimal.valueOf(48),
            sum = BigDecimal.valueOf(49),
        ),
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

    private val expectedPrevious = BudgetCostsCalculationResultFull(
        staff = BigDecimal.valueOf(30),
        office = BigDecimal.valueOf(31),
        travel = BigDecimal.valueOf(32),
        external = BigDecimal.valueOf(33),
        equipment = BigDecimal.valueOf(34),
        infrastructure = BigDecimal.valueOf(35),
        other = BigDecimal.valueOf(36),
        lumpSum = BigDecimal.valueOf(4733, 2), /* +10.33 from ready FT lump sum */
        unitCost = BigDecimal.valueOf(38),
        sum = BigDecimal.valueOf(4933, 2), /* +10.33 from ready FT lump sum */
    )

    private val expectedPreviouslyReportedCoFinancing = PreviouslyReportedCoFinancing(
        fundsSorted = listOf(
            PreviouslyReportedFund(
                fund.id, percentage = BigDecimal.valueOf(30),
                total = BigDecimal.valueOf(570, 2), previouslyReported = BigDecimal.valueOf(1709, 2),
                previouslyPaid = BigDecimal.valueOf(11)
            ),
            PreviouslyReportedFund(
                -1L, percentage = BigDecimal.ZERO,
                total = BigDecimal.ZERO, previouslyReported = BigDecimal.TEN,
                previouslyPaid = BigDecimal.valueOf(0)
            ),
            PreviouslyReportedFund(
                null, percentage = BigDecimal.valueOf(70),
                total = BigDecimal.valueOf(1330, 2), previouslyReported = BigDecimal.valueOf(3223, 2),
                previouslyPaid = BigDecimal.valueOf(0)
            ),
        ),
        totalPartner = BigDecimal.valueOf(1),
        totalPublic = BigDecimal.valueOf(0),
        totalAutoPublic = BigDecimal.valueOf(1),
        totalPrivate = BigDecimal.valueOf(0),
        totalSum = BigDecimal.valueOf(19),
        previouslyReportedPartner = BigDecimal.valueOf(1623, 2),
        previouslyReportedPublic = BigDecimal.valueOf(200, 2),
        previouslyReportedAutoPublic = BigDecimal.valueOf(354, 2),
        previouslyReportedPrivate = BigDecimal.valueOf(400, 2),
        previouslyReportedSum = BigDecimal.valueOf(1533, 2),
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

    private val investmentSummaries = listOf(
        InvestmentSummary(
            id = 1L,
            investmentNumber = 1,
            workPackageNumber = 1,
            deactivated = false,
        ),
        InvestmentSummary(
            id = 2L,
            investmentNumber = 2,
            workPackageNumber = 1,
            deactivated = false,
        ))

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
    lateinit var paymentPersistence: PaymentRegularPersistence

    @MockK
    lateinit var reportLumpSumPersistence: ProjectPartnerReportLumpSumPersistence

    @MockK
    lateinit var reportInvestmentPersistence: ProjectPartnerReportInvestmentPersistence

    @MockK
    lateinit var reportExpenditurePersistence: ProjectPartnerReportExpenditurePersistence

    @MockK
    lateinit var projectWorkPackagePersistence: WorkPackagePersistence

    @MockK
    lateinit var reportUnitCostPersistence: ProjectPartnerReportUnitCostPersistence

    @InjectMockKs
    lateinit var service: CreateProjectPartnerReportBudget

    @Test
    fun createReportBudget() {
        val partnerId = 76L
        val projectId = 30L
        val version = "v4.2"
        val budgetOptions = mockk<ProjectPartnerBudgetOptions>()
        val partner = mockInputsAndGetPartner(projectId, partnerId = partnerId, version, budgetOptions)

        every {
            projectWorkPackagePersistence.getProjectInvestmentSummaries(
                projectId,
                version
            )
        } returns investmentSummaries
        every { reportExpenditurePersistence.getPartnerReportExpenditureCosts(partnerId, 408L) } returns emptyList()

        val result = service.retrieveBudgetDataFor(projectId, partner, version, coFinancing, investments)

        assertThat(result.contributions).hasSize(3)
        assertThat(result.availableLumpSums).containsExactly(
            PartnerReportLumpSum(
                lumpSumId = 44L,
                orderNr = 14,
                period = 3,
                total = BigDecimal.TEN,
                previouslyReported = BigDecimal.TEN,
                previouslyPaid = BigDecimal.ZERO,
                previouslyReportedParked = BigDecimal.TEN
            ),
            PartnerReportLumpSum(
                lumpSumId = 45L,
                orderNr = 15,
                period = 4,
                total = BigDecimal.valueOf(13),
                previouslyReported = BigDecimal.valueOf(100),
                previouslyPaid = BigDecimal.ZERO,
                previouslyReportedParked = BigDecimal.TEN
            ),
            PartnerReportLumpSum(
                lumpSumId = 45L,
                orderNr = 16,
                period = 4,
                // is getting 200 from previous reports and 10.33 from ready fast track
                total = BigDecimal.valueOf(1033, 2),
                previouslyReported = BigDecimal.valueOf(21033, 2),
                previouslyPaid = BigDecimal.valueOf(11),
                previouslyReportedParked = BigDecimal.TEN
            ),
        )
        assertThat(result.unitCosts.map { it.unitCostId }).containsExactlyInAnyOrder(4, 5, 6, 7, 8, 9, 10)
        assertThat(result.unitCosts.first { it.unitCostId == 6L }.previouslyReported).isEqualTo(BigDecimal.TEN)
        assertThat(result.unitCosts.first { it.unitCostId == 7L }.previouslyReported).isEqualTo(BigDecimal.valueOf(100))
        assertThat(result.investments).containsExactly(expectedInvestment)
        assertThat(result.budgetPerPeriod).containsExactly(
            ProjectPartnerReportPeriod(1, BigDecimal.ONE, BigDecimal.ONE, 1, 3),
            ProjectPartnerReportPeriod(2, BigDecimal.TEN, BigDecimal.valueOf(11, 0), 4, 6),
        )
        assertThat(result.expenditureSetup.options).isEqualTo(budgetOptions)
        assertThat(result.expenditureSetup.totalsFromAF).isEqualTo(expectedTotal)
        assertThat(result.expenditureSetup.currentlyReported).isEqualTo(zeros)
        assertThat(result.expenditureSetup.previouslyReported).isEqualTo(expectedPrevious)

        assertThat(result.previouslyReportedCoFinancing).isEqualTo(expectedPreviouslyReportedCoFinancing)

        // this we cannot mock
        val newUuid = result.contributions[0].historyIdentifier
        assertThat(result.contributions[0]).isEqualTo(expectedContribution1.copy(historyIdentifier = newUuid))
        assertThat(result.contributions[1]).isEqualTo(expectedContribution2)
        assertThat(result.contributions[2]).isEqualTo(expectedContribution3)
    }

    @Test
    fun `createReportBudget - empty co-financing in AF`() {
        val partnerId = 79L
        val projectId = 35L
        val version = "v8.1"
        val budgetOptions = mockk<ProjectPartnerBudgetOptions>()
        val partner = mockInputsAndGetPartner(projectId, partnerId = partnerId, version, budgetOptions)

        every {
            projectWorkPackagePersistence.getProjectInvestmentSummaries(
                projectId,
                version
            )
        } returns investmentSummaries
        every { reportExpenditurePersistence.getPartnerReportExpenditureCosts(partnerId, 408L) } returns emptyList()
        every { reportExpenditureCoFinancingPersistence.getCoFinancingCumulative(setOf(408L)) } returns previousReportedCoFinancing.copy(
            funds = emptyMap()
        )

        val result = service.retrieveBudgetDataFor(
            projectId,
            partner,
            version,
            coFinancing.copy(finances = emptyList()),
            emptyList()
        )

        assertThat(result.previouslyReportedCoFinancing)
            .isEqualTo(
                expectedPreviouslyReportedCoFinancing.copy(
                    fundsSorted = listOf(
                        PreviouslyReportedFund(
                            fundId = null,
                            percentage = BigDecimal.valueOf(100),
                            total = BigDecimal.ZERO,
                            previouslyReported = BigDecimal.valueOf(0, 2),
                            previouslyPaid = BigDecimal.ZERO,
                        ),
                    ),
                    previouslyReportedPartner = BigDecimal.valueOf(900, 2), /* should be no change on empty */
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
        // contribution
        every { reportPersistence.getSubmittedPartnerReportIds(partnerId) } returns setOf(408L)
        every { reportContributionPersistence.getAllContributionsForReportIds(setOf(408L)) } returns previousContributions
        // lump sums
        every { lumpSumPersistence.getLumpSums(projectId, version) } returns lumpSums(partnerId)
        every { reportLumpSumPersistence.getLumpSumCumulative(setOf(408L)) } returns
            mapOf(
                14 to ExpenditureLumpSumCurrent(current = BigDecimal.TEN, currentParked = BigDecimal.TEN),
                15 to ExpenditureLumpSumCurrent(current = BigDecimal.valueOf(100), currentParked = BigDecimal.TEN),
                16 to ExpenditureLumpSumCurrent(current = BigDecimal.valueOf(200), currentParked = BigDecimal.TEN)
            )
        // unit costs
        every { partnerBudgetCostsPersistence.getBudgetStaffCosts(partnerId, version) } returns staffCosts
        every {
            partnerBudgetCostsPersistence.getBudgetTravelAndAccommodationCosts(
                partnerId,
                version
            )
        } returns travelCosts
        every {
            partnerBudgetCostsPersistence.getBudgetExternalExpertiseAndServicesCosts(
                partnerId,
                version
            )
        } returns externalCosts
        every { partnerBudgetCostsPersistence.getBudgetEquipmentCosts(partnerId, version) } returns equipmentCosts
        every {
            partnerBudgetCostsPersistence.getBudgetInfrastructureAndWorksCosts(
                partnerId,
                version
            )
        } returns infrastructureCosts
        every { partnerBudgetCostsPersistence.getBudgetUnitCosts(partnerId, version) } returns unitCosts
        every { reportUnitCostPersistence.getUnitCostCumulative(setOf(408L)) } returns
            mapOf(
                6L to ExpenditureUnitCostCurrent(current = BigDecimal.TEN, currentParked = BigDecimal.ZERO),
                7L to ExpenditureUnitCostCurrent(current = BigDecimal.valueOf(100), currentParked = BigDecimal.ZERO),
                8L to ExpenditureUnitCostCurrent(current = BigDecimal.ZERO, currentParked = BigDecimal.ZERO)
            )
        // investments
        every { reportInvestmentPersistence.getInvestmentsCumulative(setOf(408L)) } returns mapOf(
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
        every { reportExpenditureCostCategoryPersistence.getCostCategoriesCumulative(setOf(408L)) } returns previousExpenditures
        // previouslyReportedCoFinancing
        every { reportExpenditureCoFinancingPersistence.getCoFinancingCumulative(setOf(408L)) } returns previousReportedCoFinancing

        return partner
    }

}
