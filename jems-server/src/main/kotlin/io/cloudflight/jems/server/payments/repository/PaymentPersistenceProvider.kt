package io.cloudflight.jems.server.payments.repository

import io.cloudflight.jems.server.payments.PaymentPersistence
import io.cloudflight.jems.server.payments.entity.PaymentToProjectEntity
import io.cloudflight.jems.server.payments.entity.toListModel
import io.cloudflight.jems.server.payments.entity.toModel
import io.cloudflight.jems.server.payments.service.model.ComputedPaymentToProject
import io.cloudflight.jems.server.payments.service.model.PaymentRow
import io.cloudflight.jems.server.payments.service.model.PaymentToProject
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
class PaymentPersistenceProvider(private val paymentRepository: PaymentRepository): PaymentPersistence {

    @Transactional(readOnly = true)
    override fun getAllPayments(pageable: Pageable): Page<PaymentToProject> =
        this.paymentRepository.findAll(pageable).toListModel()

    @Transactional
    override fun getApprovedAmountsPerFundByProjectId(projectId: Long): List<ComputedPaymentToProject> =
        this.paymentRepository.getApprovedAmountsPerFundByProjectId(projectId).toModel()

    @Transactional
    override fun deleteAllByProjectIdAndOrderNrIn(projectId: Long, orderNr: Set<Int>): List<PaymentToProjectEntity> =
        this.paymentRepository.deleteAllByProjectIdAndOrderNr(projectId, orderNr)

}
