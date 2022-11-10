package io.cloudflight.jems.server.payments.service.getAdvancePayments

import io.cloudflight.jems.server.payments.service.model.AdvancePayment
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface GetAdvancePaymentsInteractor {

    fun list(pageable: Pageable): Page<AdvancePayment>

}
