package io.cloudflight.jems.server.payments.service.regular

import io.cloudflight.jems.server.payments.entity.PaymentGroupingId
import io.cloudflight.jems.server.payments.model.ec.PaymentToEcExtension
import io.cloudflight.jems.server.payments.model.ec.PaymentToEcPayment
import io.cloudflight.jems.server.payments.model.regular.PartnerPayment
import io.cloudflight.jems.server.payments.model.regular.PartnerPaymentSimple
import io.cloudflight.jems.server.payments.model.regular.PaymentConfirmedInfo
import io.cloudflight.jems.server.payments.model.regular.PaymentDetail
import io.cloudflight.jems.server.payments.model.regular.PaymentPartnerInstallment
import io.cloudflight.jems.server.payments.model.regular.PaymentPartnerInstallmentUpdate
import io.cloudflight.jems.server.payments.model.regular.PaymentPerPartner
import io.cloudflight.jems.server.payments.model.regular.toCreate.PaymentRegularToCreate
import io.cloudflight.jems.server.payments.model.regular.PaymentSearchRequest
import io.cloudflight.jems.server.payments.model.regular.PaymentSearchRequestScoBasis
import io.cloudflight.jems.server.payments.model.regular.PaymentToProject
import io.cloudflight.jems.server.payments.model.regular.contributionMeta.ContributionMeta
import io.cloudflight.jems.server.payments.model.regular.toCreate.PaymentFtlsToCreate
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

    fun saveFTLSPayments(projectId: Long, paymentsToBeSaved: Map<PaymentGroupingId, PaymentFtlsToCreate>)

    fun saveRegularPayments(projectReportId: Long, paymentsToBeSaved: Map<Long, PaymentRegularToCreate>)

    fun getPaymentPartnersIdsByPaymentId(paymentId: Long): List<Long>

    fun getPaymentPartnerId(paymentId: Long, partnerId: Long): Long

    fun findPaymentPartnerInstallments(paymentPartnerId: Long): List<PaymentPartnerInstallment>

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

    fun getPaymentsLinkedToEcPayment(ecPaymentId: Long):  List<PaymentToEcExtension>

    fun getPaymentIdsInstallmentsExistsByProjectReportId(projectReportId: Long): Set<Long>

    fun deleteRegularPayments(projectReportId: Long)
}
