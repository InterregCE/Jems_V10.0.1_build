package io.cloudflight.jems.server.payments.service.paymentApplicationsToEc.createPaymentApplicationToEc

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.payments.authorization.CanUpdatePaymentApplicationsToEc
import io.cloudflight.jems.server.payments.model.ec.PaymentApplicationToEcDetail
import io.cloudflight.jems.server.payments.model.ec.PaymentApplicationToEcSummaryUpdate
import io.cloudflight.jems.server.payments.service.paymentApplicationToEcCreated
import io.cloudflight.jems.server.payments.service.paymentApplicationsToEc.PaymentApplicationToEcPersistence
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CreatePaymentApplicationToEc(
    private val auditPublisher: ApplicationEventPublisher,
    private val paymentApplicationsToEcPersistence: PaymentApplicationToEcPersistence,
) : CreatePaymentApplicationToEcInteractor {

    @CanUpdatePaymentApplicationsToEc
    @Transactional
    @ExceptionWrapper(CreatePaymentApplicationToEcException::class)
    override fun createPaymentApplicationToEc(paymentApplicationToEcUpdate: PaymentApplicationToEcSummaryUpdate): PaymentApplicationToEcDetail =
        paymentApplicationsToEcPersistence.createPaymentApplicationToEc(paymentApplicationToEcUpdate).also{
            auditPublisher.publishEvent(paymentApplicationToEcCreated(context = this, paymentApplicationToEc = it))
        }


}
