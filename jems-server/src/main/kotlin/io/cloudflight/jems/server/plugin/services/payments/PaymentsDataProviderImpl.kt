package io.cloudflight.jems.server.plugin.services.payments

import io.cloudflight.jems.plugin.contract.models.payments.regular.PaymentDetailData
import io.cloudflight.jems.plugin.contract.models.payments.regular.PaymentToProjectData
import io.cloudflight.jems.plugin.contract.services.payments.PaymentsDataProvider
import io.cloudflight.jems.server.payments.service.regular.PaymentPersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PaymentsDataProviderImpl(
    private val paymentPersistence: PaymentPersistence
) : PaymentsDataProvider {

    @Transactional(readOnly = true)
    override fun getPaymentDetail(paymentId: Long): PaymentDetailData =
        paymentPersistence.getPaymentDetails(paymentId).toDataModel()

    @Transactional(readOnly = true)
    override fun getPaymentsForProject(projectId: Long): List<PaymentToProjectData> =
        paymentPersistence.getPaymentsByProjectId(projectId).toDataModelList()

}