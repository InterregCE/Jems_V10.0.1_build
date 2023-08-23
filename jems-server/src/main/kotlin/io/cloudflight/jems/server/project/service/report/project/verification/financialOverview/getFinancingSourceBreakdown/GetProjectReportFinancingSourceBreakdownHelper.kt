package io.cloudflight.jems.server.project.service.report.project.verification.financialOverview.getFinancingSourceBreakdown

import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFund
import io.cloudflight.jems.server.project.service.budget.model.BudgetCostsCalculationResultFull
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerBudgetOptions
import io.cloudflight.jems.server.project.service.report.model.project.financialOverview.coFinancing.ReportCertificateCoFinancingColumn
import io.cloudflight.jems.server.project.service.report.model.project.verification.expenditure.ProjectReportVerificationExpenditureLine
import io.cloudflight.jems.server.project.service.report.model.project.verification.financialOverview.financingSource.FinancingSourceBreakdownLine
import io.cloudflight.jems.server.project.service.report.model.project.verification.financialOverview.financingSource.FinancingSourceBreakdownSplitLine
import io.cloudflight.jems.server.project.service.report.model.project.verification.financialOverview.financingSource.PartnerReportFinancialData
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.getReportCoFinancingBreakdown.GetReportExpenditureCoFinancingBreakdownCalculator
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.getReportCoFinancingBreakdown.applyPercentage
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.getReportExpenditureBreakdown.calculateCostCategoriesFor
import java.math.BigDecimal
import java.math.RoundingMode

private val emptySumUp = FinancingSourceBreakdownLine(
    partnerReportId = null,
    partnerReportNumber = null,

    partnerId = null,
    partnerRole = null,
    partnerNumber = null,

    fundsSorted = emptyList(),

    partnerContribution = BigDecimal.ZERO,
    publicContribution = BigDecimal.ZERO,
    automaticPublicContribution = BigDecimal.ZERO,
    privateContribution = BigDecimal.ZERO,
    total = BigDecimal.ZERO,
    split = emptyList(),
)

fun List<FinancingSourceBreakdownLine>.sumUp(): FinancingSourceBreakdownLine =
    fold(emptySumUp) { first, second -> first.plus(second) }

private fun FinancingSourceBreakdownLine.plus(other: FinancingSourceBreakdownLine) =
    FinancingSourceBreakdownLine(
        partnerReportId = null,
        partnerReportNumber = null,

        partnerId = null,
        partnerRole = null,
        partnerNumber = null,

        fundsSorted = fundsSorted.mergeWith(other.fundsSorted),

        partnerContribution = partnerContribution.plus(other.partnerContribution),
        publicContribution = publicContribution.plus(other.publicContribution),
        automaticPublicContribution = automaticPublicContribution.plus(other.automaticPublicContribution),
        privateContribution = privateContribution.plus(other.privateContribution),
        total = total.plus(other.total),
        split = emptyList(),
    )

private fun List<Pair<ProgrammeFund, BigDecimal>>.mergeWith(
    other: List<Pair<ProgrammeFund, BigDecimal>>
): List<Pair<ProgrammeFund, BigDecimal>> {
    val firstByFundId = this.associateBy { it.first.id }
    val secondByFundId = other.associateBy { it.first.id }

    val sortedKeys = (firstByFundId.keys union secondByFundId.keys).sorted()

    return sortedKeys.map {
        Pair(
            firstByFundId[it]?.first ?: secondByFundId[it]!!.first,
            sum(firstByFundId[it]?.second, secondByFundId[it]?.second),
        )
    }
}

private fun sum(x: BigDecimal?, y: BigDecimal?): BigDecimal {
    return (x ?: BigDecimal.ZERO).plus(y ?: BigDecimal.ZERO)
}

fun ReportCertificateCoFinancingColumn.toTotalLine(availableFunds: List<ProgrammeFund>) =
    FinancingSourceBreakdownLine(
        partnerReportId = null,
        partnerReportNumber = null,

        partnerId = null,
        partnerRole = null,
        partnerNumber = null,

        fundsSorted = availableFunds.map { Pair(it, funds[it.id] ?: BigDecimal.ZERO) },

        partnerContribution = partnerContribution,
        publicContribution = publicContribution,
        automaticPublicContribution = automaticPublicContribution,
        privateContribution = privateContribution,
        total = sum,
        split = emptyList(),
    )

