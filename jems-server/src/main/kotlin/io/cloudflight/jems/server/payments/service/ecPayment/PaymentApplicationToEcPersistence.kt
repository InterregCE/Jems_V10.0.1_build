package io.cloudflight.jems.server.payments.service.ecPayment

import io.cloudflight.jems.server.payments.model.ec.PaymentApplicationToEc
import io.cloudflight.jems.server.payments.model.ec.PaymentApplicationToEcCreate
import io.cloudflight.jems.server.payments.model.ec.PaymentApplicationToEcDetail
import io.cloudflight.jems.server.payments.model.ec.PaymentApplicationToEcSummaryUpdate
import io.cloudflight.jems.server.payments.model.ec.AccountingYearAvailability
import io.cloudflight.jems.server.payments.model.regular.PaymentEcStatus
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

    fun existsDraftByFundAndAccountingYear(programmeFundId: Long, accountingYearId: Long): Boolean

    fun getAvailableAccountingYearsForFund(programmeFundId: Long): List<AccountingYearAvailability>

}
