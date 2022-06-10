package io.cloudflight.jems.server.project.service.report.partner.createProjectPartnerReport

import io.cloudflight.jems.api.project.dto.partner.cofinancing.ProjectPartnerContributionStatusDTO
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.service.budget.get_partner_budget_per_period.GetPartnerBudgetPerPeriodInteractor
import io.cloudflight.jems.server.project.service.lumpsum.ProjectLumpSumPersistence
import io.cloudflight.jems.server.project.service.lumpsum.model.ProjectLumpSum
import io.cloudflight.jems.server.project.service.lumpsum.model.ProjectPartnerLumpSum
import io.cloudflight.jems.server.project.service.model.*
import io.cloudflight.jems.server.project.service.partner.budget.ProjectPartnerBudgetCostsPersistence
import io.cloudflight.jems.server.project.service.partner.budget.ProjectPartnerBudgetOptionsPersistence
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerContribution
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerContributionStatus
import io.cloudflight.jems.server.project.service.partner.model.*
import io.cloudflight.jems.server.project.service.report.ProjectReportPersistence
import io.cloudflight.jems.server.project.service.report.model.ProjectPartnerReportSummary
import io.cloudflight.jems.server.project.service.report.model.ReportStatus
import io.cloudflight.jems.server.project.service.report.model.contribution.create.CreateProjectPartnerReportContribution
import io.cloudflight.jems.server.project.service.report.model.contribution.withoutCalculations.ProjectPartnerReportEntityContribution
import io.cloudflight.jems.server.project.service.report.model.create.*
import io.cloudflight.jems.server.project.service.report.model.file.ProjectReportFileMetadata
import io.cloudflight.jems.server.project.service.report.partner.contribution.ProjectReportContributionPersistence
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.ZonedDateTime
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

    private val contributions = listOf(contribNonPartner2, contribPartner, contribNonPartner1)

    private val reportsForContribution = listOf(
        ProjectPartnerReportSummary(
            id = 408L,
            reportNumber = 4,
            status = ReportStatus.Submitted,
            version = "10.1",
            firstSubmission = ZonedDateTime.now().minusDays(10),
            createdAt = ZonedDateTime.now().minusDays(20),
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
            attachment = ProjectReportFileMetadata(780L, "this_is_ignored", mockk()),
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
            programmeLumpSumId = 44L,
            period = 3,
            lumpSumContributions = listOf(
                ProjectPartnerLumpSum(
                    partnerId = partnerId,
                    amount = BigDecimal.TEN,
                ),
            ),
        )
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

    private fun generalCost(unitCostId: Long): BudgetGeneralCostEntry {
        val cost = mockk<BudgetGeneralCostEntry>()
        every { cost.unitCostId } returns unitCostId
        every { cost.rowSum } returns BigDecimal.ZERO
        every { cost.numberOfUnits } returns BigDecimal.ZERO
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
        generalCost(7L),
    )

    private val equipmentCosts = listOf(
        generalCost(7L),
        generalCost(8L),
    )

    private val infrastructureCosts = listOf(
        generalCost(9L),
    )

    private val unitCosts = listOf(
        unitCost(10L),
    )

    private fun perPeriodBudget(number: Int, value: BigDecimal) = ProjectPeriodBudget(
        periodNumber = number,
        periodStart = 0,
        periodEnd = 0,
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

    @MockK
    lateinit var reportPersistence: ProjectReportPersistence
    @MockK
    lateinit var reportContributionPersistence: ProjectReportContributionPersistence
    @MockK
    lateinit var lumpSumPersistence: ProjectLumpSumPersistence
    @MockK
    lateinit var partnerBudgetCostsPersistence: ProjectPartnerBudgetCostsPersistence
    @MockK
    lateinit var getPartnerBudgetPerPeriod: GetPartnerBudgetPerPeriodInteractor
    @MockK
    lateinit var projectPartnerBudgetOptionsPersistence: ProjectPartnerBudgetOptionsPersistence

    @InjectMockKs
    lateinit var service: CreateProjectPartnerReportBudget

    @Test
    fun createReportBudget() {
        val partnerId = 76L
        val projectId = 30L
        val version = "v4.2"
        // contribution
        every { reportPersistence.listSubmittedPartnerReports(partnerId) } returns reportsForContribution
        every { reportContributionPersistence.getAllContributionsForReportIds(setOf(408L)) } returns previousContributions
        // lump sums
        every { lumpSumPersistence.getLumpSums(projectId, version) } returns lumpSums(partnerId)
        // unit costs
        every { partnerBudgetCostsPersistence.getBudgetStaffCosts(partnerId, version) } returns staffCosts
        every { partnerBudgetCostsPersistence.getBudgetTravelAndAccommodationCosts(partnerId, version) } returns travelCosts
        every { partnerBudgetCostsPersistence.getBudgetExternalExpertiseAndServicesCosts(partnerId, version) } returns externalCosts
        every { partnerBudgetCostsPersistence.getBudgetEquipmentCosts(partnerId, version) } returns equipmentCosts
        every { partnerBudgetCostsPersistence.getBudgetInfrastructureAndWorksCosts(partnerId, version) } returns infrastructureCosts
        every { partnerBudgetCostsPersistence.getBudgetUnitCosts(partnerId, version) } returns unitCosts
        // budget per period
        every { getPartnerBudgetPerPeriod.getPartnerBudgetPerPeriod(projectId, version) } returns
            ProjectBudgetOverviewPerPartnerPerPeriod(partnersBudgetPerPeriod = perPeriod(partnerId), totals = emptyList(), totalsPercentage = emptyList())
        // options
        val budgetOptions = mockk<ProjectPartnerBudgetOptions>()
        every { projectPartnerBudgetOptionsPersistence.getBudgetOptions(partnerId, version) } returns budgetOptions

        val result = service.retrieveBudgetDataFor(projectId, partnerId, version, contributions)

        assertThat(result.contributions).hasSize(3)
        assertThat(result.lumpSums).containsExactly(PartnerReportLumpSum(lumpSumId = 44L, period = 3, value = BigDecimal.TEN))
        assertThat(result.unitCosts.map {it.unitCostId}).containsExactly(4, 5, 6, 7, 8, 9, 10)
        assertThat(result.budgetPerPeriod).containsExactly(Pair(1, BigDecimal.ONE), Pair(2, BigDecimal.TEN))
        assertThat(result.budgetOptions).isEqualTo(budgetOptions)

        // this we cannot mock
        val newUuid = result.contributions[0].historyIdentifier
        assertThat(result.contributions[0]).isEqualTo(expectedContribution1.copy(historyIdentifier = newUuid))
        assertThat(result.contributions[1]).isEqualTo(expectedContribution2)
        assertThat(result.contributions[2]).isEqualTo(expectedContribution3)
    }

}
