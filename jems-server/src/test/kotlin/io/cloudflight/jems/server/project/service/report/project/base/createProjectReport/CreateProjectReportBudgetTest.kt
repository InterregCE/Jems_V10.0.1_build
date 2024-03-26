package io.cloudflight.jems.server.project.service.report.project.base.createProjectReport

import io.cloudflight.jems.api.project.dto.partner.cofinancing.ProjectPartnerCoFinancingFundTypeDTO
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.call.service.CallPersistence
import io.cloudflight.jems.server.payments.model.regular.PaymentToProject
import io.cloudflight.jems.server.payments.model.regular.PaymentType
import io.cloudflight.jems.server.payments.service.regular.PaymentPersistence
import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFund
import io.cloudflight.jems.server.project.repository.partner.cofinancing.ProjectPartnerCoFinancingPersistenceProvider
import io.cloudflight.jems.server.project.repository.report.project.financialOverview.coFinancing.ProjectReportCertificateCoFinancingPersistenceProvider
import io.cloudflight.jems.server.project.service.budget.ProjectBudgetPersistence
import io.cloudflight.jems.server.project.service.budget.get_partner_budget_per_funds.GetPartnerBudgetPerFundService
import io.cloudflight.jems.server.project.service.budget.get_project_budget.GetProjectBudget
import io.cloudflight.jems.server.project.service.budget.model.BudgetCostsCalculationResultFull
import io.cloudflight.jems.server.project.service.budget.model.PartnerBudget
import io.cloudflight.jems.server.project.service.contracting.model.reporting.ContractingDeadlineType
import io.cloudflight.jems.server.project.service.lumpsum.ProjectLumpSumPersistence
import io.cloudflight.jems.server.project.service.lumpsum.model.ProjectLumpSum
import io.cloudflight.jems.server.project.service.lumpsum.model.ProjectPartnerLumpSum
import io.cloudflight.jems.server.project.service.model.PartnerBudgetPerFund
import io.cloudflight.jems.server.project.service.model.ProjectPartnerBudgetPerFund
import io.cloudflight.jems.server.project.service.partner.budget.ProjectPartnerBudgetCostsPersistence
import io.cloudflight.jems.server.project.service.partner.budget.get_budget_total_cost.GetBudgetTotalCost
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerCoFinancing
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerCoFinancingAndContributionSpf
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerContributionSpf
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerContributionStatus
import io.cloudflight.jems.server.project.service.partner.model.BudgetGeneralCostEntry
import io.cloudflight.jems.server.project.service.partner.model.BudgetStaffCostEntry
import io.cloudflight.jems.server.project.service.partner.model.BudgetTravelAndAccommodationCostEntry
import io.cloudflight.jems.server.project.service.partner.model.BudgetUnitCostEntry
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerRole
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerSummary
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.PartnerReportInvestmentSummary
import io.cloudflight.jems.server.project.service.report.model.project.ProjectReportStatus
import io.cloudflight.jems.server.project.service.report.model.project.base.create.PreviouslyProjectReportedCoFinancing
import io.cloudflight.jems.server.project.service.report.model.project.base.create.PreviouslyProjectReportedFund
import io.cloudflight.jems.server.project.service.report.model.project.base.create.ProjectReportInvestment
import io.cloudflight.jems.server.project.service.report.model.project.base.create.ProjectReportLumpSum
import io.cloudflight.jems.server.project.service.report.model.project.base.create.ProjectReportStatusAndType
import io.cloudflight.jems.server.project.service.report.model.project.base.create.ProjectReportUnitCostBase
import io.cloudflight.jems.server.project.service.report.model.project.financialOverview.coFinancing.PaymentCumulativeAmounts
import io.cloudflight.jems.server.project.service.report.model.project.financialOverview.coFinancing.PaymentCumulativeData
import io.cloudflight.jems.server.project.service.report.model.project.financialOverview.coFinancing.ReportCertificateCoFinancingColumn
import io.cloudflight.jems.server.project.service.report.model.project.financialOverview.coFinancing.ReportCertificateCoFinancingPrevious
import io.cloudflight.jems.server.project.service.report.model.project.financialOverview.costCategory.CertificateCostCategoryPrevious
import io.cloudflight.jems.server.project.service.report.model.project.financialOverview.costCategory.ReportCertificateCostCategory
import io.cloudflight.jems.server.project.service.report.model.project.spfContributionClaim.ProjectReportSpfContributionClaimCreate
import io.cloudflight.jems.server.project.service.report.model.project.spfContributionClaim.SpfPreviouslyReportedByContributionSource
import io.cloudflight.jems.server.project.service.report.model.project.spfContributionClaim.SpfPreviouslyReportedContributionRow
import io.cloudflight.jems.server.project.service.report.project.financialOverview.ProjectReportCertificateCostCategoryPersistence
import io.cloudflight.jems.server.project.service.report.project.financialOverview.ProjectReportCertificateInvestmentPersistence
import io.cloudflight.jems.server.project.service.report.project.financialOverview.ProjectReportCertificateLumpSumPersistence
import io.cloudflight.jems.server.project.service.report.project.financialOverview.ProjectReportCertificateUnitCostPersistence
import io.cloudflight.jems.server.project.service.report.project.spfContributionClaim.ProjectReportSpfContributionClaimPersistence
import io.cloudflight.jems.server.toScaledBigDecimal
import io.cloudflight.jems.server.utils.ERDF_FUND
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.ZonedDateTime

