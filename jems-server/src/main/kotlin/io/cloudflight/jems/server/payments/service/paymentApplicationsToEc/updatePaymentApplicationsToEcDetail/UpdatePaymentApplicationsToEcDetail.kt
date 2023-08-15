package io.cloudflight.jems.server.payments.service.paymentApplicationsToEc.updatePaymentApplicationsToEcDetail

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.payments.authorization.CanUpdatePaymentApplicationsToEc
import io.cloudflight.jems.server.payments.model.ec.PaymentApplicationsToEcDetail
import io.cloudflight.jems.server.payments.model.ec.PaymentApplicationsToEcUpdate
import io.cloudflight.jems.server.payments.service.paymentApplicationToEcCreated
import io.cloudflight.jems.server.payments.service.paymentApplicationsToEc.PaymentApplicationsToEcPersistence
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UpdatePaymentApplicationsToEcDetail(
    private val paymentApplicationsToEcPersistence: PaymentApplicationsToEcPersistence,
    private val auditPublisher: ApplicationEventPublisher
) : UpdatePaymentApplicationsToEcDetailInteractor {

    @CanUpdatePaymentApplicationsToEc
    @Transactional
    @ExceptionWrapper(UpdatePaymentApplicationsToEcDetailException::class)
    override fun updatePaymentApplicationsToEc(paymentApplicationstoEcUpdate: PaymentApplicationsToEcUpdate): PaymentApplicationsToEcDetail {
        val existing = if (paymentApplicationstoEcUpdate.id != null) {
            paymentApplicationsToEcPersistence.getPaymentApplicationsToEcDetail(paymentApplicationstoEcUpdate.id)
        } else {
            null
        }

        return paymentApplicationsToEcPersistence.updatePaymentApplicationsToEc(paymentApplicationstoEcUpdate).also {
            if (existing == null) {
                auditPublisher.publishEvent(paymentApplicationToEcCreated(context = this, paymentApplicationToEc = it))
            }
        }
    }

}
