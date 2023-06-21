package io.cloudflight.jems.server.project.service.report.project.base.createProjectReport

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.payments.model.regular.PaymentToProject
import io.cloudflight.jems.server.payments.model.regular.PaymentType
import io.cloudflight.jems.server.payments.service.regular.PaymentRegularPersistence
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
import io.cloudflight.jems.server.project.service.model.ProjectPartnerBudgetPerFund
import io.cloudflight.jems.server.project.service.partner.budget.ProjectPartnerBudgetCostsPersistence
import io.cloudflight.jems.server.project.service.partner.model.BudgetUnitCostEntry
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerSummary
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerRole
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.PartnerReportInvestmentSummary
import io.cloudflight.jems.server.project.service.report.model.project.base.create.PreviouslyProjectReportedFund
import io.cloudflight.jems.server.project.service.report.model.project.financialOverview.coFinancing.ReportCertificateCoFinancingColumn
import io.cloudflight.jems.server.project.service.report.model.project.financialOverview.costCategory.CertificateCostCategoryPreviouslyReported
import io.cloudflight.jems.server.project.service.report.project.base.ProjectReportPersistence
import io.cloudflight.jems.server.project.service.report.project.financialOverview.ProjectReportCertificateCostCategoryPersistence
import io.cloudflight.jems.server.project.service.report.project.financialOverview.ProjectReportCertificateInvestmentPersistence
import io.cloudflight.jems.server.project.service.report.project.financialOverview.ProjectReportCertificateLumpSumPersistence
import io.cloudflight.jems.server.project.service.report.project.financialOverview.ProjectReportCertificateUnitCostPersistence
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.ZonedDateTime

internal class CreateProjectReportBudgetTest : UnitTest() {

    private fun partners(): List<ProjectPartnerSummary> {
        val partner = mockk<ProjectPartnerSummary>()
        every { partner.id } returns 1L
        return listOf(partner)
    }

