package io.cloudflight.jems.server.plugin.services.payments

import io.cloudflight.jems.plugin.contract.models.common.paging.Page
import io.cloudflight.jems.plugin.contract.models.common.paging.Pageable
import io.cloudflight.jems.plugin.contract.models.payments.account.AmountWithdrawnPerPriorityData
import io.cloudflight.jems.plugin.contract.models.payments.account.PaymentAccountAmountSummaryData
import io.cloudflight.jems.plugin.contract.models.payments.account.PaymentAccountCorrectionLinkingData
import io.cloudflight.jems.plugin.contract.models.payments.account.PaymentAccountData
import io.cloudflight.jems.plugin.contract.models.payments.account.PaymentAccountOverviewData
import io.cloudflight.jems.plugin.contract.models.payments.account.ReconciledAmountPerPriorityData
import io.cloudflight.jems.plugin.contract.services.payments.PaymentAccountDataProvider
import io.cloudflight.jems.server.payments.service.account.PaymentAccountPersistence
import io.cloudflight.jems.server.payments.service.account.finance.correction.getAvailableClosedCorrections.PaymentAccountCorrectionsService
import io.cloudflight.jems.server.payments.service.account.finance.correction.getOverview.PaymentAccountCorrectionsOverviewService
import io.cloudflight.jems.server.payments.service.account.finance.getAmountSummary.PaymentAccountAmountSummaryService
import io.cloudflight.jems.server.payments.service.account.finance.reconciliation.getReconciliationOverview.PaymentAccountReconciliationOverviewService
import io.cloudflight.jems.server.payments.service.account.finance.withdrawn.getWithdrawnOverview.PaymentsAccountWithdrawnOverviewService
import io.cloudflight.jems.server.payments.service.account.listPaymentAccount.PaymentAccountsListService
import io.cloudflight.jems.server.plugin.services.toJpaPage
import io.cloudflight.jems.server.plugin.services.toPluginPage
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PaymentsAccountDataProviderImpl(
    private val paymentAccountsListService: PaymentAccountsListService,
    private val paymentAccountCorrectionsService: PaymentAccountCorrectionsService,
    private val paymentAccountPersistence: PaymentAccountPersistence,
    private val paymentAccountAmountSummaryService: PaymentAccountAmountSummaryService,
    private val paymentsAccountWithdrawnOverviewService: PaymentsAccountWithdrawnOverviewService,
    private val paymentAccountCorrectionsOverviewService: PaymentAccountCorrectionsOverviewService,
    private val paymentAccountReconciliationOverviewService: PaymentAccountReconciliationOverviewService,
): PaymentAccountDataProvider {

    @Transactional(readOnly = true)
    override fun listPaymentAccount(): List<PaymentAccountOverviewData> =
        paymentAccountsListService.listPaymentAccount().toDataModel()

    @Transactional(readOnly = true)
    override fun getPaymentAccount(paymentAccountId: Long): PaymentAccountData =
        paymentAccountPersistence.getByPaymentAccountId(paymentAccountId).toDataModel()

    @Transactional(readOnly = true)
    override fun getPaymentAccountAmountSummary(paymentAccountId: Long): PaymentAccountAmountSummaryData =
        paymentAccountAmountSummaryService.getSummaryOverview(paymentAccountId).toDataModel()

    @Transactional(readOnly = true)
    override fun getWithdrawnOverview(paymentAccountId: Long): List<AmountWithdrawnPerPriorityData> =
        paymentsAccountWithdrawnOverviewService.getWithdrawnOverview(paymentAccountId).toAmountWithDrawnDataModelList()

    @Transactional(readOnly = true)
    override fun getAvailableCorrections(
        pageable: Pageable,
        paymentAccountId: Long
    ): Page<PaymentAccountCorrectionLinkingData> =
        paymentAccountCorrectionsService.getClosedCorrections(pageable.toJpaPage(), paymentAccountId)
            .toPluginPage { it.toDataModel() }

    @Transactional(readOnly = true)
    override fun getPaymentAccountCorrectionsOverview(paymentAccountId: Long): PaymentAccountAmountSummaryData =
        paymentAccountCorrectionsOverviewService.getCurrentOverview(paymentAccountId).toDataModel()

    @Transactional(readOnly = true)
    override  fun getPaymentAccountReconciliationOverview(paymentAccountId: Long): List<ReconciledAmountPerPriorityData> =
        paymentAccountReconciliationOverviewService.getReconciliationOverview(paymentAccountId).toReconciledAmountDataModelList()
}
