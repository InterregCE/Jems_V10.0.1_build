package io.cloudflight.jems.server.payments.service.regular

import io.cloudflight.jems.server.payments.entity.PaymentGroupingId
import io.cloudflight.jems.server.payments.model.ec.PaymentToEcLinkingUpdate
import io.cloudflight.jems.server.payments.model.ec.PaymentToEcPayment
import io.cloudflight.jems.server.payments.model.regular.*
import io.cloudflight.jems.server.payments.model.regular.contributionMeta.ContributionMeta
import io.cloudflight.jems.server.project.service.report.model.partner.financialOverview.coFinancing.ReportExpenditureCoFinancingColumn
import io.cloudflight.jems.server.project.service.report.model.project.financialOverview.coFinancing.PaymentCumulativeData
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface PaymentPersistence {

    fun existsById(id: Long): Boolean

    fun getAllPaymentToProject(pageable: Pageable, filters: PaymentSearchRequest): Page<PaymentToProject>

    fun getAllPaymentToEcPayment(pageable: Pageable, filters: PaymentSearchRequest): Page<PaymentToEcPayment>

    fun getConfirmedInfosForPayment(paymentId: Long): PaymentConfirmedInfo

    fun getPaymentDetails(paymentId: Long): PaymentDetail

    fun getAllPartnerPayments(paymentId: Long): List<PartnerPayment>

    fun getAllPartnerPaymentsForPartner(partnerId: Long): List<PartnerPaymentSimple>

    fun deleteFTLSByProjectIdAndOrderNrIn(projectId: Long, orderNr: Set<Int>)

    fun getAmountPerPartnerByProjectIdAndLumpSumOrderNrIn(
        projectId: Long,
        orderNrsToBeAdded: Set<Int>,
    ): List<PaymentPerPartner>

    fun saveFTLSPayments(projectId: Long, paymentsToBeSaved:  Map<PaymentGroupingId, PaymentToCreate>)

    fun saveRegularPayments(projectReportId:Long, paymentsToBeSaved: List<PaymentRegularToCreate>)

    fun getPaymentPartnersIdsByPaymentId(paymentId: Long): List<Long>

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

    fun getFtlsCumulativeForPartner(partnerId: Long): ReportExpenditureCoFinancingColumn

    fun getFtlsCumulativeForProject(projectId: Long): PaymentCumulativeData

    fun getPaymentIdsAvailableForEcPayments(fundId: Long, basis: PaymentSearchRequestScoBasis): Set<Long>

}
