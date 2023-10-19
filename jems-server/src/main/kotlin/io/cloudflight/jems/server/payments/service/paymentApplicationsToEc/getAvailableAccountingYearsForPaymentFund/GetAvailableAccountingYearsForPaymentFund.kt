package io.cloudflight.jems.server.payments.service.paymentApplicationsToEc.getAvailableAccountingYearsForPaymentFund

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.payments.authorization.CanRetrievePaymentApplicationsToEc
import io.cloudflight.jems.server.payments.model.regular.AccountingYear
import io.cloudflight.jems.server.payments.service.paymentApplicationsToEc.PaymentApplicationToEcPersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetAvailableAccountingYearsForPaymentFund(
    private val paymentApplicationsToEcPersistence: PaymentApplicationToEcPersistence,
): GetAvailableAccountingYearsForPaymentFundInteractor {

    @CanRetrievePaymentApplicationsToEc
    @Transactional(readOnly = true)
    @ExceptionWrapper(GetAvailableAccountingYearsForPaymentFundException::class)
    override fun getAvailableAccountingYearsForPaymentFund(programmeFundId: Long): List<AccountingYear> =
        paymentApplicationsToEcPersistence.getAvailableAccountingYearsForFund(programmeFundId)
}