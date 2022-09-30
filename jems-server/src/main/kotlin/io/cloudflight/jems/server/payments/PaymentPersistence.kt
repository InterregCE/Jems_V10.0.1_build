package io.cloudflight.jems.server.payments

import io.cloudflight.jems.server.payments.entity.PaymentGroupingId
import io.cloudflight.jems.server.payments.service.model.PartnerPayment
import io.cloudflight.jems.server.payments.service.model.PaymentDetail
import io.cloudflight.jems.server.payments.service.model.PaymentToCreate
import io.cloudflight.jems.server.payments.service.model.PaymentPerPartner
import io.cloudflight.jems.server.payments.service.model.PaymentToProject
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface PaymentPersistence {

    fun getAllPaymentToProject(pageable: Pageable): Page<PaymentToProject>

    fun getPaymentDetails(paymentId: Long): PaymentDetail

    fun getAllPartnerPayments(paymentId: Long): List<PartnerPayment>

    fun deleteAllByProjectIdAndOrderNrIn(projectId: Long, orderNr: Set<Int>)

    fun getAmountPerPartnerByProjectIdAndLumpSumOrderNrIn(projectId: Long, orderNrsToBeAdded: MutableSet<Int>): List<PaymentPerPartner>

    fun savePaymentToProjects(projectId: Long, paymentsToBeSaved:  Map<PaymentGroupingId, PaymentToCreate>)
}
