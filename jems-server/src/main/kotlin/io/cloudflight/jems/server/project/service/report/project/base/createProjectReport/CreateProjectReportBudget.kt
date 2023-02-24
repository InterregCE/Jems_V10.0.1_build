package io.cloudflight.jems.server.project.service.report.project.base.createProjectReport

import io.cloudflight.jems.server.payments.service.regular.PaymentRegularPersistence
import io.cloudflight.jems.server.payments.model.regular.PaymentToProject
import io.cloudflight.jems.server.project.repository.report.project.coFinancing.ProjectReportCertificateCoFinancingPersistenceProvider
import io.cloudflight.jems.server.project.service.budget.get_project_budget.GetProjectBudget
import io.cloudflight.jems.server.project.service.lumpsum.ProjectLumpSumPersistence
import io.cloudflight.jems.server.project.service.lumpsum.model.ProjectLumpSum
import io.cloudflight.jems.server.project.service.model.ProjectPartnerBudgetPerFund
import io.cloudflight.jems.server.project.service.report.model.project.base.create.PreviouslyProjectReportedCoFinancing
import io.cloudflight.jems.server.project.service.report.model.project.base.create.PreviouslyProjectReportedFund
import io.cloudflight.jems.server.project.service.report.model.project.base.create.ProjectReportBudget
import io.cloudflight.jems.server.project.service.report.model.project.financialOverview.coFinancing.ReportCertificateCoFinancingColumn
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.getReportCoFinancingBreakdown.applyPercentage
import io.cloudflight.jems.server.project.service.report.project.base.ProjectReportPersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.math.BigDecimal.ZERO
import java.math.RoundingMode
import java.util.LinkedList

