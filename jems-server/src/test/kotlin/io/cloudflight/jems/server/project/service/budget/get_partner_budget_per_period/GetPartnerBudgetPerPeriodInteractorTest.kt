package io.cloudflight.jems.server.project.service.budget.get_partner_budget_per_period

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.budget.ProjectBudgetPersistence
import io.cloudflight.jems.server.project.service.budget.model.BudgetCostsCalculationResult
import io.cloudflight.jems.server.project.service.budget.model.ProjectPartnerBudget
import io.cloudflight.jems.server.project.service.common.BudgetCostsCalculatorService
import io.cloudflight.jems.server.project.service.lumpsum.ProjectLumpSumPersistence
import io.cloudflight.jems.server.project.service.lumpsum.model.ProjectLumpSum
import io.cloudflight.jems.server.project.service.lumpsum.model.ProjectPartnerLumpSum
import io.cloudflight.jems.server.project.service.model.ProjectPartnerBudgetPerPeriod
import io.cloudflight.jems.server.project.service.model.ProjectPeriod
import io.cloudflight.jems.server.project.service.model.ProjectPeriodBudget
import io.cloudflight.jems.server.project.service.partner.budget.ProjectPartnerBudgetOptionsPersistence
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerBudgetOptions
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerRole
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerSummary
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerTotalBudget
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

    private fun lumpSumEntry(partnerId: Long, amount: BigDecimal) = ProjectLumpSum(
        programmeLumpSumId = 2L,
        period = 1,
        lumpSumContributions = listOf(ProjectPartnerLumpSum(partnerId, amount))
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
    lateinit var budgetCostsCalculatorService: BudgetCostsCalculatorService
    @MockK
    lateinit var projectPersistence: ProjectPersistence
    @MockK
    lateinit var lumpSumPersistence: ProjectLumpSumPersistence

    @InjectMockKs
    private lateinit var getPartnerBudgetPerPeriod: GetPartnerBudgetPerPeriod

    @Test
    fun `getPartnerBudgetPerPeriod - without periods`() {
        val projectId = 1L

        val partnerTotal = ProjectPartnerTotalBudget(
            partner1Id,
            null,
            null,
            null,
            null,
            null,
            BigDecimal.ZERO,
            BigDecimal.ZERO,
            BigDecimal.ZERO,
            BigDecimal.ZERO,
            BigDecimal.ZERO,
            BigDecimal.ZERO,
            BigDecimal.ZERO
        )

        every { persistence.getPartnersForProjectId(projectId) } returns listOf(partner1)
        every { optionPersistence.getBudgetOptions(setOf(partner1Id), projectId) } returns emptyList()
        every { lumpSumPersistence.getLumpSums(projectId) } returns emptyList()

        every { persistence.getBudgetPerPartner(setOf(partner1Id), projectId) } returns emptyList()
        // partner 1
        every { projectPersistence.getProjectPeriods(projectId) } returns emptyList()
        every { persistence.getBudgetTotalForPartners(setOf(partner1Id), projectId) } returns mapOf(Pair(partner1Id, partnerTotal))
        every { budgetCostsCalculatorService.calculateCosts(
            ProjectPartnerBudgetOptions(
                partnerId = partnerTotal.partnerId,
                officeAndAdministrationOnStaffCostsFlatRate = partnerTotal.officeAndAdministrationOnStaffCostsFlatRate,
                officeAndAdministrationOnDirectCostsFlatRate = partnerTotal.officeAndAdministrationOnDirectCostsFlatRate,
                otherCostsOnStaffCostsFlatRate = partnerTotal.otherCostsOnStaffCostsFlatRate,
                travelAndAccommodationOnStaffCostsFlatRate = partnerTotal.travelAndAccommodationOnStaffCostsFlatRate,
                staffCostsFlatRate = partnerTotal.staffCostsFlatRate
            ),
            partnerTotal.unitCostTotal,
            partnerTotal.lumpSumsTotal,
            partnerTotal.externalCostTotal,
            partnerTotal.equipmentCostTotal,
            partnerTotal.infrastructureCostTotal,
            partnerTotal.travelCostTotal,
            partnerTotal.staffCostTotal
        ) } returns BudgetCostsCalculationResult(totalCosts = BigDecimal.ZERO)

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

        val partnerTotal = ProjectPartnerTotalBudget(
            partner1Id,
            null,
            null,
            null,
            null,
            null,
            BigDecimal.ZERO,
            BigDecimal.ZERO,
            BigDecimal.ZERO,
            BigDecimal.ZERO,
            BigDecimal.ZERO,
            BigDecimal.ZERO,
            BigDecimal.ZERO
        )

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
        val budgetPeriod1 = ProjectPartnerBudget(
            id = partner1Id,
            periodNumber = 1,
            equipmentCostsPerPeriod = 50.toScaledBigDecimal(),
            externalExpertiseAndServicesCostsPerPeriod = 133.33.toScaledBigDecimal()
        )
        val budgetPeriod2 = ProjectPartnerBudget(
            id = partner1Id,
            periodNumber = 2,
            equipmentCostsPerPeriod = 50.toScaledBigDecimal(),
            externalExpertiseAndServicesCostsPerPeriod = 100.toScaledBigDecimal()
        )
        val budgetPeriod3 = ProjectPartnerBudget(
            id = partner1Id,
            periodNumber = 3,
            equipmentCostsPerPeriod = 100.toScaledBigDecimal(),
            externalExpertiseAndServicesCostsPerPeriod = 66.67.toScaledBigDecimal()
        )
        every { persistence.getBudgetPerPartner(setOf(partner1Id), projectId) } returns listOf(budgetPeriod1, budgetPeriod2, budgetPeriod3)
        every { projectPersistence.getProjectPeriods(projectId) } returns listOf(
            ProjectPeriod(number = 1, start = 1, end = 6),
            ProjectPeriod(number = 2, start = 7, end = 12),
            ProjectPeriod(number = 3, start = 13, end = 15)
        )
        every { persistence.getBudgetTotalForPartners(setOf(partner1Id), projectId) } returns mapOf(Pair(partner1Id, partnerTotal))
        every { budgetCostsCalculatorService.calculateCosts(
            ProjectPartnerBudgetOptions(
                partnerId = partnerTotal.partnerId,
                officeAndAdministrationOnStaffCostsFlatRate = partnerTotal.officeAndAdministrationOnStaffCostsFlatRate,
                officeAndAdministrationOnDirectCostsFlatRate = partnerTotal.officeAndAdministrationOnDirectCostsFlatRate,
                otherCostsOnStaffCostsFlatRate = partnerTotal.otherCostsOnStaffCostsFlatRate,
                travelAndAccommodationOnStaffCostsFlatRate = partnerTotal.travelAndAccommodationOnStaffCostsFlatRate,
                staffCostsFlatRate = partnerTotal.staffCostsFlatRate
            ),
            partnerTotal.unitCostTotal,
            partnerTotal.lumpSumsTotal,
            partnerTotal.externalCostTotal,
            partnerTotal.equipmentCostTotal,
            partnerTotal.infrastructureCostTotal,
            partnerTotal.travelCostTotal,
            partnerTotal.staffCostTotal
        ) } returns BudgetCostsCalculationResult(totalCosts = 562.5.toScaledBigDecimal())

        assertThat(getPartnerBudgetPerPeriod.getPartnerBudgetPerPeriod(projectId))
            .containsExactlyInAnyOrder(
                ProjectPartnerBudgetPerPeriod(
                    partner = partner1,
                    periodBudgets = mutableListOf(
                        ProjectPeriodBudget(0, 0, 0, BigDecimal.ZERO, false),
                        ProjectPeriodBudget(1, 1, 6, 206.23.toScaledBigDecimal(), false),
                        ProjectPeriodBudget(2, 7, 12, 168.75.toScaledBigDecimal(), false),
                        ProjectPeriodBudget(3, 13, 15, 187.52.toScaledBigDecimal(), false),
                        ProjectPeriodBudget(255, 0, 0, BigDecimal.ZERO, true)
                    ),
                    totalPartnerBudget = 562.5.toScaledBigDecimal()
                )
            )
    }

    @Test
    fun `getPartnerBudgetPerPeriod - historic version`() {
        val projectId = 1L
        val version = "1.0"

        val partnerTotal1 = ProjectPartnerTotalBudget(
            partner1Id,
            null,
            null,
            null,
            null,
            null,
            BigDecimal.ZERO,
            BigDecimal.ZERO,
            BigDecimal.ZERO,
            BigDecimal.ZERO,
            BigDecimal.ZERO,
            BigDecimal.ZERO,
            BigDecimal.ZERO
        )

        val partnerTotal2 = ProjectPartnerTotalBudget(
            partner2Id,
            null,
            null,
            null,
            null,
            null,
            BigDecimal.ZERO,
            BigDecimal.ZERO,
            BigDecimal.ZERO,
            BigDecimal.ZERO,
            BigDecimal.ZERO,
            BigDecimal.ZERO,
            BigDecimal.ZERO
        )

        every { persistence.getPartnersForProjectId(projectId, version) } returns listOf(partner1, partner2)
        every { optionPersistence.getBudgetOptions(setOf(partner1Id, partner2Id), projectId, version) } returns listOf(
            ProjectPartnerBudgetOptions(
                partnerId = partner1Id,
                officeAndAdministrationOnDirectCostsFlatRate = 10,
                travelAndAccommodationOnStaffCostsFlatRate = 10
            )
        )
        every { lumpSumPersistence.getLumpSums(projectId, version) } returns listOf(
            lumpSumEntry(partner2Id, 100.toBigDecimal())
        )
        // partner 1
        val p1budgetPeriod1 = ProjectPartnerBudget(
            id = partner1Id,
            periodNumber = 1,
            staffCostsPerPeriod = 50.toScaledBigDecimal(),
            externalExpertiseAndServicesCostsPerPeriod = 100.toScaledBigDecimal()
        )
        val p1budgetPeriod2 = ProjectPartnerBudget(
            id = partner1Id,
            periodNumber = 2,
            staffCostsPerPeriod = 50.toScaledBigDecimal(),
            infrastructureAndWorksCostsPerPeriod = 100.toScaledBigDecimal()
        )
        every { persistence.getBudgetTotalForPartners(setOf(partner1Id, partner2Id), projectId, version) } returns mapOf(Pair(partner1Id, partnerTotal1), Pair(partner2Id, partnerTotal2))
        every { budgetCostsCalculatorService.calculateCosts(
            ProjectPartnerBudgetOptions(
                partnerId = partnerTotal1.partnerId,
                officeAndAdministrationOnStaffCostsFlatRate = partnerTotal1.officeAndAdministrationOnStaffCostsFlatRate,
                officeAndAdministrationOnDirectCostsFlatRate = partnerTotal1.officeAndAdministrationOnDirectCostsFlatRate,
                otherCostsOnStaffCostsFlatRate = partnerTotal1.otherCostsOnStaffCostsFlatRate,
                travelAndAccommodationOnStaffCostsFlatRate = partnerTotal1.travelAndAccommodationOnStaffCostsFlatRate,
                staffCostsFlatRate = partnerTotal1.staffCostsFlatRate
            ),
            partnerTotal1.unitCostTotal,
            partnerTotal1.lumpSumsTotal,
            partnerTotal1.externalCostTotal,
            partnerTotal1.equipmentCostTotal,
            partnerTotal1.infrastructureCostTotal,
            partnerTotal1.travelCostTotal,
            partnerTotal1.staffCostTotal
        ) } returns BudgetCostsCalculationResult(totalCosts = 300.toScaledBigDecimal())
        // partner 2
        val p2budgetPeriod1 = ProjectPartnerBudget(
            id = partner2Id,
            periodNumber = 1,
            staffCostsPerPeriod = 100.toScaledBigDecimal(),
            externalExpertiseAndServicesCostsPerPeriod = 50.toScaledBigDecimal()
        )

        every { budgetCostsCalculatorService.calculateCosts(
            ProjectPartnerBudgetOptions(
                partnerId = partnerTotal2.partnerId,
                officeAndAdministrationOnStaffCostsFlatRate = partnerTotal2.officeAndAdministrationOnStaffCostsFlatRate,
                officeAndAdministrationOnDirectCostsFlatRate = partnerTotal2.officeAndAdministrationOnDirectCostsFlatRate,
                otherCostsOnStaffCostsFlatRate = partnerTotal2.otherCostsOnStaffCostsFlatRate,
                travelAndAccommodationOnStaffCostsFlatRate = partnerTotal2.travelAndAccommodationOnStaffCostsFlatRate,
                staffCostsFlatRate = partnerTotal2.staffCostsFlatRate
            ),
            partnerTotal2.unitCostTotal,
            partnerTotal2.lumpSumsTotal,
            partnerTotal2.externalCostTotal,
            partnerTotal2.equipmentCostTotal,
            partnerTotal2.infrastructureCostTotal,
            partnerTotal2.travelCostTotal,
            partnerTotal2.staffCostTotal
        ) } returns BudgetCostsCalculationResult(totalCosts = 250.toScaledBigDecimal())

        every { persistence.getBudgetPerPartner(setOf(partner1Id, partner2Id), projectId, version) } returns
            listOf(p1budgetPeriod1, p1budgetPeriod2, p2budgetPeriod1)
        every { projectPersistence.getProjectPeriods(projectId, version) } returns projectPeriods()

        assertThat(getPartnerBudgetPerPeriod.getPartnerBudgetPerPeriod(projectId, version))
            .containsExactlyInAnyOrder(
                ProjectPartnerBudgetPerPeriod(
                    partner = partner1,
                    periodBudgets = getProjectPeriods(170.00.toScaledBigDecimal(), 130.00.toScaledBigDecimal()),
                    totalPartnerBudget = 300.toScaledBigDecimal()
                ),
                ProjectPartnerBudgetPerPeriod(
                    partner = partner2,
                    periodBudgets = getProjectPeriods(250.toScaledBigDecimal(), 0.toScaledBigDecimal()),
                    totalPartnerBudget = 250.toScaledBigDecimal()
                )
            )
    }

    @Test
    fun `getPartnerBudgetPerPeriod - including lump sums`() {
        val projectId = 1L

        val partnerTotal1 = ProjectPartnerTotalBudget(
            partner1Id,
            null,
            null,
            null,
            null,
            null,
            BigDecimal.ZERO,
            BigDecimal.ZERO,
            BigDecimal.ZERO,
            BigDecimal.ZERO,
            BigDecimal.ZERO,
            BigDecimal.ZERO,
            BigDecimal.ZERO
        )

        val partnerTotal2 = ProjectPartnerTotalBudget(
            partner2Id,
            null,
            null,
            null,
            null,
            null,
            BigDecimal.ZERO,
            BigDecimal.ZERO,
            BigDecimal.ZERO,
            BigDecimal.ZERO,
            BigDecimal.ZERO,
            BigDecimal.ZERO,
            BigDecimal.ZERO
        )

        every { persistence.getPartnersForProjectId(projectId) } returns listOf(partner1, partner2)
        every { optionPersistence.getBudgetOptions(setOf(partner1Id, partner2Id), projectId) } returns listOf(
            ProjectPartnerBudgetOptions(
                partnerId = partner1Id,
                officeAndAdministrationOnStaffCostsFlatRate = 10,
                otherCostsOnStaffCostsFlatRate = 5
            ),
            ProjectPartnerBudgetOptions(
                partnerId = partner2Id,
                staffCostsFlatRate = 10,
                travelAndAccommodationOnStaffCostsFlatRate = 15
            )
        )
        every { lumpSumPersistence.getLumpSums(projectId) } returns listOf(
            lumpSumEntry(partner1Id, BigDecimal.TEN)
        )

        // partner 1
        val p1budgetPeriod1 = ProjectPartnerBudget(
            id = partner1Id,
            periodNumber = 1,
            staffCostsPerPeriod = 50.toScaledBigDecimal(),
            travelAndAccommodationCostsPerPeriod = 11.toScaledBigDecimal(),
            externalExpertiseAndServicesCostsPerPeriod = 20.toScaledBigDecimal(),
            unitCostsPerPeriod = 25.toScaledBigDecimal()
        )
        val p1budgetPeriod2 = ProjectPartnerBudget(
            id = partner1Id,
            periodNumber = 2,
            staffCostsPerPeriod = 10.toScaledBigDecimal(),
            infrastructureAndWorksCostsPerPeriod = 33.3.toScaledBigDecimal(),
            unitCostsPerPeriod = 25.toScaledBigDecimal()
        )

        every { persistence.getBudgetTotalForPartners(setOf(partner1Id, partner2Id), projectId) } returns mapOf(Pair(partner1Id, partnerTotal1), Pair(partner2Id, partnerTotal2))
        every { budgetCostsCalculatorService.calculateCosts(
            ProjectPartnerBudgetOptions(
                partnerId = partner1Id,
                officeAndAdministrationOnStaffCostsFlatRate = partnerTotal1.officeAndAdministrationOnStaffCostsFlatRate,
                officeAndAdministrationOnDirectCostsFlatRate = partnerTotal1.officeAndAdministrationOnDirectCostsFlatRate,
                otherCostsOnStaffCostsFlatRate = partnerTotal1.otherCostsOnStaffCostsFlatRate,
                travelAndAccommodationOnStaffCostsFlatRate = partnerTotal1.travelAndAccommodationOnStaffCostsFlatRate,
                staffCostsFlatRate = partnerTotal1.staffCostsFlatRate
            ),
            partnerTotal1.unitCostTotal,
            partnerTotal1.lumpSumsTotal,
            partnerTotal1.externalCostTotal,
            partnerTotal1.equipmentCostTotal,
            partnerTotal1.infrastructureCostTotal,
            partnerTotal1.travelCostTotal,
            partnerTotal1.staffCostTotal
        ) } returns BudgetCostsCalculationResult(totalCosts = 174.3.toScaledBigDecimal())
        // partner 2
        val p2budgetPeriod1 = ProjectPartnerBudget(
            id = partner2Id,
            periodNumber = 1,
            externalExpertiseAndServicesCostsPerPeriod = 10.toScaledBigDecimal(),
            infrastructureAndWorksCostsPerPeriod = 2.toScaledBigDecimal()
        )
        every { budgetCostsCalculatorService.calculateCosts(
            ProjectPartnerBudgetOptions(
                partnerId = partner2Id,
                officeAndAdministrationOnStaffCostsFlatRate = partnerTotal2.officeAndAdministrationOnStaffCostsFlatRate,
                officeAndAdministrationOnDirectCostsFlatRate = partnerTotal2.officeAndAdministrationOnDirectCostsFlatRate,
                otherCostsOnStaffCostsFlatRate = partnerTotal2.otherCostsOnStaffCostsFlatRate,
                travelAndAccommodationOnStaffCostsFlatRate = partnerTotal2.travelAndAccommodationOnStaffCostsFlatRate,
                staffCostsFlatRate = partnerTotal2.staffCostsFlatRate
            ),
            partnerTotal2.unitCostTotal,
            partnerTotal2.lumpSumsTotal,
            partnerTotal2.externalCostTotal,
            partnerTotal2.equipmentCostTotal,
            partnerTotal2.infrastructureCostTotal,
            partnerTotal2.travelCostTotal,
            partnerTotal2.staffCostTotal
        ) } returns BudgetCostsCalculationResult(totalCosts = 13.38.toScaledBigDecimal())

        every { persistence.getBudgetPerPartner(setOf(partner1Id, partner2Id), projectId) } returns
            listOf(p1budgetPeriod1, p1budgetPeriod2, p2budgetPeriod1)
        every { projectPersistence.getProjectPeriods(projectId) } returns projectPeriods()

        assertThat(getPartnerBudgetPerPeriod.getPartnerBudgetPerPeriod(projectId))
            .containsExactlyInAnyOrder(
                ProjectPartnerBudgetPerPeriod(
                    partner = partner1,
                    periodBudgets = getProjectPeriods(123.50.toScaledBigDecimal(), 50.80.toScaledBigDecimal()),
                    totalPartnerBudget = 174.3.toScaledBigDecimal()
                ),
                ProjectPartnerBudgetPerPeriod(
                    partner = partner2,
                    periodBudgets = getProjectPeriods(13.38.toScaledBigDecimal(), 0.toScaledBigDecimal()),
                    totalPartnerBudget = 13.38.toScaledBigDecimal()
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
