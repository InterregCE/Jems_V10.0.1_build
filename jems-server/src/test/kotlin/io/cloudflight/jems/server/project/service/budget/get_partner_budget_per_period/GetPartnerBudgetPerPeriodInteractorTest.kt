package io.cloudflight.jems.server.project.service.budget.get_partner_budget_per_period

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.budget.ProjectBudgetPersistence
import io.cloudflight.jems.server.project.service.lumpsum.ProjectLumpSumPersistence
import io.cloudflight.jems.server.project.service.lumpsum.model.ProjectLumpSum
import io.cloudflight.jems.server.project.service.lumpsum.model.ProjectPartnerLumpSum
import io.cloudflight.jems.server.project.service.model.ProjectPartnerBudgetPerPeriod
import io.cloudflight.jems.server.project.service.model.ProjectPeriod
import io.cloudflight.jems.server.project.service.model.ProjectPeriodBudget
import io.cloudflight.jems.server.project.service.partner.budget.ProjectPartnerBudgetCostsPersistence
import io.cloudflight.jems.server.project.service.partner.budget.ProjectPartnerBudgetOptionsPersistence
import io.cloudflight.jems.server.project.service.partner.budget.get_budget_total_cost.GetBudgetTotalCost
import io.cloudflight.jems.server.project.service.partner.model.BudgetGeneralCostEntry
import io.cloudflight.jems.server.project.service.partner.model.BudgetPeriod
import io.cloudflight.jems.server.project.service.partner.model.BudgetStaffCostEntry
import io.cloudflight.jems.server.project.service.partner.model.BudgetTravelAndAccommodationCostEntry
import io.cloudflight.jems.server.project.service.partner.model.BudgetUnitCostEntry
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerBudgetOptions
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerRole
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerSummary
import io.cloudflight.jems.server.toScaledBigDecimal
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class GetPartnerBudgetPerPeriodInteractorTest : UnitTest() {

    private val partner1Id = 1L
    private val partner2Id = 2L
    private val partner1 = ProjectPartnerSummary(
        id = partner1Id,
        abbreviation = "PP 2",
        role = ProjectPartnerRole.PARTNER,
        sortNumber = 2
    )
    private val partner2 = ProjectPartnerSummary(
        id = partner2Id,
        abbreviation = "LP 1",
        role = ProjectPartnerRole.LEAD_PARTNER,
        sortNumber = 1
    )

    private val partner1Options = ProjectPartnerBudgetOptions(
        partnerId = partner1.id!!,
        officeAndAdministrationOnStaffCostsFlatRate = 10,
        officeAndAdministrationOnDirectCostsFlatRate = 5,
        staffCostsFlatRate = 10,
        travelAndAccommodationOnStaffCostsFlatRate = 15,
        otherCostsOnStaffCostsFlatRate = 5
    )

    private fun lumpSumEntry(partnerId: Long, amount: BigDecimal) = ProjectLumpSum(
        programmeLumpSumId = 2L,
        period = 1,
        lumpSumContributions = listOf(ProjectPartnerLumpSum(partnerId, amount))
    )

    private fun budgetStaffCostEntry(partnerId: Long, pricePerUnit: BigDecimal) = BudgetStaffCostEntry(
        id = partnerId,
        numberOfUnits = BigDecimal.ONE,
        rowSum = pricePerUnit,
        budgetPeriods = mutableSetOf(BudgetPeriod(1, BigDecimal.ONE)),
        unitCostId = null,
        pricePerUnit = pricePerUnit,
        description = emptySet(),
        comment = emptySet(),
        unitType = emptySet()
    )
    private fun budgetTravelEntry(partnerId: Long, pricePerUnit: BigDecimal) = BudgetTravelAndAccommodationCostEntry(
        id = partnerId,
        numberOfUnits = BigDecimal.ONE,
        rowSum = pricePerUnit,
        budgetPeriods = mutableSetOf(BudgetPeriod(1, BigDecimal.ONE)),
        unitCostId = null,
        pricePerUnit = pricePerUnit,
        description = emptySet(),
        unitType = emptySet()
    )
    private fun budgetGeneralEntry(partnerId: Long, pricePerUnit: BigDecimal) = BudgetGeneralCostEntry(
        id = partnerId,
        numberOfUnits = BigDecimal.ONE,
        rowSum = pricePerUnit,
        budgetPeriods = mutableSetOf(BudgetPeriod(1, BigDecimal.ONE)),
        unitCostId = null,
        pricePerUnit = pricePerUnit,
        investmentId = null,
        description = emptySet(),
        unitType = emptySet()
    )
    private fun budgetUnitCostEntry(partnerId: Long, rowSum: BigDecimal) = BudgetUnitCostEntry(
        id = partnerId,
        numberOfUnits = BigDecimal.ONE,
        budgetPeriods = mutableSetOf(BudgetPeriod(1, BigDecimal.ONE)),
        rowSum = rowSum,
        unitCostId = 0
    )
    private fun projectPeriods() = listOf(
        ProjectPeriod(number = 1, start = 1, end = 2),
        ProjectPeriod(number = 2, start = 2, end = 2)
    )

    @MockK
    lateinit var persistence: ProjectBudgetPersistence
    @MockK
    lateinit var optionPersistence: ProjectPartnerBudgetOptionsPersistence
    @MockK
    lateinit var budgetCostsPersistence: ProjectPartnerBudgetCostsPersistence
    @MockK
    lateinit var getBudgetTotalCost: GetBudgetTotalCost
    @MockK
    lateinit var projectPersistence: ProjectPersistence
    @MockK
    lateinit var lumpSumPersistence: ProjectLumpSumPersistence

    @InjectMockKs
    private lateinit var getPartnerBudgetPerPeriod: GetPartnerBudgetPerPeriod

    @Test
    fun `getPartnerBudgetPerPeriod - without periods`() {
        val projectId = 1L
        every { persistence.getPartnersForProjectId(projectId) } returns listOf(partner1)
        every { optionPersistence.getBudgetOptions(setOf(partner1Id), projectId) } returns emptyList()
        every { lumpSumPersistence.getLumpSums(projectId) } returns emptyList()

        // partner 1
        every { budgetCostsPersistence.getBudgetStaffCosts(partner1Id) } returns emptyList()
        every { budgetCostsPersistence.getBudgetTravelAndAccommodationCosts(partner1Id) } returns emptyList()
        every { budgetCostsPersistence.getBudgetExternalExpertiseAndServicesCosts(partner1Id) } returns emptyList()
        every { budgetCostsPersistence.getBudgetEquipmentCosts(partner1Id) } returns emptyList()
        every { budgetCostsPersistence.getBudgetInfrastructureAndWorksCosts(partner1Id) } returns emptyList()
        every { budgetCostsPersistence.getBudgetUnitCosts(partner1Id) } returns emptyList()
        every { projectPersistence.getProjectPeriods(projectId) } returns emptyList()
        every { getBudgetTotalCost.getBudgetTotalCost(partner1Id) } returns BigDecimal.ZERO

        assertThat(getPartnerBudgetPerPeriod.getPartnerBudgetPerPeriod(projectId))
            .containsExactlyInAnyOrder(
                ProjectPartnerBudgetPerPeriod(
                    partner = partner1,
                    periodBudgets = mutableListOf(
                        ProjectPeriodBudget(0, 0, 0, BigDecimal.ZERO, false),
                        ProjectPeriodBudget(255, 0, 0, BigDecimal.ZERO, true)
                    ),
                    totalPartnerBudget = BigDecimal.ZERO
                )
            )
    }

    @Test
    fun `getPartnerBudgetPerPeriod - simple with flat rates`() {
        val projectId = 1L
        every { persistence.getPartnersForProjectId(projectId) } returns listOf(partner1)
        every { optionPersistence.getBudgetOptions(setOf(partner1Id), projectId) } returns listOf(
            ProjectPartnerBudgetOptions(
                partnerId = partner1.id!!,
                staffCostsFlatRate = 10,
                officeAndAdministrationOnStaffCostsFlatRate = 15,
                travelAndAccommodationOnStaffCostsFlatRate = 10
            )
        )
        every { lumpSumPersistence.getLumpSums(projectId) } returns emptyList()

        // partner 1
        every { budgetCostsPersistence.getBudgetStaffCosts(partner1Id) } returns emptyList()
        every { budgetCostsPersistence.getBudgetTravelAndAccommodationCosts(partner1Id) } returns emptyList()
        val external = BudgetGeneralCostEntry(
            id = partner1Id,
            numberOfUnits = BigDecimal.ONE,
            rowSum = 300.toBigDecimal(),
            budgetPeriods = mutableSetOf(
                BudgetPeriod(1, 133.33.toScaledBigDecimal()),
                BudgetPeriod(2, 100.toScaledBigDecimal()),
                BudgetPeriod(3, 66.67.toScaledBigDecimal())
            ),
            unitCostId = null,
            pricePerUnit = BigDecimal.ZERO,
            investmentId = null,
            description = emptySet(),
            unitType = emptySet()
        )
        every { budgetCostsPersistence.getBudgetExternalExpertiseAndServicesCosts(partner1Id) } returns listOf(external)
        val equipment = BudgetGeneralCostEntry(
            id = partner1Id,
            numberOfUnits = BigDecimal.ONE,
            rowSum = 200.toBigDecimal(),
            budgetPeriods = mutableSetOf(
                BudgetPeriod(1, 50.toScaledBigDecimal()),
                BudgetPeriod(2, 50.toScaledBigDecimal()),
                BudgetPeriod(3, 100.toScaledBigDecimal())
            ),
            unitCostId = null,
            pricePerUnit = BigDecimal.ZERO,
            investmentId = null,
            description = emptySet(),
            unitType = emptySet()
        )
        every { budgetCostsPersistence.getBudgetEquipmentCosts(partner1Id) } returns listOf(equipment)
        every { budgetCostsPersistence.getBudgetInfrastructureAndWorksCosts(partner1Id) } returns emptyList()
        every { budgetCostsPersistence.getBudgetUnitCosts(partner1Id) } returns emptyList()
        every { projectPersistence.getProjectPeriods(projectId) } returns listOf(
            ProjectPeriod(number = 1, start = 1, end = 1),
            ProjectPeriod(number = 2, start = 2, end = 2),
            ProjectPeriod(number = 3, start = 3, end = 3)
        )
        every { getBudgetTotalCost.getBudgetTotalCost(partner1Id) } returns 562.5.toScaledBigDecimal()

        assertThat(getPartnerBudgetPerPeriod.getPartnerBudgetPerPeriod(projectId))
            .containsExactlyInAnyOrder(
                ProjectPartnerBudgetPerPeriod(
                    partner = partner1,
                    periodBudgets = mutableListOf(
                        ProjectPeriodBudget(0, 0, 0, BigDecimal.ZERO, false),
                        ProjectPeriodBudget(1, 1, 1, 206.23.toScaledBigDecimal(), false),
                        ProjectPeriodBudget(2, 2, 2, 168.75.toScaledBigDecimal(), false),
                        ProjectPeriodBudget(3, 3, 3, 187.52.toScaledBigDecimal(), false),
                        ProjectPeriodBudget(255, 0, 0, BigDecimal.ZERO, true)
                    ),
                    totalPartnerBudget = 562.5.toScaledBigDecimal()
                )
            )
    }

    @Test
    fun `getPartnerBudgetPerPeriod - including lump sums`() {
        val projectId = 1L
        every { persistence.getPartnersForProjectId(projectId) } returns listOf(partner1, partner2)
        every { optionPersistence.getBudgetOptions(setOf(partner1Id, partner2Id), projectId) } returns listOf(
            partner1Options
        )
        every { lumpSumPersistence.getLumpSums(projectId) } returns listOf(
            lumpSumEntry(partner1Id, BigDecimal.TEN)
        )

        // partner 1
        every { budgetCostsPersistence.getBudgetStaffCosts(partner1Id) } returns listOf(budgetStaffCostEntry(partner1Id, 50.toBigDecimal()))
        every { budgetCostsPersistence.getBudgetTravelAndAccommodationCosts(partner1Id) } returns listOf(budgetTravelEntry(partner1Id, 11.toBigDecimal()))
        every { budgetCostsPersistence.getBudgetExternalExpertiseAndServicesCosts(partner1Id) } returns listOf(budgetGeneralEntry(partner1Id, 20.toBigDecimal()))
        every { budgetCostsPersistence.getBudgetEquipmentCosts(partner1Id) } returns emptyList()
        every { budgetCostsPersistence.getBudgetInfrastructureAndWorksCosts(partner1Id) } returns emptyList()
        every { budgetCostsPersistence.getBudgetUnitCosts(partner1Id) } returns listOf(budgetUnitCostEntry(partner1Id, 25.toBigDecimal()))
        every { projectPersistence.getProjectPeriods(projectId) } returns projectPeriods()
        every { getBudgetTotalCost.getBudgetTotalCost(partner1Id) } returns 100.toBigDecimal()

        // partner 2
        every { budgetCostsPersistence.getBudgetStaffCosts(partner2Id) } returns listOf(budgetStaffCostEntry(partner2Id, 10.toBigDecimal()))
        every { budgetCostsPersistence.getBudgetTravelAndAccommodationCosts(partner2Id) } returns emptyList()
        every { budgetCostsPersistence.getBudgetExternalExpertiseAndServicesCosts(partner2Id) } returns listOf(budgetGeneralEntry(partner2Id, 2.toBigDecimal()))
        every { budgetCostsPersistence.getBudgetEquipmentCosts(partner2Id) } returns emptyList()
        every { budgetCostsPersistence.getBudgetInfrastructureAndWorksCosts(partner2Id) } returns emptyList()
        every { budgetCostsPersistence.getBudgetUnitCosts(partner2Id) } returns emptyList()
        every { getBudgetTotalCost.getBudgetTotalCost(partner2Id) } returns 80.toBigDecimal()

        assertThat(getPartnerBudgetPerPeriod.getPartnerBudgetPerPeriod(projectId))
            .containsExactlyInAnyOrder(
                ProjectPartnerBudgetPerPeriod(
                    partner = partner1,
                    periodBudgets = getProjectPeriods(14.70.toScaledBigDecimal(), 85.30.toScaledBigDecimal()),
                    totalPartnerBudget = 100.toBigDecimal()
                ),
                ProjectPartnerBudgetPerPeriod(
                    partner = partner2,
                    periodBudgets = getProjectPeriods(2.toBigDecimal(), 78.00.toScaledBigDecimal()),
                    totalPartnerBudget = 80.toBigDecimal()
                )
            )
    }

    private fun getProjectPeriods(total1: BigDecimal, total2: BigDecimal): MutableList<ProjectPeriodBudget> = mutableListOf(
        ProjectPeriodBudget(
            periodNumber = 0,
            periodStart = 0,
            periodEnd = 0,
            totalBudgetPerPeriod = BigDecimal.ZERO,
            isLastPeriod = false
        ),
        ProjectPeriodBudget(
            periodNumber = 1,
            periodStart = 1,
            periodEnd = 2,
            totalBudgetPerPeriod = total1,
            isLastPeriod = false
        ),
        ProjectPeriodBudget(
            periodNumber = 2,
            periodStart = 2,
            periodEnd = 2,
            totalBudgetPerPeriod = total2,
            isLastPeriod = false
        ),
        ProjectPeriodBudget(
            periodNumber = 255,
            periodStart = 0,
            periodEnd = 0,
            totalBudgetPerPeriod = BigDecimal.ZERO,
            isLastPeriod = true
        )
    )

}
