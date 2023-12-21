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
    spfLine = false,

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

fun List<FinancingSourceBreakdownLine>.sumUp(): FinancingSourceBreakdownLine  {

    val financingSourcesTotal = fold(emptySumUp) { first, second -> first.plus(second) }

    return  FinancingSourceBreakdownLine(
        partnerReportId = null,
        partnerReportNumber = null,
        spfLine = false,

        partnerId = null,
        partnerRole = null,
        partnerNumber = null,

        fundsSorted = financingSourcesTotal.fundsSorted,

        partnerContribution = financingSourcesTotal.partnerContribution,
        publicContribution = financingSourcesTotal.publicContribution,
        automaticPublicContribution = financingSourcesTotal.automaticPublicContribution,
        privateContribution = financingSourcesTotal.privateContribution,
        total = financingSourcesTotal.total,
        split = emptyList(),
    )
}


private fun FinancingSourceBreakdownLine.plus(other: FinancingSourceBreakdownLine) =
    FinancingSourceBreakdownLine(
        partnerReportId = null,
        partnerReportNumber = null,
        spfLine = false,

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
        spfLine = false,

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
    availableFundsResolver: (Long) -> List<ProgrammeFund>,
    partnerReportFinancialDataResolver: (Long) -> PartnerReportFinancialData,
): MutableList<FinancingSourceBreakdownLine> {
    val expendituresByCertificate = verification.groupBy { it.expenditure.partnerReportId }
    val certificateLines = expendituresByCertificate.map { (certificateId, verifications) ->
        val reportFinancialData = partnerReportFinancialDataResolver.invoke(certificateId)

        val certificateTotal = verifications.calculateAfterVerification(reportFinancialData.flatRatesFromAF).sum
        val splitForCertificateLine = reportFinancialData.splitThisValue(certificateTotal)
        val fundIdsAvailable = splitForCertificateLine.funds.mapNotNull { it.key }

        val certificateAvailableFunds = availableFundsResolver.invoke(certificateId)

        val fundsSorted = certificateAvailableFunds.map { Pair(it, splitForCertificateLine.funds.getOrDefault(it.id, BigDecimal.ZERO)) }
            .filter { it.first.id in fundIdsAvailable }
        val expenditure = verifications.first().expenditure

        return@map FinancingSourceBreakdownLine(
            partnerReportId = certificateId,
            partnerReportNumber = expenditure.partnerReportNumber,
            spfLine = false,
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
        ).fillInAdditionalSplitsForEachFund(
            reportFinancialData,
            splitForCertificateLine.sum,
            splitForCertificateLine.partnerContribution
        )
    }.toMutableList()
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
    split = fundsSorted.map {
        val allFundsTotal = certificateTotal.minus(partnerContribution)

        if ( it.second.compareTo(BigDecimal.ZERO) == 0 ) {
            return@map it.toZeroSplitLine()
        }

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

fun FinancingSourceBreakdownLine.fillInAdditionalSplitsForSpf() = this.apply {
    if (fundsSorted.isEmpty())
        return@apply

    val allFundsTotal = fundsSorted.sumOf { it.second }
    if (allFundsTotal.isZero()) {
        split = listOf(
            FinancingSourceBreakdownSplitLine(
                fundId = fundsSorted.first().first.id,
                value = fundsSorted.first().second,
                partnerContribution = partnerContribution,
                publicContribution = publicContribution,
                automaticPublicContribution = automaticPublicContribution,
                privateContribution = privateContribution,
                total = fundsSorted.first().second.plus(partnerContribution),
            )
        )
        return@apply
    }

    split = fundsSorted.map {
        val percentagePartOfFunds = it.second.divide(allFundsTotal, 21, RoundingMode.DOWN).multiply(BigDecimal.valueOf(100))
        return@map FinancingSourceBreakdownSplitLine(
            fundId = it.first.id,
            value = it.second,
            partnerContribution = partnerContribution.applyPercentage(percentagePartOfFunds),
            publicContribution = publicContribution.applyPercentage(percentagePartOfFunds),
            automaticPublicContribution = automaticPublicContribution.applyPercentage(percentagePartOfFunds),
            privateContribution = privateContribution.applyPercentage(percentagePartOfFunds),
            total = it.second.plus(partnerContribution.applyPercentage(percentagePartOfFunds)),
        )
    }
}

private fun Pair<ProgrammeFund, BigDecimal>.toZeroSplitLine() = FinancingSourceBreakdownSplitLine(
    fundId = this.first.id,
    value = this.second,
    partnerContribution = BigDecimal.ZERO,
    publicContribution = BigDecimal.ZERO,
    automaticPublicContribution = BigDecimal.ZERO,
    privateContribution = BigDecimal.ZERO,
    total = BigDecimal.ZERO,
)

fun BigDecimal.isZero() = this.compareTo(BigDecimal.ZERO) == 0
