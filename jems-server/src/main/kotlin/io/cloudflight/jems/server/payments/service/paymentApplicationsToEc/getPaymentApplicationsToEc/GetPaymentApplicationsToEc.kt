package io.cloudflight.jems.server.payments.service.paymentApplicationsToEc.getPaymentApplicationsToEc

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.payments.authorization.CanRetrievePaymentApplicationsToEc
import io.cloudflight.jems.server.payments.model.ec.PaymentApplicationToEc
import io.cloudflight.jems.server.payments.service.paymentApplicationsToEc.PaymentApplicationToEcPersistence
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetPaymentApplicationsToEc(
    private val persistence: PaymentApplicationToEcPersistence
) : GetPaymentApplicationsToEcInteractor {

    @CanRetrievePaymentApplicationsToEc
    @Transactional(readOnly = true)
    @ExceptionWrapper(GetPaymentApplicationsToEcException::class)
    override fun getPaymentApplicationsToEc(pageable: Pageable): Page<PaymentApplicationToEc> =
        persistence.findAll(pageable)

}
