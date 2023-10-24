package io.cloudflight.jems.server.payments.service.ecPayment.getPaymentApplicationToEcList

import io.cloudflight.jems.server.payments.model.ec.PaymentApplicationToEc
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface GetPaymentApplicationToEcListInteractor {

    fun getPaymentApplicationsToEc(pageable: Pageable): Page<PaymentApplicationToEc>
}
