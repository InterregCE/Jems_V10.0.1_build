package io.cloudflight.jems.server.payments.service.ecPayment.getAvailableAccountingYearsForPaymentFund

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.payments.authorization.CanRetrievePaymentApplicationsToEc
import io.cloudflight.jems.server.payments.model.ec.AccountingYearAvailability
import io.cloudflight.jems.server.payments.service.ecPayment.PaymentApplicationToEcPersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetAvailableAccountingYearsForPaymentFund(
    private val ecPaymentPersistence: PaymentApplicationToEcPersistence,
): GetAvailableAccountingYearsForPaymentFundInteractor {

    @CanRetrievePaymentApplicationsToEc
    @Transactional(readOnly = true)
    @ExceptionWrapper(GetAvailableAccountingYearsForPaymentFundException::class)
    override fun getAvailableAccountingYearsForPaymentFund(programmeFundId: Long): List<AccountingYearAvailability> =
        ecPaymentPersistence.getAvailableAccountingYearsForFund(programmeFundId)
}