internal class CreateProjectReportBudgetTest : UnitTest() {

    private val partnerId = 18L
    private val fundId = 51L
    private val unitCostId = 88L
    private val unitCostId_Multiple = 89L
    private val investmentId = 72L
    private fun partners(): List<ProjectPartnerSummary> {
        val partner = mockk<ProjectPartnerSummary>()
        every { partner.id } returns partnerId
        return listOf(partner)
    }
    private fun fund(id: Long): ProgrammeFund {
        val fund = mockk<ProgrammeFund>()
        every { fund.id } returns id
        return fund
    }

    private fun budgetUnitCostEntries() = listOf(
        BudgetUnitCostEntry(
            id = 1L,
            numberOfUnits = BigDecimal.ONE,
            budgetPeriods = mutableSetOf(),
            rowSum = BigDecimal.ONE,
            unitCostId = unitCostId_Multiple,
        )
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
                    amount = BigDecimal.valueOf(10_002_000L),
                ),
            ),
            fastTrack = true,
            readyForPayment = true,
        ),
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

    private val currentTime = ZonedDateTime.now()

    private val payment = PaymentToProject(
        id = 1L,
        paymentType = PaymentType.FTLS,
        projectId = 2L,
        projectCustomIdentifier = "PR1",
        projectAcronym = "Test Project",
        paymentClaimId = null,
        paymentClaimNo = 0,
        paymentToEcId = 6L,
        lumpSumId = 45L,
        orderNr = 16,
        fund = mockk(),
        fundAmount = BigDecimal(100),
        amountPaidPerFund = BigDecimal.valueOf(6789L, 2),
        amountAuthorizedPerFund = BigDecimal.valueOf(6789L, 2),
        paymentApprovalDate = currentTime,
        paymentClaimSubmissionDate = null,
        totalEligibleAmount = BigDecimal(10),
        dateOfLastPayment = mockk(),
        lastApprovedVersionBeforeReadyForPayment = "v1.0",
        remainingToBePaid = BigDecimal.valueOf(954L),
    )

    private val previouslyReportedCostCategory = CertificateCostCategoryPrevious(
        previouslyReported = BudgetCostsCalculationResultFull(
            staff = BigDecimal.valueOf(16),
            office = BigDecimal.valueOf(17),
            travel = BigDecimal.valueOf(18),
            external = BigDecimal.valueOf(19),
            equipment = BigDecimal.valueOf(20),
            infrastructure = BigDecimal.valueOf(21),
            other = BigDecimal.valueOf(22),
            lumpSum = BigDecimal.valueOf(23),
            unitCost = BigDecimal.valueOf(24),
            spfCost = BigDecimal.valueOf(25),
            sum = BigDecimal(205),
        ),
        previouslyVerified = BudgetCostsCalculationResultFull(
            staff = BigDecimal.valueOf(21),
            office = BigDecimal.valueOf(22),
            travel = BigDecimal.valueOf(23),
            external = BigDecimal.valueOf(24),
            equipment = BigDecimal.valueOf(25),
            infrastructure = BigDecimal.valueOf(26),
            other = BigDecimal.valueOf(27),
            lumpSum = BigDecimal.valueOf(28),
            unitCost = BigDecimal.valueOf(29),
            spfCost = BigDecimal.valueOf(30),
            sum = BigDecimal(255),
        )
    )

    private val totalPerFundLineFromAF = ProjectPartnerBudgetPerFund(
        partner = null, // total line (sums)
        budgetPerFund = setOf(
            PartnerBudgetPerFund(fund = fund(fundId), percentage = mockk(), value = BigDecimal.valueOf(12_345L)),
        ),
        publicContribution = BigDecimal.valueOf(222L),
        autoPublicContribution = BigDecimal.valueOf(333L),
        privateContribution = BigDecimal.valueOf(444L),
        totalPartnerContribution = BigDecimal.valueOf(999L),
        totalEligibleBudget = BigDecimal.valueOf(100_000L),
    )

    private val previousCoFinancing = ReportCertificateCoFinancingPrevious(
        previouslyReported = ReportCertificateCoFinancingColumn(
            funds = mapOf(
                fundId to BigDecimal.valueOf(3275L, 2),
                null to BigDecimal.valueOf(4725L, 2),
            ),
            partnerContribution = BigDecimal.valueOf(4725L, 2),
            publicContribution = BigDecimal.valueOf(1574L, 2),
            automaticPublicContribution = BigDecimal.valueOf(1575L, 2),
            privateContribution = BigDecimal.valueOf(1576L, 2),
            sum = BigDecimal.valueOf(8000L, 2),
        ),
        previouslyVerified = ReportCertificateCoFinancingColumn(
            funds = mapOf(
                fundId to BigDecimal.valueOf(475L, 2),
                null to BigDecimal.valueOf(525L, 2),
            ),
            partnerContribution = BigDecimal.valueOf(525L, 2),
            publicContribution = BigDecimal.valueOf(174L, 2),
            automaticPublicContribution = BigDecimal.valueOf(175L, 2),
            privateContribution = BigDecimal.valueOf(176L, 2),
            sum = BigDecimal.valueOf(1000L, 2),
        ),
    )

    private val paymentCumulative = PaymentCumulativeData(
        amounts = PaymentCumulativeAmounts(
            funds = mapOf(fundId to BigDecimal.valueOf(10_000_000L)),
            partnerContribution = BigDecimal.valueOf(9_000_000L),
            publicContribution = BigDecimal.valueOf(2_000_000L),
            automaticPublicContribution = BigDecimal.valueOf(3_000_000L),
            privateContribution = BigDecimal.valueOf(4_000_000L),
        ),
        confirmedAndPaid = mapOf(
            fundId to BigDecimal.valueOf(500_000L),
        ),
    )

    private fun staffCost(unitCostId: Long?): BudgetStaffCostEntry {
        val cost = mockk<BudgetStaffCostEntry>()
        every { cost.unitCostId } returns unitCostId
        every { cost.rowSum } returns BigDecimal.valueOf(300L)
        every { cost.numberOfUnits } returns BigDecimal.valueOf(3L)
        return cost
    }

    private fun travelCost(unitCostId: Long?): BudgetTravelAndAccommodationCostEntry {
        val cost = mockk<BudgetTravelAndAccommodationCostEntry>()
        every { cost.unitCostId } returns unitCostId
        every { cost.rowSum } returns BigDecimal.valueOf(425L)
        every { cost.numberOfUnits } returns BigDecimal.valueOf(425L, 2)
        return cost
    }

    private fun generalCost(unitCostId: Long?, investmentId: Long?): BudgetGeneralCostEntry {
        val cost = mockk<BudgetGeneralCostEntry>()
        every { cost.unitCostId } returns unitCostId
        every { cost.rowSum } returns BigDecimal.valueOf(271L)
        every { cost.numberOfUnits } returns BigDecimal.valueOf(271L, 2)
        every { cost.investmentId } returns investmentId
        return cost
    }

    private val expectedCoFinancing = PreviouslyProjectReportedCoFinancing(
        fundsSorted = listOf(
            PreviouslyProjectReportedFund(
                fundId = fundId,
                total = BigDecimal.valueOf(12345L),
                previouslyReported = BigDecimal.valueOf(10_000_032_75L, 2),
                previouslyVerified = BigDecimal.valueOf(10_000_004_75L, 2),
                previouslyPaid = BigDecimal.valueOf(500_000L),
            ),
            PreviouslyProjectReportedFund(
                fundId = null,
                total = BigDecimal.valueOf(999L),
                previouslyReported = BigDecimal.valueOf(2_047_25L, 2),
                previouslyVerified = BigDecimal.valueOf(2_005_25L, 2),
                previouslyPaid = BigDecimal.ZERO,
            ),
        ),

        totalPartner = BigDecimal.valueOf(999L),
        totalPublic = BigDecimal.valueOf(222L),
        totalAutoPublic = BigDecimal.valueOf(333L),
        totalPrivate = BigDecimal.valueOf(444L),
        totalSum = BigDecimal.valueOf(100_000L),

        previouslyReportedPartner = BigDecimal.valueOf(2_047_25L, 2),
        previouslyReportedPublic = BigDecimal.valueOf(2_000_015_74L, 2),
        previouslyReportedAutoPublic = BigDecimal.valueOf(3_000_015_75L, 2),
        previouslyReportedPrivate = BigDecimal.valueOf(4_000_015_76L, 2),
        previouslyReportedSum = BigDecimal.valueOf(10_002_080_00L, 2),

        previouslyVerifiedPartner = BigDecimal.valueOf(2_005_25L, 2),
        previouslyVerifiedPublic = BigDecimal.valueOf(2_000_001_74L, 2),
        previouslyVerifiedAutoPublic = BigDecimal.valueOf(3_000_001_75L, 2),
        previouslyVerifiedPrivate = BigDecimal.valueOf(4_000_001_76L, 2),
        previouslyVerifiedSum = BigDecimal.valueOf(1_000_2010_00L, 2),
    )

    private val expectedCostCategory = ReportCertificateCostCategory(
        totalsFromAF = BudgetCostsCalculationResultFull(
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
        ),
        currentlyReported = BudgetCostsCalculationResultFull(
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
        ),
        previouslyReported = BudgetCostsCalculationResultFull(
            staff = BigDecimal.valueOf(16),
            office = BigDecimal.valueOf(17),
            travel = BigDecimal.valueOf(18),
            external = BigDecimal.valueOf(19),
            equipment = BigDecimal.valueOf(20),
            infrastructure = BigDecimal.valueOf(21),
            other = BigDecimal.valueOf(22),
            lumpSum = BigDecimal.valueOf(10_002_023L),
            unitCost = BigDecimal.valueOf(24),
            spfCost = BigDecimal.valueOf(25L),
            sum = BigDecimal.valueOf(10_002_205L),
        ),
        currentVerified = BudgetCostsCalculationResultFull(
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
        ),
        previouslyVerified =  BudgetCostsCalculationResultFull(
            staff = BigDecimal.valueOf(21),
            office = BigDecimal.valueOf(22),
            travel = BigDecimal.valueOf(23),
            external = BigDecimal.valueOf(24),
            equipment = BigDecimal.valueOf(25),
            infrastructure = BigDecimal.valueOf(26),
            other = BigDecimal.valueOf(27),
            lumpSum = BigDecimal.valueOf(10_002_028L),
            unitCost = BigDecimal.valueOf(29),
            spfCost = BigDecimal.valueOf(30),
            sum = BigDecimal.valueOf(10_002_255L),
        )
    )

    private val expectedLumpSum_14 = ProjectReportLumpSum(
        lumpSumId = 44L,
        orderNr = 14,
        period = 3,
        total = BigDecimal.valueOf(10),
        previouslyReported = BigDecimal.ZERO,
        previouslyPaid = BigDecimal.ZERO,
        previouslyVerified = BigDecimal.ZERO,
    )
    private val expectedLumpSum_15 = ProjectReportLumpSum(
        lumpSumId = 45L,
        orderNr = 15,
        period = 4,
        total = BigDecimal.valueOf(13),
        previouslyReported = BigDecimal.ZERO,
        previouslyPaid = BigDecimal.ZERO,
        previouslyVerified = BigDecimal.ZERO,
    )
    private val expectedLumpSum_16 = ProjectReportLumpSum(
        lumpSumId = 45L,
        orderNr = 16,
        period = 4,
        total = BigDecimal.valueOf(10_002_000L),
        previouslyReported = BigDecimal.valueOf(10_002_000L),
        previouslyPaid = BigDecimal.valueOf(6789L, 2),
        previouslyVerified = BigDecimal.valueOf(20_004_000L),
    )

    private val expectedUnitCost = ProjectReportUnitCostBase(
        unitCostId = unitCostId,
        numberOfUnits = BigDecimal.valueOf(1267L, 2),
        totalCost = BigDecimal.valueOf(1267L),
        previouslyReported = BigDecimal.valueOf(10),
        previouslyVerified = BigDecimal.valueOf(20),
    )
    private val expectedUnitCost_Multiple = ProjectReportUnitCostBase(
        unitCostId = unitCostId_Multiple,
        numberOfUnits = BigDecimal.ONE,
        totalCost = BigDecimal.ONE,
        previouslyReported = BigDecimal.ZERO,
        previouslyVerified = BigDecimal.ZERO,
    )
    private val expectedInvestment = ProjectReportInvestment(
        investmentId = investmentId,
        investmentNumber = 2,
        workPackageNumber = 3,
        title = setOf(),
        deactivated = false,
        total = BigDecimal.valueOf(542L),
        previouslyReported = BigDecimal.TEN,
        previouslyVerified = BigDecimal.valueOf(15),
    )


    private val expectedSpfContributions = listOf(
        ProjectReportSpfContributionClaimCreate(
            fundId = 1L,
            idFromApplicationForm = null,
            sourceOfContribution = null,
            legalStatus = null,
            amountInAf = 500.00.toScaledBigDecimal(),
            previouslyReported = BigDecimal.valueOf(100.00),
            currentlyReported = BigDecimal.ZERO
        ),
        ProjectReportSpfContributionClaimCreate(
            fundId = null,
            idFromApplicationForm = 1L,
            sourceOfContribution = "Contribution source one",
            legalStatus = ProjectPartnerContributionStatus.Private,
            amountInAf = BigDecimal.valueOf(500.00),
            previouslyReported = BigDecimal.valueOf(50.00),
            currentlyReported = BigDecimal.ZERO
        )
    )

    @MockK private lateinit var lumpSumPersistence: ProjectLumpSumPersistence
    @MockK private lateinit var getProjectBudget: GetProjectBudget
    @MockK private lateinit var reportCertificateCoFinancingPersistence: ProjectReportCertificateCoFinancingPersistenceProvider
    @MockK private lateinit var paymentPersistence: PaymentPersistence
    @MockK private lateinit var reportCertificateCostCategoryPersistence: ProjectReportCertificateCostCategoryPersistence
    @MockK private lateinit var getPartnerBudgetPerFundService: GetPartnerBudgetPerFundService
    @MockK private lateinit var reportCertificateLumpSumPersistence: ProjectReportCertificateLumpSumPersistence
    @MockK private lateinit var partnerBudgetCostsPersistence: ProjectPartnerBudgetCostsPersistence
    @MockK private lateinit var projectBudgetPersistence: ProjectBudgetPersistence
    @MockK private lateinit var reportCertificateUnitCostPersistence: ProjectReportCertificateUnitCostPersistence
    @MockK private lateinit var reportInvestmentPersistence: ProjectReportCertificateInvestmentPersistence
    @MockK private lateinit var projectPartnerCoFinancingPersistence: ProjectPartnerCoFinancingPersistenceProvider
    @MockK private lateinit var projectReportSpfContributionClaimPersistence: ProjectReportSpfContributionClaimPersistence
    @MockK private lateinit var callPersistence: CallPersistence
    @MockK private lateinit var getBudgetTotalCost: GetBudgetTotalCost

    @InjectMockKs private lateinit var service: CreateProjectReportBudget

    @BeforeEach
    fun reset() {
        clearMocks(
            lumpSumPersistence,
            getProjectBudget,
            reportCertificateCoFinancingPersistence,
            paymentPersistence,
            reportCertificateCostCategoryPersistence,
            getPartnerBudgetPerFundService,
            reportCertificateLumpSumPersistence,
            partnerBudgetCostsPersistence,
            projectBudgetPersistence,
            reportCertificateUnitCostPersistence,
            reportInvestmentPersistence,
            projectPartnerCoFinancingPersistence,
            projectReportSpfContributionClaimPersistence,
            callPersistence,
            getBudgetTotalCost
        )
    }

    @Test
    fun createReportBudget() {
        val projectId = 30L
        val version = "v4.2"

        every { projectBudgetPersistence.getPartnersForProjectId(projectId, version) } returns partners()
        every { partnerBudgetCostsPersistence.getBudgetStaffCosts(setOf(partnerId), version) } returns listOf(staffCost(unitCostId))
        every { partnerBudgetCostsPersistence.getBudgetTravelAndAccommodationCosts(setOf(partnerId), version) } returns listOf(travelCost(unitCostId))
        every { partnerBudgetCostsPersistence.getBudgetExternalExpertiseAndServicesCosts(setOf(partnerId), version) } returns listOf(
            generalCost(unitCostId, investmentId)
        )
        every { partnerBudgetCostsPersistence.getBudgetEquipmentCosts(setOf(partnerId), version) } returns listOf(
            generalCost(null, investmentId)
        )
        every { partnerBudgetCostsPersistence.getBudgetInfrastructureAndWorksCosts(setOf(partnerId), version) } returns listOf(
            generalCost(unitCostId, null)
        )
        every { partnerBudgetCostsPersistence.getBudgetUnitCosts(setOf(partnerId), version) } returns budgetUnitCostEntries()
        every { lumpSumPersistence.getLumpSums(projectId, version) } returns lumpSums(partnerId = partnerId)
        every { reportCertificateCostCategoryPersistence.getCostCategoriesCumulative(setOf(21L, 28L), setOf(28L)) } returns previouslyReportedCostCategory
        every { getPartnerBudgetPerFundService.getProjectPartnerBudgetPerFund(projectId, version) } returns listOf(totalPerFundLineFromAF)
        every { getProjectBudget.getBudget(projectId, version) } returns listOf(partnerBudget(ProjectPartnerSummary(
            id = 1L,
            abbreviation = "LP",
            active = true,
            role = ProjectPartnerRole.LEAD_PARTNER
        )))
        every { reportCertificateCoFinancingPersistence.getCoFinancingCumulative(setOf(21L, 28L), setOf(28L)) } returns previousCoFinancing
        every { paymentPersistence.getFtlsCumulativeForProject(projectId) } returns paymentCumulative

        every { reportCertificateLumpSumPersistence.getReportedLumpSumCumulative(setOf(21L, 28L)) } returns mapOf(Pair(1, BigDecimal.valueOf(1_944L)))
        every { reportCertificateLumpSumPersistence.getVerifiedLumpSumCumulative(setOf(28L)) } returns mapOf(Pair(16, BigDecimal.valueOf(10_002_000L)))

        every { paymentPersistence.getPaymentsByProjectId(projectId) } returns listOf(payment)

        every { reportCertificateUnitCostPersistence.getReportedUnitCostsCumulative(setOf(21L, 28L)) } returns mapOf(Pair(unitCostId, BigDecimal.TEN))
        every { reportCertificateUnitCostPersistence.getVerifiedUnitCostsCumulative(setOf(28L)) } returns mapOf(Pair(unitCostId, BigDecimal.valueOf(20)))

        every { reportInvestmentPersistence.getReportedInvestmentCumulative(setOf(21L, 28L)) } returns mapOf(Pair(investmentId, BigDecimal.TEN))
        every { reportInvestmentPersistence.getVerifiedInvestmentCumulative(setOf(28L)) } returns mapOf(Pair(investmentId, BigDecimal.valueOf(15)))

        every { callPersistence.getCallByProjectId(projectId).isSpf() } returns false

        val result = service.retrieveBudgetDataFor(
            projectId = projectId,
            version = version,
            investments = listOf(
                PartnerReportInvestmentSummary(investmentId = investmentId, 2, 3, emptySet(), false)
            ),
            submittedReports = listOf(
                ProjectReportStatusAndType(21L, ProjectReportStatus.Submitted, ContractingDeadlineType.Both),
                ProjectReportStatusAndType(28L, ProjectReportStatus.Finalized, ContractingDeadlineType.Both),
            ),
        )

        assertThat(result.coFinancing).isEqualTo(expectedCoFinancing)
        assertThat(result.costCategorySetup).isEqualTo(expectedCostCategory)
        assertThat(result.availableLumpSums).containsExactly(expectedLumpSum_14, expectedLumpSum_15, expectedLumpSum_16)
        assertThat(result.unitCosts).containsExactly(expectedUnitCost, expectedUnitCost_Multiple)
        assertThat(result.investments).containsExactly(expectedInvestment)
    }


    @Test
    fun `createReportBudget - with spf co financing`() {
        val projectId = 30L
        val version = "v4.2"
        val coFinancingAndContributionSpf = ProjectPartnerCoFinancingAndContributionSpf(
            finances = listOf(
                ProjectPartnerCoFinancing(
                    fundType = ProjectPartnerCoFinancingFundTypeDTO.MainFund,
                    fund = ERDF_FUND,
                    percentage = BigDecimal.valueOf(50.00)
                )
            ),
            partnerContributions = listOf(
                ProjectPartnerContributionSpf(
                    id = 1L,
                    name = "Contribution source one",
                    status = ProjectPartnerContributionStatus.Private,
                    amount = BigDecimal.valueOf(500.00)
                )
            )
        )
        val totalCostSpf = 1000.toScaledBigDecimal()
        val previouslyReportedSpfCoFin = SpfPreviouslyReportedByContributionSource(
            finances = mapOf(
                1L to SpfPreviouslyReportedContributionRow(
                    id = 1L,
                    programmeFundId = 1L,
                    applicationFormPartnerContributionId = null,
                    sourceOfContribution = null,
                    legalStatus = null,
                    previouslyReportedAmount = BigDecimal.valueOf(100.00)
                )
            ),
            partnerContributions = mapOf(
                1L to SpfPreviouslyReportedContributionRow(
                    id = 1L,
                    programmeFundId = 1L,
                    applicationFormPartnerContributionId = null,
                    sourceOfContribution = null,
                    legalStatus = null,
                    previouslyReportedAmount = BigDecimal.valueOf(50.00)
                )
            ),
        )

        every { projectBudgetPersistence.getPartnersForProjectId(projectId, version) } returns partners()
        every { partnerBudgetCostsPersistence.getBudgetStaffCosts(setOf(partnerId), version) } returns listOf(staffCost(unitCostId))
        every { partnerBudgetCostsPersistence.getBudgetTravelAndAccommodationCosts(setOf(partnerId), version) } returns listOf(travelCost(unitCostId))
        every { partnerBudgetCostsPersistence.getBudgetExternalExpertiseAndServicesCosts(setOf(partnerId), version) } returns listOf(
            generalCost(unitCostId, investmentId)
        )
        every { partnerBudgetCostsPersistence.getBudgetEquipmentCosts(setOf(partnerId), version) } returns listOf(
            generalCost(null, investmentId)
        )
        every { partnerBudgetCostsPersistence.getBudgetInfrastructureAndWorksCosts(setOf(partnerId), version) } returns listOf(
            generalCost(unitCostId, null)
        )
        every { partnerBudgetCostsPersistence.getBudgetUnitCosts(setOf(partnerId), version) } returns budgetUnitCostEntries()
        every { lumpSumPersistence.getLumpSums(projectId, version) } returns lumpSums(partnerId = partnerId)
        every { reportCertificateCostCategoryPersistence.getCostCategoriesCumulative(setOf(21L, 28L), setOf(28L)) } returns previouslyReportedCostCategory
        every { getPartnerBudgetPerFundService.getProjectPartnerBudgetPerFund(projectId, version) } returns listOf(totalPerFundLineFromAF)
        every { getProjectBudget.getBudget(projectId, version) } returns listOf(partnerBudget(ProjectPartnerSummary(
            id = 1L,
            abbreviation = "LP",
            active = true,
            role = ProjectPartnerRole.LEAD_PARTNER
        )))
        every { reportCertificateCoFinancingPersistence.getCoFinancingCumulative(setOf(21L, 28L), setOf(28L)) } returns previousCoFinancing
        every { paymentPersistence.getFtlsCumulativeForProject(projectId) } returns paymentCumulative

        every { reportCertificateLumpSumPersistence.getReportedLumpSumCumulative(setOf(21L, 28L)) } returns mapOf(Pair(1, BigDecimal.valueOf(1_944L)))
        every { reportCertificateLumpSumPersistence.getVerifiedLumpSumCumulative(setOf(28L)) } returns mapOf(Pair(16, BigDecimal.valueOf(10_002_000L)))

        every { paymentPersistence.getPaymentsByProjectId(projectId) } returns listOf(payment)

        every { reportCertificateUnitCostPersistence.getReportedUnitCostsCumulative(setOf(21L, 28L)) } returns mapOf(Pair(unitCostId, BigDecimal.TEN))
        every { reportCertificateUnitCostPersistence.getVerifiedUnitCostsCumulative(setOf(28L)) } returns mapOf(Pair(unitCostId, BigDecimal.valueOf(20)))

        every { reportInvestmentPersistence.getReportedInvestmentCumulative(setOf(21L, 28L)) } returns mapOf(Pair(investmentId, BigDecimal.TEN))
        every { reportInvestmentPersistence.getVerifiedInvestmentCumulative(setOf(28L)) } returns mapOf(Pair(investmentId, BigDecimal.valueOf(15)))

        every { callPersistence.getCallByProjectId(projectId).isSpf() } returns true
        every { projectPartnerCoFinancingPersistence.getSpfCoFinancingAndContributions( partnerId, version) } returns coFinancingAndContributionSpf
        every { projectReportSpfContributionClaimPersistence.getSpfContributionCumulative(setOf(21L, 28L)) } returns previouslyReportedSpfCoFin
        every { getBudgetTotalCost.getBudgetTotalSpfCost( partnerId, version) } returns totalCostSpf

        val result = service.retrieveBudgetDataFor(
            projectId = projectId,
            version = version,
            investments = listOf(
                PartnerReportInvestmentSummary(investmentId = investmentId, 2, 3, emptySet(), false)
            ),
            submittedReports = listOf(
                ProjectReportStatusAndType(21L, ProjectReportStatus.Submitted, ContractingDeadlineType.Both),
                ProjectReportStatusAndType(28L, ProjectReportStatus.Finalized, ContractingDeadlineType.Both),
            ),
        )

        assertThat(result.coFinancing).isEqualTo(expectedCoFinancing)
        assertThat(result.costCategorySetup).isEqualTo(expectedCostCategory)
        assertThat(result.availableLumpSums).containsExactly(expectedLumpSum_14, expectedLumpSum_15, expectedLumpSum_16)
        assertThat(result.unitCosts).containsExactly(expectedUnitCost, expectedUnitCost_Multiple)
        assertThat(result.investments).containsExactly(expectedInvestment)

        assertThat(result.spfContributionClaims).isEqualTo(expectedSpfContributions)
    }

}
