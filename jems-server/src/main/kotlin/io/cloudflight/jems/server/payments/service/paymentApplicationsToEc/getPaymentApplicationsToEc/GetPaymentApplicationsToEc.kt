package io.cloudflight.jems.server.payments.service.paymentApplicationsToEc.getPaymentApplicationsToEc

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.payments.authorization.CanRetrievePaymentApplicationsToEc
import io.cloudflight.jems.server.payments.model.ec.PaymentApplicationsToEc
import io.cloudflight.jems.server.payments.service.paymentApplicationsToEc.PaymentApplicationsToEcPersistence
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetPaymentApplicationsToEc(
    private val persistence: PaymentApplicationsToEcPersistence
) : GetPaymentApplicationsToEcInteractor {

    @CanRetrievePaymentApplicationsToEc
    @Transactional(readOnly = true)
    @ExceptionWrapper(GetPaymentApplicationsToEcException::class)
    override fun getPaymentApplicationsToEc(pageable: Pageable): Page<PaymentApplicationsToEc> =
        persistence.findAll(pageable)

}
