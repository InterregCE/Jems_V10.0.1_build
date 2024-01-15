package io.cloudflight.jems.server.payments.service.ecPayment.deletePaymentApplicationToEc

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.payments.authorization.CanUpdatePaymentApplicationsToEc
import io.cloudflight.jems.server.payments.model.ec.PaymentToEcExtension
import io.cloudflight.jems.server.payments.service.ecPayment.PaymentApplicationToEcPersistence
import io.cloudflight.jems.server.payments.service.ecPayment.linkToPayment.PaymentApplicationToEcLinkPersistence
import io.cloudflight.jems.server.payments.service.paymentApplicationToEcDeleted
import io.cloudflight.jems.server.payments.service.regular.PaymentPersistence
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DeletePaymentApplicationToEc(
    private val ecPaymentPersistence: PaymentApplicationToEcPersistence,
    private val ecPaymentLinkPersistence: PaymentApplicationToEcLinkPersistence,
    private val paymentPersistence: PaymentPersistence,
    private val auditPublisher: ApplicationEventPublisher
) : DeletePaymentApplicationToEcInteractor {

    @CanUpdatePaymentApplicationsToEc
    @Transactional
    @ExceptionWrapper(DeletePaymentApplicationToEcException::class)
    override fun deleteById(id: Long) {
        val ecPayment = ecPaymentPersistence.getPaymentApplicationToEcDetail(id)
        if (ecPayment.status.isFinished())
            throw PaymentFinishedException()

        val toResetPaymentIds = paymentPersistence.getPaymentsLinkedToEcPayment(id).onlyIds()
        ecPaymentLinkPersistence.deselectPaymentFromEcPaymentAndResetFields(toResetPaymentIds)

        ecPaymentPersistence.deleteById(id).also {
            auditPublisher.publishEvent(
                paymentApplicationToEcDeleted(context = this, ecPayment)
            )
        }
    }


    private fun List<PaymentToEcExtension>.onlyIds() = mapTo(HashSet()) { it.paymentId }

}
