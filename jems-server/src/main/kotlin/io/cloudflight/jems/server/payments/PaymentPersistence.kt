package io.cloudflight.jems.server.payments

import io.cloudflight.jems.server.payments.entity.PaymentToProjectEntity
import io.cloudflight.jems.server.payments.service.model.ComputedPaymentToProject
import io.cloudflight.jems.server.payments.service.model.PaymentRow
import io.cloudflight.jems.server.payments.service.model.PaymentToProject
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface PaymentPersistence {

    fun getAllPayments(pageable: Pageable): Page<PaymentToProject>

    fun deleteAllByProjectIdAndOrderNrIn(projectId: Long, orderNr: Set<Int>): List<PaymentToProjectEntity>

    fun getApprovedAmountsPerFundByProjectId(projectId: Long): List<ComputedPaymentToProject>
}
