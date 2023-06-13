package io.cloudflight.jems.server.plugin.services.payments

import io.cloudflight.jems.plugin.contract.models.payments.advance.AdvancePaymentData
import io.cloudflight.jems.plugin.contract.models.payments.advance.AdvancePaymentDetailData
import io.cloudflight.jems.plugin.contract.services.payments.PaymentsAdvanceDataProvider
import io.cloudflight.jems.server.payments.service.advance.PaymentAdvancePersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PaymentsAdvanceDataProviderImpl(
    private val paymentPersistence: PaymentAdvancePersistence
) : PaymentsAdvanceDataProvider {

    @Transactional(readOnly = true)
    override fun getAdvancePaymentsForProject(projectId: Long): List<AdvancePaymentData> =
        paymentPersistence.getPaymentsByProjectId(projectId).toDataModelList()

    @Transactional(readOnly = true)
    override fun getPaymentDetail(paymentId: Long): AdvancePaymentDetailData =
        paymentPersistence.getPaymentDetail(paymentId).toDataModel()

}