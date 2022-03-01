package io.cloudflight.jems.server.project.service.budget.get_partner_budget_per_period

import io.cloudflight.jems.server.project.service.budget.model.BudgetCostsCalculationResult
import io.cloudflight.jems.server.project.service.budget.model.PartnerLumpSum
import io.cloudflight.jems.server.project.service.budget.model.PartnersAggregatedInfo
import io.cloudflight.jems.server.project.service.budget.model.ProjectPartnerBudget
import io.cloudflight.jems.server.project.service.common.BudgetCostsCalculatorService
import io.cloudflight.jems.server.project.service.lumpsum.model.CLOSURE_PERIOD_NUMBER
import io.cloudflight.jems.server.project.service.lumpsum.model.PREPARATION_PERIOD_NUMBER
import io.cloudflight.jems.server.project.service.lumpsum.model.ProjectLumpSum
import io.cloudflight.jems.server.project.service.lumpsum.model.ProjectPartnerLumpSum
import io.cloudflight.jems.server.project.service.model.BudgetCostsDetail
import io.cloudflight.jems.server.project.service.model.ProjectBudgetOverviewPerPartnerPerPeriod
import io.cloudflight.jems.server.project.service.model.ProjectPartnerBudgetPerPeriod
import io.cloudflight.jems.server.project.service.model.ProjectPeriod
import io.cloudflight.jems.server.project.service.model.ProjectPeriodBudget
import io.cloudflight.jems.server.project.service.partner.model.PartnerTotalBudgetPerCostCategory
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerBudgetOptions
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.math.RoundingMode

