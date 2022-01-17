package io.cloudflight.jems.server.project.service.common

import io.cloudflight.jems.api.project.dto.partner.cofinancing.ProjectPartnerContributionStatusDTO
import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFund
import io.cloudflight.jems.server.project.service.cofinancing.model.PartnerBudgetCoFinancing
import io.cloudflight.jems.server.project.service.model.PartnerBudgetPerFund
import io.cloudflight.jems.server.project.service.model.ProjectPartnerBudgetPerFund
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerCoFinancing
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerContribution
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerSummary
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.math.BigInteger
import java.math.MathContext
import java.math.RoundingMode

@Service
class PartnerBudgetPerFundCalculator : PartnerBudgetPerFundCalculatorService {

    companion object {
        val mc = MathContext(5)
    }

    override fun calculate(
        partners: List<ProjectPartnerSummary>, projectFunds: List<ProgrammeFund>,
        coFinancing: List<PartnerBudgetCoFinancing>
    ): List<ProjectPartnerBudgetPerFund> {

        val budgetsPerFundWithoutPercentOfTotal: MutableMap<Long, List<PartnerBudgetPerFund>> = mutableMapOf()
        coFinancing.forEach {
            budgetsPerFundWithoutPercentOfTotal[it.partner.id!!] =
                getPartnerBudgetsWithoutPercentOfTotal(
                    it.projectPartnerCoFinancingAndContribution!!.finances,
                    it.total!!
                )
        }

        val totalEligibleBudget = coFinancing.sumOf { it.total!! }

        val totalBudgetsPerFund = projectFunds.map { fund ->
            PartnerBudgetPerFund(
                fund = fund,
                percentage = calculatePercentage(
                    getTotalBudgetAmountForFund(fund, budgetsPerFundWithoutPercentOfTotal),
                    totalEligibleBudget,
                    RoundingMode.HALF_UP
                ),
                value = getTotalBudgetAmountForFund(fund, budgetsPerFundWithoutPercentOfTotal)
            )
        }

        val tableRowsExceptTotal = coFinancing.map {
            ProjectPartnerBudgetPerFund(
                partner = it.partner,
                budgetPerFund = getBudgetPerFundForPartner(
                    projectFunds,
                    budgetsPerFundWithoutPercentOfTotal[it.partner.id]!!,
                    totalBudgetsPerFund
                ),
                publicContribution = getPartnerContribution(
                    it.projectPartnerCoFinancingAndContribution?.partnerContributions,
                    ProjectPartnerContributionStatusDTO.Public
                ),
                autoPublicContribution = getPartnerContribution(
                    it.projectPartnerCoFinancingAndContribution?.partnerContributions,
                    ProjectPartnerContributionStatusDTO.AutomaticPublic
                ),
                privateContribution = getPartnerContribution(
                    it.projectPartnerCoFinancingAndContribution?.partnerContributions,
                    ProjectPartnerContributionStatusDTO.Private
                ),
                totalPartnerContribution = getPartnerContribution(
                    it.projectPartnerCoFinancingAndContribution?.partnerContributions,
                    null
                ),
                totalEligibleBudget = it.total!!.setScale(2, RoundingMode.DOWN),
                percentageOfTotalEligibleBudget = calculatePercentage(
                    it.total.setScale(2, RoundingMode.DOWN),
                    totalEligibleBudget,
                    RoundingMode.HALF_UP
                )
            )
        }.toMutableList()

        val tableRowTotal = ProjectPartnerBudgetPerFund(
            partner = null,
            budgetPerFund = totalBudgetsPerFund.toSet(),
            publicContribution = tableRowsExceptTotal.sumOf { it.publicContribution!! },
            autoPublicContribution = tableRowsExceptTotal.sumOf { it.autoPublicContribution!! },
            privateContribution = tableRowsExceptTotal.sumOf { it.privateContribution!! },
            totalPartnerContribution = tableRowsExceptTotal.sumOf { it.totalPartnerContribution!! },
            totalEligibleBudget = totalEligibleBudget,
            percentageOfTotalEligibleBudget = BigDecimal(100)
        )

        tableRowsExceptTotal.add(tableRowTotal)

        return tableRowsExceptTotal
    }

