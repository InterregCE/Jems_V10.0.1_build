package io.cloudflight.jems.server.project.service.budget.get_partner_budget_per_period

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.service.budget.model.BudgetCostsCalculationResult
import io.cloudflight.jems.server.project.service.budget.model.PartnersAggregatedInfo
import io.cloudflight.jems.server.project.service.budget.model.ProjectPartnerBudget
import io.cloudflight.jems.server.project.service.budget.model.ProjectSpfBudgetPerPeriod
import io.cloudflight.jems.server.project.service.common.BudgetCostsCalculator
import io.cloudflight.jems.server.project.service.lumpsum.model.ProjectLumpSum
import io.cloudflight.jems.server.project.service.lumpsum.model.ProjectPartnerLumpSum
import io.cloudflight.jems.server.project.service.model.*
import io.cloudflight.jems.server.project.service.partner.model.PartnerTotalBudgetPerCostCategory
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerBudgetOptions
import io.cloudflight.jems.server.toScaledBigDecimal
import io.cloudflight.jems.server.utils.partner.projectPartnerSummary
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.math.BigDecimal

internal class PartnerBudgetPerPeriodCalculatorTest : UnitTest() {

    private val partner1Id = 1L
    private val partner2Id = 2L
    private val partner1 = projectPartnerSummary(id = partner1Id)
    private val partnerTotal1 = PartnerTotalBudgetPerCostCategory(
        partner1Id,
        null, null, null, null, null,
        BigDecimal.ZERO, 200.00.toScaledBigDecimal(), 300.00.toScaledBigDecimal(),
        BigDecimal.ZERO, BigDecimal.ZERO,
        BigDecimal.ZERO, BigDecimal.ZERO
    )


    @MockK
    private lateinit var budgetCostCalculator: BudgetCostsCalculator

    @InjectMockKs
    private lateinit var calculatePartnerBudgetPerPeriod: PartnerBudgetPerPeriodCalculator

