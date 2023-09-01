package io.cloudflight.jems.server.payments.service.advance

import io.cloudflight.jems.server.payments.model.advance.AdvancePayment
import io.cloudflight.jems.server.payments.model.advance.AdvancePaymentDetail
import io.cloudflight.jems.server.payments.model.advance.AdvancePaymentSearchRequest
import io.cloudflight.jems.server.payments.model.advance.AdvancePaymentUpdate
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface PaymentAdvancePersistence {

    fun list(pageable: Pageable, filters: AdvancePaymentSearchRequest): Page<AdvancePayment>

    fun existsById(id: Long): Boolean

    fun getPaymentsByProjectId(projectId: Long): List<AdvancePayment>

    fun getPaymentDetail(paymentId: Long): AdvancePaymentDetail

    fun deleteByPaymentId(paymentId: Long)

    fun updatePaymentDetail(paymentDetail: AdvancePaymentUpdate): AdvancePaymentDetail

    fun deletePaymentAdvanceAttachment(fileId: Long)

    fun getConfirmedPaymentsForProject(projectId: Long, pageable: Pageable): Page<AdvancePayment>

}
