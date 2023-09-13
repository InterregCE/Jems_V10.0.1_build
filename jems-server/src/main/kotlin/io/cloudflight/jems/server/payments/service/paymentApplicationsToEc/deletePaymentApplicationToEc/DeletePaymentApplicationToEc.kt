package io.cloudflight.jems.server.payments.service.paymentApplicationsToEc.deletePaymentApplicationToEc

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.payments.authorization.CanUpdatePaymentApplicationsToEc
import io.cloudflight.jems.server.payments.model.regular.PaymentEcStatus
import io.cloudflight.jems.server.payments.service.paymentApplicationToEcDeleted
import io.cloudflight.jems.server.payments.service.paymentApplicationsToEc.PaymentApplicationToEcPersistence
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DeletePaymentApplicationToEc(
    private val persistence: PaymentApplicationToEcPersistence,
    private val auditPublisher: ApplicationEventPublisher
) : DeletePaymentApplicationToEcInteractor {

    @CanUpdatePaymentApplicationsToEc
    @Transactional
    @ExceptionWrapper(DeletePaymentApplicationToEcException::class)
    override fun deleteById(id: Long) {
        val paymentApplicationToEc = persistence.getPaymentApplicationToEcDetail(id)
        if (paymentApplicationToEc.status == PaymentEcStatus.Finished) {
            throw PaymentFinishedException()
        }

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
