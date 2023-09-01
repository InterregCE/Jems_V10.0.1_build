package io.cloudflight.jems.server.payments.service.paymentApplicationsToEc.getPaymentApplicationsToEc

import io.cloudflight.jems.server.payments.model.ec.PaymentApplicationToEc
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface GetPaymentApplicationsToEcInteractor {

    fun getPaymentApplicationsToEc(pageable: Pageable): Page<PaymentApplicationToEc>
}
