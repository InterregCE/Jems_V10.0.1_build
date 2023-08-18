package io.cloudflight.jems.server.payments.service.paymentApplicationsToEc.updatePaymentApplicationToEcDetail

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.payments.authorization.CanUpdatePaymentApplicationsToEc
import io.cloudflight.jems.server.payments.model.ec.PaymentApplicationToEcDetail
import io.cloudflight.jems.server.payments.model.ec.PaymentApplicationToEcUpdate
import io.cloudflight.jems.server.payments.service.paymentApplicationsToEc.PaymentApplicationToEcPersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UpdatePaymentApplicationToEcDetail(
    private val paymentApplicationToEcPersistence: PaymentApplicationToEcPersistence,
) : UpdatePaymentApplicationToEcDetailInteractor {

    @CanUpdatePaymentApplicationsToEc
    @Transactional
    @ExceptionWrapper(UpdatePaymentApplicationToEcDetailException::class)
    override fun updatePaymentApplicationToEc(paymentApplicationToEcUpdate: PaymentApplicationToEcUpdate): PaymentApplicationToEcDetail =
        paymentApplicationToEcPersistence.updatePaymentApplicationToEc(paymentApplicationToEcUpdate)


}
