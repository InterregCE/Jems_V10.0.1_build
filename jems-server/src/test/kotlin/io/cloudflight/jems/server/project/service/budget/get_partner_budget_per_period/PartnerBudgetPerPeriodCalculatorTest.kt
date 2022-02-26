package io.cloudflight.jems.server.project.service.budget.get_partner_budget_per_period

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.service.budget.model.BudgetCostsCalculationResult
import io.cloudflight.jems.server.project.service.budget.model.ProjectPartnerBudget
import io.cloudflight.jems.server.project.service.common.BudgetCostsCalculator
import io.cloudflight.jems.server.project.service.lumpsum.model.ProjectLumpSum
import io.cloudflight.jems.server.project.service.lumpsum.model.ProjectPartnerLumpSum
import io.cloudflight.jems.server.project.service.model.ProjectBudgetOverviewPerPartnerPerPeriod
import io.cloudflight.jems.server.project.service.model.ProjectPartnerBudgetPerPeriod
import io.cloudflight.jems.server.project.service.model.ProjectPeriod
import io.cloudflight.jems.server.project.service.model.ProjectPeriodBudget
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerBudgetOptions
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerRole
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerSummary
import io.cloudflight.jems.server.project.service.partner.model.PartnerTotalBudgetPerCostCategory
import io.cloudflight.jems.server.toScaledBigDecimal
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.math.BigDecimal

internal class PartnerBudgetPerPeriodCalculatorTest : UnitTest() {

    private val partner1Id = 1L
    private val partner2Id = 2L
    private val partner1 = ProjectPartnerSummary(
        id = partner1Id,
        active = true,
        abbreviation = "PP 2",
        role = ProjectPartnerRole.PARTNER,
        sortNumber = 2
    )
    private val partner2 = ProjectPartnerSummary(
        id = partner2Id,
        active = true,
        abbreviation = "LP 1",
        role = ProjectPartnerRole.LEAD_PARTNER,
        sortNumber = 1
    )

    private fun lumpSumEntry(partnerId: Long, amount: BigDecimal) = ProjectLumpSum(
        programmeLumpSumId = 2L,
        period = 1,
        lumpSumContributions = listOf(ProjectPartnerLumpSum(partnerId, amount))
    )

    val partnerTotal1 = PartnerTotalBudgetPerCostCategory(
        partner1Id, null, null, null, null, null,
        BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
        BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO
    )

    val partnerTotal2 = PartnerTotalBudgetPerCostCategory(
        partner2Id, null, null, null, null, null,
        BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
        BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO
    )

    @MockK
    private lateinit var budgetCostCalculator: BudgetCostsCalculator

    @InjectMockKs
    private lateinit var calculatePartnerBudgetPerPeriod: PartnerBudgetPerPeriodCalculator

    @Test
    fun `calculate project budget overview per partner per period when there is no period`() {
        every {
            budgetCostCalculator.calculateCosts(
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
            )
        } returns BudgetCostsCalculationResult(totalCosts = BigDecimal.ZERO)

        assertThat(
            calculatePartnerBudgetPerPeriod.calculate(
                partners = listOf(partner1),
                budgetOptions = emptyList(),
                budgetPerPartner = emptyList(),
                lumpSums = emptyList(),
                projectPeriods = emptyList(),
                partnersTotalBudgetPerCostCategory = mapOf(Pair(partner1Id, partnerTotal1))
            )
        ).isEqualTo(
            ProjectBudgetOverviewPerPartnerPerPeriod(
                partnersBudgetPerPeriod = listOf(
                    ProjectPartnerBudgetPerPeriod(
                        partner = partner1,
                        periodBudgets = mutableListOf(
                            ProjectPeriodBudget(0, 0, 0, BigDecimal.ZERO, false),
                            ProjectPeriodBudget(255, 0, 0, BigDecimal.ZERO, true)
                        ),
                        totalPartnerBudget = BigDecimal.ZERO
                    )
                ),
                totals = listOf(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO),
                totalsPercentage = listOf(BigDecimal.valueOf(0, 2), BigDecimal.valueOf(0, 2), BigDecimal.valueOf(0, 2))
            )
        )
    }