@Service
class PartnerBudgetPerPeriodCalculator(private val budgetCostsCalculator: BudgetCostsCalculatorService) :
    PartnerBudgetPerPeriodCalculatorService {

    override fun calculate(
        partnersInfo: PartnersAggregatedInfo,
        lumpSums: List<ProjectLumpSum>,
        projectPeriods: List<ProjectPeriod>,
    ): ProjectBudgetOverviewPerPartnerPerPeriod {

        val partnerBudgetPerPeriod = calculatePartnersBudgetPerPeriod(partnersInfo, lumpSums, projectPeriods)
        val totals = calculateTotals(partnerBudgetPerPeriod, projectPeriods)

        return ProjectBudgetOverviewPerPartnerPerPeriod(
            partnerBudgetPerPeriod, totals, calculateTotalPercentages(totals)
        )
    }

    private fun calculatePartnersBudgetPerPeriod(
        partnersInfo: PartnersAggregatedInfo, lumpSums: List<ProjectLumpSum>, projectPeriods: List<ProjectPeriod>,
    ): List<ProjectPartnerBudgetPerPeriod> =
        partnersInfo.partners.filter { it.id != null }.map { partner ->
            val budgetOptions = partnersInfo.getBudgetOptionsByPartnerId(partner.id!!)
            val totalBudgetPerCostCategory = partnersInfo.partnersTotalBudgetPerCostCategory[partner.id]!!

            budgetCostsCalculator.calculateCosts(
                budgetOptions,
                totalBudgetPerCostCategory.unitCostTotal,
                totalBudgetPerCostCategory.lumpSumsTotal,
                totalBudgetPerCostCategory.externalCostTotal,
                totalBudgetPerCostCategory.equipmentCostTotal,
                totalBudgetPerCostCategory.infrastructureCostTotal,
                totalBudgetPerCostCategory.travelCostTotal,
                totalBudgetPerCostCategory.staffCostTotal
            ).let { totalBudgetCostsCalculationResult ->
                ProjectPartnerBudgetPerPeriod(
                    partner = partner,
                    periodBudgets = getPeriodBudgets(partner.id, lumpSums, projectPeriods,partnersInfo, totalBudgetCostsCalculationResult),
                    totalPartnerBudget = totalBudgetCostsCalculationResult.totalCosts,
                    totalPartnerBudgetDetail = BudgetCostsDetail(
                        unitCosts = totalBudgetPerCostCategory.unitCostTotal,
                        lumpSumsCosts = totalBudgetPerCostCategory.lumpSumsTotal,
                        externalCosts = totalBudgetPerCostCategory.externalCostTotal,
                        equipmentCosts = totalBudgetPerCostCategory.equipmentCostTotal,
                        infrastructureCosts = totalBudgetPerCostCategory.infrastructureCostTotal,
                        officeAndAdministrationCosts = totalBudgetCostsCalculationResult.officeAndAdministrationCosts,
                        travelCosts = totalBudgetCostsCalculationResult.travelCosts,
                        staffCosts = totalBudgetCostsCalculationResult.staffCosts,
                        otherCosts = totalBudgetCostsCalculationResult.otherCosts
                    )
                )
            }

        }

    private fun getPeriodBudgets(
        partnerId: Long,
        lumpSums: List<ProjectLumpSum>,
        projectPeriods: List<ProjectPeriod>,
        partnersInfo: PartnersAggregatedInfo,
        budgetCostsCalculationResult: BudgetCostsCalculationResult,
    ) =
        mutableListOf<ProjectPeriodBudget>().also { periodBudgets ->

            val partnerLumpSums = lumpSums.map { lumpSum ->
                PartnerLumpSum(
                    period = lumpSum.period,
                    amount = getPartnerLumpSumsAmountForPeriod(lumpSum.lumpSumContributions, partnerId)
                )
            }
            val totalLumpSumsInPreparation =
                partnerLumpSums.filter { it.period == PREPARATION_PERIOD_NUMBER }.sumOf { it.amount }
            val totalLumpSumsInClosure =
                partnerLumpSums.filter { it.period == CLOSURE_PERIOD_NUMBER }.sumOf { it.amount }

            periodBudgets.add(getPreparationPeriodBudgets(totalLumpSumsInPreparation))

            val periodBudgetsExcludingLastPeriod = calculatePeriodBudgetsExcludingLastPeriod(
                partnerId, projectPeriods, partnersInfo.getBudgetOptionsByPartnerId(partnerId), partnersInfo.budgetPerPartner, partnerLumpSums
            )

            periodBudgets.addAll(periodBudgetsExcludingLastPeriod)

            val lastPeriod = getLastPeriodOrNull(projectPeriods)
            if (lastPeriod != null)
                periodBudgets.add(
                    calculateLastPeriodBudgets(
                        lastPeriod, periodBudgetsExcludingLastPeriod,
                        budgetCostsCalculationResult, partnersInfo.partnersTotalBudgetPerCostCategory[partnerId]!!,
                        totalLumpSumsInPreparation.plus(totalLumpSumsInClosure)
                    )
                )
            periodBudgets.add(getClosurePeriodBudgets(totalLumpSumsInClosure))
            periodBudgets.sortBy { it.periodNumber }
        }

    private fun getPreparationPeriodBudgets(totalLumpSumsInPreparation: BigDecimal) =
        ProjectPeriodBudget(
            periodNumber = PREPARATION_PERIOD_NUMBER,
            periodStart = 0,
            periodEnd = 0,
            totalBudgetPerPeriod = totalLumpSumsInPreparation,
            budgetPerPeriodDetail = BudgetCostsDetail(lumpSumsCosts = totalLumpSumsInPreparation),
            lastPeriod = false
        )

    private fun calculatePeriodBudgetsExcludingLastPeriod(
        partnerId: Long, projectPeriods: List<ProjectPeriod>, budgetOptions: ProjectPartnerBudgetOptions?,
        budget: List<ProjectPartnerBudget>, partnerLumpSums: List<PartnerLumpSum>
    ): List<ProjectPeriodBudget> {
        val lastPeriod = getLastPeriodOrNull(projectPeriods)
        return projectPeriods.filter { period -> period.number != lastPeriod?.number }.map { period ->
            val totalLumpSum = getLumpSumForPeriod(partnerLumpSums, period.number)
            val totalBudget =
                budget.filter { it.id == partnerId }.firstOrNull { budget -> budget.periodNumber == period.number }
                    ?: ProjectPartnerBudget(partnerId, period.number)
            budgetCostsCalculator.calculateCosts(
                budgetOptions = budgetOptions,
                unitCosts = totalBudget.unitCostsPerPeriod,
                lumpSumsCosts = totalLumpSum,
                externalCosts = totalBudget.externalExpertiseAndServicesCostsPerPeriod,
                equipmentCosts = totalBudget.equipmentCostsPerPeriod,
                infrastructureCosts = totalBudget.infrastructureAndWorksCostsPerPeriod,
                travelCosts = totalBudget.travelAndAccommodationCostsPerPeriod,
                staffCosts = totalBudget.staffCostsPerPeriod
            ).let { budgetCostsCalculationResultPerPeriod ->
                ProjectPeriodBudget(
                    periodNumber = period.number,
                    periodStart = period.start,
                    periodEnd = period.end,
                    totalBudgetPerPeriod = budgetCostsCalculationResultPerPeriod.totalCosts,
                    budgetPerPeriodDetail = BudgetCostsDetail(
                        unitCosts = totalBudget.unitCostsPerPeriod,
                        lumpSumsCosts = totalLumpSum,
                        externalCosts = totalBudget.externalExpertiseAndServicesCostsPerPeriod,
                        equipmentCosts = totalBudget.equipmentCostsPerPeriod,
                        infrastructureCosts = totalBudget.infrastructureAndWorksCostsPerPeriod,
                        officeAndAdministrationCosts = budgetCostsCalculationResultPerPeriod.officeAndAdministrationCosts,
                        travelCosts = budgetCostsCalculationResultPerPeriod.travelCosts,
                        staffCosts = budgetCostsCalculationResultPerPeriod.staffCosts,
                        otherCosts = budgetCostsCalculationResultPerPeriod.otherCosts
                    ),
                    lastPeriod = false,
                )
            }
        }
    }

    private fun calculateLastPeriodBudgets(
        lastPeriod: ProjectPeriod, periodBudgetsExcludingLastPeriod: List<ProjectPeriodBudget>,
        budgetCostsCalculationResult: BudgetCostsCalculationResult,
        totalBudgetPerCostCategory: PartnerTotalBudgetPerCostCategory,
        totalLumpSumsInPreparationAndClosure: BigDecimal
    ) =
        periodBudgetsExcludingLastPeriod.sumOf { it.totalBudgetPerPeriod }
            .let { totalBudgetPerPeriodExcludingLastPeriod ->
                ProjectPeriodBudget(
                    periodNumber = lastPeriod.number,
                    periodStart = lastPeriod.start,
                    periodEnd = lastPeriod.end,
                    totalBudgetPerPeriod = budgetCostsCalculationResult.totalCosts
                        .minus(totalBudgetPerPeriodExcludingLastPeriod)
                        .minus(totalLumpSumsInPreparationAndClosure)
                        .setScale(2, RoundingMode.DOWN),
                    budgetPerPeriodDetail = BudgetCostsDetail(
                        unitCosts = totalBudgetPerCostCategory.unitCostTotal
                            .minus(periodBudgetsExcludingLastPeriod.sumOf { it.budgetPerPeriodDetail.unitCosts }),
                        lumpSumsCosts = totalBudgetPerCostCategory.lumpSumsTotal
                            .minus(periodBudgetsExcludingLastPeriod.sumOf { it.budgetPerPeriodDetail.lumpSumsCosts }),
                        externalCosts = totalBudgetPerCostCategory.externalCostTotal
                            .minus(periodBudgetsExcludingLastPeriod.sumOf { it.budgetPerPeriodDetail.externalCosts }),
                        equipmentCosts = totalBudgetPerCostCategory.equipmentCostTotal
                            .minus(periodBudgetsExcludingLastPeriod.sumOf { it.budgetPerPeriodDetail.equipmentCosts }),
                        infrastructureCosts = totalBudgetPerCostCategory.infrastructureCostTotal
                            .minus(periodBudgetsExcludingLastPeriod.sumOf { it.budgetPerPeriodDetail.infrastructureCosts }),
                        officeAndAdministrationCosts = budgetCostsCalculationResult.officeAndAdministrationCosts
                            .minus(periodBudgetsExcludingLastPeriod.sumOf { it.budgetPerPeriodDetail.officeAndAdministrationCosts }),
                        travelCosts = budgetCostsCalculationResult.travelCosts
                            .minus(periodBudgetsExcludingLastPeriod.sumOf { it.budgetPerPeriodDetail.travelCosts }),
                        staffCosts = budgetCostsCalculationResult.staffCosts
                            .minus(periodBudgetsExcludingLastPeriod.sumOf { it.budgetPerPeriodDetail.staffCosts }),
                        otherCosts = budgetCostsCalculationResult.otherCosts
                            .minus(periodBudgetsExcludingLastPeriod.sumOf { it.budgetPerPeriodDetail.otherCosts }),
                    ),
                    lastPeriod = false
                )
            }

    private fun getClosurePeriodBudgets(totalLumpSumsInClosure: BigDecimal) =
        ProjectPeriodBudget(
            periodNumber = CLOSURE_PERIOD_NUMBER,
            periodStart = 0,
            periodEnd = 0,
            totalBudgetPerPeriod = totalLumpSumsInClosure,
            budgetPerPeriodDetail = BudgetCostsDetail(lumpSumsCosts = totalLumpSumsInClosure),
            lastPeriod = true
        )

    private fun calculateTotals(
        partnerBudgetPerPeriod: List<ProjectPartnerBudgetPerPeriod>, projectPeriods: List<ProjectPeriod>
    ): List<BigDecimal> =
        listOf(PREPARATION_PERIOD_NUMBER).plus(projectPeriods.map { it.number }).plus(CLOSURE_PERIOD_NUMBER)
            .map { periodNumber ->
                partnerBudgetPerPeriod.flatMap { item -> item.periodBudgets.filter { it.periodNumber == periodNumber } }
                    .sumOf { it.totalBudgetPerPeriod }
            }.let { totals -> totals.plus(totals.sumOf { it }) }

    private fun calculateTotalPercentages(totals: List<BigDecimal>): List<BigDecimal> =
        totals.last().let { it ->
            val totalEligibleBudgetOrOne = if (it.compareTo(BigDecimal.ZERO) == 0) BigDecimal.ONE else totals.last()
            totals.map {
                it.multiply(BigDecimal.valueOf(100)).divide(totalEligibleBudgetOrOne, 2, RoundingMode.HALF_UP)
            }
                .toMutableList().also { totalsPercentages ->
                    // to make sure that there is at least one period
                    if (totalsPercentages.size > 4) {
                        val lastPeriod = totalsPercentages[totalsPercentages.size - 3]
                        // to get 100 percent in total take last period from 100 instead of calculated values
                        totalsPercentages[totalsPercentages.size - 3] =
                            BigDecimal.valueOf(100)
                                .minus((totalsPercentages.dropLast(1).sumOf { it }.minus(lastPeriod)))
                    }
                }
        }

    private fun getLumpSumForPeriod(partnerLumpSums: List<PartnerLumpSum>, periodNumber: Int) =
        partnerLumpSums.filter { lumpSum -> lumpSum.period != null && lumpSum.period == periodNumber }
            .sumOf { it.amount }

    private fun getPartnerLumpSumsAmountForPeriod(
        lumpSumContributionsPerPeriod: List<ProjectPartnerLumpSum>,
        partnerId: Long
    ) =
        lumpSumContributionsPerPeriod.filter { it.partnerId == partnerId }
            .map { it.amount }
            .fold(BigDecimal.ZERO) { first, second -> first.add(second) }

    private fun getLastPeriodOrNull(projectPeriods: List<ProjectPeriod>) =
        projectPeriods.maxByOrNull { it.number }
}
