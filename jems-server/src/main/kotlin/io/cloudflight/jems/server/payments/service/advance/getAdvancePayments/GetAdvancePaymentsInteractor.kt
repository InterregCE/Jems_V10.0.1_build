package io.cloudflight.jems.server.payments.service.advance.getAdvancePayments

import io.cloudflight.jems.server.payments.model.advance.AdvancePayment
import io.cloudflight.jems.server.payments.model.advance.AdvancePaymentSearchRequest
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface GetAdvancePaymentsInteractor {

    fun list(pageable: Pageable, filters: AdvancePaymentSearchRequest): Page<AdvancePayment>

}