    private fun budgetUnitCostEntries() = listOf(
        BudgetUnitCostEntry(
            id = 1L,
            numberOfUnits = BigDecimal.ONE,
            budgetPeriods = mutableSetOf(),
            rowSum = BigDecimal.ONE,
            unitCostId = 1L
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
                    amount = BigDecimal.valueOf(1033, 2),
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
        totalCosts = BigDecimal.valueOf(19),
    )

    private val currentTime = ZonedDateTime.now()

    private val payment = PaymentToProject(
        id = 1L,
        paymentType = PaymentType.FTLS,
        projectCustomIdentifier = "PR1",
        projectAcronym = "Test Project",
        paymentClaimNo = 0,
        fundName = "OTHER",
        fundId = 1L,
        amountApprovedPerFund = BigDecimal(100),
        amountPaidPerFund = BigDecimal.ZERO,
        paymentApprovalDate = currentTime,
        paymentClaimSubmissionDate = null,
        totalEligibleAmount = BigDecimal(10),
        lastApprovedVersionBeforeReadyForPayment = "v1.0"
    )

    private val cumulativeFund = ReportCertificateCoFinancingColumn(
        funds = mapOf(1L to BigDecimal(100), null to BigDecimal.valueOf(184L)),
        partnerContribution = BigDecimal.valueOf(24),
        publicContribution = BigDecimal.valueOf(25),
        automaticPublicContribution = BigDecimal.valueOf(26),
        privateContribution = BigDecimal.valueOf(27),
        sum = BigDecimal.valueOf(28),
    )

    private val costCategoryBreakdownFromAF = BudgetCostsCalculationResultFull(
        staff = BigDecimal.TEN,
        office = BigDecimal.TEN,
        travel = BigDecimal.TEN,
        external = BigDecimal.TEN,
        equipment = BigDecimal.TEN,
        infrastructure = BigDecimal.TEN,
        other = BigDecimal.TEN,
        lumpSum = BigDecimal.TEN,
        unitCost = BigDecimal.TEN,
        sum = BigDecimal(90),
    )

    private val previouslyReportedCostCategory = CertificateCostCategoryPreviouslyReported(
        previouslyReported = costCategoryBreakdownFromAF
    )

    private val projectBudgetPerFund = listOf(
        ProjectPartnerBudgetPerFund()
    )

    @MockK
    lateinit var reportPersistence: ProjectReportPersistence

    @MockK
    lateinit var lumpSumPersistence: ProjectLumpSumPersistence

    @MockK
    lateinit var getProjectBudget: GetProjectBudget

    @MockK
    lateinit var reportCertificateCoFinancingPersistence: ProjectReportCertificateCoFinancingPersistenceProvider

    @MockK
    lateinit var paymentPersistence: PaymentRegularPersistence

    @MockK
    lateinit var reportCertificateCostCategoryPersistence: ProjectReportCertificateCostCategoryPersistence

    @MockK
    lateinit var getPartnerBudgetPerFundService: GetPartnerBudgetPerFundService

    @MockK
    lateinit var reportCertificateLumpSumPersistence: ProjectReportCertificateLumpSumPersistence

    @MockK
    lateinit var partnerBudgetCostsPersistence: ProjectPartnerBudgetCostsPersistence

    @MockK
    lateinit var projectBudgetPersistence: ProjectBudgetPersistence

    @MockK
    lateinit var reportCertificateUnitCostPersistence: ProjectReportCertificateUnitCostPersistence

    @MockK
    lateinit var reportInvestmentPersistence: ProjectReportCertificateInvestmentPersistence

    @InjectMockKs
    lateinit var service: CreateProjectReportBudget

    @Test
    fun createReportBudget() {
        val projectId = 30L
        val version = "v4.2"

        every { reportPersistence.getSubmittedProjectReportIds(projectId) } returns listOf(Pair(1L, ContractingDeadlineType.Finance))
        every { getProjectBudget.getBudget(projectId, version) } returns listOf(partnerBudget(ProjectPartnerSummary(
            id = 1L,
            abbreviation = "LP",
            active = true,
            role = ProjectPartnerRole.LEAD_PARTNER
        )))
        every { lumpSumPersistence.getLumpSums(projectId, version) } returns lumpSums(1L)
        every { paymentPersistence.getPaymentsByProjectId(projectId) } returns listOf(payment)
        every { reportCertificateCoFinancingPersistence.getCoFinancingCumulative(setOf(1L)) } returns cumulativeFund
        every { reportCertificateCostCategoryPersistence.getCostCategoriesCumulative(setOf(1L)) } returns previouslyReportedCostCategory
        every { getPartnerBudgetPerFundService.getProjectPartnerBudgetPerFund(projectId, version) } returns projectBudgetPerFund
        every { reportCertificateLumpSumPersistence.getLumpSumCumulative(setOf(1L)) } returns mapOf(Pair(1, BigDecimal.TEN))
        every { projectBudgetPersistence.getPartnersForProjectId(projectId, version) } returns partners()
        every { partnerBudgetCostsPersistence.getBudgetStaffCosts(setOf(1L), version) } returns emptyList()
        every { partnerBudgetCostsPersistence.getBudgetTravelAndAccommodationCosts(setOf(1L), version) } returns emptyList()
        every { partnerBudgetCostsPersistence.getBudgetExternalExpertiseAndServicesCosts(setOf(1L), version) } returns emptyList()
        every { partnerBudgetCostsPersistence.getBudgetEquipmentCosts(setOf(1L), version) } returns emptyList()
        every { partnerBudgetCostsPersistence.getBudgetInfrastructureAndWorksCosts(setOf(1L), version) } returns emptyList()
        every { partnerBudgetCostsPersistence.getBudgetUnitCosts(setOf(1L), version) } returns budgetUnitCostEntries()
        every { reportCertificateUnitCostPersistence.getUnitCostsCumulative(setOf(1L)) } returns mapOf(Pair(1L, BigDecimal.TEN))
        every { reportInvestmentPersistence.getInvestmentCumulative(setOf(1L)) } returns mapOf(Pair(1L, BigDecimal.TEN))

        val result = service.retrieveBudgetDataFor(
            projectId,
            version,
            listOf(
                PartnerReportInvestmentSummary(
                    investmentId = 1L,
                    investmentNumber = 1,
                    workPackageNumber = 1,
                    title = emptySet(),
                    deactivated = false
                )
            )
        )

        assertThat(result.coFinancing.previouslyReportedSum).isEqualTo(BigDecimal(38.33).setScale(2, RoundingMode.HALF_EVEN))
        assertThat(result.coFinancing.previouslyReportedPartner).isEqualTo(BigDecimal(34.33).setScale(2, RoundingMode.HALF_EVEN))
        assertThat(result.coFinancing.previouslyReportedPrivate).isEqualTo(BigDecimal(27.00).setScale(2, RoundingMode.HALF_EVEN))
        assertThat(result.coFinancing.previouslyReportedPublic).isEqualTo(BigDecimal(25.00).setScale(2, RoundingMode.HALF_EVEN))
        assertThat(result.coFinancing.previouslyReportedAutoPublic).isEqualTo(BigDecimal(26.00).setScale(2, RoundingMode.HALF_EVEN))
        assertThat(result.coFinancing.totalAutoPublic).isEqualTo(BigDecimal.ZERO)
        assertThat(result.coFinancing.totalPartner).isEqualTo(BigDecimal.ZERO)
        assertThat(result.coFinancing.totalPrivate).isEqualTo(BigDecimal.ZERO)
        assertThat(result.coFinancing.totalPublic).isEqualTo(BigDecimal.ZERO)
        assertThat(result.coFinancing.totalSum).isEqualTo(BigDecimal(19))
        assertThat(result.coFinancing.fundsSorted).containsExactlyInAnyOrder(
            PreviouslyProjectReportedFund(
                fundId = 1L,
                percentage = BigDecimal(0),
                total = BigDecimal(0),
                previouslyReported = BigDecimal(100).setScale(2, RoundingMode.HALF_EVEN),
                previouslyPaid = BigDecimal(0)
            ),
            PreviouslyProjectReportedFund(
                fundId = null,
                percentage = BigDecimal(100),
                total = BigDecimal(0),
                previouslyReported = BigDecimal(34.33).setScale(2, RoundingMode.HALF_EVEN),
                previouslyPaid = BigDecimal(0)
            )
        )
        assertThat(result.costCategorySetup.currentlyReported.staff).isEqualTo(BigDecimal.ZERO)
        assertThat(result.costCategorySetup.currentlyReported.office).isEqualTo(BigDecimal.ZERO)
        assertThat(result.costCategorySetup.currentlyReported.travel).isEqualTo(BigDecimal.ZERO)
        assertThat(result.costCategorySetup.currentlyReported.equipment).isEqualTo(BigDecimal.ZERO)
        assertThat(result.costCategorySetup.currentlyReported.external).isEqualTo(BigDecimal.ZERO)
        assertThat(result.costCategorySetup.currentlyReported.infrastructure).isEqualTo(BigDecimal.ZERO)
        assertThat(result.costCategorySetup.currentlyReported.other).isEqualTo(BigDecimal.ZERO)
        assertThat(result.costCategorySetup.currentlyReported.lumpSum).isEqualTo(BigDecimal.ZERO)
        assertThat(result.costCategorySetup.currentlyReported.unitCost).isEqualTo(BigDecimal.ZERO)
        assertThat(result.costCategorySetup.currentlyReported.sum).isEqualTo(BigDecimal.ZERO)
        assertThat(result.costCategorySetup.previouslyReported.staff).isEqualTo(BigDecimal.TEN)
        assertThat(result.costCategorySetup.previouslyReported.office).isEqualTo(BigDecimal.TEN)
        assertThat(result.costCategorySetup.previouslyReported.travel).isEqualTo(BigDecimal.TEN)
        assertThat(result.costCategorySetup.previouslyReported.equipment).isEqualTo(BigDecimal.TEN)
        assertThat(result.costCategorySetup.previouslyReported.external).isEqualTo(BigDecimal.TEN)
        assertThat(result.costCategorySetup.previouslyReported.infrastructure).isEqualTo(BigDecimal.TEN)
        assertThat(result.costCategorySetup.previouslyReported.other).isEqualTo(BigDecimal.TEN)
        assertThat(result.costCategorySetup.previouslyReported.lumpSum).isEqualTo(BigDecimal(20.33).setScale(2, RoundingMode.HALF_EVEN))
        assertThat(result.costCategorySetup.previouslyReported.unitCost).isEqualTo(BigDecimal.TEN)
        assertThat(result.costCategorySetup.previouslyReported.sum).isEqualTo(BigDecimal(100.33).setScale(2, RoundingMode.HALF_EVEN))
        assertThat(result.costCategorySetup.totalsFromAF.staff).isEqualTo(BigDecimal.TEN)
        assertThat(result.costCategorySetup.totalsFromAF.office).isEqualTo(BigDecimal(11))
        assertThat(result.costCategorySetup.totalsFromAF.travel).isEqualTo(BigDecimal(12))
        assertThat(result.costCategorySetup.totalsFromAF.equipment).isEqualTo(BigDecimal(14))
        assertThat(result.costCategorySetup.totalsFromAF.external).isEqualTo(BigDecimal(13))
        assertThat(result.costCategorySetup.totalsFromAF.infrastructure).isEqualTo(BigDecimal(15))
        assertThat(result.costCategorySetup.totalsFromAF.other).isEqualTo(BigDecimal(16))
        assertThat(result.costCategorySetup.totalsFromAF.lumpSum).isEqualTo(BigDecimal(17))
        assertThat(result.costCategorySetup.totalsFromAF.unitCost).isEqualTo(BigDecimal(18))
        assertThat(result.costCategorySetup.totalsFromAF.sum).isEqualTo(BigDecimal(19))
    }
}
