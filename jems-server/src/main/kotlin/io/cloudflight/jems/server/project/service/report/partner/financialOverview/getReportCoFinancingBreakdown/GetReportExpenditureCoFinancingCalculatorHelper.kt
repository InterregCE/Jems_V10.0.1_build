package io.cloudflight.jems.server.project.service.report.partner.financialOverview.getReportCoFinancingBreakdown

import io.cloudflight.jems.api.project.dto.partner.cofinancing.ProjectPartnerCoFinancingFundTypeDTO.MainFund
import io.cloudflight.jems.api.project.dto.partner.cofinancing.ProjectPartnerCoFinancingFundTypeDTO.PartnerContribution
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerCoFinancing
import io.cloudflight.jems.server.project.service.report.model.contribution.ProjectPartnerReportContributionOverview
import io.cloudflight.jems.server.project.service.report.model.financialOverview.coFinancing.*
import java.math.BigDecimal
import java.math.RoundingMode

fun ReportExpenditureCoFinancing.toLinesModel() = ExpenditureCoFinancingBreakdown(
    funds = totalsFromAF.funds.map {
        ExpenditureCoFinancingBreakdownLine(
            fundId = it.key,
            totalEligibleBudget = it.value,
            previouslyReported = previouslyReported.funds.getOrDefault(it.key, BigDecimal.ZERO),
            currentReport = currentlyReported.funds.getOrDefault(it.key, BigDecimal.ZERO),
        )
    },
    partnerContribution = ExpenditureCoFinancingBreakdownLine(
        totalEligibleBudget = totalsFromAF.partnerContribution,
        previouslyReported = previouslyReported.partnerContribution,
        currentReport = currentlyReported.partnerContribution,
    ),
    publicContribution = ExpenditureCoFinancingBreakdownLine(
        totalEligibleBudget = totalsFromAF.publicContribution,
        previouslyReported = previouslyReported.publicContribution,
        currentReport = currentlyReported.publicContribution,
    ),
    automaticPublicContribution = ExpenditureCoFinancingBreakdownLine(
        totalEligibleBudget = totalsFromAF.automaticPublicContribution,
        previouslyReported = previouslyReported.automaticPublicContribution,
        currentReport = currentlyReported.automaticPublicContribution,
    ),
    privateContribution = ExpenditureCoFinancingBreakdownLine(
        totalEligibleBudget = totalsFromAF.privateContribution,
        previouslyReported = previouslyReported.privateContribution,
        currentReport = currentlyReported.privateContribution,
    ),
    total = ExpenditureCoFinancingBreakdownLine(
        totalEligibleBudget = totalsFromAF.sum,
        previouslyReported = previouslyReported.sum,
        currentReport = currentlyReported.sum,
    ),
)

fun ExpenditureCoFinancingBreakdown.fillInCurrent(current: ReportExpenditureCoFinancingColumn) = apply {
    funds.forEach { fund ->
        fund.currentReport = current.funds.getOrDefault(fund.fundId, BigDecimal.ZERO)
    }
    partnerContribution.currentReport = current.partnerContribution
    publicContribution.currentReport = current.publicContribution
    automaticPublicContribution.currentReport = current.automaticPublicContribution
    privateContribution.currentReport = current.privateContribution
    total.currentReport = current.sum
}

fun ExpenditureCoFinancingBreakdown.fillInOverviewFields() = apply {
    funds.forEach { it.fillInOverviewFields() }
    partnerContribution.fillInOverviewFields()
    publicContribution.fillInOverviewFields()
    automaticPublicContribution.fillInOverviewFields()
    privateContribution.fillInOverviewFields()
    total.fillInOverviewFields()
}

private fun ExpenditureCoFinancingBreakdownLine.fillInOverviewFields() = apply {
    totalReportedSoFar = previouslyReported.plus(currentReport)
    totalReportedSoFarPercentage = if (totalEligibleBudget.compareTo(BigDecimal.ZERO) == 0) BigDecimal.ZERO else
        totalReportedSoFar.multiply(BigDecimal.valueOf(100)).divide(totalEligibleBudget, 2, RoundingMode.HALF_UP)
    remainingBudget = totalEligibleBudget.minus(totalReportedSoFar)
}

fun BigDecimal.applyPercentage(percentage: BigDecimal, roundingMode: RoundingMode = RoundingMode.DOWN): BigDecimal = this.multiply(
    percentage.divide(BigDecimal.valueOf(100))
).setScale(2, roundingMode)

fun getCurrentFrom(input: ReportExpenditureCoFinancingCalculationInput): ReportExpenditureCoFinancingColumn {
    with(input) {
        val partnerContrib = currentTotal.applyPercentage(partnerContributionPercentage)

        return ReportExpenditureCoFinancingColumn(
            // main funds + partner contribution
            funds = fundsPercentages.mapValues { fundPercentage -> currentTotal.applyPercentage(fundPercentage.value) }
                .plus(Pair(null, partnerContrib)),

            partnerContribution = currentTotal.applyPercentage(partnerContributionPercentage),
            publicContribution = currentTotal.applyPercentage(publicPercentage),
            automaticPublicContribution = currentTotal.applyPercentage(automaticPublicPercentage),
            privateContribution = currentTotal.applyPercentage(privatePercentage),
            sum = currentTotal,
        )
    }
}

/**
 * Will calculate percentages, which are then applied to split current total value between contribution types
 */
fun ProjectPartnerReportContributionOverview.generateCoFinCalculationInputData(
    totalEligibleBudget: BigDecimal,
    currentExpenditure: BigDecimal,
    funds: List<ProjectPartnerCoFinancing>,
): ReportExpenditureCoFinancingCalculationInput =
    ReportExpenditureCoFinancingCalculationInput(
        currentTotal = currentExpenditure,
        fundsPercentages = funds.getMainFunds().associateBy({ it.fund!!.id }, { it.percentage}),
        partnerContributionPercentage = funds.getPartnerContributionPercentage(),
        publicPercentage = public.amount.getPercentageOf(totalEligibleBudget),
        automaticPublicPercentage = automaticPublic.amount.getPercentageOf(totalEligibleBudget),
        privatePercentage = private.amount.getPercentageOf(totalEligibleBudget),
    )

private fun BigDecimal.getPercentageOf(total: BigDecimal) = this.divide(total, 4, RoundingMode.DOWN)
    .multiply(BigDecimal.valueOf(100))

private fun List<ProjectPartnerCoFinancing>.getMainFunds() = filter { it.fundType == MainFund }

private fun List<ProjectPartnerCoFinancing>.getPartnerContributionPercentage() =
    firstOrNull { it.fundType == PartnerContribution }?.percentage ?: BigDecimal.ZERO
