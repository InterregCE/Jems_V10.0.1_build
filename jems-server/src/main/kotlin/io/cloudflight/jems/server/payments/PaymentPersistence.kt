package io.cloudflight.jems.server.payments

import io.cloudflight.jems.server.payments.entity.PaymentToProjectEntity
import io.cloudflight.jems.server.payments.service.model.ComputedPaymentToProject
import io.cloudflight.jems.server.payments.service.model.PaymentToProject
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface PaymentPersistence {

    fun getAllPaymentToProject(pageable: Pageable): Page<PaymentToProject>

    fun deleteAllByProjectIdAndOrderNrIn(projectId: Long, orderNr: Set<Int>): List<PaymentToProjectEntity>

    fun deleteAllByProjectId(projectId: Long): List<PaymentToProjectEntity>

    fun getAmountPerPartnerByProjectIdAndLumpSumOrderNrIn(projectId: Long, orderNrsToBeAdded: MutableSet<Int>): List<ComputedPaymentToProject>

    fun savePaymentToProjects(projectId: Long, calculatedAmountsToBeAdded: List<ComputedPaymentToProject>)
}
