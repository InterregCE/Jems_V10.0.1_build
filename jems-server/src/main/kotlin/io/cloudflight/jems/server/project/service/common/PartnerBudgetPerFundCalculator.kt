package io.cloudflight.jems.server.project.service.common

import io.cloudflight.jems.api.project.dto.partner.cofinancing.ProjectPartnerContributionStatusDTO
import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFund
import io.cloudflight.jems.server.project.service.cofinancing.model.PartnerBudgetCoFinancing
import io.cloudflight.jems.server.project.service.cofinancing.model.PartnerBudgetSpfCoFinancing
import io.cloudflight.jems.server.project.service.model.PartnerBudgetPerFund
import io.cloudflight.jems.server.project.service.model.ProjectPartnerBudgetPerFund
import io.cloudflight.jems.server.project.service.model.ProjectPartnerCostType
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerCoFinancing
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectContribution
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
        partners: List<ProjectPartnerSummary>,
        projectFunds: List<ProgrammeFund>,
        coFinancing: List<PartnerBudgetCoFinancing>,
        spfCoFinancing: List<PartnerBudgetSpfCoFinancing?>
    ): List<ProjectPartnerBudgetPerFund> {

        val totalEligibleBudgetManagement = coFinancing.sumOf { it.total!! }
        val totalEligibleBudgetSum =
            if (spfCoFinancing.any { it?.total != null }) {
                totalEligibleBudgetManagement.add(
                    spfCoFinancing
                        .filter { it?.total != null }
                        .sumOf { it?.total ?: BigDecimal.ZERO }
                )
            } else {
                totalEligibleBudgetManagement
            }

        // add partner lines - (management in case of SPF) costs
        val tableRowsExceptTotal = coFinancing.map {
            val coFinancingTotal = (it.total ?: BigDecimal.ZERO).setScale(2, RoundingMode.DOWN)
            val partnerContributions = it.projectPartnerCoFinancingAndContribution?.partnerContributions ?: emptyList()
            ProjectPartnerBudgetPerFund(
                partner = it.partner,
                costType = ProjectPartnerCostType.Management,
                budgetPerFund = getBudgetPerFundForPartner(
                    projectFunds,
                    it.projectPartnerCoFinancingAndContribution?.finances ?: emptyList(),
                    coFinancingTotal
                ),
                publicContribution = getPartnerContribution(partnerContributions, ProjectPartnerContributionStatusDTO.Public),
                autoPublicContribution = getPartnerContribution(partnerContributions, ProjectPartnerContributionStatusDTO.AutomaticPublic),
                privateContribution = getPartnerContribution(partnerContributions, ProjectPartnerContributionStatusDTO.Private),
                totalPartnerContribution = getPartnerContribution(partnerContributions, null),
                totalEligibleBudget = coFinancingTotal,
                percentageOfTotalEligibleBudget = calculatePercentage(coFinancingTotal, totalEligibleBudgetSum)
            )
        }.toMutableList()

        // add lines for SPF costs
        if (spfCoFinancing.any { it != null }) {
            spfCoFinancing.forEach {
                if (it != null) {
                    val spfCoFinancingTotal = (it.total ?: BigDecimal.ZERO).setScale(2, RoundingMode.DOWN)
                    val partnerContributions = it.projectPartnerCoFinancingAndContribution.partnerContributions
                    tableRowsExceptTotal.add(
                        ProjectPartnerBudgetPerFund(
                            partner = it.partner,
                            costType = ProjectPartnerCostType.Spf,
                            budgetPerFund = getBudgetPerFundForPartner(
                                projectFunds,
                                it.projectPartnerCoFinancingAndContribution.finances,
                                spfCoFinancingTotal
                            ),
                            publicContribution = getPartnerContribution(
                                partnerContributions,
                                ProjectPartnerContributionStatusDTO.Public
                            ),
                            autoPublicContribution = getPartnerContribution(
                                partnerContributions,
                                ProjectPartnerContributionStatusDTO.AutomaticPublic
                            ),
                            privateContribution = getPartnerContribution(
                                partnerContributions,
                                ProjectPartnerContributionStatusDTO.Private
                            ),
                            totalPartnerContribution = getPartnerContribution(partnerContributions, null),
                            totalEligibleBudget = spfCoFinancingTotal,
                            percentageOfTotalEligibleBudget = calculatePercentage(
                                spfCoFinancingTotal,
                                totalEligibleBudgetSum
                            )
                        )
                    )
                }
            }
        }

        val totalBudgetsPerFundTotal = projectFunds.map { fund ->
            val budgetPerFundTotalValue = tableRowsExceptTotal.sumOf { row ->
                row.budgetPerFund.firstOrNull { it.fund?.id == fund.id }?.value ?: BigDecimal.ZERO
            }
            PartnerBudgetPerFund(
                fund = fund,
                value = budgetPerFundTotalValue,
                percentage = calculatePercentage(
                    budgetPerFundTotalValue,
                    totalEligibleBudgetSum
                )
            )
        }
        // set total percentages for funds in previous rows
        setBudgetPerFundForPartnerTotal(budgetRows = tableRowsExceptTotal)

        // add summary row with totals
        tableRowsExceptTotal.add(
            ProjectPartnerBudgetPerFund(
                partner = null,
                costType = null,
                budgetPerFund = totalBudgetsPerFundTotal.toSet(),
                publicContribution = tableRowsExceptTotal.sumOf { it.publicContribution!! },
                autoPublicContribution = tableRowsExceptTotal.sumOf { it.autoPublicContribution!! },
                privateContribution = tableRowsExceptTotal.sumOf { it.privateContribution!! },
                totalPartnerContribution = tableRowsExceptTotal.sumOf { it.totalPartnerContribution!! },
                totalEligibleBudget = totalEligibleBudgetSum,
                percentageOfTotalEligibleBudget = BigDecimal(100)
            )
        )

        return tableRowsExceptTotal
    }

    private fun getBudgetPerFundForPartner(
        projectChosenFunds: List<ProgrammeFund>,
        coFinancing: List<ProjectPartnerCoFinancing>?,
        totalEligibleBudget: BigDecimal
    ): Set<PartnerBudgetPerFund> {
        val partnerBudgetPerFund = mutableSetOf<PartnerBudgetPerFund>()

        projectChosenFunds.forEach { fund ->
            val fundPercentage = coFinancing?.firstOrNull { it.fund?.id == fund.id }?.percentage
            val fundValue =
                if (fundPercentage != null) {
                    totalEligibleBudget
                        .multiply(fundPercentage.divide(BigDecimal(100)))
                        .setScale(2, RoundingMode.DOWN)
                } else {
                    BigDecimal.ZERO
                }
            partnerBudgetPerFund.add(
                PartnerBudgetPerFund(
                    fund = fund,
                    value = fundValue,
                    percentage = fundPercentage ?: BigDecimal.ZERO
                )
            )
        }
        return partnerBudgetPerFund
    }

    private fun setBudgetPerFundForPartnerTotal(
        budgetRows: MutableList<ProjectPartnerBudgetPerFund>
    ): List<ProjectPartnerBudgetPerFund> {
        budgetRows.forEach { row ->
            row.budgetPerFund.forEach { perFund ->
                val total = budgetRows.sumOf { rowForTotal ->
                    rowForTotal.budgetPerFund
                        .firstOrNull { it.fund?.id == perFund.fund?.id }?.value ?: BigDecimal.ZERO
                }.setScale(2, RoundingMode.DOWN)

                perFund.percentageOfTotal = calculatePercentage(perFund.value, total)
            }
        }
        return budgetRows
    }

    private fun getPartnerContribution(
        partnerContributions: Collection<ProjectContribution>?,
        status: ProjectPartnerContributionStatusDTO?
    ): BigDecimal {
        var partnerContribution = BigDecimal.ZERO
        if (!partnerContributions.isNullOrEmpty()) {
            if (status == null) {
                partnerContribution = partnerContributions.sumOf { it.amount!! }
            }
            val contribution = partnerContributions.filter { it.status == status }
            if (contribution.isNotEmpty()) {
                partnerContribution = contribution.sumOf { it.amount!! }
            }
        }
        return partnerContribution
    }

    private fun calculatePercentage(toDivide: BigDecimal, divisor: BigDecimal): BigDecimal {
        if (toDivide == BigDecimal.ZERO || divisor == BigDecimal.ZERO
            || toDivide == BigDecimal(BigInteger("0"), 2)
            || divisor == BigDecimal(BigInteger("0"), 2)) {
            return BigDecimal.ZERO
        }

        return toDivide
            .divide(divisor, mc)
            .multiply(BigDecimal(100))
            .setScale(2, RoundingMode.HALF_UP)
    }
}
