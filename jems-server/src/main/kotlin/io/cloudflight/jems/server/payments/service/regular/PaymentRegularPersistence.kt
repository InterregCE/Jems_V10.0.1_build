package io.cloudflight.jems.server.payments.service.regular

import io.cloudflight.jems.server.payments.entity.PaymentGroupingId
import io.cloudflight.jems.server.payments.model.regular.PartnerPayment
import io.cloudflight.jems.server.payments.model.regular.PartnerPaymentSimple
import io.cloudflight.jems.server.payments.model.regular.PaymentConfirmedInfo
import io.cloudflight.jems.server.payments.model.regular.PaymentDetail
import io.cloudflight.jems.server.payments.model.regular.PaymentPartnerInstallment
import io.cloudflight.jems.server.payments.model.regular.PaymentPartnerInstallmentUpdate
import io.cloudflight.jems.server.payments.model.regular.PaymentPerPartner
import io.cloudflight.jems.server.payments.model.regular.PaymentSearchRequest
import io.cloudflight.jems.server.payments.model.regular.PaymentToCreate
import io.cloudflight.jems.server.payments.model.regular.PaymentToProject
import io.cloudflight.jems.server.payments.model.regular.contributionMeta.ContributionMeta
import io.cloudflight.jems.server.project.service.report.model.partner.financialOverview.coFinancing.ReportExpenditureCoFinancingColumn
import io.cloudflight.jems.server.project.service.report.model.project.financialOverview.coFinancing.PaymentCumulativeData
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface PaymentRegularPersistence {

    fun existsById(id: Long): Boolean

    fun getAllPaymentToProject(pageable: Pageable, filters: PaymentSearchRequest): Page<PaymentToProject>

    fun getConfirmedInfosForPayment(paymentId: Long): PaymentConfirmedInfo

    fun getPaymentDetails(paymentId: Long): PaymentDetail

    fun getAllPartnerPayments(paymentId: Long): List<PartnerPayment>

    fun getAllPartnerPaymentsForPartner(partnerId: Long): List<PartnerPaymentSimple>

    fun deleteAllByProjectIdAndOrderNrIn(projectId: Long, orderNr: Set<Int>)

    fun getAmountPerPartnerByProjectIdAndLumpSumOrderNrIn(
        projectId: Long,
        orderNrsToBeAdded: MutableSet<Int>
    ): List<PaymentPerPartner>

    fun savePaymentToProjects(projectId: Long, paymentsToBeSaved:  Map<PaymentGroupingId, PaymentToCreate>)

    fun getPaymentPartnerId(paymentId: Long, partnerId: Long): Long

    fun findPaymentPartnerInstallments(paymentPartnerId: Long): List <PaymentPartnerInstallment>

    fun findByPartnerId(partnerId: Long): List<PaymentPartnerInstallment>

    fun updatePaymentPartnerInstallments(
        paymentPartnerId: Long,
        toDeleteInstallmentIds: Set<Long>,
        paymentPartnerInstallments: List<PaymentPartnerInstallmentUpdate>
    ): List<PaymentPartnerInstallment>

    fun deletePaymentAttachment(fileId: Long)

    fun getPaymentsByProjectId(projectId: Long): List<PaymentToProject>

    fun storePartnerContributionsWhenReadyForPayment(contributions: Collection<ContributionMeta>)

    fun deleteContributionsWhenReadyForPaymentReverted(projectId: Long, orderNrs: Set<Int>)

    fun getCoFinancingAndContributionsCumulative(partnerId: Long): ReportExpenditureCoFinancingColumn

    fun getPaymentsCumulativeForProject(projectId: Long): PaymentCumulativeData

}
