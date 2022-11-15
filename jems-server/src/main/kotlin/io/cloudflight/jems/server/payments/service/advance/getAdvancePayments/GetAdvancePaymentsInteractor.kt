package io.cloudflight.jems.server.payments.service.advance.getAdvancePayments

import io.cloudflight.jems.server.payments.model.advance.AdvancePayment
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface GetAdvancePaymentsInteractor {

    fun list(pageable: Pageable): Page<AdvancePayment>

}