    @Test
    fun `calculate project budget overview per partner per period when there is no period`() {
        val partner1BudgetOptions = ProjectPartnerBudgetOptions(partner1Id)
        every {
            budgetCostCalculator.calculateCosts(
                partner1BudgetOptions,
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
                PartnersAggregatedInfo(
                    listOf(partner1), listOf(partner1BudgetOptions),
                    emptyList(), mapOf(Pair(partner1Id, partnerTotal1))
                ),
                lumpSums = emptyList(),
                projectPeriods = emptyList(),
                spfPartnerBudgetPerPeriod = emptyList()
            )
        ).isEqualTo(
            ProjectBudgetOverviewPerPartnerPerPeriod(
                partnersBudgetPerPeriod = listOf(
                    ProjectPartnerBudgetPerPeriod(
                        partner = partner1,
                        periodBudgets = listOfPeriodBudgets(),
                        totalPartnerBudget = BigDecimal.ZERO,
                        totalPartnerBudgetDetail = BudgetCostsDetail(
                            externalCosts = 300.00.toScaledBigDecimal(),
                            equipmentCosts = 200.00.toScaledBigDecimal()
                        ),
                        costType = ProjectPartnerCostType.Management
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

        val budgetOptions = ProjectPartnerBudgetOptions(
            partnerId = partner1.id!!,
            staffCostsFlatRate = 10,
            officeAndAdministrationOnStaffCostsFlatRate = 15,
            travelAndAccommodationOnStaffCostsFlatRate = 10
        )

        every {
            budgetCostCalculator.calculateCosts(
                budgetOptions,
                partnerTotal1.unitCostTotal,
                partnerTotal1.lumpSumsTotal,
                partnerTotal1.externalCostTotal,
                partnerTotal1.equipmentCostTotal,
                partnerTotal1.infrastructureCostTotal,
                partnerTotal1.travelCostTotal,
                partnerTotal1.staffCostTotal
            )
        } returns BudgetCostsCalculationResult(
            staffCosts = 50.00.toScaledBigDecimal(),
            travelCosts = 5.00.toScaledBigDecimal(),
            officeAndAdministrationCosts = 7.50.toScaledBigDecimal(),
            totalCosts = 562.5.toScaledBigDecimal()
        )

        every {
            budgetCostCalculator.calculateCosts(
                budgetOptions,
                BigDecimal.ZERO, BigDecimal.ZERO,
                133.33.toScaledBigDecimal(), 50.00.toScaledBigDecimal(),
                BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO
            )
        } returns BudgetCostsCalculationResult(
            staffCosts = 18.33.toScaledBigDecimal(),
            travelCosts = 1.83.toScaledBigDecimal(),
            officeAndAdministrationCosts = 2.74.toScaledBigDecimal(),
            totalCosts = 206.23.toScaledBigDecimal()
        )

        every {
            budgetCostCalculator.calculateCosts(
                budgetOptions,
                BigDecimal.ZERO, BigDecimal.ZERO,
                100.00.toScaledBigDecimal(), 50.00.toScaledBigDecimal(),
                BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO
            )
        } returns BudgetCostsCalculationResult(
            staffCosts = 15.00.toScaledBigDecimal(),
            travelCosts = 1.50.toScaledBigDecimal(),
            officeAndAdministrationCosts = 2.25.toScaledBigDecimal(),
            totalCosts = 168.75.toScaledBigDecimal()
        )


        assertThat(
            calculatePartnerBudgetPerPeriod.calculate(
                PartnersAggregatedInfo(
                    listOf(partner1), listOf(budgetOptions), listOf(budgetPeriod1, budgetPeriod2, budgetPeriod3),
                    mapOf(Pair(partner1Id, partnerTotal1))
                ),
                lumpSums = emptyList(),
                projectPeriods = listOf(
                    ProjectPeriod(number = 1, start = 1, end = 6),
                    ProjectPeriod(number = 2, start = 7, end = 12),
                    ProjectPeriod(number = 3, start = 13, end = 15)
                ),
                spfPartnerBudgetPerPeriod = emptyList()
            )
        ).isEqualTo(
            ProjectBudgetOverviewPerPartnerPerPeriod(
                partnersBudgetPerPeriod = listOf(
                    ProjectPartnerBudgetPerPeriod(
                        partner = partner1,
                        periodBudgets = mutableListOf(
                            ProjectPeriodBudget(0, 0, 0, BigDecimal.ZERO, BudgetCostsDetail(), false),
                            ProjectPeriodBudget(
                                1, 1, 6, 206.23.toScaledBigDecimal(),
                                BudgetCostsDetail(
                                    staffCosts = 18.33.toScaledBigDecimal(),
                                    travelCosts = 1.83.toScaledBigDecimal(),
                                    officeAndAdministrationCosts = 2.74.toScaledBigDecimal(),
                                    externalCosts = 133.33.toScaledBigDecimal(),
                                    equipmentCosts = 50.00.toScaledBigDecimal()
                                ),
                                false
                            ),
                            ProjectPeriodBudget(
                                2, 7, 12, 168.75.toScaledBigDecimal(),
                                BudgetCostsDetail(
                                    staffCosts = 15.00.toScaledBigDecimal(),
                                    travelCosts = 1.50.toScaledBigDecimal(),
                                    officeAndAdministrationCosts = 2.25.toScaledBigDecimal(),
                                    externalCosts = 100.00.toScaledBigDecimal(),
                                    equipmentCosts = 50.00.toScaledBigDecimal()
                                ),
                                false
                            ),
                            ProjectPeriodBudget(
                                3, 13, 15, 187.52.toScaledBigDecimal(),
                                BudgetCostsDetail(
                                    staffCosts = 16.67.toScaledBigDecimal(),
                                    travelCosts = 1.67.toScaledBigDecimal(),
                                    officeAndAdministrationCosts = 2.51.toBigDecimal(),
                                    externalCosts = 66.67.toScaledBigDecimal(),
                                    equipmentCosts = 100.00.toScaledBigDecimal()
                                ),
                                false
                            ),
                            ProjectPeriodBudget(255, 0, 0, BigDecimal.ZERO, BudgetCostsDetail(), true)
                        ),
                        totalPartnerBudget = 562.5.toScaledBigDecimal(),
                        totalPartnerBudgetDetail = BudgetCostsDetail(
                            externalCosts = 300.00.toScaledBigDecimal(),
                            equipmentCosts = 200.00.toScaledBigDecimal(),
                            officeAndAdministrationCosts = 7.50.toScaledBigDecimal(),
                            travelCosts = 5.00.toScaledBigDecimal(),
                            staffCosts = 50.00.toScaledBigDecimal()
                        ),
                        costType = ProjectPartnerCostType.Management
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
        val partner1BudgetOptions = ProjectPartnerBudgetOptions(
            partnerId = partner1Id,
            officeAndAdministrationOnStaffCostsFlatRate = 10,
            otherCostsOnStaffCostsFlatRate = 5
        )
        val partnerTotal1 = PartnerTotalBudgetPerCostCategory(
            partner1Id,
            partner1BudgetOptions.officeAndAdministrationOnStaffCostsFlatRate,
            partner1BudgetOptions.officeAndAdministrationOnDirectCostsFlatRate,
            partner1BudgetOptions.travelAndAccommodationOnStaffCostsFlatRate,
            partner1BudgetOptions.staffCostsFlatRate,
            partner1BudgetOptions.otherCostsOnStaffCostsFlatRate,
            50.46.toScaledBigDecimal(), 13.11.toBigDecimal(), 30.62.toScaledBigDecimal(), 33.3.toScaledBigDecimal(),
            11.10.toScaledBigDecimal(), 62.91.toScaledBigDecimal(), 10.22.toScaledBigDecimal()
        )
        val p1LumpSumPeriod1 = ProjectLumpSum(
            orderNr = 1,
            programmeLumpSumId = 2L,
            period = 1,
            lumpSumContributions = listOf(ProjectPartnerLumpSum(partner1Id, 10.22.toScaledBigDecimal()))
        )
        val p1budgetPeriod1 = ProjectPartnerBudget(
            id = partner1Id,
            periodNumber = 1,
            staffCostsPerPeriod = 50.20.toScaledBigDecimal(),
            travelAndAccommodationCostsPerPeriod = 11.10.toScaledBigDecimal(),
            externalExpertiseAndServicesCostsPerPeriod = 20.32.toScaledBigDecimal(),
            unitCostsPerPeriod = 25.01.toScaledBigDecimal()
        )
        val p1budgetPeriod2 = ProjectPartnerBudget(
            id = partner1Id,
            periodNumber = 2,
            staffCostsPerPeriod = 12.71.toScaledBigDecimal(),
            infrastructureAndWorksCostsPerPeriod = 33.3.toScaledBigDecimal(),
            unitCostsPerPeriod = 25.45.toScaledBigDecimal(),
            equipmentCostsPerPeriod = 13.11.toBigDecimal(),
            externalExpertiseAndServicesCostsPerPeriod = 10.30.toScaledBigDecimal(),
        )


        val partner2 = projectPartnerSummary(id = partner2Id)
        val partner2BudgetOptions = ProjectPartnerBudgetOptions(
            partnerId = partner2Id,
            staffCostsFlatRate = 10,
            travelAndAccommodationOnStaffCostsFlatRate = 15
        )
        val partnerTotal2 = PartnerTotalBudgetPerCostCategory(
            partner2Id,
            partner2BudgetOptions.officeAndAdministrationOnStaffCostsFlatRate,
            partner2BudgetOptions.officeAndAdministrationOnDirectCostsFlatRate,
            partner2BudgetOptions.travelAndAccommodationOnStaffCostsFlatRate,
            partner2BudgetOptions.staffCostsFlatRate,
            partner2BudgetOptions.otherCostsOnStaffCostsFlatRate,
            BigDecimal.ZERO, BigDecimal.ZERO, 10.toScaledBigDecimal(), 2.toScaledBigDecimal(),
            BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO
        )
        val p2budgetPeriod1 = ProjectPartnerBudget(
            id = partner2Id,
            periodNumber = 1,
            externalExpertiseAndServicesCostsPerPeriod = 10.toScaledBigDecimal(),
            infrastructureAndWorksCostsPerPeriod = 2.toScaledBigDecimal()
        )


        every {
            budgetCostCalculator.calculateCosts(
                partner1BudgetOptions,
                partnerTotal1.unitCostTotal,
                partnerTotal1.lumpSumsTotal,
                partnerTotal1.externalCostTotal,
                partnerTotal1.equipmentCostTotal,
                partnerTotal1.infrastructureCostTotal,
                partnerTotal1.travelCostTotal,
                partnerTotal1.staffCostTotal
            )
        } returns BudgetCostsCalculationResult(
            staffCosts = 62.91.toScaledBigDecimal(), travelCosts = 11.1.toScaledBigDecimal(),
            otherCosts = 3.14.toScaledBigDecimal(),
            officeAndAdministrationCosts = 6.29.toScaledBigDecimal(), totalCosts = 221.15.toScaledBigDecimal()
        )

        every {
            budgetCostCalculator.calculateCosts(
                partner1BudgetOptions,
                p1budgetPeriod1.unitCostsPerPeriod, p1LumpSumPeriod1.lumpSumContributions.first().amount,
                p1budgetPeriod1.externalExpertiseAndServicesCostsPerPeriod,
                p1budgetPeriod1.equipmentCostsPerPeriod, p1budgetPeriod1.infrastructureAndWorksCostsPerPeriod,
                p1budgetPeriod1.travelAndAccommodationCostsPerPeriod, p1budgetPeriod1.staffCostsPerPeriod
            )
        } returns BudgetCostsCalculationResult(
            staffCosts = 50.2.toScaledBigDecimal(), travelCosts = 11.1.toScaledBigDecimal(),
            officeAndAdministrationCosts = 5.02.toScaledBigDecimal(), otherCosts = 2.51.toScaledBigDecimal(),
            totalCosts = 124.38.toScaledBigDecimal()
        )

        every {
            budgetCostCalculator.calculateCosts(
                partner2BudgetOptions,
                partnerTotal2.unitCostTotal,
                partnerTotal2.lumpSumsTotal,
                partnerTotal2.externalCostTotal,
                partnerTotal2.equipmentCostTotal,
                partnerTotal2.infrastructureCostTotal,
                partnerTotal2.travelCostTotal,
                partnerTotal2.staffCostTotal
            )
        } returns BudgetCostsCalculationResult(
            staffCosts = 1.20.toScaledBigDecimal(),
            travelCosts = 0.18.toScaledBigDecimal(),
            totalCosts = 13.38.toScaledBigDecimal()
        )


        val result = calculatePartnerBudgetPerPeriod.calculate(
            PartnersAggregatedInfo(
                listOf(partner1, partner2),
                listOf(partner1BudgetOptions, partner2BudgetOptions),
                listOf(p1budgetPeriod1, p1budgetPeriod2, p2budgetPeriod1),
                mapOf(Pair(partner1Id, partnerTotal1), Pair(partner2Id, partnerTotal2))
            ),
            lumpSums = listOf(p1LumpSumPeriod1),
            projectPeriods = listOf(
                ProjectPeriod(number = 1, start = 1, end = 2),
                ProjectPeriod(number = 2, start = 2, end = 2)
            ),
            spfPartnerBudgetPerPeriod = emptyList()
        )

        assertThat(result)
            .isEqualTo(
                ProjectBudgetOverviewPerPartnerPerPeriod(
                    partnersBudgetPerPeriod = listOf(
                        ProjectPartnerBudgetPerPeriod(
                            partner = partner1,
                            periodBudgets =
                            mutableListOf(
                                ProjectPeriodBudget(
                                    periodNumber = 0,
                                    periodStart = 0,
                                    periodEnd = 0,
                                    totalBudgetPerPeriod = BigDecimal.ZERO,
                                    lastPeriod = false,
                                    budgetPerPeriodDetail = BudgetCostsDetail()
                                ),
                                ProjectPeriodBudget(
                                    periodNumber = 1,
                                    periodStart = 1,
                                    periodEnd = 2,
                                    totalBudgetPerPeriod = 124.38.toScaledBigDecimal(),
                                    lastPeriod = false,
                                    budgetPerPeriodDetail = BudgetCostsDetail(
                                        unitCosts = 25.01.toScaledBigDecimal(),
                                        lumpSumsCosts = 10.22.toScaledBigDecimal(),
                                        externalCosts = 20.32.toScaledBigDecimal(),
                                        officeAndAdministrationCosts = 5.02.toScaledBigDecimal(),
                                        travelCosts = 11.10.toScaledBigDecimal(),
                                        staffCosts = 50.20.toScaledBigDecimal(),
                                        otherCosts = 2.51.toScaledBigDecimal()
                                    )
                                ),
                                ProjectPeriodBudget(
                                    periodNumber = 2,
                                    periodStart = 2,
                                    periodEnd = 2,
                                    totalBudgetPerPeriod = 96.77.toScaledBigDecimal(),
                                    lastPeriod = false,
                                    budgetPerPeriodDetail = BudgetCostsDetail(
                                        unitCosts = 25.45.toScaledBigDecimal(),
                                        lumpSumsCosts = 0.00.toScaledBigDecimal(),
                                        externalCosts = 10.30.toScaledBigDecimal(),
                                        equipmentCosts = 13.11.toScaledBigDecimal(),
                                        infrastructureCosts = 33.30.toScaledBigDecimal(),
                                        officeAndAdministrationCosts = 1.27.toScaledBigDecimal(),
                                        travelCosts = 0.00.toScaledBigDecimal(),
                                        staffCosts = 12.71.toScaledBigDecimal(),
                                        otherCosts = 0.63.toScaledBigDecimal()

                                    )
                                ),
                                ProjectPeriodBudget(
                                    periodNumber = 255,
                                    periodStart = 0,
                                    periodEnd = 0,
                                    totalBudgetPerPeriod = BigDecimal.ZERO,
                                    lastPeriod = true,
                                    budgetPerPeriodDetail = BudgetCostsDetail()
                                )
                            ),
                            totalPartnerBudget = 221.15.toScaledBigDecimal(),
                            totalPartnerBudgetDetail = BudgetCostsDetail(
                                unitCosts = 50.46.toScaledBigDecimal(),
                                lumpSumsCosts = 10.22.toScaledBigDecimal(),
                                externalCosts = 30.62.toScaledBigDecimal(),
                                equipmentCosts = 13.11.toScaledBigDecimal(),
                                infrastructureCosts = 33.30.toScaledBigDecimal(),
                                officeAndAdministrationCosts = 6.29.toScaledBigDecimal(),
                                travelCosts = 11.10.toScaledBigDecimal(),
                                staffCosts = 62.91.toScaledBigDecimal(),
                                otherCosts = 3.14.toScaledBigDecimal()
                            ),
                            costType = ProjectPartnerCostType.Management
                        ),
                        ProjectPartnerBudgetPerPeriod(
                            partner = partner2,
                            periodBudgets =
                            mutableListOf(
                                ProjectPeriodBudget(
                                    periodNumber = 0,
                                    periodStart = 0,
                                    periodEnd = 0,
                                    totalBudgetPerPeriod = BigDecimal.ZERO,
                                    lastPeriod = false,
                                    budgetPerPeriodDetail = BudgetCostsDetail()
                                ),
                                ProjectPeriodBudget(
                                    periodNumber = 1,
                                    periodStart = 1,
                                    periodEnd = 2,
                                    totalBudgetPerPeriod = 13.38.toScaledBigDecimal(),
                                    lastPeriod = false,
                                    budgetPerPeriodDetail = BudgetCostsDetail(
                                        externalCosts = 10.00.toScaledBigDecimal(),
                                        infrastructureCosts = 2.00.toScaledBigDecimal(),
                                        travelCosts = 0.18.toScaledBigDecimal(),
                                        staffCosts = 1.20.toScaledBigDecimal()
                                    )
                                ),
                                ProjectPeriodBudget(
                                    periodNumber = 2,
                                    periodStart = 2,
                                    periodEnd = 2,
                                    totalBudgetPerPeriod = 0.00.toScaledBigDecimal(),
                                    lastPeriod = false,
                                    budgetPerPeriodDetail = BudgetCostsDetail(
                                        externalCosts = 0.00.toScaledBigDecimal(),
                                        infrastructureCosts = 0.00.toScaledBigDecimal(),
                                        travelCosts = 0.00.toScaledBigDecimal(),
                                        staffCosts = 0.00.toScaledBigDecimal()
                                    )
                                ),
                                ProjectPeriodBudget(
                                    periodNumber = 255,
                                    periodStart = 0,
                                    periodEnd = 0,
                                    totalBudgetPerPeriod = BigDecimal.ZERO,
                                    lastPeriod = true,
                                    budgetPerPeriodDetail = BudgetCostsDetail()
                                )
                            ),
                            totalPartnerBudget = 13.38.toScaledBigDecimal(),
                            totalPartnerBudgetDetail = BudgetCostsDetail(
                                externalCosts = 10.00.toScaledBigDecimal(),
                                infrastructureCosts = 2.00.toScaledBigDecimal(),
                                travelCosts = 0.18.toScaledBigDecimal(),
                                staffCosts = 1.20.toScaledBigDecimal()
                            ),
                            costType = ProjectPartnerCostType.Management
                        )
                    ),
                    totals = listOf(
                        BigDecimal.valueOf(0),
                        BigDecimal.valueOf(13776, 2),
                        BigDecimal.valueOf(9677, 2),
                        BigDecimal.valueOf(0),
                        BigDecimal.valueOf(23453, 2),
                    ),
                    totalsPercentage = listOf(
                        BigDecimal.valueOf(0, 2),
                        BigDecimal.valueOf(5874, 2),
                        BigDecimal.valueOf(4126, 2),
                        BigDecimal.valueOf(0, 2),
                        BigDecimal.valueOf(10000, 2),
                    )
                )
            )
    }

    @Test
    fun `test calculation project budget overview per partner per period of SPF empty`() {
        val periodBudgets = listOfPeriodBudgets()

        val result = calculatePartnerBudgetPerPeriod.calculateSpfPartnerBudgetPerPeriod(
            spfBeneficiary = partner1,
            spfBudgetPerPeriod = emptyList(),
            spfTotalBudget = BigDecimal.TEN,
            projectPeriods = emptyList()
        )
        assertThat(result).containsExactly(
            ProjectPartnerBudgetPerPeriod(
                partner = partner1,
                periodBudgets = periodBudgets,
                totalPartnerBudget = BigDecimal.TEN,
                totalPartnerBudgetDetail = BudgetCostsDetail(),
                costType = ProjectPartnerCostType.Spf
            )
        )
    }

    @Test
    fun `test calculation project budget overview per partner per period of SPF values`() {
        val periodBudgets = listOfPeriodBudgets(2, listOf(BigDecimal.TEN, BigDecimal.ZERO))
        val budgetPerPeriod1 = ProjectSpfBudgetPerPeriod(1, BigDecimal.TEN)
        val budgetPerPeriod2 = ProjectSpfBudgetPerPeriod(2, BigDecimal.ZERO)

        val result = calculatePartnerBudgetPerPeriod.calculateSpfPartnerBudgetPerPeriod(
            spfBeneficiary = partner1,
            spfBudgetPerPeriod = listOf(budgetPerPeriod1, budgetPerPeriod2),
            spfTotalBudget = BigDecimal.TEN,
            projectPeriods = listOfProjectPeriods(2)
        )
        assertThat(result).containsExactly(
            ProjectPartnerBudgetPerPeriod(
                partner = partner1,
                periodBudgets = periodBudgets,
                totalPartnerBudget = BigDecimal.TEN,
                totalPartnerBudgetDetail = BudgetCostsDetail(),
                costType = ProjectPartnerCostType.Spf
            )
        )
    }

    private fun listOfProjectPeriods(amount: Long = 0): MutableList<ProjectPeriod> {
        val periodBudgets = mutableListOf<ProjectPeriod>()
        for (i in 1..amount) {
            val pBtoAdd = ProjectPeriod(i.toInt(), i.toInt(), i.toInt())
            periodBudgets.add(pBtoAdd)
        }
        return periodBudgets
    }

    private fun listOfPeriodBudgets(amount: Long = 0, totalBudgets: List<BigDecimal> = emptyList()): MutableList<ProjectPeriodBudget> {
        val periodBudgets = mutableListOf(ProjectPeriodBudget(0, 0, 0, BigDecimal.ZERO, BudgetCostsDetail(), false))
        for (i in 1..amount) {
            val period = i.toInt()
            val pBtoAdd = ProjectPeriodBudget(period, period, period, totalBudgets[period-1], BudgetCostsDetail(), false)
            periodBudgets.add(pBtoAdd)
        }
        periodBudgets.add(ProjectPeriodBudget(255, 0, 0, BigDecimal.ZERO, BudgetCostsDetail(), true))
        return periodBudgets
    }
}
