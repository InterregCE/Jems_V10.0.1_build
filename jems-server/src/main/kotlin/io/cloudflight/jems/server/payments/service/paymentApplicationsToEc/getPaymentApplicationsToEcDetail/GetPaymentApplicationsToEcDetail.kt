package io.cloudflight.jems.server.payments.service.paymentApplicationsToEc.getPaymentApplicationsToEcDetail

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.payments.authorization.CanRetrievePaymentApplicationsToEc
import io.cloudflight.jems.server.payments.model.ec.PaymentApplicationsToEcDetail
import io.cloudflight.jems.server.payments.service.paymentApplicationsToEc.PaymentApplicationsToEcPersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetPaymentApplicationsToEcDetail(
    private val persistence: PaymentApplicationsToEcPersistence
) : GetPaymentApplicationsToEcDetailInteractor {

    @CanRetrievePaymentApplicationsToEc
    @Transactional(readOnly = true)
    @ExceptionWrapper(GetPaymentApplicationsToEcDetailException::class)
    override fun getPaymentApplicationsToEcDetail(id: Long): PaymentApplicationsToEcDetail {
        return persistence.getPaymentApplicationsToEcDetail(id)
    }

}
