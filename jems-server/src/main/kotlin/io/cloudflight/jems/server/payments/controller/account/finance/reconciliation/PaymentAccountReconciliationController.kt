package io.cloudflight.jems.server.payments.controller.account.finance.reconciliation

import io.cloudflight.jems.api.payments.account.finance.PaymentAccountReconciliationApi
import io.cloudflight.jems.api.payments.dto.account.finance.reconciliation.ReconciledAmountPerPriorityDTO
import io.cloudflight.jems.api.payments.dto.account.finance.reconciliation.ReconciledAmountUpdateDTO
import io.cloudflight.jems.server.payments.controller.account.finance.toAmountReconciledDto
import io.cloudflight.jems.server.payments.controller.account.finance.toModel
import io.cloudflight.jems.server.payments.service.account.finance.reconciliation.getReconciliationOverview.GetReconciliationOverviewInteractor
import io.cloudflight.jems.server.payments.service.account.finance.reconciliation.updateReconciliation.UpdatePaymentReconciliationInteractor
import org.springframework.web.bind.annotation.RestController

@RestController
class PaymentAccountReconciliationController(
    private val getReconciliationOverview: GetReconciliationOverviewInteractor,
    private val updateReconciliation: UpdatePaymentReconciliationInteractor,
) : PaymentAccountReconciliationApi {

    override fun getReconciliationOverview(paymentAccountId: Long): List<ReconciledAmountPerPriorityDTO> =
        getReconciliationOverview.getReconciliationOverview(paymentAccountId).toAmountReconciledDto()

    override fun updateReconciliationComment(
        paymentAccountId: Long,
        reconciliationUpdate: ReconciledAmountUpdateDTO
    ) =
        updateReconciliation.updatePaymentReconciliation(paymentAccountId, reconciliationUpdate.toModel())

}
