package io.cloudflight.jems.server.payments.service.advance.updateStatus

import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.payments.authorization.CanUpdateAdvancePayments
import io.cloudflight.jems.server.payments.model.advance.AdvancePaymentStatus
import io.cloudflight.jems.server.payments.service.advance.AdvancePaymentValidator
import io.cloudflight.jems.server.payments.service.advance.PaymentAdvancePersistence
import io.cloudflight.jems.server.payments.service.advancePaymentAuthorized
import io.cloudflight.jems.server.payments.service.advancePaymentConfirmed
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UpdateAdvancePaymentStatus(
    private val advancePaymentPersistence: PaymentAdvancePersistence,
    private val auditPublisher: ApplicationEventPublisher,
    private val securityService: SecurityService,
    private val advancePaymentValidator: AdvancePaymentValidator,
) : UpdateAdvancePaymentStatusInteractor {

    @CanUpdateAdvancePayments
    @Transactional
    @ExceptionWrapper(UpdateAdvancePaymentStatusException::class)
    override fun updateStatus(paymentId: Long, status: AdvancePaymentStatus) {
        val advancePayment = advancePaymentPersistence.getPaymentDetail(paymentId)

        advancePaymentValidator.validateStatus(status, advancePayment)

        val currentUserId = securityService.getUserIdOrThrow()
        advancePaymentPersistence.updateAdvancePaymentStatus(paymentId = paymentId, status = status, currentUserId = currentUserId).also {
            if (status == AdvancePaymentStatus.AUTHORIZED && advancePayment.paymentConfirmed == false)
                auditPublisher.publishEvent(advancePaymentAuthorized(context = this, paymentDetail = it))
            if (status == AdvancePaymentStatus.CONFIRMED)
                auditPublisher.publishEvent(advancePaymentConfirmed(context = this, paymentDetail = it))
        }
    }

}
