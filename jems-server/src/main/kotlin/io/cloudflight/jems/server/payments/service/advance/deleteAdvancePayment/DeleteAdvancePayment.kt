package io.cloudflight.jems.server.payments.service.advance.deleteAdvancePayment

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.payments.service.advance.PaymentAdvancePersistence
import io.cloudflight.jems.server.payments.authorization.CanUpdateAdvancePayments
import io.cloudflight.jems.server.payments.service.advance.AdvancePaymentValidator
import io.cloudflight.jems.server.payments.service.advancePaymentDeleted
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DeleteAdvancePayment(
    private val advancePaymentPersistence: PaymentAdvancePersistence,
    private val validator: AdvancePaymentValidator,
    private val auditPublisher: ApplicationEventPublisher
) : DeleteAdvancePaymentInteractor {

    @CanUpdateAdvancePayments
    @Transactional
    @ExceptionWrapper(DeleteAdvancePaymentException::class)
    override fun delete(paymentId: Long) {
        if (!advancePaymentPersistence.existsById(paymentId))
            throw DeleteAdvancePaymentNotFoundException()
        val existing = advancePaymentPersistence.getPaymentDetail(paymentId)
        validator.validateDeletion(existing)

        advancePaymentPersistence.deleteByPaymentId(paymentId).also {
            auditPublisher.publishEvent(advancePaymentDeleted(context = this, paymentDetail = existing))
        }
    }

}
