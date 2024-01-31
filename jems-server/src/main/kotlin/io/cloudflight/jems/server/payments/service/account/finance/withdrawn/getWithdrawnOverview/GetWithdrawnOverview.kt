package io.cloudflight.jems.server.payments.service.account.finance.withdrawn.getWithdrawnOverview

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.payments.authorization.CanRetrievePaymentsAccount
import io.cloudflight.jems.server.payments.model.account.finance.withdrawn.AmountWithdrawnPerPriority
import io.cloudflight.jems.server.payments.model.account.finance.withdrawn.AmountWithdrawnPerYear
import io.cloudflight.jems.server.payments.model.account.finance.withdrawn.CorrectionAmountWithdrawn
import io.cloudflight.jems.server.payments.model.ec.AccountingYear
import io.cloudflight.jems.server.payments.service.account.PaymentAccountFinancePersistence
import io.cloudflight.jems.server.payments.service.account.PaymentAccountPersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

@Service
class GetWithdrawnOverview(
    private val persistence: PaymentAccountPersistence,
    private val financePersistence: PaymentAccountFinancePersistence,
) : GetWithdrawnOverviewInteractor {

    @CanRetrievePaymentsAccount
    @Transactional(readOnly = true)
    @ExceptionWrapper(GetWithdrawnOverviewException::class)
    override fun getWithdrawnOverview(paymentAccountId: Long): List<AmountWithdrawnPerPriority> {
        val account = persistence.getByPaymentAccountId(paymentAccountId)

        val corrections = financePersistence.getCorrectionsOnlyDeductionsAndNonClericalMistakeAndOnlyFinished(
            fundId = account.fund.id,
            accountingYearId = account.accountingYear.id,
        )

        return corrections
            .groupByPriorityAndYear()
            .calculateSumByYear()
            .fillInPerPrioritySums()
    }

    private fun Collection<CorrectionAmountWithdrawn>.total() = sumOf { it.total }
    private fun Collection<CorrectionAmountWithdrawn>.public() = sumOf { it.public }

    private fun Collection<CorrectionAmountWithdrawn>.onlyAaAudits() =
        filter { it.controllingBody.isAaAudit() }

    private fun Collection<CorrectionAmountWithdrawn>.onlyEcOrEcaOrOlaf() =
        filter { it.controllingBody.isEcOrEcaOrOlafInvestigation() }

    private fun Iterable<CorrectionAmountWithdrawn>.groupByPriorityAndYear() =
        groupBy { it.priorityAxis }
            .mapValues { it.value.groupBy { it.yearWhenFound } }

    private fun Map<String, Map<AccountingYear, List<CorrectionAmountWithdrawn>>>.calculateSumByYear() =
        map { (priorityAxis, correctionsPerPriority) ->
            AmountWithdrawnPerPriority(
                priorityAxis = priorityAxis,
                perYear = correctionsPerPriority.map { (year, correctionsPerYear) ->
                    AmountWithdrawnPerYear(
                        year = year,
                        withdrawalTotal = correctionsPerYear.total(),
                        withdrawalPublic = correctionsPerYear.public(),
                        withdrawalTotalOfWhichAa = correctionsPerYear.onlyAaAudits().total(),
                        withdrawalPublicOfWhichAa = correctionsPerYear.onlyAaAudits().public(),
                        withdrawalTotalOfWhichEc = correctionsPerYear.onlyEcOrEcaOrOlaf().total(),
                        withdrawalPublicOfWhichEc = correctionsPerYear.onlyEcOrEcaOrOlaf().public(),
                    )
                },
                withdrawalTotal = BigDecimal.ZERO,
                withdrawalPublic = BigDecimal.ZERO,
            )
        }

    private fun List<AmountWithdrawnPerPriority>.fillInPerPrioritySums() = onEach {
        it.withdrawalTotal = it.perYear.sumOf { year -> year.withdrawalTotal }
        it.withdrawalPublic = it.perYear.sumOf { year -> year.withdrawalPublic }
    }

}