fun Collection<ProjectReportVerificationExpenditureLine>.calculateAfterVerification(options: ProjectPartnerBudgetOptions): BudgetCostsCalculationResultFull =
    calculateCostCategoriesFor(options) { it.amountAfterVerification }

fun calculateSourcesAndSplits(
    verification: List<ProjectReportVerificationExpenditureLine>,
    availableFunds: List<ProgrammeFund>,
    partnerReportFinancialDataResolver: (Long) -> PartnerReportFinancialData,
): List<FinancingSourceBreakdownLine> {
    val expendituresByCertificate = verification.groupBy { it.expenditure.partnerReportId }
    val certificateLines = expendituresByCertificate.map { (certificateId, verifications) ->
        val reportFinancialData = partnerReportFinancialDataResolver.invoke(certificateId)

        val certificateTotal = verifications.calculateAfterVerification(reportFinancialData.flatRatesFromAF).sum
        val splitForCertificateLine = reportFinancialData.splitThisValue(certificateTotal)

        val fundsSorted = availableFunds.map { Pair(it, splitForCertificateLine.funds.getOrDefault(it.id, BigDecimal.ZERO)) }
        val expenditure = verifications.first().expenditure

        val certificateLine = FinancingSourceBreakdownLine(
            partnerReportId = certificateId,
            partnerReportNumber = expenditure.partnerReportNumber,
            partnerId = expenditure.partnerId,
            partnerRole = expenditure.partnerRole,
            partnerNumber = expenditure.partnerNumber,
            fundsSorted = fundsSorted,
            partnerContribution = splitForCertificateLine.partnerContribution,
            publicContribution = splitForCertificateLine.publicContribution,
            automaticPublicContribution = splitForCertificateLine.automaticPublicContribution,
            privateContribution = splitForCertificateLine.privateContribution,
            total = splitForCertificateLine.sum,
            split = emptyList(),
        )

        if (certificateLine.fundsSorted.size > 1)
            certificateLine.fillInAdditionalSplitsForEachFund(reportFinancialData, splitForCertificateLine.sum, splitForCertificateLine.partnerContribution)

        return@map certificateLine
    }
    return certificateLines
}

private fun PartnerReportFinancialData.splitThisValue(valueToSplit: BigDecimal) = GetReportExpenditureCoFinancingBreakdownCalculator.split(
    toSplit = valueToSplit,
    contributions = contributionsFromAF,
    funds = coFinancingFromAF,
    total = totalEligibleBudgetFromAF,
)

private fun FinancingSourceBreakdownLine.fillInAdditionalSplitsForEachFund(
    reportFinancialData: PartnerReportFinancialData,
    certificateTotal: BigDecimal,
    partnerContribution: BigDecimal,
) = this.apply {
    split = fundsSorted.mapNotNull {
        val allFundsTotal = certificateTotal.minus(partnerContribution)
        if (it.second.compareTo(BigDecimal.ZERO) == 0 || allFundsTotal.compareTo(BigDecimal.ZERO) == 0)
            return@mapNotNull null

        val percentagePartOfFunds = it.second.divide(allFundsTotal, 21, RoundingMode.DOWN).multiply(BigDecimal.valueOf(100))
        val totalForFund = certificateTotal.applyPercentage(percentagePartOfFunds, RoundingMode.HALF_UP)

        val afterVerificationSplitForThisFund = GetReportExpenditureCoFinancingBreakdownCalculator.split(
            toSplit = totalForFund,
            contributions = reportFinancialData.contributionsFromAF,
            funds = reportFinancialData.coFinancingFromAF,
            total = reportFinancialData.totalEligibleBudgetFromAF,
        )

        FinancingSourceBreakdownSplitLine(
            fundId = it.first.id,
            value = it.second,
            partnerContribution = totalForFund.minus(it.second),
            publicContribution = afterVerificationSplitForThisFund.publicContribution,
            automaticPublicContribution = afterVerificationSplitForThisFund.automaticPublicContribution,
            privateContribution = afterVerificationSplitForThisFund.privateContribution,
            total = totalForFund,
        )
    }
}