    @Test
    fun `calculate project budget overview per partner per period when flat rates are set`() {
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

        every {
            budgetCostCalculator.calculateCosts(
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
            )
        } returns BudgetCostsCalculationResult(totalCosts = 562.5.toScaledBigDecimal())

        assertThat(
            calculatePartnerBudgetPerPeriod.calculate(
                partners = listOf(partner1),
                budgetOptions = listOf(
                    ProjectPartnerBudgetOptions(
                        partnerId = partner1.id!!,
                        staffCostsFlatRate = 10,
                        officeAndAdministrationOnStaffCostsFlatRate = 15,
                        travelAndAccommodationOnStaffCostsFlatRate = 10
                    )
                ),
                budgetPerPartner = listOf(budgetPeriod1, budgetPeriod2, budgetPeriod3),
                lumpSums = emptyList(),
                projectPeriods = listOf(
                    ProjectPeriod(number = 1, start = 1, end = 6),
                    ProjectPeriod(number = 2, start = 7, end = 12),
                    ProjectPeriod(number = 3, start = 13, end = 15)
                ),
                partnersTotalBudgetPerCostCategory = mapOf(Pair(partner1Id, partnerTotal1))
            )
        ).isEqualTo(
                ProjectBudgetOverviewPerPartnerPerPeriod(
                    partnersBudgetPerPeriod = listOf(
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
                    ),
                    totals = listOf(
                        BigDecimal.valueOf(0),
                        BigDecimal.valueOf(20623, 2),
                        BigDecimal.valueOf(16875, 2),
                        BigDecimal.valueOf(18752, 2),
                        BigDecimal.valueOf(0),
                        BigDecimal.valueOf(56250, 2)
                    ),
                    totalsPercentage = listOf(
                        BigDecimal.valueOf(0, 2),
                        BigDecimal.valueOf(3666, 2),
                        BigDecimal.valueOf(3000, 2),
                        BigDecimal.valueOf(3334, 2),
                        BigDecimal.valueOf(0, 2),
                        BigDecimal.valueOf(10000, 2)
                    )
                )
            )
    }

    @Test
    fun `calculate project budget overview per partner per period when lump sums are set`() {
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
        // partner 2
        val p2budgetPeriod1 = ProjectPartnerBudget(
            id = partner2Id,
            periodNumber = 1,
            externalExpertiseAndServicesCostsPerPeriod = 10.toScaledBigDecimal(),
            infrastructureAndWorksCostsPerPeriod = 2.toScaledBigDecimal()
        )

        every {
            budgetCostCalculator.calculateCosts(
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
            )
        } returns BudgetCostsCalculationResult(totalCosts = 174.3.toScaledBigDecimal())

        every {
            budgetCostCalculator.calculateCosts(
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
            )
        } returns BudgetCostsCalculationResult(totalCosts = 13.38.toScaledBigDecimal())

        val result = calculatePartnerBudgetPerPeriod.calculate(
            partners = listOf(partner1, partner2),
            budgetOptions = listOf(
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
            ),
            budgetPerPartner = listOf(p1budgetPeriod1, p1budgetPeriod2, p2budgetPeriod1),
            lumpSums = listOf(
                lumpSumEntry(partner1Id, BigDecimal.TEN)
            ),
            projectPeriods = listOf(
                ProjectPeriod(number = 1, start = 1, end = 2),
                ProjectPeriod(number = 2, start = 2, end = 2)
            ),
            partnersTotalBudgetPerCostCategory = mapOf(Pair(partner1Id, partnerTotal1), Pair(partner2Id, partnerTotal2))
        )

        assertThat(result)
            .isEqualTo(
                ProjectBudgetOverviewPerPartnerPerPeriod(
                    partnersBudgetPerPeriod = listOf(
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
                    ),
                    totals = listOf(
                        BigDecimal.valueOf(0),
                        BigDecimal.valueOf(13688, 2),
                        BigDecimal.valueOf(5080, 2),
                        BigDecimal.valueOf(0),
                        BigDecimal.valueOf(18768, 2),
                    ),
                    totalsPercentage = listOf(
                        BigDecimal.valueOf(0, 2),
                        BigDecimal.valueOf(7293, 2),
                        BigDecimal.valueOf(2707, 2),
                        BigDecimal.valueOf(0, 2),
                        BigDecimal.valueOf(10000, 2),
                    )
                )
            )
    }

    private fun getProjectPeriods(total1: BigDecimal, total2: BigDecimal): MutableList<ProjectPeriodBudget> =
        mutableListOf(
            ProjectPeriodBudget(
                periodNumber = 0,
                periodStart = 0,
                periodEnd = 0,
                totalBudgetPerPeriod = BigDecimal.ZERO,
                lastPeriod = false
            ),
            ProjectPeriodBudget(
                periodNumber = 1,
                periodStart = 1,
                periodEnd = 2,
                totalBudgetPerPeriod = total1,
                lastPeriod = false
            ),
            ProjectPeriodBudget(
                periodNumber = 2,
                periodStart = 2,
                periodEnd = 2,
                totalBudgetPerPeriod = total2,
                lastPeriod = false
            ),
            ProjectPeriodBudget(
                periodNumber = 255,
                periodStart = 0,
                periodEnd = 0,
                totalBudgetPerPeriod = BigDecimal.ZERO,
                lastPeriod = true
            )
        )
}