@Service
class CreateProjectReportBudget(
    private val reportPersistence: ProjectReportPersistence,
    private val lumpSumPersistence: ProjectLumpSumPersistence,
    private val getProjectBudget: GetProjectBudget,
    private val reportCertificateCoFinancingPersistence: ProjectReportCertificateCoFinancingPersistenceProvider,
    private val paymentPersistence: PaymentRegularPersistence,
) {

    @Transactional
    fun retrieveBudgetDataFor(
        projectId: Long,
        version: String?,
        totalsFromAF: ProjectPartnerBudgetPerFund
    ): ProjectReportBudget {
        val submittedReportIds = reportPersistence.getSubmittedProjectReportIds(projectId = projectId)

        val budget = getProjectBudget.getBudget(projectId, version)

        val lumpSums = lumpSumPersistence.getLumpSums(projectId, version = version)
        val sumOfPaymentReady = lumpSums.sumOfPaymentReady()

        val installmentsPaid = paymentPersistence.getPaymentsByProjectId(projectId)

        return ProjectReportBudget(
            previouslyReportedCoFinancing = reportCertificateCoFinancingPersistence
                .getCoFinancingCumulative(submittedReportIds)
                .toCreateModel(
                    totalsFromAF = totalsFromAF,
                    total = budget.sumOf { it.totalCosts },
                    paymentReadyFastTrackLumpSums = sumOfPaymentReady,
                    paymentPaid = installmentsPaid.byFund(),
                ),
        )
    }

    private fun ProjectLumpSum.isReady() = fastTrack && readyForPayment

    private fun ReportCertificateCoFinancingColumn.toCreateModel(
        totalsFromAF: ProjectPartnerBudgetPerFund,
        total: BigDecimal,
        paymentReadyFastTrackLumpSums: BigDecimal,
        paymentPaid: Map<Long, BigDecimal>,
    ): PreviouslyProjectReportedCoFinancing {
        val totals = totalsFromAF.budgetPerFund.filter{ it.fund !== null }
            .associateBy({ it.fund?.id }, { it.value })
            .toMutableMap()

        // partner contribution = total - all funds
        totals[null] = total.minus(totals.values.sumOf { it })

        val currentFunds = totalsFromAF.budgetPerFund.filter{ it.fund !== null }.mapTo(LinkedList()) {
            PreviouslyProjectReportedFund(
                fundId = it.fund?.id,
                percentage = it.percentage,
                total = totals[it.fund?.id]!!,
                previouslyReported = funds.getOrDefault(it.fund?.id, ZERO),
                previouslyPaid = paymentPaid.getOrDefault(it.fund?.id, ZERO),
            )
        }

        // in case in modification some funds have been removed, we still need it in reporting
        val removedFunds = funds.mapNotNullTo(LinkedHashSet()) { it.key }.minus(
            currentFunds.mapNotNullTo(HashSet()) { it.fundId }
        ).map { fundId ->
            PreviouslyProjectReportedFund(
                fundId = fundId,
                percentage = ZERO,
                total = ZERO,
                previouslyReported = funds[fundId]!!,
                previouslyPaid = paymentPaid.getOrDefault(fundId, ZERO),
            )
        }
        currentFunds.addAll(maxOf(currentFunds.size - 1, 0), removedFunds) /* insert removed funds before partner contribution */

        // if co-financing has not been filled-in in Application Form, mock Partner contribution line
        if (currentFunds.isEmpty())
            currentFunds.add(
                PreviouslyProjectReportedFund(
                    fundId = null,
                    percentage = BigDecimal.valueOf(100),
                    total = ZERO,
                    previouslyReported = funds.getOrDefault(null, ZERO),
                    previouslyPaid = ZERO,
                )
            )

        val publicTotalAmount = totalsFromAF.publicContribution ?: ZERO
        val autoPublicTotalAmount = totalsFromAF.autoPublicContribution ?: ZERO
        val privateTotalAmount = totalsFromAF.privateContribution ?: ZERO

        val partnerContributionsFund = PreviouslyProjectReportedFund(
            fundId = null,
            percentage = BigDecimal.valueOf(100).minus(currentFunds.sumOf { it.percentage }),
            total = publicTotalAmount.plus(autoPublicTotalAmount).plus(privateTotalAmount),
            previouslyReported = partnerContribution,
            previouslyPaid = ZERO
        )

        // calculate lump sums values to be added from payments
        val currentFundsPercentages = currentFunds.associateBy ({it.fundId}, {it.percentage })
        val partnerContributionPercentage = BigDecimal.valueOf(100).minus(currentFunds.sumOf { it.percentage })

        val currentLumpSumValues = ReportCertificateCoFinancingColumn(
            funds = currentFundsPercentages.mapValues { fundPercentage -> paymentReadyFastTrackLumpSums.applyPercentage(fundPercentage.value) }
                .plus(Pair(null, paymentReadyFastTrackLumpSums.applyPercentage(partnerContributionPercentage))),
            partnerContribution = paymentReadyFastTrackLumpSums.applyPercentage(partnerContributionPercentage),
            publicContribution = paymentReadyFastTrackLumpSums.applyPercentage(publicTotalAmount.getPercentageOf(total)),
            automaticPublicContribution = paymentReadyFastTrackLumpSums.applyPercentage(autoPublicTotalAmount.getPercentageOf(total)),
            privateContribution = paymentReadyFastTrackLumpSums.applyPercentage(privateTotalAmount.getPercentageOf(total)),
            sum = paymentReadyFastTrackLumpSums
        )

        //add one extra fund to generate the partner contribution total
        currentFunds.add(partnerContributionsFund)

        return PreviouslyProjectReportedCoFinancing(
            fundsSorted = currentFunds,

            totalPartner = publicTotalAmount.plus(autoPublicTotalAmount).plus(privateTotalAmount),
            totalPublic = publicTotalAmount,
            totalAutoPublic = autoPublicTotalAmount,
            totalPrivate = privateTotalAmount,
            totalSum = total,

            previouslyReportedPartner = partnerContribution,
            previouslyReportedPublic = publicContribution,
            previouslyReportedAutoPublic = automaticPublicContribution,
            previouslyReportedPrivate = privateContribution,
            previouslyReportedSum = sum
        ).addExtraLumpSumValues(currentLumpSumValues)
    }

    private fun List<PaymentToProject>.byFund() =
        groupBy { it.fundId }
            .mapValues { (_, payments) -> payments.sumOf { it.amountPaidPerFund } }

    private fun Collection<ProjectLumpSum>.onlyReadyForPayment() = filter { it.isReady() }

    private fun Collection<ProjectLumpSum>.sumOfPaymentReady(): BigDecimal =
        onlyReadyForPayment()
            .flatMap { it.lumpSumContributions }
            .sumOf { it.amount }

    private fun PreviouslyProjectReportedCoFinancing.addExtraLumpSumValues(
        paymentLumpSums: ReportCertificateCoFinancingColumn
    ): PreviouslyProjectReportedCoFinancing {
        return this.copy(
            fundsSorted = fundsSorted.mergeWith(otherFundSums = paymentLumpSums.funds),
            previouslyReportedPartner = previouslyReportedPartner.plus(paymentLumpSums.partnerContribution),
            previouslyReportedPublic = previouslyReportedPublic.plus(paymentLumpSums.publicContribution),
            previouslyReportedAutoPublic = previouslyReportedAutoPublic.plus(paymentLumpSums.automaticPublicContribution),
            previouslyReportedPrivate = previouslyReportedPrivate.plus(paymentLumpSums.privateContribution),
            previouslyReportedSum = previouslyReportedSum.plus(paymentLumpSums.sum),
        )
    }

    private fun List<PreviouslyProjectReportedFund>.mergeWith(otherFundSums: Map<Long?, BigDecimal>) = map { previouslyReportedFund ->
        previouslyReportedFund.copy(
            previouslyReported = previouslyReportedFund.previouslyReported
                .plus(otherFundSums.getOrDefault(previouslyReportedFund.fundId, ZERO))
        ) }

    private fun BigDecimal.getPercentageOf(total: BigDecimal) =
        if (total.compareTo(ZERO) == 0)
            ZERO
        else
            this.multiply(BigDecimal.valueOf(100))
                .divide(total, 17, RoundingMode.DOWN)

}
