package io.cloudflight.jems.server.payments.service.advance

import io.cloudflight.jems.server.payments.model.advance.AdvancePayment
import io.cloudflight.jems.server.payments.model.advance.AdvancePaymentDetail
import io.cloudflight.jems.server.payments.model.advance.AdvancePaymentUpdate
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface PaymentAdvancePersistence {

    fun list(pageable: Pageable): Page<AdvancePayment>

    fun existsById(id: Long): Boolean

    fun getPaymentDetail(paymentId: Long): AdvancePaymentDetail

    fun deleteByPaymentId(paymentId: Long)

    fun updatePaymentDetail(paymentDetail: AdvancePaymentUpdate): AdvancePaymentDetail

}
