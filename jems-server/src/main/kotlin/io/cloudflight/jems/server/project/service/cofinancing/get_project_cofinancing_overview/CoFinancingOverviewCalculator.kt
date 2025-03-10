package io.cloudflight.jems.server.project.service.cofinancing.get_project_cofinancing_overview

import io.cloudflight.jems.api.project.dto.partner.cofinancing.ProjectPartnerContributionStatusDTO
import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFund
import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFundType
import io.cloudflight.jems.server.project.service.cofinancing.model.ProjectCoFinancingByFundOverview
import io.cloudflight.jems.server.project.service.cofinancing.model.ProjectCoFinancingCategoryOverview
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectCoFinancingAndContribution
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectContribution
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerContributionStatus
import java.math.BigDecimal
import java.math.RoundingMode

class CoFinancingOverviewCalculator {

    companion object {
        fun calculateCoFinancingOverview(
            partnerIds: Set<Long>,
            getBudgetTotalCost: (Long) -> BigDecimal,
            getCoFinancingAndContributions: (Long) -> ProjectCoFinancingAndContribution,
            funds: Set<ProgrammeFund>,
        ): ProjectCoFinancingCategoryOverview {
            if (partnerIds.isEmpty()) {
                return ProjectCoFinancingCategoryOverview()
            }
            val partnerTotals = partnerIds.associateWith { getBudgetTotalCost.invoke(it) }
            val partnerCoFinancing = partnerIds.associateWith { getCoFinancingAndContributions.invoke(it) }

            val fundOverviews = getFundOverviews(funds, partnerCoFinancing, partnerTotals)
            val euFundOverviews = fundOverviews.filter { it.fundType != ProgrammeFundType.OTHER }
            val totalEuFundingAmount = euFundOverviews.map { it.fundingAmount }.sumUp()
            val totalEuContribution = euFundOverviews.map { it.totalContribution }.sumUp()
            val totalEuFundAndContribution = listOf(totalEuFundingAmount, totalEuContribution).sumUp()
            val totalFundingAmount = fundOverviews.map { it.fundingAmount }.sumUp()
            val totalFundAndContribution = partnerTotals.values.sumUp()

            return ProjectCoFinancingCategoryOverview(
                fundOverviews = fundOverviews,
                totalFundingAmount = fundOverviews.map { it.fundingAmount }.sumUp(),
                totalEuFundingAmount = totalEuFundingAmount,
                averageCoFinancingRate = divide(totalFundingAmount.multiply(BigDecimal(100)), totalFundAndContribution),
                averageEuFinancingRate = divide(totalEuFundingAmount.multiply(BigDecimal(100)), totalEuFundAndContribution),
                totalAutoPublicContribution = fundOverviews.map { it.autoPublicContribution }.sumUp(),
                totalEuAutoPublicContribution = euFundOverviews.map { it.autoPublicContribution }.sumUp(),
                totalOtherPublicContribution = fundOverviews.map { it.otherPublicContribution }.sumUp(),
                totalEuOtherPublicContribution = euFundOverviews.map { it.otherPublicContribution }.sumUp(),
                totalPublicContribution = fundOverviews.map { it.totalPublicContribution }.sumUp(),
                totalEuPublicContribution = euFundOverviews.map { it.totalPublicContribution }.sumUp(),
                totalPrivateContribution = fundOverviews.map { it.privateContribution }.sumUp(),
                totalEuPrivateContribution = euFundOverviews.map { it.privateContribution }.sumUp(),
                totalContribution = fundOverviews.map { it.totalContribution }.sumUp(),
                totalEuContribution = totalEuContribution,
                totalFundAndContribution = totalFundAndContribution,
                totalEuFundAndContribution = totalEuFundAndContribution,
            )
        }

        private fun getFundOverviews(
            funds: Set<ProgrammeFund>,
            partnerCoFinancing: Map<Long, ProjectCoFinancingAndContribution>,
            partnerTotals: Map<Long, BigDecimal>
        ): List<ProjectCoFinancingByFundOverview> {
            return funds.map {
                val entriesByFund = partnerCoFinancing.entries
                    .filter { entry -> entry.value.finances.map { finance -> finance.fund?.id }.contains(it.id) }
                val fundingAmount = entriesByFund
                    .map { entry ->
                        val percentage = entry.value.finances.find { finance -> finance.fund?.id == it.id }!!.percentage
                        partnerTotals[entry.key]?.divide(BigDecimal(100))?.multiply(percentage)?.setScale(2, RoundingMode.DOWN) ?: BigDecimal.ZERO
                    }
                    .sumUp()

                val contributionsByFund = entriesByFund.flatMap { entry -> entry.value.partnerContributions }
                val autoPublic =
                    getContributionTotal(contributionsByFund, ProjectPartnerContributionStatus.AutomaticPublic)
                val otherPublic = getContributionTotal(contributionsByFund, ProjectPartnerContributionStatus.Public)
                val private = getContributionTotal(contributionsByFund, ProjectPartnerContributionStatus.Private)
                val totalContribution = listOf(autoPublic, otherPublic, private).sumUp()
                val totalFundAndContribution = listOf(fundingAmount, totalContribution).sumUp()

                ProjectCoFinancingByFundOverview(
                    fundId = it.id,
                    fundType = it.type,
                    fundAbbreviation = it.abbreviation,
                    fundingAmount = fundingAmount,
                    coFinancingRate = divide(fundingAmount.multiply(BigDecimal(100)), totalFundAndContribution),
                    autoPublicContribution = autoPublic,
                    otherPublicContribution = otherPublic,
                    totalPublicContribution = listOf(autoPublic, otherPublic).sumUp(),
                    privateContribution = private,
                    totalContribution = totalContribution,
                    totalFundAndContribution = totalFundAndContribution,
                )
            }
        }

        private fun getContributionTotal(
            contributions: List<ProjectContribution>,
            status: ProjectPartnerContributionStatus
        ): BigDecimal = contributions
            .filter { contribution -> contribution.amount != null && contribution.status == status }
            .map { it.amount ?: BigDecimal.ZERO }
            .sumUp()


        private fun Collection<BigDecimal>.sumUp() = fold(BigDecimal.ZERO) { first, second -> first.add(second) }

        private fun divide(divisor: BigDecimal, dividend: BigDecimal): BigDecimal =
            if (dividend.compareTo(BigDecimal.ZERO) == 0)
                BigDecimal.ZERO
            else divisor.divide(dividend, 2, RoundingMode.HALF_DOWN)

    }

}
