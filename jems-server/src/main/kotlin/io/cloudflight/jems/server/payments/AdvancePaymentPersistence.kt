package io.cloudflight.jems.server.payments

import io.cloudflight.jems.server.payments.service.model.AdvancePayment
import io.cloudflight.jems.server.payments.service.model.AdvancePaymentDetail
import io.cloudflight.jems.server.payments.service.model.AdvancePaymentUpdate
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface AdvancePaymentPersistence {

    fun list(pageable: Pageable): Page<AdvancePayment>

    fun existsById(id: Long): Boolean

    fun getPaymentDetail(paymentId: Long): AdvancePaymentDetail

    fun deleteByPaymentId(paymentId: Long)

    fun updatePaymentDetail(paymentDetail: AdvancePaymentUpdate): AdvancePaymentDetail

}