    private fun getPartnerBudgetsWithoutPercentOfTotal(
        finances: List<ProjectPartnerCoFinancing>,
        totalBudget: BigDecimal
    ): List<PartnerBudgetPerFund> {
        return finances.filter { it.fund != null }.map {
            PartnerBudgetPerFund(
                fund = it.fund,
                percentage = it.percentage,
                value = totalBudget
                    .multiply(
                        it.percentage
                            .divide(BigDecimal(100))
                    )
                    .setScale(2, RoundingMode.DOWN)
            )
        }
    }

    private fun getTotalBudgetAmountForFund(
        fund: ProgrammeFund,
        partnersBudgetsPerFund: Map<Long, List<PartnerBudgetPerFund>>
    ): BigDecimal {
        var totalSum = BigDecimal.ZERO
        partnersBudgetsPerFund.values.forEach { budgetsPerFund ->
            budgetsPerFund.filter { it.fund?.id == fund.id }.forEach { fund -> totalSum += fund.value }
        }
        return totalSum.setScale(2, RoundingMode.DOWN)
    }

    private fun getBudgetPerFundForPartner(
        projectChosenFunds: List<ProgrammeFund>,
        budgetPerFund: List<PartnerBudgetPerFund>,
        totalBudgetPerFund: List<PartnerBudgetPerFund>
    ): Set<PartnerBudgetPerFund> {
        val partnerBudgetPerFund = mutableSetOf<PartnerBudgetPerFund>()

        projectChosenFunds.forEach { fund ->
            val budgetOfFund = budgetPerFund.firstOrNull { it.fund?.id == fund.id }
            val totalBudgetOfFund = totalBudgetPerFund.firstOrNull { it.fund?.id == fund.id }
            if (budgetOfFund != null && totalBudgetOfFund != null) {
                partnerBudgetPerFund.add(
                    PartnerBudgetPerFund(
                        fund = fund,
                        value = budgetOfFund.value,
                        percentage = budgetOfFund.percentage,
                        percentageOfTotal = calculatePercentage(
                            budgetOfFund.value,
                            totalBudgetOfFund.value,
                            RoundingMode.HALF_UP
                        )
                    )
                )
            } else {
                partnerBudgetPerFund.add(
                    PartnerBudgetPerFund(
                        fund = fund,
                        value = BigDecimal.ZERO,
                        percentage = BigDecimal.ZERO
                    )
                )
            }
        }
        return partnerBudgetPerFund
    }

    private fun getPartnerContribution(
        partnerContributions: Collection<ProjectPartnerContribution>?,
        status: ProjectPartnerContributionStatusDTO?
    ): BigDecimal {
        if (partnerContributions.isNullOrEmpty()) {
            return BigDecimal.ZERO
        }

        if (status == null) {
            return partnerContributions.sumOf { it.amount!! }
        }

        val contribution = partnerContributions.filter { it.status == status }

        if (contribution.isNotEmpty()) {
            return contribution.sumOf { it.amount!! }
        }

        return BigDecimal.ZERO
    }

    private fun calculatePercentage(toDivide: BigDecimal, divisor: BigDecimal, roundingMode: RoundingMode): BigDecimal {
        if (toDivide == BigDecimal.ZERO || divisor == BigDecimal.ZERO || toDivide == BigDecimal(BigInteger("0"), 2) || divisor == BigDecimal(BigInteger("0"), 2)) {
            return BigDecimal.ZERO
        }

        return toDivide
            .divide(divisor, mc)
            .multiply(BigDecimal(100))
            .setScale(2, roundingMode)
    }
}
