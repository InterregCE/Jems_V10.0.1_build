package io.cloudflight.jems.server.payments.service.ecPayment.getPaymentApplicationToEcList

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.payments.authorization.CanRetrievePaymentApplicationsToEc
import io.cloudflight.jems.server.payments.model.ec.PaymentApplicationToEc
import io.cloudflight.jems.server.payments.service.ecPayment.PaymentApplicationToEcPersistence
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetPaymentApplicationToEcList(
    private val persistence: PaymentApplicationToEcPersistence
) : GetPaymentApplicationToEcListInteractor {

    @CanRetrievePaymentApplicationsToEc
    @Transactional(readOnly = true)
    @ExceptionWrapper(GetPaymentApplicationToEcListException::class)
    override fun getPaymentApplicationsToEc(pageable: Pageable): Page<PaymentApplicationToEc> =
        persistence.findAll(pageable)

}
