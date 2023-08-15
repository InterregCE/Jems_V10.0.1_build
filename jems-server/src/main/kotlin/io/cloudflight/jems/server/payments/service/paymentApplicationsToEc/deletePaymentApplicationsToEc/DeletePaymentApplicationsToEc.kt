package io.cloudflight.jems.server.payments.service.paymentApplicationsToEc.deletePaymentApplicationsToEc

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.payments.authorization.CanUpdatePaymentApplicationsToEc
import io.cloudflight.jems.server.payments.service.paymentApplicationToEcDeleted
import io.cloudflight.jems.server.payments.service.paymentApplicationsToEc.PaymentApplicationsToEcPersistence
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DeletePaymentApplicationsToEc(
    private val persistence: PaymentApplicationsToEcPersistence,
    private val auditPublisher: ApplicationEventPublisher
) : DeletePaymentApplicationsToEcInteractor {

    @CanUpdatePaymentApplicationsToEc
    @Transactional
    @ExceptionWrapper(DeletePaymentApplicationsToEcException::class)
    override fun deleteById(id: Long) {
        val paymentApplicationToEc = persistence.getPaymentApplicationsToEcDetail(id)

        persistence.deleteById(id).also {
            auditPublisher.publishEvent(
                paymentApplicationToEcDeleted(
                    context = this,
                    paymentApplicationToEc = paymentApplicationToEc
                )
            )
        }
    }

}
