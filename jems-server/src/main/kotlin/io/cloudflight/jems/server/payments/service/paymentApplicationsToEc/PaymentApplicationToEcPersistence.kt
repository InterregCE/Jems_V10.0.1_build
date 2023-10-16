package io.cloudflight.jems.server.payments.service.paymentApplicationsToEc

import io.cloudflight.jems.server.payments.model.ec.PaymentApplicationToEc
import io.cloudflight.jems.server.payments.model.ec.PaymentApplicationToEcCreate
import io.cloudflight.jems.server.payments.model.ec.PaymentApplicationToEcDetail
import io.cloudflight.jems.server.payments.model.ec.PaymentApplicationToEcSummaryUpdate
import io.cloudflight.jems.server.payments.model.ec.PaymentToEcAmountSummaryLine
import io.cloudflight.jems.server.payments.model.ec.PaymentToEcAmountSummaryLineTmp
import io.cloudflight.jems.server.payments.model.ec.PaymentToEcExtension
import io.cloudflight.jems.server.payments.model.ec.PaymentToEcLinkingUpdate
import io.cloudflight.jems.server.payments.model.regular.AccountingYear
import io.cloudflight.jems.server.payments.model.regular.PaymentEcStatus
import io.cloudflight.jems.server.payments.model.regular.PaymentSearchRequestScoBasis
import io.cloudflight.jems.server.payments.model.regular.PaymentType
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface PaymentApplicationToEcPersistence {

    fun createPaymentApplicationToEc(paymentApplicationsToEcUpdate: PaymentApplicationToEcCreate): PaymentApplicationToEcDetail

    fun updatePaymentApplicationToEc(
        paymentApplicationId: Long,
        paymentApplicationsToEcUpdate: PaymentApplicationToEcSummaryUpdate
    ): PaymentApplicationToEcDetail

    fun updatePaymentToEcSummaryOtherSection(paymentToEcUpdate: PaymentApplicationToEcSummaryUpdate): PaymentApplicationToEcDetail

    fun getPaymentApplicationToEcDetail(id: Long): PaymentApplicationToEcDetail

    fun findAll(pageable: Pageable): Page<PaymentApplicationToEc>

    fun updatePaymentApplicationToEcStatus(paymentId: Long, status: PaymentEcStatus): PaymentApplicationToEcDetail

    fun deleteById(id: Long)

    fun deletePaymentToEcAttachment(fileId: Long)

    fun getPaymentExtension(paymentId: Long): PaymentToEcExtension

    fun getPaymentsLinkedToEcPayment(ecPaymentId: Long): Map<Long, PaymentType>

    fun selectPaymentToEcPayment(paymentIds: Set<Long>, ecPaymentId: Long)

    fun deselectPaymentFromEcPaymentAndResetFields(paymentId: Long)

    fun updatePaymentToEcCorrectedAmounts(paymentId: Long, paymentToEcLinkingUpdate: PaymentToEcLinkingUpdate)

    fun existsDraftByFundAndAccountingYear(programmeFundId: Long, accountingYearId: Long): Boolean

    fun getAvailableAccountingYearsForFund(programmeFundId: Long): List<AccountingYear>

    fun calculateAndGetTotals(ecPaymentId: Long): Map<PaymentSearchRequestScoBasis, List<PaymentToEcAmountSummaryLineTmp>>

    fun saveTotalsWhenFinishingEcPayment(
        ecPaymentId: Long,
        totals: Map<PaymentSearchRequestScoBasis, List<PaymentToEcAmountSummaryLine>>,
    )

    fun getTotalsForFinishedEcPayment(
        ecPaymentId: Long,
    ): Map<PaymentSearchRequestScoBasis, List<PaymentToEcAmountSummaryLine>>

    fun updatePaymentToEcFinalScoBasis(paymentIds: Set<Long>, finalScoBasis: PaymentSearchRequestScoBasis)
}
